name := "Exercise2"

initialCommands in console :=
  """
  import BIDMTMath._

  """

version := "1.0"

scalaVersion := "2.13.0"

scalacOptions ++= Seq("-unchecked", "-deprecation")

libraryDependencies += "org.scalactic" %% "scalactic" % "3.0.8"

mainClass in (Compile, run) := Some("BIDMTMath.SimpleMath")
