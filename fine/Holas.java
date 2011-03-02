import java.io.*;
public class Holas {
  public static void main(String[] args) throws Exception {

    long end = System.currentTimeMillis();
    long start = end/2;
    try{
         // Create file 
         FileWriter fstream = new FileWriter("Times.txt",true);
         BufferedWriter out = new BufferedWriter(fstream);
         //out.write(start-end + "ms \n");
         out.write("hi dude!");
         //Close the output stream
         out.close();
        }catch (Exception e){//Catch exception if any
           System.err.println("Error: " + e.getMessage());
        }


    }
}
