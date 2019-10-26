package dk.itu.BIDMT.F19.P2.Part2

import com.typesafe.config.ConfigFactory
import org.apache.spark.sql.functions.{avg, count, desc, sum}
import org.apache.spark.sql.{DataFrame, SparkSession}


object YelpAnalysis {

  val spark = SparkSession
    .builder()
    .appName("YelpDataAnalysis")
    .master("local[4]") //comment before you create the jar file to be run on the cluster
    .getOrCreate()

  spark.sparkContext.setLogLevel("WARN")

  //load the data
  def dataLoader(path: String):DataFrame ={
    spark.read.json(path)
  }

  //Q1:
  /**
    *  use SQL statements: add all the number of reviews for all businesses
    *
    * @return a dataframe with one value representing the total number of reviews for all businesses
    */
    //SOPHIA: Net sicher ob das ist was wir machen sollen in diesem Query. Siehe Out folder und yelpAnalysis_Q1_SQL
  def totalReviewsSQL():DataFrame = {
    spark.sql("""
      select SUM(`review_count`)
      from yelpBusinessesView
      """
  )
  }

  /**
    * use DataFrame transformations: add all the number of reviews for all businesses
    * @param yelpBusinesses
    * @return a dataframe with one value representing the total number of reviews for all businesses
    */
  def totalReviewsbDF(yelpBusinesses : DataFrame):DataFrame = {
    yelpBusinesses.agg(sum("review_count"))
  }


   //Q2:
   /**
     * use SQL statements: find all businesses in yelpBusinesses
     * that have received 5 stars and that have been reviewed by 1000 or more users
     *
     * @return a Dataframe of (name, stars, review_count) of five star most reviewed businesses
     */
     //SOPHIA: In der Beschreibung stet dass es mind. 1000 review_count sein soll was aber keinen Sinn macht weil
     //es keine gibt mit 1000. Habe bereits ins Forum in LearnIT geschriebn und diesbzgl gefragt. Habe es jetzt mal mit 10 ausprobiert
   def fiveStarBusinessesSQL():DataFrame = {
     spark.sql("""
         select `name`, `stars`, `review_count`
         from yelpBusinessesView
         where stars = 5.0
         and review_count >=1000
         """)
   }


   /**
     * use DataFrame transformations: find all businesses in yelpBusinesses
     * that have received 5 stars and that have been reviewed by 1000 or more users
     * @param yelpBusinesses
     * @return a Dataframe of (name, stars, review_count) of five star most reviewed businesses
     */
   def fiveStarBusinessesDF(yelpBusinesses: DataFrame):DataFrame = {
     yelpBusinesses
       .select("name", "stars", "review_count")
       .filter("stars = 5.0" )
       .filter("review_count >= 1000")
   }

   //Q3:
   /**
     * use SQL statements: find the influencer users in yelpUsers who have written more than 1000 reviews
     *
     * @return DataFrame of user_id of influencer users
     */
   def findInfluencerUserSQL():DataFrame = {
     spark.sql("""
         select `user_id`
         from yelpUsersView
         where review_count >= 1000
         """)
   }

    /**
      * use DataFrame transformations: find the influencer users in yelpUsers who have written more than 1000 reviews
      * @param yelpUsers
      * @return DataFrame of user_id of influencer users
      */
    def findInfluencerUserDF(yelpUsers : DataFrame):DataFrame = {
      yelpUsers
        .select("user_id")
        .filter("review_count >= 1000")
    }

    //Q4:
    /**
      * use SQL statements: find the businesses in yelpBusinesses that have appeared in reviews in yelpReviews by more than 5 influencer users
      * sort the result in a descending order according to the count of reviews
      *
      * @return DataFrame of names of businesses that match the criteria
      */
      //SOPHIA: Mein Ansatz der aber bis jetzt nicht funktioniert, weiss net was falsch ist!
    def findFamousBusinessesSQL() : DataFrame = {
      spark.sql("""
          select `name`
          from yelpBusinessView
          where SUM(`business_id`) > 5 IN
            (select `business_id`
            from yelpReviewsView)
          """)
    }

