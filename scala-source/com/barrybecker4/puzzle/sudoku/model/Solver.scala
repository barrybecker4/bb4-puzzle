// Copyright by Barry G. Becker, 2017 - 2019. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.puzzle.sudoku.model

/** Solves a Sudoku Board configuration */
private case class Solver(board: Board, refresh: Option[Board => Unit] = None) {

  private var numIterations = 0

  /** @return the solved board, or None, if could not be solved */
  def solve(): Option[Board] = {
    val updatedBoard = board.updateFromInitialData()
    if (updatedBoard.isDefined) {
      return searchForSolution(Some(board))
    }
    None
  }

  def getNumIterations: Int = numIterations

  private def searchForSolution(board: Option[Board]): Option[Board] = {
    board match {
      case None => None
      case Some(b) =>
        if (b.valuesMap.values.forall(_.size == 1)) {
          b.doRefresh(refresh)
          return Some(b)
        }

        // Chose the unfilled square, s, with the fewest possibilities greater than one (helps performance)
        val minSq: Location = (for (s <- b.comps.squares; if b.valuesMap(s).size > 1)
          yield (b.valuesMap(s).size, s)).min._2
        for (d <- b.valuesMap(minSq)) {
          numIterations += 1
          b.doRefresh(refresh)
          val newValuesMap = b.assign(minSq, d, b.valuesMap)
          if (newValuesMap.isDefined)
            return searchForSolution(Some(Board(b.cells, newValuesMap.get)))
        }
        None
    }
  }

}
