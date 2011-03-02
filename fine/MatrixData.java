
/*
Constant Data Manager for the matrix metadata such as size, number of elements, precision, etc.
*/

package org.apache.hadoop.examples;

public class MatrixData{

     static private int width = 10000;
     static private int length = 10000;

     //zero based 
     static private int sourceX = 350;
     static private int sourceY = 350;

     static private float temperature = 35;

     public static int Length(){
        return length;
     }
     
     public static int Width(){
        return width;
     }
     //zero based
     public static int LinearSize(){
        return length*width-1;
     }

     public static int HeatSourceX(){
        return sourceX;
     }

     public static int HeatSourceY(){
        return sourceY;
     }
     
     public static int HeatSourceLinearPos(){
        return (sourceY*width + sourceX);
     }

     public static float HeatSourceTemperature(){
        return temperature;
     }

}

