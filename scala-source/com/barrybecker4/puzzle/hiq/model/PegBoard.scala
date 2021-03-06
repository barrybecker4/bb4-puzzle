// Copyright by Barry G. Becker, 2000-2018. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.puzzle.hiq.model

import com.barrybecker4.common.geometry.{ByteLocation, Location}
import PegBoard._
import scala.collection.mutable


object PegBoard {

  /** this must be odd */
  val SIZE = 7
  private[model] val NUM_PEG_HOLES = 33
  private val CENTER: Byte = 3
  private val CORNER_SIZE: Int = 2

  /** The initial board position constant */
  val INITIAL_BOARD_POSITION: PegBoard = createInitialBoard()

  private def createInitialBoard(): PegBoard ={
    var b = new PegBoard()
    for (i <- 0 until SIZE; j <- 0 until SIZE if isValidPosition(i, j))
      b = b.setPosition(i.toByte, j.toByte, value = true)
    b.setPosition(CENTER, CENTER, value = false)
  }

  /** @return true if the coordinates refer to one of the 33 board positions that can hold a peg. */
  def isValidPosition(row: Int, col: Int): Boolean = {
    if (row < 0 || row >= SIZE || col < 0 || col >= SIZE) return false
    (row >= CORNER_SIZE && row < SIZE - CORNER_SIZE) || (col >= CORNER_SIZE && col < SIZE - CORNER_SIZE)
  }

  private def createNewBoardState(pos: PegBoard, move: PegMove, undo: Boolean): PegBoard = {
    val fromRow = move.getFromRow
    val fromCol = move.getFromCol
    val toRow = move.getToRow
    val toCol = move.getToCol
    var board = pos.setPosition(fromRow, fromCol, undo)
    // Remove or replace the piece that was jumped as appropriate
    board = board.setPosition(((fromRow + toRow) >> 1).toByte, ((fromCol + toCol) >> 1).toByte, undo)
    board.setPosition(toRow, toCol, !undo)
  }
}

/**
  * Immutable representation of a PegBoard.
  * Maintains the compressed peg position information for the board.
  * @author Barry Becker
  */
case class PegBoard(bits: PegBits) {
  def this() = { this(PegBits()) }
  def getPosition(row: Byte, col: Byte): Boolean = get(bits.getIndexForPosition(row, col))

  /** Private so others can not modify our immutable state after construction. */
  private def setPosition(row: Byte, col: Byte, value: Boolean): PegBoard =
    PegBoard(set(bits.getIndexForPosition(row, col), value))

  def isEmpty(row: Byte, col: Byte): Boolean = !getPosition(row, col)

  /** Because of symmetry, there is really only one first move not 4.
    * @return Move the first move.
    */
  def getFirstMove = new PegMove(CENTER, (CENTER - 2).toByte, CENTER, CENTER)

  def isSolved: Boolean = getNumPegsLeft == 1 && getPosition(CENTER, CENTER)

  /** Creates a new board with the move applied */
  def doMove(move: PegMove, undo: Boolean = false): PegBoard = createNewBoardState(this, move, undo)

  /** @param pegged boolean if true, get pegged locations, else empty locations
    * @return List of pegged or empty locations
    */
  def getLocations(pegged: Boolean): List[Location] = {
    var list = List[Location]()
    for {
      i <- 0 until SIZE
      j <- 0 until SIZE
      if isValidPosition(i, j) && getPosition(i.toByte, j.toByte) == pegged
    } list +:= new ByteLocation(i, j)
    list
  }

  /** @return number of pegs left on the board. */
  def getNumPegsLeft: Int = bits.getNumPegsLeft

  def containedIn(setOfBoards: mutable.Set[PegBoard]): Boolean = {
    var visited = false
    var i = 0
    while (!visited && i < PegBoardSymmetries.SYMMETRIES) {
        if (setOfBoards.contains(symmetry(i))) visited = true
        i += 1
    }
    visited
  }

  /** Check all 8 symmetries
    * if rotateIndex = 0 then no rotation
    * if rotateIndex = 1 mirror image of this,
    * if rotateIndex = 2 then 90 degree rotation of this,
    * if rotateIndex = 3 then mirror image of 2, etc
    * @return specified rotation of the board.
    */
  private def symmetry(symmIndex: Int) =
    if (symmIndex == 0) this else rotate(PegBoardSymmetries.getSymmetry(symmIndex))

  override def equals(b: Any): Boolean = bits equals b.asInstanceOf[PegBoard].bits

  /** All but one bit accounted for in the hash. */
  override def hashCode: Int = bits.hashCode

  /** Rotate the board according to symmetry.
    * Not all are rotational symmetries, but you get the idea....
    * @return new board with specified rotation applied.
    */
  private def rotate(rotateIndices: Array[Byte]): PegBoard = {
    var rotatedBoard = new PegBoard()
    for (i <- 0 until NUM_PEG_HOLES)
        rotatedBoard = PegBoard(rotatedBoard.set(i, get(rotateIndices(i))))
    rotatedBoard
  }

  private def set(i: Int, value: Boolean): PegBits = bits.set(i, value)
  private def get(i: Int): Boolean = bits.get(i)

  override def toString: String = bits.toString
}