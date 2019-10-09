name := "Exercise6"

version := "1.0"
//crucial to have the scalaVersion to 2.11.12 since that's what Spark 2.4.4 uses
scalaVersion := "2.11.12"

mainClass in (Compile, run) := Some("dk.itu.BIDMT.SparkDemo.Demo")

initialCommands in console :=
  """
  import org.apache.spark.sql.SparkSession
  import org.apache.spark.sql.functions._
  org.apache.log4j.Logger getLogger "org" setLevel (org.apache.log4j.Level.OFF)
  org.apache.log4j.Logger getLogger "akka" setLevel (org.apache.log4j.Level.OFF)
  val spark = SparkSession.builder().master("local").appName("spark-shell").getOrCreate()
  spark.sparkContext.setLogLevel("OFF")
  import spark.implicits._
  val sc = spark.sparkContext
  """

cleanupCommands in console := "spark.stop()"

scalacOptions ++= Seq("-unchecked", "-deprecation")

libraryDependencies += "org.apache.spark" %% "spark-core" % "2.4.4" % "provided"
libraryDependencies += "org.apache.spark" %% "spark-sql" % "2.4.4"  % "provided"
libraryDependencies += "org.apache.spark" %% "spark-yarn" % "2.4.4" % "provided"

assemblyMergeStrategy in assembly := {
  case m if m.toLowerCase.endsWith("manifest.mf")          => MergeStrategy.discard
  case m if m.toLowerCase.matches("meta-inf.*\\.sf$")      => MergeStrategy.discard
  case "log4j.properties"                                  => MergeStrategy.discard
  case m if m.toLowerCase.startsWith("meta-inf/services/") => MergeStrategy.filterDistinctLines
  case "reference.conf"                                    => MergeStrategy.concat
  case _                                                   => MergeStrategy.first
}

test in assembly := {}