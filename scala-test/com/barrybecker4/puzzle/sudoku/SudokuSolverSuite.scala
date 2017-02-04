// Copyright by Barry G. Becker, 2017. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.puzzle.sudoku

import com.barrybecker4.puzzle.sudoku.data.TestData._
import com.barrybecker4.puzzle.sudoku.model.board.Board
import org.junit.Assert.{assertFalse, assertTrue}
import org.scalatest.{BeforeAndAfter, FunSuite}

import scala.util.Random

/**
  * @author Barry Becker
  */
class SudokuSolverSuite extends FunSuite with BeforeAndAfter {
  
  /** instance under test. */
  private var solver: SudokuSolver = _
  private var generator: SudokuGenerator = _
  private var rand: Random = _

  /**
    * common initialization for all go test cases.
    */
  before {
    rand = new Random()
    rand.setSeed(1)
  }

  test("CaseSimpleSample") {
    solver = new SudokuSolver()
    val solved = solver.solvePuzzle(new Board(SIMPLE_9))
    assertTrue("Did not solve SIMPLE_9 successfully", solved)
  }

  /** negative test case */
  test("ImpossiblePuzzle") {
    solver = new SudokuSolver()
    val solved = solver.solvePuzzle(new Board(INCONSISTENT_9))
    assertFalse("Solved impossible SIMPLE_9 puzzle. Should not have.", solved)
  }

  test("Solving16x16Puzzle") {
    solver = new SudokuSolver
    val solved = solver.solvePuzzle(new Board(COMPLEX_16))
    assertTrue("Unexpectedly could not solve 16x16 puzzle.", solved)
  }

  test("GenerateAndSolve2") {
    generateAndSolve(2, rand)
  }

  test("GenerateAndSolve3") {
    generateAndSolve(3, rand)
  }

  test("GenerateLotsAndSolveMany") {
    for (r <- 0 until 40)  {
      rand = new Random()
      rand.setSeed(r)
      generateAndSolve(3, rand)
    }

  }

  /** The large tests takes a long time because of the exponential growth with the size of the puzzle. */
  test("GenerateAndSolve") {
    // super exponential run time
    generateAndSolve(2, rand) // 16  cells       32 ms
    generateAndSolve(3, rand) // 81  cells      265 ms
    // generateAndSolve(4);  // 256 cells    2,077 ms
    // generateAndSolve(5);  // 625 cells  687,600 ms    too slow
  }

  private def generateAndSolve(baseSize: Int, rand: Random) {
    val board = generatePuzzle(baseSize, rand)
    solve(board, rand)
  }

  private def generatePuzzle(baseSize: Int, rand: Random) = {
    generator = new SudokuGenerator(baseSize, null, rand)
    val start = System.currentTimeMillis
    val b = generator.generatePuzzleBoard
    System.out.println("Time to generate size=" + baseSize + " was " + (System.currentTimeMillis - start))
    b
  }

  private def solve(board: Board, rand: Random) {
    val solver = new SudokuSolver()
    val start = System.currentTimeMillis
    val solved = solver.solvePuzzle(board)
    System.out.println("Time to solve was " + (System.currentTimeMillis - start))
    assertTrue("Unexpectedly not solved.", solved)
  }
}

