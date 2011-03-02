package org.apache.hadoop.examples;

import java.util.*;
import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.*;
import org.apache.hadoop.mapreduce.lib.output.*;
import org.apache.hadoop.mapred.lib.MultipleInputs;

//import org.apache.hadoop.mapred.lib.MultipleInputs;
import org.apache.hadoop.util.GenericOptionsParser;


public class TestConvergence {

  public static class TokenizerMapper
    extends Mapper<IntWritable, FloatWritable, IntWritable, FloatWritable>{


    //Length of the matrix
    private final static int length = 1000;

    public void map(IntWritable key, FloatWritable fwValue, Context context
                            ) throws IOException, InterruptedException {
          int myKey = key.get();

          context.write(new IntWritable(myKey - 1), fwValue);	
          System.out.println("Soy un map con key: " + key.toString() + " \n");

        }
  }

  public static class IntSumReducer
       extends Reducer<IntWritable, FloatWritable, IntWritable, FloatWritable> {

    private final static int length = 1000;

    public void reduce(IntWritable key, Iterable<FloatWritable> fwValues, 
                       Context context) throws IOException, InterruptedException {

      float result = 0.0f;
      //System.out.println("Soy un reduce con key: " + key.get() + "\n");

      //Handle first and last "cold" boundaries
      if(key.get()<0 || key.get()>length*100 -1){
         return;
      }

      if(key.get()==3500){
         context.write(key, new FloatWritable(23f));
         return;
      }


/* debugging
      for(KeyArrayValue kav : values) {

           System.out.println(key.get() +":" + kav.getKey());
           System.out.println("\n");
      }
*/

      //Add all the values

      for(FloatWritable fw : fwValues) {
           result += fw.get();
           System.out.println("Soy un reduce con key: " + key.get() + "y value: " + fw.get()+ "\n");
      }

      context.write(key, new FloatWritable(result/4));

    }
  }

  public static void main(String[] args) throws Exception {
    Configuration conf = new Configuration();

    String[] otherArgs = new GenericOptionsParser(conf, args).getRemainingArgs();
    if (otherArgs.length != 2) {
      System.err.println("Usage: HeatTransfer <in> <out>");
      System.exit(2);
    }
    Job job = new Job(conf, "heat transfer");
    job.setJarByClass(TestConvergence.class);
    job.setMapperClass(TokenizerMapper.class);
    //job.setCombinerClass(IntSumReducer.class);
    job.setReducerClass(IntSumReducer.class);

    //set job's input format
    job.setInputFormatClass(SequenceFileInputFormat.class);
    
    //map output/reduce input
    job.setMapOutputKeyClass(IntWritable.class);
    job.setMapOutputValueClass(FloatWritable.class);

    //reduce output
    job.setOutputKeyClass(IntWritable.class);
    job.setOutputValueClass(FloatWritable.class);
    SequenceFileInputFormat.addInputPath(job, new Path(otherArgs[0]));
    SequenceFileInputFormat.addInputPath(job, new Path(otherArgs[1]));
    
    //MultipleInputs.addInputPath(jobConf, new Path(otherArgs[0]),
    //SequenceFileInputFormat.class);//, TokenizerMapper.class);
    
    //MultipleInputs.addInputPath(jobConf, new Path(otherArgs[1]),
    //SequenceFileInputFormat.class);//, TokenizerMapper.class);

    FileOutputFormat.setOutputPath(job, new Path("\\out"));

    //set job's output format
    job.setOutputFormatClass(SequenceFileOutputFormat.class);
    System.exit(job.waitForCompletion(true) ? 0 : 1);
  }
}
