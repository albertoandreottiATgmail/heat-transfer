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
    extends Mapper<IntWritable, FloatWritable, IntWritable, FloatWritable>{

    public void map(IntWritable linearPos, FloatWritable heat, Context context
                            ) throws IOException, InterruptedException {
       int myLinearPos = linearPos.get();
       
       //Distribute my value to the previous and the next
       linearPos.set(myLinearPos - 1);
       context.write(linearPos, heat);
       linearPos.set(myLinearPos + 1);
       context.write(linearPos, heat);

       //Distribute my value to the cells above and below
       linearPos.set(myLinearPos - MatrixData.Length());
       context.write(linearPos, heat);
       linearPos.set(myLinearPos + MatrixData.Length());
       context.write(linearPos, heat);

    }//end map
}

public static class IntSumReducer
       extends Reducer<IntWritable, FloatWritable, IntWritable, FloatWritable> {

    public void reduce(IntWritable linearPos, Iterable<FloatWritable> heats, 
                       Context context) throws IOException, InterruptedException {

       //Handle first and last "cold" boundaries
       if(linearPos.get()<0 || linearPos.get()>MatrixData.LinearSize()){
          return;
       }

       if(linearPos.get()==MatrixData.HeatSourceLinearPos()){
          context.write(linearPos, new FloatWritable(MatrixData.HeatSourceTemperature()));
          return;
       }
       
       float result = 0.0f;
       //Add all the values
       for(FloatWritable heat : fwValues) {
          result += heat.get();
       }
       
      context.write(linearPos, new FloatWritable(result/4) );

    }//end reduce
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
    FileOutputFormat.setOutputPath(job, new Path(otherArgs[1]));
    //set job's output format
    job.setOutputFormatClass(SequenceFileOutputFormat.class);

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

}//end main
}//end HeatTransfer

