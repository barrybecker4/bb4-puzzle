package com.barrybecker4.puzzle.common.solver;

import com.barrybecker4.common.math.MathUtil;
import com.barrybecker4.puzzle.common.PuzzleController;
import com.barrybecker4.puzzle.common.model.PuzzleNode;

import java.security.AccessControlException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * Concurrent version of puzzle solver.
 * Does not recognize when there is no solution (use ConcurrentPuzzle Solver instead)
 * P is the Puzzle type
 * M is the move type
 *
 * @author Brian Goetz
 * @author Tim Peierls
 * @author Barry Becker
 */
public class BaseConcurrentPuzzleSolver<P, M>  implements PuzzleSolver<M> {

    private static final int THREAD_POOL_SIZE = 100;

    private final PuzzleController<P, M> puzzle;
    private final ExecutorService exec;

    private final Set<P> seen;
    protected final ValueLatch<PuzzleNode<P, M>> solution = new ValueLatch<>();
    private volatile int numTries;
    /** default is a mixture between depth (0) (sequential) and breadth (1.0) (concurrent) first search. */
    private float depthBreadthFactor = 0.4f;

    /**
     * Constructor
     * @param puzzle the puzzle instance to solve.
     */
    public BaseConcurrentPuzzleSolver(PuzzleController<P, M> puzzle) {
        this.puzzle = puzzle;
        this.exec = initThreadPool();
        this.seen = new HashSet<>();
        numTries = 0;
        if (exec instanceof ThreadPoolExecutor) {
            ThreadPoolExecutor tpe = (ThreadPoolExecutor) exec;
            tpe.setRejectedExecutionHandler(new ThreadPoolExecutor.DiscardPolicy());
        }
    }

    /**
     * The amount that you want the search to use depth first or breadth first search.
     * If factor is 0, then all depth first traversal and no concurrent,
     * if 1, then all breadth first search and not sequential.
     * If the search is large, it is easier to run out of memory at the extremes.
     * Must be greater than 0 to have some amount of concurrency used.
     * @param factor a number between 0 and 1. One being all breadth first search and not sequential.
     */
    protected void setDepthBreadthFactor(float factor) {
        depthBreadthFactor = factor;
    }

    /** initialize the thread pool with some initial fixed size */
    private ExecutorService initThreadPool() {
        return Executors.newFixedThreadPool(THREAD_POOL_SIZE);
    }

    @Override
    public List<M> solve() throws InterruptedException {
        try {
            return doSolve();
        } finally {
            try {
                exec.shutdown();
            } catch (AccessControlException e) {
                System.out.println("AccessControlException shutting down exec thread. " +
                        "Probably because running in a secure sandbox.");
            }
        }
    }

    /**
     * Solve the puzzle concurrently
     * @return list fo moves leading to the solution (assuming one was found).
     *  Null is returned if there was no solution.
     * @throws InterruptedException if interrupted during processing.
     */
    private List<M> doSolve() throws InterruptedException {
        P p = puzzle.initialPosition();
        long startTime = System.currentTimeMillis();
        exec.execute(newTask(p, null, null));

        // block until solution found
        PuzzleNode<P, M> solutionPuzzleNode = solution.getValue();

        List<M> path = (solutionPuzzleNode == null) ? null : solutionPuzzleNode.asMoveList();
        long elapsedTime = System.currentTimeMillis() - startTime;
        P position = (solutionPuzzleNode == null) ? null : solutionPuzzleNode.getPosition();
        System.out.println("solution = " + position);

        puzzle.finalRefresh(path, position, numTries, elapsedTime);

        return path;
    }

    protected SolverTask newTask(P p, M m, PuzzleNode<P, M> n) {
        return new SolverTask(p, m, n);
    }

    /**
     * Runnable used to solve a puzzle.
     */
    protected class SolverTask extends PuzzleNode<P, M> implements Runnable {
        SolverTask(P pos, M move, PuzzleNode<P, M> prev) {
            super(pos, move, prev);
        }

        @Override
        public void run() {

            numTries++;
            if (solution.isSet() || puzzle.alreadySeen(getPosition(), seen)) {
                return; // already solved or seen this position
            }
            puzzle.refresh(getPosition(), numTries);

            if (puzzle.isGoal(getPosition())) {
                solution.setValue(this);
            }
            else {
                for (M move : puzzle.legalMoves(getPosition())) {
                    SolverTask task = newTask(puzzle.move(getPosition(), move), move, this);

                    // either process the children sequentially or concurrently based on depthBreadthFactor
                    if (MathUtil.RANDOM.nextFloat() > depthBreadthFactor) {
                        // go deep
                        task.run();
                    } else {
                        // go wide
                        exec.execute(task);
                    }
                }
            }
        }
    }
}

