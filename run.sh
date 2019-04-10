#!/bin/bash

docker build -t ta/geo-spark:latest .
docker run -v $(pwd)/:/tmp/ ta/geo-spark spark-2.4.1-bin-hadoop2.7/bin/spark-submit \
    --class "GeolocateByIATA" --master local[4] \
    /tmp/fatjar/datateam-challenge.jar /tmp/
#docker rmi $(docker images | grep 'ta/geo-spark' | awk '{print $3}')
