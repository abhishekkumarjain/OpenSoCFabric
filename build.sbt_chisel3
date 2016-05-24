lazy val buildSettings = Seq (
    organization := "gov.lbl.crd.csd.cag",
    version := "1.1",
    name := "OpenSoC",
    scalaVersion := "2.11.7",
    libraryDependencies += "edu.berkeley.cs" %% "firrtl" % "0.1-SNAPSHOT"
)
lazy val root = (project in file(".")).settings(buildSettings: _*).dependsOn(chisel,chiselTesters)
lazy val chisel = project in file("chisel3")
lazy val chiselTesters = project in file("chisel-testers")
