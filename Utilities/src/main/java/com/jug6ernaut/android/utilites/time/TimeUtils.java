package com.jug6ernaut.android.utilites.time;

import java.text.SimpleDateFormat;
import java.util.concurrent.TimeUnit;

/**
 * Created with IntelliJ IDEA.
 * User: williamwebb
 * Date: 10/3/13
 * Time: 6:00 PM
 */
public class TimeUtils {

    private static final SimpleDateFormat dateFormatter = new SimpleDateFormat("dd/MM/yyyy");
    private static final SimpleDateFormat monthYearFormater = new SimpleDateFormat("MM/yyyy");
    private static final SimpleDateFormat timeFormatter = new SimpleDateFormat("hh:mm:ss");
    private static final SimpleDateFormat datetimeFormatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
    private static final SimpleDateFormat datetimeFormatterNoSecond = new SimpleDateFormat("dd/MM/yyyy HH:mm");
    private static final SimpleDateFormat hourMinuteFormater = new SimpleDateFormat("HH:mm");
    private static final SimpleDateFormat timeStampFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");


    public static String milliToString(long milliSeconds){
        //return ((milli / 1000) % 60) + ":" + ((milli) % 1000);
        return String.format("%d:%02d:%03d",
                ((int)((milliSeconds / (1000*60)) % 60)), // minutes
                ((int)(((milliSeconds / 1000) % 60))),    // seconds
                (int)((milliSeconds) % 1000));            // milli
    }

    public static long convert(TimeUnit from,long source, TimeUnit to){
        return from.convert(source,to);
    }
}
