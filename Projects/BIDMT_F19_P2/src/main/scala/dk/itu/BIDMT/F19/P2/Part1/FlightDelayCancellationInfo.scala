package dk.itu.BIDMT.F19.P2.Part1


case class FlightDelayCancellationInfo (
                                         fL_DATE : String,
                                         OP_CARRIER : String,
                                         OP_CARRIER_FL_NUM : String,
                                         ORIGIN : String,
                                         DEST : String,
                                         CRS_DEP_TIME : String,
                                         DEP_TIME : String,
                                         DEP_DELAY : String,
                                         TAXI_OUT : String,
                                         WHEELS_OFF : String,
                                         WHEELS_ON : String,
                                         TAXI_IN : String,
                                         CRS_ARR_TIME : String,
                                         ARR_TIME : String,
                                         ARR_DELAY : String,
                                         CANCELLED : String,
                                         CANCELLATION_CODE : String,
                                         DIVERTED : String,
                                         CRS_ELAPSED_TIME : String,
                                         ACTUAL_ELAPSED_TIME : String,
                                         AIR_TIME : String,
                                         DISTANCE : String,
                                         CARRIER_DELAY : String,
                                         WEATHER_DELAY : String,
                                         NAS_DELAY : String,
                                         SECURITY_DELAY : String,
                                         LATE_AIRCRAFT_DELAY : String,
                                         Unnamed : String
                                       )


object FlightDelayCancellationInfo {

  def apply(flightInfo : String) : FlightDelayCancellationInfo = {
    val flightInfoSplitted = flightInfo.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1)
    if(flightInfoSplitted.length<28)
      println("Error loading the data with the expected number of columns" + flightInfoSplitted)
    FlightDelayCancellationInfo(
      flightInfoSplitted(0),
      flightInfoSplitted(1),
      flightInfoSplitted(2),
      flightInfoSplitted(3),
      flightInfoSplitted(4),
      flightInfoSplitted(5),
      flightInfoSplitted(6),
      flightInfoSplitted(7),
      flightInfoSplitted(8),
      flightInfoSplitted(9),
      flightInfoSplitted(10),
      flightInfoSplitted(11),
      flightInfoSplitted(12),
      flightInfoSplitted(13),
      flightInfoSplitted(14),
      flightInfoSplitted(15),
      flightInfoSplitted(16),
      flightInfoSplitted(17),
      flightInfoSplitted(18),
      flightInfoSplitted(19),
      flightInfoSplitted(20),
      flightInfoSplitted(21),
      flightInfoSplitted(22),
      flightInfoSplitted(23),
      flightInfoSplitted(24),
      flightInfoSplitted(25),
      flightInfoSplitted(26),
      flightInfoSplitted(27))

  }

}
