# Spark-geolocate example

## Implementation

I decided to implement this project using Spark with Scala as it translated well with the requirements of parsing csv data
and keeping the application scalable.

For this prototype we are just running the application on a single master node locally but it could be easily scaled over a cluster thanks to Spark.

The main challange of this task was computing the closest `IATA code` in a reasonable amount of time.
Since we are forced to do this computation for every event and the list of `optd-sample-20161201.csv.gz` is fairly big, a brute force approach
would have a O(n*m) complexity and it results in a very slow computation over big amounts of data.

Thus I focused on a strategy to optimise the geodistance calculation. One option could have been using some clustering algorithm (like dbscan)
to reduce the coordinates but I couldn't find any good library for that in scala. So I end up implementing my own k-d tree 
data structure instead (quite a challenge in Scala :)), which organize our geo-coordinates  in a binary tree and 
it's very useful to perform a nearest neighbor search in O(log n). This improved a lot execution time. 
 
The application is reading all the data at once, processing it and writing the result in a CSV. 
In a real world scenario we would probably want to stream events from a kafka cluster or process a batched amount of events per second.
This goal could be easily reached with Spark streaming.

## Running

Can be run dockerized through `run.sh` or directly on Spark
