package org.apache.hadoop.examples;
import java.io.*;
import org.apache.hadoop.io.*;
import org.apache.hadoop.examples.FloatArrayWritable;

public class KeyArrayValue implements WritableComparable<KeyArrayValue> {
  private IntWritable key;
  private FloatArrayWritable faw;

  public KeyArrayValue() {
      key = new IntWritable(0);
      faw = new FloatArrayWritable();
  }

  public KeyArrayValue(int keyValue, float value, int size) {
      key = new IntWritable(keyValue); 

      FloatWritable[] FloatArray = new FloatWritable[size];  
      for(int i = 0; i<size; i++){
        FloatArray[i] = new FloatWritable(value);
      }
      
      //set 
      faw = new FloatArrayWritable();
      faw.set(FloatArray);
  }


  public void setHeatSource(float heat, int pos){
      FloatWritable[] fw = (FloatWritable[])faw.toArray();
      //TODO:check pos
      fw[pos].set(heat);
      faw.set(fw);
  }


  public void setKey(int keyValue) {
      key.set(keyValue);
  }

  public int getKey() {
      return key.get();
  }

  public Object toArray() {
      return faw.toArray();
  }

  public void setArray(FloatArrayWritable fawValue){
      faw = fawValue;
  }

  @Override
  public void write(DataOutput out) throws IOException {
    key.write(out);
    faw.write(out);
  }
  @Override
  public void readFields(DataInput in) throws IOException {
    key.readFields(in);
    faw.readFields(in);

  }
  @Override
  public int hashCode() {
    return key.hashCode() * 163 + faw.hashCode();
  }
  @Override
  public boolean equals(Object o) {
    if (o instanceof KeyArrayValue) {
      KeyArrayValue tp = (KeyArrayValue) o;
      return key.equals(tp.key) && faw.equals(tp.faw);
    }
    return false;
  }
  @Override
  public String toString() {
    return key + "\t" + faw;
  }
  @Override
  public int compareTo(KeyArrayValue tp) {
    int cmp = key.compareTo(tp.key);
    if (cmp != 0) {
      return cmp;
    }
    return faw.compareTo(tp.faw);
  }
}

