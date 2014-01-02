package com.jug6ernaut.android.logging;

import android.util.Log;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.LogRecord;

public class JSONLogWriter {

	public static String toJSONString(LogRecord record){
		JSONObject json = new JSONObject();
		try {
            json.put(LogEntry.LEVEL,record.getLevel().getName());
			json.put(LogEntry.WHEN, record.getMillis());
			//json.put(JSONLog.CLASS, record.getSourceClassName() + "." + record.getSourceMethodName());
			String s = (record.getMessage()==null?"":record.getMessage());
			json.put(LogEntry.MESSAGE, s);
			
		} catch (JSONException e) {
			Log.e("LDN",record.toString(),e);
		}
			
		return json.toString() + System.getProperty("line.separator");
	}
	
	public static String toString(LogRecord record){
		DateFormat format = new SimpleDateFormat("h:mm:ss:SSS");
		String sep = System.getProperty("line.separator");
		StringBuilder sb = new StringBuilder();
		
		sb.append("[T[");
		sb.append(format.format(new Date(record.getMillis())));
		sb.append("]:");
		Level level = record.getLevel();
		if(level.equals(Level.INFO))sb.append("INFO");
		else if(level.equals(Level.WARNING))sb.append("WARNING");
		else if(level.equals(Level.SEVERE))sb.append("SEVERE");
		else sb.append("ALL");
		
		
		sb.append("]");
		sb.append(record.getMessage());
		sb.append(sep);
		
		return sb.toString();
	}
}