    /*
    /**
      * use DataFrame transformations: find the businesses in yelpBusinesses  that have appeared in reviews in yelpReviews by more than 5 influencer users
      * sort the result in a descending order according to the count of reviews
      *
      * @param yelpBusinesses
      * @param yelpReviews
      * @param influencerUsersDF
      * @return DataFrame of names of businesses that match the criteria
      */
    def findFamousBusinessesDF(yelpBusinesses: DataFrame, yelpReviews: DataFrame, influencerUsersDF: DataFrame): DataFrame = ???

    //Q5:
    /**
      * use SQL statements: find a descendingly ordered list of users based on  the average star counts given by each of them
      * in all the reviews that they have written
      *
      * You need to average the stars given by each user in reviews that appear in yelpReviews and then sort them
      *
      * @return DataFrame of (user names and average stars)
      */
    def findavgStarsByUserSQL():DataFrame = ???

    /**
      * use DataFrame transformations: find a descendingly ordered list of users based on the average star counts given by each of them
      * in all the reviews that they have written in yelpReviews
      *
      * You need to average the stars given by each user in reviews that appear in yelpReviews and then sort them
      *
      * @param yelpReviews
      * @param yelpUsers
      * @return DataFrame of (user names and average stars)
      */
    def findavgStarsByUserDF(yelpReviews: DataFrame, yelpUsers: DataFrame):DataFrame = ???
  */

