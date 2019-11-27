name := "Exercise12"

version := "1.0"

scalaVersion := "2.11.12"

mainClass in (Compile, run) := Some("dk.itu.BIDMT.Exercises.Exercise12.MeteoriteCluster")


cleanupCommands in console := "spark.stop()"

scalacOptions ++= Seq("-unchecked", "-deprecation")

libraryDependencies += "org.apache.spark" %% "spark-core" % "2.4.4" //% "provided"
libraryDependencies += "org.apache.spark" %% "spark-sql" % "2.4.4"  //% "provided"
libraryDependencies += "org.apache.spark" %% "spark-yarn" % "2.4.4" //% "provided"
libraryDependencies += "org.apache.spark" %% "spark-mllib" % "2.4.4" //% "runtime"

libraryDependencies += "com.typesafe" % "config" % "1.3.0"
logBuffered in Test := false

assemblyMergeStrategy in assembly := {
  case m if m.toLowerCase.endsWith("manifest.mf")          => MergeStrategy.discard
  case m if m.toLowerCase.matches("meta-inf.*\\.sf$")      => MergeStrategy.discard
  case "log4j.properties"                                  => MergeStrategy.discard
  case m if m.toLowerCase.startsWith("meta-inf/services/") => MergeStrategy.filterDistinctLines
  case "reference.conf"                                    => MergeStrategy.concat
  case _                                                   => MergeStrategy.first
}

test in assembly := {}