package org.apache.hadoop.examples;
import org.apache.hadoop.io.*;
import java.io.*;

public class FloatArrayWritable extends ArrayWritable
{
    int len;
    public FloatArrayWritable() {
        super(FloatWritable.class);
    }
    public FloatArrayWritable(LongWritable[] values) {
        super(FloatWritable.class, values);
    }
	
    public FloatArrayWritable(int length) {
        super(FloatWritable.class);
        this.len=length;
	FloatWritable[] FloatArray = new FloatWritable[length];  
        for(int i = 0; i<length; i++){
            FloatArray[i] = new FloatWritable(0f);
        }
        this.set(FloatArray);
    }
	
    public float floatAt(int i) {
        return ((FloatWritable)(this.get())[i]).get();
    }
    
    public void set(int i, float val) {
        ((FloatWritable)(this.get())[i]).set(val);
    }
    public void setHeatSource(float heat, int pos){

        //TODO:check pos
        FloatWritable[] FloatArray = new FloatWritable[len]; 
        for(int i = 0; i<len; i++){
            FloatArray[i] = new FloatWritable(0f);
        }
        if(pos>len) return;
        FloatArray[pos]= new FloatWritable(heat);
        this.set(FloatArray);
  }


    public int compareTo(Object o) {
        return 1;
    } 
}

