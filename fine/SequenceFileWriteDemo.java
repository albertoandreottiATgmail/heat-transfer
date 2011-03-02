package org.apache.hadoop.examples;

import java.io.*;
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
     FloatWritable value = new FloatWritable(0.0f);

     SequenceFile.Writer writer = null;
     try {
       writer = SequenceFile.createWriter(fs, conf, path, key.getClass(), value.getClass());

     int step = MatrixData.LinearSize()/10;
     int limit = step;
     for (int i = 0; i <= MatrixData.LinearSize(); i++) {
        key.set(i);
        if(i>limit){
             System.out.println("*");
             limit +=step;
        }
          if(i==MatrixData.HeatSourceLinearPos()) {
            writer.append(key, new FloatWritable(MatrixData.HeatSourceTemperature()));
            continue;
          }

        writer.append(key, value);

      }
    } finally {
      IOUtils.closeStream(writer);
    }
  }
}


