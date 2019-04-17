# Spark-geolocate scala example

## The Objective

Find the closest airport for a list of tracking signals containing the approximate geolocation of users on internet. 

## Data-sets

The two sample data sets to consume are:

* `optd-sample-20161201.csv.gz` - A simplified version of a data set from Open Travel Data, containing geo-coordinates of major airports:
    * IATA airport code - a three-character identifier of global airports (first column)
    * Latitude and Longitude in floating point format (second and third columns, respectively)

* `sample_data.csv.gz` - Some sample input data for your application, containing:
    * A universally unique identifier (uuid) which acts as a user-id for some end-user (first column)
    * Latitude and Longitude in floating point format (second and third columns, respectively)
    * Each row of the CSV can be considered as a single user tracking event.

## Task.

The application needs to:
  
* Parse the input data-source containing events with the same fields as in the sample csv: user-id and geo-coordinates.

* Take the users's coordinates and compare this with the set of airport coordinates to get the IATA code with the closest geodistance to the user.

* For each input event, output one event containing two fields: user-id and an IATA code corresponding to the closest airport.

* The application must be scalable. In a real-world scenario, there might be thousands of such events per second.

## Implementation

I decided to implement this project using Spark with Scala as it translated well with the requirements of parsing csv data
and keeping the application scalable.

For this prototype we are just running the application on a single master node locally but it could be easily scaled over a cluster thanks to Spark.

The main challange of this task is computing the closest `IATA code` quickly. Since we are forced to do this computation 
for every event and the list of `optd-sample-20161201.csv.gz` is fairly big, a brute force approach
has O(n*m) complexity and it results in a very slow computation over big amounts of data.

Thus I focused on a strategy to optimise the geodistance calculation. One idea I followed was using some clustering algorithm
to organize the data into smaller "buckets" of coordinates close to each other and speed up the search but it was a bit too complicated for the purpose of this task.    

I end up using a quicker solution with R-trees (another option being k-d trees). These trees index multi-dimensional information 
such as geo-coordinates and perform a k-nearest neighbor search in O(log n). Thus improving search time. 

There are two limitations though: 
- in some edge-cases the nearest neighbor might be missed depending on how the coordinates are partitioned in the tree. 
Some splitting strategies (or different tree) might behave better but if we favour consistency rather than speed we might just use a linear search and have a bigger cluster. Compromises.
- for this example we use the eucledian distance to calculate the closest coordinate. This is faster but error-prone (especially at the poles). 
It's easily possible to use a spherical distance (like haversine) but it slowed down a lot the computation. There might be some precomputation/approximation strategy to improve this.  
 
 
The application is reading all the data at once, processing it and writing the result in a CSV. 
In a real world scenario we would probably want to stream events from a kafka cluster or process a batched amount of events per second.
This prototype could be easily modified to use Spark streaming.

## Running

Can be run dockerized through `run.sh` or directly on Spark
