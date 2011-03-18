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
          key.set(myKey-1);
          context.write(key, value);
          key.set(myKey);
          context.write(key, value);
          key.set(myKey+1);
          context.write(key, value);
        }
  }
  
  public static class IntSumReducer 
       extends Reducer<IntWritable, KeyArrayValue, IntWritable, KeyArrayValue> {

    private IntWritable result = new IntWritable();

    public void reduce(IntWritable key, Iterable<KeyArrayValue> values, 
                       Context context) throws IOException, InterruptedException {
      
      KeyArrayValue fresult = new KeyArrayValue();
      fresult.setKey(key.get());
      FloatArrayWritable result = new FloatArrayWritable();
      FloatWritable[] FloatArray = new FloatWritable[MatrixData.Width()];  
      FloatWritable[] current=null, next=null, previous= null;
      FloatWritable intermediate = new FloatWritable(0);

      //Keys for which no output is produced
      if(key.get()<0||key.get()>(MatrixData.Height()-1)) 
        return;

     int zBasedWidth = MatrixData.Width()-1;
	 //Get the values       
     for(KeyArrayValue kav : values) {
	            
        //middle row
		if(kav.getKey()==key.get()){
			FloatArray[0]+= (FloatWritable[])kav.toArray()[0];
			FloatArray[zBasedWidth]+= (FloatWritable[])kav.toArray()[zBasedWidth-1];
		    for(int i=1; i<(MatrixData.Width()-1);i++){
				res = (FloatWritable[])kav.toArray()[i-1] + (FloatWritable[])kav.toArray()[i+1];
				intermediate.set(res);
				FloatArray[i] = intermediate;
		 		}
			}

		//row above and below
        if(kav.getKey()==key.get()+1 || kav.getKey()==key.get()-1) {
		    FloatArray[0]+= (FloatWritable[])kav.toArray()[0];
			FloatArray[zBasedWidth]+= (FloatWritable[])kav.toArray()[zBasedWidth];
			
            for(int i=1; i<(MatrixData.Width()-1);i++){
				res = (FloatWritable[])kav.toArray()[i];
				intermediate.set(res);
				FloatArray[i] = intermediate;
		 		}
			}
		}

	  
	  //Set heat source
      if(key.get()==MatrixData.HeatSourceY())
      FloatArray[MatrixData.HeatSourceX()].set(MatrixData.HeatSourceTemperature());
      

      //Make the division and write the result
      for(int i=0; i<(MatrixData.Width());i++)
			FloatArray[i] /= 4;

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

