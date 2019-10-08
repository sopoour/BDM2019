package dk.itu.BIDMT.Exercises.Exercise07

case class BikeShareData (
                      trip_id: Int,
                      duration : Int,
                      start_time : String,
                      end_time : String,
                      start_station : String,
                      start_lat : String,
                      start_lon : String,
                      end_station : String,
                      end_lat : String,
                      end_lon : String,
                      bike_id : String,
                      plan_duration : Int,
                      trip_route_category : String,
                      passholder_type : String,
                      bike_type : String
                    )

object BikeShareData{
  def apply(bikeShareInfo : String) :  BikeShareData = {
    val bikeShareInfoSplitted = bikeShareInfo.split(",")
    new BikeShareData(
      bikeShareInfoSplitted(0).toInt,
      bikeShareInfoSplitted(1).toInt,
      bikeShareInfoSplitted(2),
      bikeShareInfoSplitted(3),
      bikeShareInfoSplitted(4),
      bikeShareInfoSplitted(5),
      bikeShareInfoSplitted(6),
      bikeShareInfoSplitted(7),
      bikeShareInfoSplitted(8),
      bikeShareInfoSplitted(9),
      bikeShareInfoSplitted(10),
      bikeShareInfoSplitted(11).toInt,
      bikeShareInfoSplitted(12),
      bikeShareInfoSplitted(13),
      bikeShareInfoSplitted(14)
    )
  }
}
