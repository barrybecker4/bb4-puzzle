// Copyright by Barry G. Becker, 2021 Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.puzzle.rubixcube.ui

import com.barrybecker4.puzzle.common.ui.{DoneListener, PathNavigator, PuzzleViewer}
import com.barrybecker4.puzzle.rubixcube.model.{Cube, CubeMove}
import com.barrybecker4.puzzle.rubixcube.ui.util.CubeMoveTransition
import com.jme3.app.LegacyApplication
import com.jme3.system.{AppSettings, JmeCanvasContext}

import java.awt.{BorderLayout, Canvas, Graphics}



/**
  * UI for drawing the current best solution to the puzzle.
  * @param doneListener called when the puzzle has been solved.
  */
final class CubeViewer(var doneListener: DoneListener)
      extends PuzzleViewer[Cube, CubeMove] with PathNavigator {

  private var path: List[CubeMove] = _
  private var transition: Option[CubeMoveTransition] = None

  def getPath: List[CubeMove] = path

  private var app: LegacyApplication = _
  private var context: JmeCanvasContext = _
  private val appClass = "com.barrybecker4.puzzle.rubixcube.ui.CubeSceneRenderer"
  private val canvas = createCanvas(appClass)
  this.add(canvas, BorderLayout.CENTER)


  def createCanvas(appClass: String): Canvas = {
    val settings = new AppSettings(true)

    val clazz = Class.forName(appClass)
    app = clazz.getDeclaredConstructor().newInstance().asInstanceOf[LegacyApplication]

    app.setPauseOnLostFocus(false)
    app.setSettings(settings)
    app.createCanvas()
    app.startCanvas()
    context = app.getContext.asInstanceOf[JmeCanvasContext]
    val canvas = context.getCanvas
    canvas
  }

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
    animateMove(move, undo)
    board = board.doMove(move)
    repaint()
  }

  def animateMove(move: CubeMove, undo: Boolean): Unit = {
  }

  /** This renders the current state of the puzzle to the screen. */
  override protected def paintComponent(g: Graphics): Unit = {

    if (board != null)
      canvas.setSize(getWidth, getHeight)
    /*
    super.paintComponent(g)
    if (board != null) renderer.render(g, board, getWidth, getHeight, transition)
     */
  }

  private def showPath(thePath: List[CubeMove], theBoard: Cube): Unit = {
    path = thePath
    board = theBoard
    if (doneListener != null) doneListener.done()
  }
}
