// Copyright by Barry G. Becker, 2017. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.puzzle.bridge.ui

import java.awt.{Color, Font, Graphics}

import com.barrybecker4.puzzle.bridge.model.{Bridge, BridgeMove}
import com.barrybecker4.puzzle.bridge.ui.BridgeRenderer._
import com.barrybecker4.puzzle.common.ui.PuzzleRenderer

/**
  * Singleton class that renders the current state of the Bridge1 puzzle.
  * Having the renderer separate from the viewer helps to separate out the rendering logic
  * from other features of the BridgeViewer1.
  *
  * @author Barry Becker
  */
object BridgeRenderer {
  val INC = 60
  val BRIDGE_WIDTH = 300
  private val MARGIN = 50
  private val TEXT_WIDTH = 250
  private val TEXT_Y = 190
  private val FONT = new Font("Sans Serif", Font.PLAIN, INC / 2)
  private val LIGHT_RADIUS = 30
}

/**
  * private constructor because this class is a singleton.
  * Use getPieceRenderer instead.
  */
class BridgeRenderer extends PuzzleRenderer[Bridge] {

  /**
    * This renders the current state of the Bridge1 to the screen.
    * Show the people that have not yet crossed on the left; those that have on the right.
    */
  def render(g: Graphics, board: Bridge, width: Int, height: Int): Unit = {
    render(g, board, None, width, height)
  }

  /**
    * This renders the current state of the Bridge1 to the screen.
    * Show the people that have not yet crossed on the left; those that have on the right.
    */
  def render(g: Graphics, board: Bridge, lastMove: Option[BridgeMove], width: Int, height: Int): Unit = {
    drawBridge(g)
    drawPeople(g, board.uncrossed, MARGIN)
    drawPeople(g, board.crossed, MARGIN + TEXT_WIDTH + BRIDGE_WIDTH)
    drawLight(g, board.lightCrossed)
    drawPeopleMove(g, lastMove, MARGIN + TEXT_WIDTH + BRIDGE_WIDTH / 2)
  }

  /**
    * Draw the bridge that the people will cross with the light
    */
  private def drawBridge(g: Graphics): Unit = {
    val leftX = MARGIN + TEXT_WIDTH
    val rightX = leftX + BridgeRenderer.BRIDGE_WIDTH
    g.setColor(Color.darkGray)
    for (i <- 0 to 10) {
      val ypos = TEXT_Y + i
      g.drawLine(leftX, ypos, rightX, ypos)
    }
  }

  private def drawPeople(g: Graphics, people: List[Int], xpos: Int): Unit = {
    g.setColor(Color.BLACK)
    g.setFont(FONT)
    val peopleListString = people.toString
    if (people.size <= 5) g.drawString(peopleListString, xpos + 10, TEXT_Y)
    else {
      // split into 2 lines for better readability
      val idx = peopleListString.indexOf(",", 12) + 1
      val part1 = peopleListString.substring(0, idx)
      val part2 = peopleListString.substring(idx)
      g.drawString(part1, xpos + 10, TEXT_Y)
      g.drawString(part2, xpos + 10, TEXT_Y + 40)
    }
  }

  private def drawLight(g: Graphics, isLightCrossed: Boolean): Unit = {
    val leftPos = MARGIN + 20
    val rightPos = MARGIN + TEXT_WIDTH + BRIDGE_WIDTH + 20
    val xpos = if (isLightCrossed) rightPos else leftPos
    g.setColor(Color.YELLOW)
    g.fillOval(xpos, TEXT_Y - 80, LIGHT_RADIUS, LIGHT_RADIUS)
    g.setColor(Color.BLACK)
    g.drawOval(xpos, TEXT_Y - 80, LIGHT_RADIUS, BridgeRenderer.LIGHT_RADIUS)
  }

  private def drawPeopleMove(g: Graphics, lastMove: Option[BridgeMove], xpos: Int): Unit = {
    g.setColor(Color.BLACK)
    g.setFont(FONT)
    if (lastMove.isDefined) {
      val move = lastMove.get
      val prefix = if (move.direction) "" else " <= "
      val suffix = if (move.direction) " => " else ""
      val peopleListString = move.people.mkString(prefix, ", ", suffix)

      g.drawString(peopleListString, xpos - 50, TEXT_Y - 15)
    }
  }
}


