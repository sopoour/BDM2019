#/bin/bash
spark-submit \
  --master yarn \
  --deploy-mode cluster \
  --class dk.itu.BIDMT.F19.P3.AmazonProductsClustering \
  --files application.conf \
  --conf spark.driver.extraJavaOptions=-Dconfig.file=application.conf \
  --conf spark.executor.extraJavaOptions=-Dconfig.file=application.conf \
  BIDMT_F19_P3-assembly-0.1.jar
