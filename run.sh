#!/bin/bash

if [[ "$(docker images -q ta/geo-spark:latest 2> /dev/null)" == "" ]]; then
  docker build -t ta/geo-spark:latest .
fi

docker run -v $(pwd)/:/tmp/ ta/geo-spark spark-2.4.1-bin-hadoop2.7/bin/spark-submit \
    --class "GeolocateByIATA" --master local[4] \
    /tmp/fatjar/spark-geolocate.jar /tmp/

# to remove the image
# docker rmi $(docker images | grep 'ta/geo-spark' | awk '{print $3}')
