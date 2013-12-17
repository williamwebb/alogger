package com.jug6ernaut.android.utilites;

import android.content.Context;
import android.content.res.AssetManager;

import java.io.*;

/**
 * Created with IntelliJ IDEA.
 * User: williamwebb
 * Date: 6/29/13
 * Time: 10:02 PM
 */
public class Assets {

    public static void extract(Context context) {
        String source = "";
        String destination = context.getFilesDir().toString();
        new File(destination).mkdirs();
        copyFolder(context.getAssets(),source,destination);
    }

    private static void copyFolder(AssetManager am, String sourceFolder, String destinationFolder) {
        // "Name" is the name of your folder!
        String[] files = null;

        try {
            files = am.list(sourceFolder);
        } catch (IOException e) {
            System.err.println("Failed to list base folder: " + sourceFolder);
            e.printStackTrace();
            return;
        }

        // Analyzing all file on assets subfolder
        for (String filename : files) {

            String sourceFile,destinationFile;

            if(sourceFolder.length()==0)sourceFile = filename;
            else sourceFile = sourceFolder + "/" + filename;
            destinationFile = destinationFolder + "/" + filename;

            File destFile = new File(destinationFile);
            if(destFile.exists())continue;
            boolean isDir = false;

            try{
                am.open(sourceFile);
            }catch (FileNotFoundException fnfe){
                isDir=true;
            } catch (IOException e) {
                isDir=true;
            }

            if(isDir){ // is a folder
                destFile.mkdirs();
                copyFolder(am,sourceFile, destinationFile);
            } else { // is a file

                InputStream in = null;
                OutputStream out = null;
                try {

                    in = am.open(sourceFile);
                    out = new FileOutputStream(destinationFile);
                    copyFile(in, out);
                    in.close();
                    in = null;
                    out.flush();
                    out.close();
                    out = null;
                } catch (IOException e) {
                    System.err.println("Failed to copy: " + sourceFile);
                    e.printStackTrace();
                }
            }
        }
    }

    // Method used by copyAssets() on purpose to copy a file.
    private static void copyFile(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[1024];
        int read;
        while ((read = in.read(buffer)) != -1) {
            out.write(buffer, 0, read);
        }
    }
}
