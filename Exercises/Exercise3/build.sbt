name := "Exercise3"

initialCommands in console :=
  """
  import dk.itu.BIDMT.binarytree._
  
  """

version := "1.0"

scalaVersion := "2.13.0"

scalacOptions ++= Seq("-unchecked", "-deprecation")

libraryDependencies += "org.scalactic" %% "scalactic" % "3.0.8"

mainClass in (Compile, run) := Some("dk.itu.BIDMT.binarytree")