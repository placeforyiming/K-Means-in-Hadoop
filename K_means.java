

package org.apache.hadoop.examples;
import java.awt.geom.Point2D;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;
import java.lang.Math;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.FileUtil;
import org.apache.hadoop.io.Text;
import java.io.File;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.jobcontrol.ControlledJob;
import org.apache.hadoop.mapreduce.lib.jobcontrol.JobControl;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

public class K_means {

    public static double Threshold;
    public static int Round ; 


    public static class Map
        extends Mapper<Object, Text, Text, Text> {
            private ArrayList<Point2D.Float> Initial_centroid;
            private Text KEY = new Text();
            private Text VALUE = new Text();

            public void setup(Context context) throws IOException {
            Configuration conf = context.getConfiguration();
            Path pt = new Path(conf.get("centersPath"));
            Scanner input = new Scanner(FileSystem.get(conf).open(pt));
            Initial_centroid = new ArrayList<Point2D.Float>();
            while (input.hasNextLine()){
                String point_come = input.nextLine();
                String[] point_all=point_come.split("\t");
                String point_one = point_all[1];
                String[] point = point_one.split(",");
                Point2D.Float C = new Point2D.Float(Float.parseFloat(point[0]), Float.parseFloat(point[1]));
                Initial_centroid.add(C);
                }
            input.close();
            }

            public void map (Object key, Text value, Context context)
                throws IOException, InterruptedException {
            String[] parts = value.toString().split(",");
            int pX = Integer.parseInt(parts[0]);
            int pY = Integer.parseInt(parts[1]);
            double min_distance = 1000000;
            int index = 0;
            for (int i=0; i<Initial_centroid.size(); i++) {
                float cX = Initial_centroid.get(i).x;
                float cY = Initial_centroid.get(i).y;
                double distance = Math.hypot(pX-cX, pY-cY);
                if (distance < min_distance) {
                    min_distance = distance;
                    index = i;
                }
            }
            String result = String.valueOf(index);
            KEY.set(result);
            VALUE.set(new Text (value));
            context.write(KEY, VALUE);
        }
    }



    public static class Reduce
        extends Reducer<Text, Text, Text, Text> {
        
        public void reduce(Text key, Iterable<Text> values, Context context) 
                throws IOException, InterruptedException {
            int len=0;
            double sum_X=0.0;
            double sum_Y=0.0;
            for (Text val: values) {
                String[] parts = val.toString().split(",");
                sum_X=sum_X+Float.parseFloat(parts[0]);
                sum_Y=sum_Y+Float.parseFloat(parts[1]);
                len=len+1;
            }
            String X_c = String.valueOf(sum_X/len);
            String Y_c = String.valueOf(sum_Y/len);
            context.write(key, new Text(X_c+","+Y_c));
           
        }

    }
        

    public static class Tools{

        public static void deletePath(String pathStr) throws IOException{
          Configuration conf = new Configuration();
          Path path = new Path(pathStr);
          FileSystem hdfs = path.getFileSystem(conf);
          hdfs.delete(path ,true);
        }


        public static boolean Compare(String centerPath,String newPath) throws IOException{

            Path path_old = new Path(centerPath);
            Path path_new = new Path(newPath+"/part-r-00000");
            double Distance_total = 0.0;
            Configuration conf = new Configuration();
            FileSystem fileSystem_old = path_old.getFileSystem(conf);
            Scanner input_old = new Scanner(fileSystem_old.open(path_old));
            FileSystem fileSystem_new = path_new.getFileSystem(conf);
            Scanner input_new = new Scanner(fileSystem_new.open(path_new));
            while (input_new.hasNextLine() && input_old.hasNextLine()){
                String[] point_old_one = input_old.nextLine().split("\t");
                String[] point_old = point_old_one[1].split(",");
                String[] point_new_one = input_new.nextLine().split("\t");
                String[] point_new = point_new_one[1].split(",");
                double distance = Math.hypot(Float.parseFloat(point_old[0])-Float.parseFloat(point_new[0]), Float.parseFloat(point_old[1])-Float.parseFloat(point_new[1]));
                Distance_total=Distance_total+distance;
                }
            input_new.close();
            input_old.close();
            if ((Distance_total-K_means.Threshold)>0){
                FileSystem filesystem = FileSystem.get(conf);
                FileUtil.copy(filesystem, path_new, filesystem, path_old , true, conf);
                Tools.deletePath(newPath);
                return false;
                }else{
                    System.out.println(Distance_total);
                    Tools.deletePath(newPath);
                    return true;
                }
    }
}


    public static void run(String centerPath,String dataPath,String newCenterPath,boolean runReduce) throws IOException, ClassNotFoundException, InterruptedException{
         Configuration conf = new Configuration();
         conf.set("centersPath", centerPath);
         
         Job job = new Job(conf, "mykmeans");
         job.setJarByClass(K_means.class);
         
         job.setMapperClass(Map.class);
 
         job.setMapOutputKeyClass(Text.class);
         job.setMapOutputValueClass(Text.class);
 
         if(runReduce){
             
             job.setReducerClass(Reduce.class);
             job.setOutputKeyClass(Text.class);
             job.setOutputValueClass(Text.class);
         }
         
        FileInputFormat.addInputPath(job, new Path(dataPath));
        FileOutputFormat.setOutputPath(job, new Path(newCenterPath));
         
        System.out.println(job.waitForCompletion(true));
     }
 

    public static void main(String[] args) throws ClassNotFoundException, IOException, InterruptedException {
         String centerPath = args[0];
         String dataPath = args[1];
         String newCenterPath = args[2];
         K_means.Round = Integer.parseInt(args[3]);
         K_means.Threshold=Double.parseDouble(args[4]);


         int count = 0;
         while(true){
                      run(centerPath,dataPath,newCenterPath,true);
             System.out.println(" The " + ++count + " round ");
             
             if(Tools.Compare(centerPath,newCenterPath) || (count== K_means.Round)){

                
                
                 run(centerPath,dataPath,newCenterPath,false);
                 break;
             }
         }
    }
}






































