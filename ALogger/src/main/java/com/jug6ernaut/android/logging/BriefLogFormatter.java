package com.jug6ernaut.android.logging;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;
 
public class BriefLogFormatter extends Formatter {
	
	private static final DateFormat format = new SimpleDateFormat("h:mm:ss");
	private static final String lineSep = System.getProperty("line.separator");
	
	/**
	 * A Custom format implementation that is designed for brevity.
	 */
	public String format(LogRecord record) {
		String s = record.getLevel() + "  :  "
	            + format.format(new Date( record.getMillis() )) + " -:- "
	            + record.getMessage() + lineSep;

		return s;		
	}

 
}