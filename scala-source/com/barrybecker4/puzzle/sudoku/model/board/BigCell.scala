// Copyright by Barry G. Becker, 2017. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.puzzle.sudoku.model.board

import scala.collection.immutable.HashSet


/**
  * A block of n*n cells in a sudoku puzzle.
  *
  * @author Barry Becker
  */
class BigCell(val board: Board, val rowOffset: Int, val colOffset: Int) extends CellSet {


  /** The number of Cells in the BigCell is n * n.  */
  private val n: Int = board.baseSize

  /** The internal data structures representing the big cell. Row, column order. */
  private val cells: Array[Array[Cell]] = Array.ofDim[Cell](n, n)

  for (i <- 0 until n; j <- 0 until n) {
    cells(i)(j) = board.getCell(rowOffset + i, colOffset + j)
    cells(i)(j).setParent(this)
  }

  def numCells: Int = n * n

  /** @return retrieve the base size of the board - sqrt(edge magnitude). */
  final def getSize: Int = n


  /**
    * If this bigCell has a row (0, n_-1) that has the only cells with candidates for value,
    * then return that row, else return -1.
    *
    * @param value value to look for in candidates
    * @return row (0 to n-1) if found, else -1
    */
  def findUniqueRowFor(value: Int): Int = {
    var rows = new HashSet[Integer]
    def rowContainsValue(i: Int): Boolean = {
      for (j <- 0 until n) {  // loop over columns
        val cands = getCell(i, j).getCandidates
        if (cands.contains(value)) return true
      }
      false
    }

    for (i <- 0 until n)  // loop over rows
      if (rowContainsValue(i)) rows += i
    if (rows.size == 1) rows.head else -1
  }

  /**
    * If this bigCell has a column (0, n_-1) that has the only cells with candidates for value,
    * then return that column, else return -1.
    *
    * @param value value to look for in candidates
    * @return column (0 to n-1) if found, else -1
    */
  def findUniqueColFor(value: Int): Int = {
    var cols = new HashSet[Integer]

    def colContainsValue(j: Int): Boolean = {
      for (i <- 0 until n) {
        val cands = getCell(i, j).getCandidates
        if (cands.contains(value)) return true
      }
      false
    }

    for (j <- 0 until n)  // loop over cols
      if (colContainsValue(j)) cols += j
    if (cols.size == 1) cols.head else -1
  }

  def getCell(position: Int): Cell = getCell(position / n, position % n)

  /**
    * returns null if there is no game piece at the position specified.
    *
    * @return the piece at the specified location. Returns null if there is no piece there.
    */
  final def getCell(row: Int, col: Int): Cell = {
    assert(row >= 0 && row < n && col >= 0 && col < n)
    cells(row)(col)
  }

  override def toString: String = cells.map(_.mkString(", ")).mkString("\n")
}
