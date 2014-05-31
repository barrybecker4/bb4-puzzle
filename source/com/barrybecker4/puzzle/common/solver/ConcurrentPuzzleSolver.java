package com.barrybecker4.puzzle.common.solver;

import com.barrybecker4.puzzle.common.PuzzleController;
import com.barrybecker4.puzzle.common.model.PuzzleNode;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Solver that recognizes when no solution exists and stops running if that happens.
 *
 * @author Brian Goetz
 * @author Tim Peierls
 */
public class ConcurrentPuzzleSolver <P, M> extends BaseConcurrentPuzzleSolver<P, M> {

    private final AtomicInteger taskCount = new AtomicInteger(0);

    /**
     * @param puzzle the puzzle to solve
     * @param depthBreadthFactor the ratio of depth first to breadth first searching to use.
     *                           May have significant performance impact.
     */
    public ConcurrentPuzzleSolver(PuzzleController<P, M> puzzle, float  depthBreadthFactor) {
        super(puzzle);
        setDepthBreadthFactor(depthBreadthFactor);
        taskCount.set(0);
    }


    @Override
    protected SolverTask newTask(P p, M m, PuzzleNode<P, M> n) {
        return new CountingSolverTask(p, m, n);
    }


    class CountingSolverTask extends SolverTask {
        CountingSolverTask(P pos, M move, PuzzleNode<P, M> prev) {
            super(pos, move, prev);
            taskCount.incrementAndGet();
        }

        @Override
        public void run() {
            try {
                super.run();
            } finally {
                if (taskCount.decrementAndGet() == 0)
                    solution.setValue(null);
            }
        }
    }
}
