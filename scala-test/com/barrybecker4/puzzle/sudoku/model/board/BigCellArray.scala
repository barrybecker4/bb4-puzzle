// Copyright by Barry G. Becker, 2017. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.puzzle.sudoku.model.board

/**
  * An array of sets of integers representing the candidates for the cells in a row or column.
  *
  * @author Barry Becker
  */
class BigCellArray(val board: Board) {

  val size: Int = board.getBaseSize
  /** n by n grid of big cells.   */
  private val bigCells = Array.ofDim[BigCell](size, size)

  for (i <- 0 until size) {
    for (j <- 0 until size) {
      bigCells(i)(j) = new BigCell(board, size * i, size * j)
    }
  }

  def getBigCell(i: Int, j: Int): BigCell = {
    assert(i >= 0 && i < size && j >= 0 && j < size)
    bigCells(i)(j)
  }

  //def getSize: Int = size

  def update(values: ValuesList) {
    for (i <- 0 until size) {
      for (j <- 0 until size) {
        getBigCell(i, j).updateCandidates(values)
      }
    }
  }

  override def toString: String = {
    val bldr = new StringBuilder
    for (row <- 0 until size) {
      for (col <- 0 until size) {
        bldr.append("cands(").append(row).append(", ").append(col).append(")=")
          .append(getBigCell(row, col).getCandidates).append("\n")
      }
    }
    bldr.toString
  }
}