  //calls the required function to be executed
  def runYelpAnalysisQuery(yelpReviewsFilePath : String, yelpBusinessFilePath : String,
                           yelpUserFilePath : String, yelpAnalysisOutFilePath: String,
                           queryNum : Int, implSelection : Int) = {
    queryNum match{
      case 1 => {
        //load data needed by Q1
        val yelpBusiness = dataLoader(yelpBusinessFilePath)

        // Q1: Analyze yelp_academic_dataset_Business.json to find the number of reviews for all businesses.
        // The output should be in the form of DataFrame of a single count.
        println("Q1: query yelp_academic_dataset_Business.json to find the total number of reviews for all businesses")

        implSelection match{
          case 1 => {

            yelpBusiness.createTempView("yelpBusinessesView")
            val totalReviewsPerBusinessSQL = totalReviewsSQL()
            totalReviewsPerBusinessSQL.write.mode("overwrite").csv(yelpAnalysisOutFilePath+"_Q1_SQL")
          }
          case 2 => {
            val totalReviewsPerBusinessDF = totalReviewsbDF(yelpBusiness)
            totalReviewsPerBusinessDF.write.mode("overwrite").csv(yelpAnalysisOutFilePath+"_Q1_DF")
          }
          case _ => println("YelpAnalysis: invalid implementation type, valid types 1 = SQL, 2 = DF")
        }
      } case 2 => {
        //load data needed by Q2
        val yelpBusiness = dataLoader(yelpBusinessFilePath)

        // Q2:  Analyze Analyze yelp_academic_dataset_Business.json to find all businesses that have received 5 stars and that have been reviewed by 1000 or more users
        println("Q2: query yelp_academic_dataset_Business.json to find businesses that have received 5 stars and that have been reviewed by 1000 or more users")

        implSelection match{
          case 1 => {
            yelpBusiness.createTempView("yelpBusinessesView")
            val topBusinessesSQL = fiveStarBusinessesSQL()
            topBusinessesSQL.write.mode("overwrite").csv(yelpAnalysisOutFilePath+"_Q2_SQL")
          }
          case 2 => {
            val topBusinessesDF = fiveStarBusinessesDF(yelpBusiness)
            topBusinessesDF.write.mode("overwrite").csv(yelpAnalysisOutFilePath+"_Q2_DF")
          }
          case _ => println("YelpAnalysis: invalid implementation type, valid types 1 = SQL, 2 = DF")
        }
      }
      case 3 => {
        //load data needed by Q3
        val yelpUsers = dataLoader(yelpUserFilePath)

        // Q3:Analyze yelp_academic_dataset_users.json to find the influencer users who have written more than 1000 reviews.
        println("Q3: query yelp_academic_dataset_users.json to find influencers")

        implSelection match{
          case 1 => {
            yelpUsers.createTempView("yelpUsersView")
            val influencerUsersSQL = findInfluencerUserSQL()
            influencerUsersSQL.write.mode("overwrite").csv(yelpAnalysisOutFilePath+"_Q3_SQL")
          }
          case 2 => {
            val influencerUsersDF = findInfluencerUserDF(yelpUsers)
            influencerUsersDF.write.mode("overwrite").csv(yelpAnalysisOutFilePath+"_Q3_DF")
          }
          case _ => println("YelpAnalysis: invalid implementation type, valid types 1 = SQL, 2 = DF")
        }
      }
      case 4 => {
        //load data needed by Q4
        val yelpBusiness = dataLoader(yelpBusinessFilePath)
        val yelpReviews = dataLoader(yelpReviewsFilePath)
        val yelpUsers = dataLoader(yelpUserFilePath)
        val influencerUsersDF = findInfluencerUserDF(yelpUsers)

        // Q4: Analyze yelp_academic_dataset_review.json and a view created from your answer to Q3  to find names of businesses that have been reviewed by more than 5 influencer users.
        println("Q4: yelp_academic_dataset_review.json and a view created from your answer to Q3  to find names of businesses that have been reviewed by more than 5 influencer users")

        implSelection match{
          case 1 => {
            yelpBusiness.createTempView("yelpBusinessView")
            yelpReviews.createTempView("yelpReviewsView")
            influencerUsersDF.createTempView("influencerUsersView")
            val businessesReviewedByInfluencersSQL = findFamousBusinessesSQL()
            businessesReviewedByInfluencersSQL.write.mode("overwrite").csv(yelpAnalysisOutFilePath+"_Q4_SQL")
          }
          case 2 => {
            //val businessesReviewedByInfluencersDF = findFamousBusinessesDF(yelpBusiness, yelpReviews, influencerUsersDF)
            //businessesReviewedByInfluencersDF.write.mode("overwrite").csv(yelpAnalysisOutFilePath+"_Q4_DF")
          }
          case _ => println("YelpAnalysis: invalid implementation type, valid types 1 = SQL, 2 = DF")

        }
      }
      case 5 => {
        //load data needed by Q5
        val yelpReviews = dataLoader(yelpReviewsFilePath)
        val yelpUsers = dataLoader(yelpUserFilePath)

        // Q5: Analyze yelp_academic_dataset_review.json  and yelp_academic_dataset_users.json to find the average stars given by each user. You need to order the users descendingly according to their average star counts.
        println("Q5: query yelp_academic_dataset_reviews.json, query yelp_academic_dataset_users.json to find average stars given by each user, descendingly ordered")

        implSelection match{
          case 1 => {
            yelpReviews.createTempView("yelpReviewsView")
            yelpUsers.createTempView("yelpUsersView")

            //val avgStarsByUserSQL = findavgStarsByUserSQL()
            //avgStarsByUserSQL.write.mode("overwrite").csv(yelpAnalysisOutFilePath+"_Q5_SQL")
          }
          case 2 => {
            //val avgStarsByUserDF = findavgStarsByUserDF(yelpReviews, yelpUsers)
            //avgStarsByUserDF.write.mode("overwrite").csv(yelpAnalysisOutFilePath+"_Q5_DF")
          }
          case _ => println("YelpAnalysis: invalid implementation type, valid types 1 = SQL, 2 = DF")
        }
      }
      case 0 => {
        //load data needed by all queries
        val yelpBusiness = dataLoader(yelpBusinessFilePath)
        val yelpReviews = dataLoader(yelpReviewsFilePath)
        val yelpUsers = dataLoader(yelpUserFilePath)

        //create views for all data files
        yelpBusiness.createTempView("yelpBusinessesView")
        yelpReviews.createTempView("yelpReviewsView")
        yelpUsers.createTempView("yelpUsersView")

        //Q1
        val totalReviewsPerBusinessSQL = totalReviewsSQL()
        totalReviewsPerBusinessSQL.write.mode("overwrite").csv(yelpAnalysisOutFilePath+"_Q1_SQL")

        val totalReviewsPerBusinessDF = totalReviewsbDF(yelpBusiness)
        totalReviewsPerBusinessDF.write.mode("overwrite").csv(yelpAnalysisOutFilePath+"_Q1_DF")

        //Q2
        val topBusinessesSQL = fiveStarBusinessesSQL()
        topBusinessesSQL.write.mode("overwrite").csv(yelpAnalysisOutFilePath+"_Q2_SQL")

        val topBusinessesDF = fiveStarBusinessesDF(yelpBusiness)
        topBusinessesDF.write.mode("overwrite").csv(yelpAnalysisOutFilePath+"_Q2_DF")

        //Q3
        val influencerUsersSQL = findInfluencerUserSQL()
        influencerUsersSQL.write.mode("overwrite").csv(yelpAnalysisOutFilePath+"_Q3_SQL")

        val influencerUsersDF = findInfluencerUserDF(yelpUsers)
        influencerUsersDF.write.mode("overwrite").csv(yelpAnalysisOutFilePath+"_Q3_DF")

        //Q4
        influencerUsersSQL.createTempView("influencerUsersView")
        val businessesReviewedByInfluencersSQL = findFamousBusinessesSQL()
        businessesReviewedByInfluencersSQL.write.mode("overwrite").csv(yelpAnalysisOutFilePath+"_Q4_SQL")
        /*
        val businessesReviewedByInfluencersDF = findFamousBusinessesDF(yelpBusiness, yelpReviews, influencerUsersDF)
        businessesReviewedByInfluencersDF.write.mode("overwrite").csv(yelpAnalysisOutFilePath+"_Q4_DF")

        //Q5
        val avgStarsByUserSQL = findavgStarsByUserSQL()
        avgStarsByUserSQL.write.mode("overwrite").csv(yelpAnalysisOutFilePath+"_Q5_SQL")
        val avgStarsByUserDF = findavgStarsByUserDF(yelpReviews, yelpUsers)
        avgStarsByUserDF.write.mode("overwrite").csv(yelpAnalysisOutFilePath+"_Q5_DF")

         */
      }
      case _ => println("YelpAnalysis: invalid query num, valid query numbers 1-5")
    }
  }

