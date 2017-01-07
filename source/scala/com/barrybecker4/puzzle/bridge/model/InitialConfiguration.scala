// Copyright by Barry G. Becker, 2017. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.puzzle.bridge.model


sealed trait InitialConfiguration {
  def label: String
  def peopleSpeeds: Array[Int]

  def getLabel: String = label + ": " + peopleSpeeds.mkString(", ")
  //def getPeopleSpeeds: Array[Int] = peopleSpeeds
}

case object STANDARD_PROBLEM extends InitialConfiguration {
  val label = "Standard Problem"
  val peopleSpeeds = Array(1, 2, 5, 8)  // shortest = 15
}

case object ALTERNATIVE_PROBLEM extends InitialConfiguration {
  val label = "Alternative Problem"
  val peopleSpeeds = Array(5, 10, 20, 25)  // shortest = 60
}

case object DIFFICULT_PROBLEM extends InitialConfiguration {
  val label = "Hard Problem"
  val peopleSpeeds = Array(1, 2, 5, 7, 8, 12, 15) // shortest = 47
}

case object HARDER_PROBLEM extends InitialConfiguration {
  val label = "Harder Problem"
  val peopleSpeeds = Array(1, 2, 5, 8)   // shortest = 79
}

case object TRIVIAL_PROBLEM extends InitialConfiguration {
  val label = "Trivial Problem"
  val peopleSpeeds = Array(1, 2, 5, 8)  // shortest = 8
}
