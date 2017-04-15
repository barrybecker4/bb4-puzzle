// Copyright by Barry G. Becker, 2017. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.puzzle.tantrix.solver.path

import com.barrybecker4.common.geometry.ByteLocation
import com.barrybecker4.common.math.MathUtil
import com.barrybecker4.puzzle.tantrix.PathTstUtil._
import com.barrybecker4.puzzle.tantrix.TantrixTstUtil._
import com.barrybecker4.puzzle.tantrix.model.HexTiles._
import com.barrybecker4.puzzle.tantrix.model.RotationEnum._
import com.barrybecker4.puzzle.tantrix.model.{PathColor, TilePlacement}
import org.scalatest.FunSuite

/**
  * @author Barry Becker
  */
class TantrixPathSuite extends FunSuite {
  /** instance under test */
  private var path: TantrixPath = _

  test("2TilePathConstruction") {
    val pivotTile = TILES.getTile(1)
    val first = TilePlacement(TILES.getTile(2), loc(2, 1), ANGLE_0)
    val second = TilePlacement(TILES.getTile(3), loc(1, 2), ANGLE_0)
    val tileList: List[TilePlacement] = List(first, second)
    path = new TantrixPath(tileList, pivotTile.primaryColor)
    assertResult(tileList) { path.tiles }
  }

  /**
    * We should get an error if the tiles are not in path order, even if they
    * do form a path.
    */
  test("5TilePathConstructionWhenPathTilesUnordered") {
    val first = TilePlacement(TILES.getTile(4), new ByteLocation(21, 22), ANGLE_0)
    val second = TilePlacement(TILES.getTile(1), new ByteLocation(22, 21), ANGLE_300)
    val third = TilePlacement(TILES.getTile(2), new ByteLocation(23, 22), ANGLE_180)
    val fourth = TilePlacement(TILES.getTile(3), new ByteLocation(20, 21), ANGLE_120)
    val fifth = TilePlacement(TILES.getTile(5), new ByteLocation(21, 21), ANGLE_240)
    val tileList = List(first, second, third, fourth, fifth)
    val caught = intercept[IllegalStateException] {
      new TantrixPath(tileList, PathColor.RED)
    }
    assert(caught.getMessage.contains("The following 5 tiles must form a primary path"))
  }

  /** we expect an exception because the tiles passed to the constructor do not form a primary path */
  test("NonLoopPathConstruction") {
    val board = place3UnsolvedTiles
    path = new TantrixPath(board.tantrix, board.primaryColor)
    assertResult(3) { path.size }
  }

  /** we expect an exception because the tiles passed to the constructor do not form a primary path */
  test("InvalidPathConstruction") {
    val board = place3NonPathTiles
    val caught = intercept[IllegalStateException] {
      new TantrixPath(board.tantrix, board.primaryColor)
    }
  }

  test("IsLoop") {
    val board = place3SolvedTiles
    val path = new TantrixPath(board.tantrix, board.primaryColor)
    assert(path.isLoop)
  }

  test("IsNotLoop") {
    val board = place3UnsolvedTiles
    val path = new TantrixPath(board.tantrix, board.primaryColor)
    assert(!path.isLoop)
  }

  test("HasOrderedPrimaryPathYellow") {
    val tiles = List(TilePlacement(TILE2, LOWER_RIGHT, ANGLE_60),TilePlacement(TILE1, UPPER, ANGLE_0), TilePlacement(TILE3, LOWER_LEFT, ANGLE_120))
    assert(TantrixPath.hasOrderedPrimaryPath(tiles, PathColor.YELLOW))
  }

  test("HasOrderedPrimaryPathRed") {
    val tiles = List(TilePlacement(TILE2, LOWER_RIGHT, ANGLE_60), TilePlacement(TILE1, UPPER, ANGLE_0), TilePlacement(TILE3, LOWER_LEFT, ANGLE_120))
    assert(!TantrixPath.hasOrderedPrimaryPath(tiles, PathColor.RED))
  }

  test("FindRandomNeighbor") {
    //val rnd = new Random()
    //rnd.setSeed(0)
    MathUtil.RANDOM.setSeed(0)
    val board = place3UnsolvedTiles
    val path = new TantrixPath(board.tantrix, board.primaryColor)
    val nbr = path.getRandomNeighbor(0.5).asInstanceOf[TantrixPath]
    val tiles = List(TilePlacement(TILES.getTile(2), new ByteLocation(22, 20), ANGLE_300),
      TilePlacement(TILES.getTile(1), new ByteLocation(21, 21), ANGLE_0), TilePlacement(TILES.getTile(3),
        new ByteLocation(22, 21), ANGLE_240))
    val expectedPath = new TantrixPath(tiles, PathColor.YELLOW)
    assertResult(expectedPath) { nbr }
  }

}