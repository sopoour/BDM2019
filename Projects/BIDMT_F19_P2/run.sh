#/bin/bash

if (( $1 == 1 )); then
    class="dk.itu.BIDMT.F19.P2.Part1.AirlineDataAnalysisRDD"
elif (( $1 == 2 )); then
    class="dk.itu.BIDMT.F19.P2.Part1.AirlineDataAnalysisSQL"
elif (( $1 == 3 )); then
    class="dk.itu.BIDMT.F19.P2.Part2.YelpAnalysis"

else
    echo "Specify class either 1, 2 or 3"
    exit
fi


echo $class


spark-submit \
  --master yarn \
  --deploy-mode cluster \
  --class $class \
  --files application.conf \
  --conf spark.driver.extraJavaOptions=-Dconfig.file=application.conf \
  --conf spark.executor.extraJavaOptions=-Dconfig.file=application.conf \
  BIDMT_F19_P2-assembly-0.1.jar $2 $3
