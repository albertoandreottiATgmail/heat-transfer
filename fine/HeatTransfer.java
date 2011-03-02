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
import org.apache.hadoop.util.GenericOptionsParser;

import org.apache.hadoop.examples.KeyArrayValue;
import org.apache.hadoop.examples.MatrixData;


public class HeatTransfer {

  public static class TokenizerMapper 
    extends Mapper<IntWritable, KeyArrayValue, IntWritable, KeyArrayValue>{
    
    public void map(IntWritable key, KeyArrayValue value, Context context
                            ) throws IOException, InterruptedException {
          int myKey;
          myKey = key.get();
          //System.out.println("Soy un map con key: " + key.toString() + " \n");
          value.setKey(myKey);

          context.write(new IntWritable(myKey - 1),value);
          context.write(new IntWritable(myKey),value);
          context.write(new IntWritable(myKey + 1),value);
        }
  }
  
  public static class IntSumReducer 
       extends Reducer<IntWritable, KeyArrayValue, IntWritable, KeyArrayValue> {

    private IntWritable result = new IntWritable();

    public void reduce(IntWritable key, Iterable<KeyArrayValue> values, 
                       Context context) throws IOException, InterruptedException {
      
	//System.out.println("Soy un reduce con key: "+key.get()+"\n");

      KeyArrayValue fresult = new KeyArrayValue();
      fresult.setKey(key.get());
      FloatArrayWritable result = new FloatArrayWritable();
      FloatWritable[] FloatArray = new FloatWritable[MatrixData.Width()];  
      FloatWritable[] current=null, next=null, previous= null;

      //these keys are generated, check some way to filter them somewhere else
      if(key.get()<0||key.get()>(MatrixData.Height()-1)) 
        return;

      //Handle first and last "cold" boundaries
      if(key.get()==0){

           for(int i=0;i<MatrixData.Width();i++){
              FloatArray[i] = new FloatWritable(MatrixData.InitialTemp());
           }
         
           previous = FloatArray;
      }

      if(key.get()==MatrixData.Height()-1){

           for(int i=0;i<MatrixData.Width();i++){
              FloatArray[i] = new FloatWritable(MatrixData.InitialTemp());
           }
         
           next = FloatArray;
      }

     //Get the values       
     for(KeyArrayValue kav : values) {
//           System.out.println("Inside");
        
         if(kav.getKey()==key.get())
           current = (FloatWritable[])kav.toArray();

         if(kav.getKey()==key.get()+1)
           next = (FloatWritable[])kav.toArray();

         if(kav.getKey()==key.get()-1)
           previous = (FloatWritable[])kav.toArray();
      }

      //calculate the result
      float res;   
      //left boundary, question: divide by 2 or 4 in the boundary??
      res = previous[0].get() + current[1].get() + next[0].get();
      FloatArray[0] = new FloatWritable(res/4);
     
      //middle elements
      for(int i=1; i<(MatrixData.Width()-1);i++){
         res = previous[i].get() + current[i-1].get() + current[i+1].get() + next[i].get();
 	 FloatArray[i] = new FloatWritable(res/4);
      }

      //right boundary, question: divide by 2 or 4 in the boundary??
      int zBasedWidth = MatrixData.Width()-1;
      res = previous[zBasedWidth].get() + current[zBasedWidth-1].get() + next[zBasedWidth].get();
      //use setters! FloatArray[999].set(res/4);		
      FloatArray[zBasedWidth] = new FloatWritable(res/4);
      
      //Set heat source

           if(key.get()==MatrixData.HeatSourceY())
               FloatArray[MatrixData.HeatSourceX()].set(MatrixData.HeatSourceTemperature());

      result.set(FloatArray);
      fresult.setArray(result);
 
      context.write(key, fresult);

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
    job.setMapOutputValueClass(KeyArrayValue.class);    
    job.setMapOutputKeyClass(IntWritable.class);    

    //reduce output
    job.setOutputKeyClass(IntWritable.class);
    job.setOutputValueClass(KeyArrayValue.class);
    SequenceFileInputFormat.addInputPath(job, new Path(otherArgs[0]));
    FileOutputFormat.setOutputPath(job, new Path(otherArgs[1]));

    //set job's output format
    job.setOutputFormatClass(SequenceFileOutputFormat.class);
    //System.exit(job.waitForCompletion(true) ? 0 : 1);
    int status = job.waitForCompletion(true) ? 0 : 1;

    long end = System.currentTimeMillis();
    System.out.println("Execution time was "+(end-start)+" ms.");
    System.exit(status);	
   
  }
}