  def main(args: Array[String]): Unit = {
    import spark.implicits._

    //load yelp data
    val yelpReviewsFilePath = ConfigFactory.load().getString("BIDMT.project2.data.YelpData.yelpReviewsFilePath")
    val yelpBusinessFilePath = ConfigFactory.load().getString("BIDMT.project2.data.YelpData.yelpBusinessFilePath")
    val yelpUserFilePath = ConfigFactory.load().getString("BIDMT.project2.data.YelpData.yelpUserFilePath")
    val yelpAnalysisOutFilePath = ConfigFactory.load().getString("BIDMT.project2.data.YelpData.outFilePath")

    if(args.length < 1){
      //run all queries
      runYelpAnalysisQuery(
        yelpReviewsFilePath,
        yelpBusinessFilePath,
        yelpUserFilePath,
        yelpAnalysisOutFilePath,
        0,0)

    }else if(args.length == 1){
      //run DF and SQL solution for one query
      runYelpAnalysisQuery(yelpReviewsFilePath,yelpBusinessFilePath,yelpUserFilePath,yelpAnalysisOutFilePath,args(0).toInt,1)
      runYelpAnalysisQuery(yelpReviewsFilePath,yelpBusinessFilePath,yelpUserFilePath,yelpAnalysisOutFilePath,args(0).toInt,2)

    }else{
      //run either DF or SQL sol for one query
      runYelpAnalysisQuery(yelpReviewsFilePath,yelpBusinessFilePath,yelpUserFilePath,yelpAnalysisOutFilePath,args(0).toInt,args(1).toInt)

    }

    //close the spark session
    spark.close()
    spark.stop()
  }

}
