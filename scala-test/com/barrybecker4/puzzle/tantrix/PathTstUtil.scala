// Copyright by Barry G. Becker, 2017. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.puzzle.tantrix

import com.barrybecker4.common.geometry.ByteLocation
import com.barrybecker4.puzzle.tantrix.model.HexTiles.TILES
import com.barrybecker4.puzzle.tantrix.model.RotationEnum._
import com.barrybecker4.puzzle.tantrix.model._
import com.barrybecker4.puzzle.tantrix.solver.path.TantrixPath

/**
  * @author Barry Becker
  */
object PathTstUtil {
  val LOWER_LEFT = new ByteLocation(22, 20)
  val LOWER_RIGHT = new ByteLocation(22, 21)
  val UPPER = new ByteLocation(21, 21)
  val UPPER_LEFT = new ByteLocation(21, 20)
  val TILE1: HexTile = TILES.getTile(1)
  val TILE2: HexTile = TILES.getTile(2)
  val TILE3: HexTile = TILES.getTile(3)
  val TILE4: HexTile = TILES.getTile(4)
  
  val LOOP_PATH3: TantrixPath = createPath(
    TilePlacement(TILE2, LOWER_RIGHT, ANGLE_60),
    TilePlacement(TILE1, UPPER, ANGLE_0),
    TilePlacement(TILE3, LOWER_LEFT, ANGLE_120)
  )
  /** left end of the yellow path is blocked by tile 2 */
  val NON_LOOP_PATH3: TantrixPath = createPath(
    TilePlacement(TILE2, LOWER_RIGHT, ANGLE_0),
    TilePlacement(TILE1, UPPER, ANGLE_0),
    TilePlacement(TILE3, LOWER_LEFT, ANGLE_120)
  )

  val LOOP_PATH4 = new TantrixPath(
    Seq(TilePlacement(TILE1, LOWER_LEFT, ANGLE_0),
      TilePlacement(TILE3, LOWER_RIGHT, ANGLE_0),
      TilePlacement(TILE4, UPPER, ANGLE_60),
      TilePlacement(TILE2, UPPER_LEFT, ANGLE_60)
    ), PathColor.RED)

  val NON_LOOP_PATH4 = new TantrixPath(Seq(
    TilePlacement(TILE1, LOWER_LEFT, ANGLE_120),
    TilePlacement(TILE2, UPPER_LEFT, ANGLE_60),
    TilePlacement(TILE4, UPPER, ANGLE_60),
    TilePlacement(TILE3, LOWER_RIGHT, ANGLE_300)
  ), PathColor.RED)

  def createPathList: List[TantrixPath] = {
    // for each of the 7 permuted paths, we expect that tile 2 will be the middle/pivot tile.
    val lowerLeft = new ByteLocation(22, 20)
    val lowerRight = new ByteLocation(22, 21)
    val pivot = TilePlacement(TILES.getTile(1), new ByteLocation(21, 21), ANGLE_0)
    val tile2 = TILES.getTile(2)
    val tile3 = TILES.getTile(3)
    List(createPath(TilePlacement(tile2, lowerLeft, ANGLE_0), pivot, TilePlacement(tile3, lowerRight, ANGLE_240)), createPath(TilePlacement(tile2, lowerLeft, ANGLE_300), pivot, TilePlacement(tile3, lowerRight, ANGLE_180)), createPath(TilePlacement(tile2, lowerLeft, ANGLE_300), pivot, TilePlacement(tile3, lowerRight, ANGLE_240)), // complete loop!
      createPath(TilePlacement(tile3, lowerLeft, ANGLE_120), pivot, TilePlacement(tile2, lowerRight, ANGLE_60)), createPath(TilePlacement(tile3, lowerLeft, ANGLE_120), pivot, TilePlacement(tile2, lowerRight, ANGLE_0)), createPath(TilePlacement(tile3, lowerLeft, ANGLE_180), pivot, TilePlacement(tile2, lowerRight, ANGLE_60)), // complete loop!
      createPath(TilePlacement(tile3, lowerLeft, ANGLE_180), pivot, TilePlacement(tile2, lowerRight, ANGLE_0)))
  }

  def createPath(placements: TilePlacement*) = new TantrixPath(placements, PathColor.YELLOW)
}
