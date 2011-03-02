
package org.apache.hadoop.examples;

import java.awt.*;
import java.awt.font.*;
import java.awt.geom.*;
import javax.swing.*;

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
import org.apache.hadoop.util.ReflectionUtils;

import org.apache.hadoop.examples.MatrixData;

public class GraphingData extends JPanel {

    //position of the window
    public static final int xloc = 1200;
    public static final int yloc = 200;


    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D)g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2.setPaint(Color.green.darker());

        String uri = "part-r-00000";
        Configuration conf = new Configuration();
        SequenceFile.Reader reader = null;

        try {
            FileSystem fs = FileSystem.get(URI.create(uri), conf);
            Path path = new Path(uri);

            reader = new SequenceFile.Reader(fs, path, conf);
            IntWritable key = (IntWritable) ReflectionUtils.newInstance(reader.getKeyClass(), conf);
            FloatWritable value = (FloatWritable)ReflectionUtils.newInstance(reader.getValueClass(), conf);
            long position = reader.getPosition();


            while (reader.next(key, value)) {
               String syncSeen = reader.syncSeen() ? "*" : "";
            
               if(value.get()>0.0f){
                  g2.setPaint(getColor(value.get()));
                  g2.drawRect(key.get()/MatrixData.Length(), key.get()%MatrixData.Length(), 1,1);
                  System.out.println(key.get());
               }
            }
        }
        catch(java.io.IOException ioe)	{ System.out.println("I/O Problem!");}
        
        finally {
           IOUtils.closeStream(reader);
        }

        System.out.println("Done \n");
}

    public static void main(String[] args) {
       JFrame f = new JFrame();
       f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
       f.add(new GraphingData());
       f.setSize(MatrixData.Width(), MatrixData.Length());
       f.setLocation(xloc, yloc);
       f.setVisible(true);
    }

    public static Color getColor(float value) {
   
       int sf = 100;

       if(value>3.0f)
          return Color.red.darker();
       if(value>2.0f)
          return Color.red;
       if(value>1.0f)
          return Color.orange.darker();
       if(value>0.5f)
          return Color.orange;
       if(value>0.25f)
          return Color.yellow.darker();
       if(value>0.125f/sf)
          return Color.yellow;
       if(value>0.060f/sf)
          return Color.green.darker();
       if(value>0.030f/sf)
          return Color.green;
       if(value>0.015f/sf)
          return Color.blue.darker();
       if(value>0.005f/sf)
          return Color.blue;

       return Color.blue;

    }

}
