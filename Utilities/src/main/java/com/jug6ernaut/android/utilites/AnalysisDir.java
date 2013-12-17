package com.jug6ernaut.android.utilites;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class AnalysisDir {
	
	private static ArrayList<String> m_Data = null;
	static long totalSize = 0;
	static long sizeLimit = 5000000;
	
	private static boolean getFiles = false;
	private static boolean getFolders = false;
	
	public static ArrayList<String> init(String path){
		m_Data = new ArrayList<String>();
		totalSize = 0;
		return readDir(new File(path));
	}
	
	public static ArrayList<String> getFiles(String path){
		getFiles = true;
		getFolders = false;
		return init(path);
	}

	public static ArrayList<String> getFolders(String path){
		getFiles = false;
		getFolders = true;
		return init(path);
	}
	
	public static ArrayList<String> getAll(String path){
		getFiles = true;
		getFolders = true;
		return init(path);
	}
	
	
	
	private static ArrayList<String> readDir(File f)
    {   	
    	
    	if(f.isDirectory())
        {
    		if(getFolders)m_Data.add(f.getPath());

    		File[] files = null;
    		
    		if(f.canRead())
    		{
    			files = f.listFiles();
    		}
    		else{
    			List<String> SUfiles = SuperUser.executeSU("ls " + f.getPath());
    			
    			if(SUfiles!=null && SUfiles.size()>0){
    				
    				files = new File[SUfiles.size()];//create new array size of # of files

    				for(String file : SUfiles){
    					files[SUfiles.indexOf(file)]=new File(f.getPath() + "/" + file);
    				}
    			}
    		}
    		
    		if(files==null)files = new File[0];
    		
    		for(File file : files){
    			
    			readDir(file);
            	
    		}
        }
        else if(f.isFile())
        {
        	if(getFiles)m_Data.add(f.getPath());
        }
        else {}//error 
    
    	return m_Data;
    }
	
}
