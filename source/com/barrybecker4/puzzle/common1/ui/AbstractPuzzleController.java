// Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.puzzle.common1.ui;

import com.barrybecker4.common.concurrency.Worker;
import com.barrybecker4.common.search.Refreshable;
import com.barrybecker4.puzzle.common1.AlgorithmEnum;
import com.barrybecker4.puzzle.common1.PuzzleController;
import com.barrybecker4.puzzle.common1.solver.PuzzleSolver;
import scala.Option;
import scala.collection.Seq;
import scala.collection.mutable.Set;

import java.util.List;

/**
 * Provides default implementation for a PuzzleController.
 * The puzzle controller is what will solve a puzzle using some provided strategy.
 * It updates the ui (refreshable) and determines what algorithm is used to solve it.
 * If a non-null Refreshable is pass into the constructor that that will be delegated
 * to when the controller is asked to do a refresh.
 *
 * @author Barry Becker
 */
public abstract class AbstractPuzzleController<P, M> implements PuzzleController<P, M> {

    /** the viewer that can show the current state. */
    protected final Refreshable<P, M> ui_;

    /** default solver. */
    protected AlgorithmEnum<P, M> algorithm_;


    /**
     * Creates a new instance of AbstractPuzzleController
     */
    public AbstractPuzzleController(Refreshable<P, M> ui) {
        ui_ = ui;
    }

    /**
     * There are different approaches we can take to solving the puzzle.
     *
     * @param algorithm strategy to use for solving the puzzle.
     */
    public void setAlgorithm(AlgorithmEnum<P, M> algorithm) {
        algorithm_ = algorithm;
    }

    /**
     * get the solver algorithm..
     */
    public AlgorithmEnum getAlgorithm() {
        return algorithm_;
    }

    /**
     * If this puzzle position was never seen before add it.
     * Must be synchronized because some solvers use concurrency.
     * @return true if this position was already seen while searching.
     */
    public synchronized boolean alreadySeen(P position, Set<P> seen) {

        if (!seen.contains(position)) {
            seen.add(position);
            return false;
        }
        return true;
    }

    /**
     * Override this to help some search algorithms prioritize the order in which they search.
     * By default this is provides no information.
     * It can only be used for puzzles that have a path from an initial state to a solution.
     * @return estimate of the cost to reach the goal from the specified position
     */
    public int distanceFromGoal(P position) {
        return 1;
    }

    /**
     * Override this when moves have varying cost for a move (rare - see bridge problem for example).
     * @param move the move to determine cost of
     * @return cost of a single move
     */
    public int getCost(M move) {
        return 1;
    }

    /** Called when the puzzle solver wants to show progress to the user somehow */
    public void refresh(P pos, long numTries) {
        if (ui_ != null) {
            ui_.refresh(pos, numTries);
        }
    }

    /** Once the puzzle search is done, this is called to show the solution (or lack thereof). */
    public void finalRefresh(Option<Seq<M>> path, Option<P> position, long numTries, long elapsedMillis) {

        if (path.isEmpty()) {
            System.out.println("No Solution found!");
        }
        else {
            System.out.println("The number of steps in path to solution = " + path.get().size());

            if (ui_ != null) {
                List<M> pathList = scala.collection.JavaConversions.seqAsJavaList(path.get());
                ui_.finalRefresh(pathList, position.get(), numTries, elapsedMillis);
            }
        }
    }

    /**
     * Begin the process of solving.
     * Do it in a separate worker thread so the UI is not blocked.
     */
    public void startSolving() {

        // Use either concurrent or sequential solver strategy
        final PuzzleSolver<M> solver = algorithm_.createSolver(this);

        Worker worker = new Worker()  {

            @Override
            public Object construct()  {

                try {
                    // this does all the heavy work of solving it.
                    solver.solve();
                } catch (InterruptedException e) {
                    assert false: "Thread interrupted. " + e.getMessage();
                }
                return true;
            }
        };

        worker.start();
    }
}