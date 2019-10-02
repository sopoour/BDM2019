package dk.itu.BIDMT.SparkDemo

import org.apache.spark.sql.{Dataset, Encoders, SparkSession, DataFrame, Row}
import org.apache.spark.sql.functions._
import org.apache.spark.sql.types._



object Demo {
    
  	org.apache.log4j.Logger getLogger "org" setLevel (org.apache.log4j.Level.OFF)
  	org.apache.log4j.Logger getLogger "akka" setLevel (org.apache.log4j.Level.OFF)

	val spark: SparkSession = SparkSession.builder
        .appName("MyApp")
        .master("local") //Remove this line when running in cluster
        .getOrCreate

	spark.sparkContext.setLogLevel("OFF")
	import spark.implicits._



	def simulatedDataLoader (spark:SparkSession, path:String):Dataset[Row] =  {
		spark
			.read
            .format("csv")
			.option("header","true")
            .option("inferSchema", "true")
            .load(path)
        
	}

	def main(args:Array[String]): Unit = {

        val ds = simulatedDataLoader(spark, "data/medium_dataset.csv")

        ds.show()
        
        val ds2 = ds
            .map{ case Row(id,name1:String,name2:String) => (100, name1 + name2) }

        ds2.show()
        println(ds.count)

        ds2
            .write
            .mode("overwrite")
            .csv("out")
            
		spark.stop
	}
}
