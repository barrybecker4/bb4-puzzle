// Copyright by Barry G. Becker, 2017. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.puzzle.tantrix.model

import com.barrybecker4.common.geometry.{ByteLocation, Location}
import com.barrybecker4.puzzle.tantrix.TantrixTstUtil.{place3SolvedTiles, place3UnsolvedTiles}
import org.junit.Assert.{assertEquals, assertNotNull}
import org.scalatest.FunSuite

/**
  * @author Barry Becker
  */
class TantrixSuite extends FunSuite {

  var tantrix: Tantrix = _

  test("test3TilePlacement") {
    tantrix = place3SolvedTiles.tantrix
    //System.out.println(tantrix)
    verifyPlacement(new ByteLocation(22, 21))
    verifyPlacement(new ByteLocation(22, 20))
    verifyPlacement(new ByteLocation(21, 21))
  }

  test("GetNeighborLocationOnOddRow") {
    tantrix = place3UnsolvedTiles.tantrix
    val loc = new ByteLocation(1, 1)
    assertEquals("Unexpected right neighbor", new ByteLocation(1, 2), HexUtil.getNeighborLocation(loc, 0))
    assertEquals("Unexpected bottom left neighbor", new ByteLocation(2, 0), HexUtil.getNeighborLocation(loc, 4))
    assertEquals("Unexpected bottom right neighbor", new ByteLocation(2, 1), HexUtil.getNeighborLocation(loc, 5))

  }

  test("GetNeighborLocationOnEvenRow") {
    tantrix = place3UnsolvedTiles.tantrix
    val loc = new ByteLocation(2, 2)
    assertEquals("Unexpected right neighbor", new ByteLocation(2, 3), HexUtil.getNeighborLocation(loc, 0))
    assertEquals("Unexpected bottom left neighbor", new ByteLocation(3, 2), HexUtil.getNeighborLocation(loc, 4))
    assertEquals("Unexpected bottom right neighbor", new ByteLocation(3, 3), HexUtil.getNeighborLocation(loc, 5))
  }

  test("GetNeighborFromUnrotatedTile") {
    tantrix = place3SolvedTiles.tantrix
    //assertEquals("Unexpected right neighbor", null, tantrix.getNeighbor(tantrix(2, 2), 0))
    val bottomLeft = Some(tantrix(22, 20))
    assertEquals("Unexpected bottom left neighbor", bottomLeft, tantrix.getNeighbor(tantrix(21, 21), 4))
    val bottomRight = Some(tantrix(22, 21))
    assertEquals("Unexpected bottom right neighbor", bottomRight, tantrix.getNeighbor(tantrix(21, 21), 5))
  }

  test("GetNeighborFromRotatedTile") {
    tantrix = place3SolvedTiles.tantrix
    //assertEquals("Unexpected right neighbor", null, tantrix.getNeighbor(tantrix(3, 2), 0.toByte))
    val topLeft = Some(tantrix(21, 21))
    assertEquals("Unexpected top left neighbor", topLeft, tantrix.getNeighbor(tantrix(22, 21), 2))
    val left = Some(tantrix(22, 20))
    assertEquals("Unexpected left neighbor", left, tantrix.getNeighbor(tantrix(22, 21), 3))
  }


  private def verifyPlacement(loc: Location) {
    val placement = tantrix(loc)
    assertNotNull("Placement at " + loc + " was unexpectedly null", placement)
    assertEquals("Unexpected tiles at " + loc, loc, placement.location)
  }
}