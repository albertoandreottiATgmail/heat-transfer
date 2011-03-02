
/*
Constant Data Manager for the matrix metadata such as size, number of elements, precision, etc.
*/

package org.apache.hadoop.examples;

public class MatrixData{

     static private int width = 12500;
     static private int height = 12500;

     //zero based 
     static private int sourceX = 35;
     static private int sourceY = 35;

     static private float temperature = 35;

     static private float initialTemp = 0.0f;

     public static int Height(){
        return height;
     }
     
     public static int Width(){
        return width;
     }
     //zero based
     public static int LinearSize(){
        return height*width-1;
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

     public static float InitialTemp(){
        return initialTemp;
     }
}

