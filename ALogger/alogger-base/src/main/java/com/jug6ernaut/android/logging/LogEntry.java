package com.jug6ernaut.android.logging;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.LogRecord;

public class LogEntry {

	public static String MESSAGE = "message";
	public static String LEVEL = "level";
	public static String WHEN = "when";

	private String message;
	private long when;
	private Level level;

	public LogEntry(Level level,long when, String message){
		this.setLevel(level);
		this.setWhen(when);
		this.setMessage(message);
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public long getWhen() {
		return when;
	}

	public void setWhen(long when) {
		this.when = when;
	}

	public Level getLevel() {
		return level;
	}

	public void setLevel(Level level) {
		this.level = level;
	}

	public String toString(){
		DateFormat format = new SimpleDateFormat("h:mm:ss:SSS");
		String sep = System.getProperty("line.separator");
		StringBuilder sb = new StringBuilder();

		sb.append("[T[");
		sb.append(format.format(new Date(when)));
		sb.append("]:");
		sb.append(level.getName());
		sb.append("]");
		sb.append(message);
		sb.append(sep);

		return sb.toString();
	}
/*
	public Spanned toColorString(){

		StringBuilder sb = new StringBuilder();

		sb.append(toString());

        LogLevel l = LogLevel.INFO;

		switch(level){
			case INFO:{
				sb.insert(0, "<font color=\"white\">");
				sb.append("</font>");
			}break;
			case Level.WARNING:{
				sb.insert(0, "<font color=\"yellow\">");
				sb.append("</font>");
			}break;
			case Level.SEVERE:{
				sb.insert(0, "<font color=\"red\">");
				sb.append("</font>");
			}break;
			case Level.ALL:{
				sb.insert(0, "<font color=\"white\">");
				sb.append("</font>");
			}break;
		}

		return Html.fromHtml(sb.toString());
	}
*/
	public static LogEntry fromLogRecord(LogRecord record){
		Level level;
		long when = 0;
		String message = "";

			when = record.getMillis();
			message = (record.getMessage()==null?"":record.getMessage());
        level = record.getLevel();

		return new LogEntry(level, when, message);
	}


}
