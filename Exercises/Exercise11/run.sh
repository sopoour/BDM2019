#/bin/bash

if [$1 == 1]
then
   spark-submit \
      --class dk.itu.BIDMT.Exercises.Exercise11.KinesisWordProducerASL \
      Exercise11Solution-assembly-0.1.jar $2 https://kinesis.us-east-1.amazonaws.com $3 $4
elif [$1 == 2]
then
   spark-submit \
      --master yarn \
      --deploy-mode cluster \
      --class dk.itu.BIDMT.Exercises.Exercise11.KinesisWordCountASL \
      Exercise11Solution-assembly-0.1.jar kinesis-producer-sample  $2 https://kinesis.us-east-1.amazonaws.com
else
   echo "usage: ./run.sh {1 or 2} kinesisStreamName [recordNum] [wordsPerRecord]"
   exit
fi
