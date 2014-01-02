package com.jug6ernaut.android.logging;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.logging.Level;

public class JSONLogReader {
	
	private static String message;
	//private static String clazz;
	private static long when;
	private static String level;

	public static LogEntry getJSONLog(String jsonString) throws JSONException{
		
		JSONObject json = new JSONObject(jsonString);
		level = json.getString(LogEntry.LEVEL);
		when = json.getLong(LogEntry.WHEN);
		//clazz = json.getString(JSONLog.CLASS);
		message = json.getString(LogEntry.MESSAGE);
		
		return new LogEntry(Level.parse(level), when, message);
	}
	
	public static ArrayList<LogEntry> getLogsFromFile(String filePath){
		ArrayList<LogEntry> logs = new ArrayList<LogEntry>();
		
		try{
			  FileInputStream fstream = new FileInputStream(filePath);
			  DataInputStream in = new DataInputStream(fstream);
			  BufferedReader br = new BufferedReader(new InputStreamReader(in));
			  Scanner scanner = new Scanner(br);
			  
			  
			  String s = "";
			  while(scanner.hasNext()){

				  s = scanner.nextLine();
				  				  
				  logs.add(getJSONLog(s));
			  }
	
			  fstream.close();
			  in.close();
			  br.close();
			  
		}catch (Exception e){e.printStackTrace();}
		
		
		return logs;
	}
}
