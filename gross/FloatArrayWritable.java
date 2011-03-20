package org.apache.hadoop.examples;
import org.apache.hadoop.io.*;
import java.io.*;

public class FloatArrayWritable extends ArrayWritable
{

    public FloatArrayWritable() {
        super(FloatWritable.class);
    }
    public FloatArrayWritable(LongWritable[] values) {
        super(FloatWritable.class, values);
    }
	
	public float floatAt(int index) {
        return ((this.get())[i]).get();
    }

    public int compareTo(Object o) {
        return 1;
    } 
}

