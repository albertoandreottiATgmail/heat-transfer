package org.apache.hadoop.examples;

import java.util.*;
import java.io.*;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.*;
import org.apache.hadoop.mapreduce.lib.output.*;
import org.apache.hadoop.util.GenericOptionsParser;

import org.apache.hadoop.examples.MatrixData;


public class HeatTransfer {

  public static class TokenizerMapper 
    extends Mapper<IntWritable, FloatArrayWritable, IntWritable, FloatArrayWritable>{
    
    public void map(IntWritable key, FloatArrayWritable value, Context context
                            ) throws IOException, InterruptedException {
          
		  int myKey = key.get();
		  int zBasedWidth = MatrixData.Width()-1;

          key.set(myKey-1);
          context.write(key, value);
          key.set(myKey+1);
          context.write(key, value);
		  
	  key.set(myKey);
	  float tmp1, tmp2;
	  
	  tmp1=value.floatAt(0);
	  value.set(0, value.floatAt(1));
	  for(int i=1; i<zBasedWidth; i++) {
		tmp2=value.floatAt(i);
		value.set(i, tmp1+value.floatAt(i+1));
		tmp1=tmp2;
	  }
	  value.set(zBasedWidth,tmp1);
		  
	  context.write(key, value);
	}
  }
  
  public static class IntSumReducer 
       extends Reducer<IntWritable, FloatArrayWritable, IntWritable, FloatArrayWritable> {

    private IntWritable result = new IntWritable();

    public void reduce(IntWritable key, Iterable<FloatArrayWritable> values, 
                       Context context) throws IOException, InterruptedException {
      
      FloatArrayWritable result = new FloatArrayWritable();
      FloatWritable[] FloatArray = new FloatWritable[MatrixData.Width()];  
      	  	  
	  //Initialize the result
	  for(int i=0; i<(MatrixData.Width());i++){
         FloatArray[i] = new FloatWritable(0f);
	  }
	  
      //Keys for which no output is produced
      if(key.get()<0||key.get()>(MatrixData.Height()-1)) 
        return;

      int zBasedWidth = MatrixData.Width()-1;
	 
	  //Add the rows
      for(FloatArrayWritable faw : values) {
         float tmp = FloatArray[0].get() + faw.floatAt(0);
	     FloatArray[0].set(tmp);

	     tmp = FloatArray[zBasedWidth].get() + faw.floatAt(zBasedWidth);
	     FloatArray[zBasedWidth].set(tmp);
			
          for(int i=1; i<(MatrixData.Width()-1);i++){
				tmp = faw.floatAt(i);
				FloatArray[i].set(tmp);
		 		}
	  }

      //Set heat source
      if(key.get()==MatrixData.HeatSourceY()){
      FloatArray[MatrixData.HeatSourceX()].set(MatrixData.HeatSourceTemperature());
      }
      //Make the division and write the result
      for(int i=0; i<(MatrixData.Width());i++){
     	FloatArray[i].set( FloatArray[i].get()/4);
	  }

      result.set(FloatArray);
      context.write(key, result);

    }
  }

  public static void main(String[] args) throws Exception {

    long start = System.currentTimeMillis();
    Configuration conf = new Configuration();
    String[] otherArgs = new GenericOptionsParser(conf, args).getRemainingArgs();
    if (otherArgs.length != 2) {
      System.err.println("Usage: HeatTransfer <in> <out>");
      System.exit(2);
    }
    Job job = new Job(conf, "heat transfer");
    job.setJarByClass(HeatTransfer.class);
    job.setMapperClass(TokenizerMapper.class);
    //job.setCombinerClass(IntSumReducer.class);
    job.setReducerClass(IntSumReducer.class);

    //set job's input format
    job.setInputFormatClass(SequenceFileInputFormat.class);
    //map output/reduce input
    job.setMapOutputValueClass(FloatArrayWritable.class);    
    job.setMapOutputKeyClass(IntWritable.class);    

    //reduce output
    job.setOutputKeyClass(IntWritable.class);
    job.setOutputValueClass(FloatArrayWritable.class);
    SequenceFileInputFormat.addInputPath(job, new Path(otherArgs[0]));
    FileOutputFormat.setOutputPath(job, new Path(otherArgs[1]));

    //set job's output format
    job.setOutputFormatClass(SequenceFileOutputFormat.class);
    //System.exit(job.waitForCompletion(true) ? 0 : 1);
    int status = job.waitForCompletion(true) ? 0 : 1;

    long end = System.currentTimeMillis();
    System.out.println("Execution time was "+(end-start)+" ms.");
    try{
        // Create file
        FileWriter fstream = new FileWriter("Times.txt",true);
        BufferedWriter out = new BufferedWriter(fstream);
        out.write(end-start + "ms \n");
        //Close the output stream
        out.close();
    }catch (Exception e){
        //Catch exception if any
        System.err.println("Error: " + e.getMessage());
        System.exit(status);
    }

    System.exit(status);	
   
  }
}

