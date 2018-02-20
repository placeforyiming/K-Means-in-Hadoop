## K-Means Clustering

K-Means clustering is a popular algorithm for clustering similar objects into K groups (clusters). It starts with an initial seed of K points (randomly chosen) as centers, and then the algorithm iteratively tries to enhance these centers. Your system should terminate if either of these two conditions become true:

**a) The K centers did not change over two consecutive iterations**

**b) The maximum number of iterations has been reached (parameter R for rounds)**

### 1. Data creation

**Step 1.** In the command line, typing :
>java WriteAFile

Then, you will get a 97M dataset file which includes 10000000 points.

**Step 2.** In the command line, typing:
>java WriteCentroid k (example: java WriteCentroid 10)

Then, you will get a centroid file with k centers, (The k in the example is 10).

**Step 3.** Create a directory in HDFS for point data and centroid data:
>hadoop fs -mkdir /user/hadoop/InputData hadoop fs -mkdir /user/hadoop/Centroid

**Step 4.** Put the dataset into the created directory:
>hadoop fs -put Location.csv InputData/Location.csv hadoop fs -put centroid.csv Centroid/centroid.csv

### 2. Single-iteration K_means:
Command line input:
In the command line, typing:
>hadoop jar ./Single_K_means.jar org.apache.hadoop.examples.Single_K_means <centroid directory> <point directory> <output directory>

Here is an example:
>hadoop jar ./Single_K_means.jar
>org.apache.hadoop.examples.Single_K_means /user/hadoop/Centroid/centroid.csv /u ser/hadoop/InputData/Location.csv /user/hadoop/temp
Mapper part:

In the setup function, we load all the centroid into the main memory.
    
In the map function, we compare each point with every centroid. We set the index of the centroid which is nearest to the point as the KEY for that point. And we set the x-coordinate and y- coordinate of that point as the VALUE.

**Reduce part:**
Gather all the point for a certain centroid, and calculating the average x and y coordinate as the new centroid.

**Job setting part:**
Just set parameters for this map-reduce job.

Run the whole map-reduce process for one time, and replace the old centroid with new centroid file and delete the output file.
Run another time without reduce part, we will get the classified point file.( We can use a parameter to control if we run reduce part. )

### 3. Multi-iteration K_means:
Command line input:
In the command line, input:
>hadoop jar ./Multi_K_means.jar
org.apache.hadoop.examples.Multi_K_means <centroid directory> <point directory> <output directory> k

Here is an example:
>hadoop jar ./Multi_K_means.jar
org.apache.hadoop.examples.Multi_K_means /user/hadoop/Centroid/centroid.csv /u ser/hadoop/InputData/Location.csv /user/hadoop/temp 6

**Mapper part:**
The same as single iteration Kmeans.

**Reduce part:**
The same as single iteration Kmeans.

**Job setting part:**
Just set parameters for one round map-reduce job.

Run the whole map-reduce process for k times with a loop, and replace the old centroid with new centroid file and delete the output file for each iteration.
Run another time without reduce part, we will get the classified point file.
  