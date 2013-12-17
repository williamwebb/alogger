package com.jug6ernaut.android.utilites;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class CopyUtils{

	 public static boolean copy(String srFile, String dtFile){
		 File f1 = new File(srFile);
	     File f2 = new File(dtFile);
	     return copy(f1,f2);
	 }
	
  public static boolean copy(File srFile, File dtFile){
    try{
    	
      InputStream in = new FileInputStream(srFile);
      
      //For Append the file.
//      OutputStream out = new FileOutputStream(f2,true);

      //For Overwrite the file.
      OutputStream out = new FileOutputStream(dtFile);

      byte[] buf = new byte[1024];
      int len;
      while ((len = in.read(buf)) > 0){
        out.write(buf, 0, len);
      }
      in.close();
      out.close();
      return true;
    }
    catch(FileNotFoundException ex){
    	ex.printStackTrace();
        return false;
    }
    catch(IOException e){
    	return false;  
    }
  }
  
  public static void copyStream(InputStream in, OutputStream out) throws IOException {
	    byte[] buffer = new byte[1024];
	    int read;
	    while((read = in.read(buffer)) != -1){
	      out.write(buffer, 0, read);
	    }
	}

}