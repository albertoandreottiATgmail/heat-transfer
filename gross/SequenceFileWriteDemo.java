package org.apache.hadoop.examples;

import java.io.*;
import org.apache.hadoop.examples.KeyArrayValue;
import java.util.StringTokenizer;
import java.net.URI;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.*;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.GenericOptionsParser;

import org.apache.hadoop.examples.MatrixData;

public class SequenceFileWriteDemo {

  public static void main(String[] args) throws IOException {
     
     //Write file in the local dir
     String uri = "/home/beto/mySeq"; 

     Configuration conf = new Configuration();
     FileSystem fs = FileSystem.get(URI.create(uri), conf);
     Path path = new Path(uri);

     IntWritable key = new IntWritable();

     //(key, default_value, size)
     KeyArrayValue kav = new KeyArrayValue(0, MatrixData.InitialTemp(), MatrixData.Width());

     SequenceFile.Writer writer = null;
     try {
       writer = SequenceFile.createWriter(fs, conf, path, key.getClass(), kav.getClass());
     int step = MatrixData.Height()/10;
     int limit = step; 
     
     for (int i = 0; i < MatrixData.Height(); i++) {
        key.set(i);
        kav.setKey(i);
        //Print progress indicator
        if(i>limit){
             System.out.println("*");
             limit +=step;
        }
        //Handle heatsource
        if(i==MatrixData.HeatSourceY()) 
                 { kav.setHeatSource(MatrixData.HeatSourceTemperature(), MatrixData.HeatSourceX());
                   writer.append(key, kav);
                   kav.setHeatSource(MatrixData.InitialTemp(), MatrixData.HeatSourceX());
                   continue;
                  }
        writer.append(key, kav);
        
      }
    } finally {
      IOUtils.closeStream(writer);
    }
  }
}


