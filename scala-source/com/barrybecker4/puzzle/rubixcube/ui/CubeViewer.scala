// Copyright by Barry G. Becker, 20121 Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.puzzle.rubixcube.ui

import com.barrybecker4.puzzle.common.ui.{DoneListener, PathNavigator, PuzzleViewer}
import com.barrybecker4.puzzle.rubixcube.model.{Cube, CubeMove}
import CubeViewer.ANIMATION_STEPS

import java.awt.event.ActionEvent
import java.awt.event.ActionListener

import java.awt.Graphics
import javax.swing.Timer

object CubeViewer {
  val ANIMATION_STEPS = 10
}

/**
  * UI for drawing the current best solution to the puzzle.
  * @param doneListener called when the puzzle has been solved.
  */
final class CubeViewer(var doneListener: DoneListener)
      extends PuzzleViewer[Cube, CubeMove] with PathNavigator {

  private val renderer: CubeRenderer = new CubeRenderer
  private var path: List[CubeMove] = _
  private var transition: Option[CubeMoveTransition] = None

  def getPath: List[CubeMove] = path

  override def refresh(theCube: Cube, numTries: Long): Unit = {
    board = theCube
    if (numTries % 500 == 0) {
      makeSound()
      status = createStatusMessage(numTries)
      simpleRefresh(board, numTries)
    }
  }

  override def finalRefresh(path: Option[Seq[CubeMove]], board: Option[Cube],
                            numTries: Long, millis: Long): Unit = {
    super.finalRefresh(path, board, numTries, millis)
    if (board.isDefined) showPath(path.get.toList, board.get)
  }

  def makeMove(currentStep: Int, undo: Boolean): Unit = {
    val move: CubeMove = getPath(currentStep)
    //animateMove(move, undo)
    board = board.doMove(move)
    repaint()
  }

  def animateMove(move: CubeMove, undo: Boolean): Unit = {

    val timerDelay = 100

    new Timer(timerDelay, new ActionListener() {
      private var step = 0

      def actionPerformed(e: ActionEvent): Unit = {
        step += 1
        val inc = if (undo) ANIMATION_STEPS - step else step
        transition = Some(CubeMoveTransition(move, (100.0 * inc) / ANIMATION_STEPS))
        println("calling repaint")
        repaint()
        if (step == ANIMATION_STEPS) e.getSource.asInstanceOf[Timer].stop()
      }
    }).start()

    transition = None
  }

  /** This renders the current state of the puzzle to the screen. */
  override protected def paintComponent(g: Graphics): Unit = {
    super.paintComponent(g)
    if (board != null) renderer.render(g, board, getWidth, getHeight, transition)
  }

  private def showPath(thePath: List[CubeMove], theBoard: Cube): Unit = {
    path = thePath
    board = theBoard
    if (doneListener != null) doneListener.done()
  }
}
