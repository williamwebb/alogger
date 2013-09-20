package com.jug6ernaut.android.logging;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;
 
public class JSONFormatter extends Formatter {
	
	/**
	 * A Custom format implementation that is designed for brevity.
	 */
	public String format(LogRecord record) {
		return JSONLogWriter.toJSONString(record);		
	}

 
}