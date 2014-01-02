package com.jug6ernaut.android.logging;

import com.jug6ernaut.android.logging.ALogger.LogLevel;

import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: williamwebb
 * Date: 2/16/13
 * Time: 10:03 AM
 */
public abstract class Logger implements Serializable {

    public static boolean FORCE_LOGGING = false;
    private static ALogger rootLogger = null;
    private String name = "";

    protected Logger(String name){
        this.name = name;
    }

    public static synchronized Logger getLogger(String name) {
        return get(name);
    }

    public static synchronized Logger getLogger(Class<?> clazz) {
        return get(clazz.getName());
    }

    private static Logger get(String name){
        if (rootLogger == null){
            rootLogger = ALogger.getRootLogger();
        }

        if(rootLogger.BuildConfigDebug  || FORCE_LOGGING)
            return new DebugLogger(name);
        else
            return new ProdLogger(name);
    }

    public void trace(Object message) {
        log(message, LogLevel.TRACE);
    }

    public void trace(Object message, Throwable t) {
        log(message, t, LogLevel.TRACE);
    }

    public void debug(Object message) {
        log(message, LogLevel.DEBUG);
    }

    public void debug(Object message, Throwable t) {
        log(message, t, LogLevel.DEBUG);
    }

    public void info(Object message) {
        log(message, LogLevel.INFO);
    }

    public void info(Object message, Throwable t) {
        log(message, t, LogLevel.INFO);
    }

    public void warn(Object message) {
        log(message, LogLevel.WARN);
    }

    public void warn(Object message, Throwable t) {
        log(message, t, LogLevel.WARN);
    }
    public void error(Object message) {
        log(message, LogLevel.ERROR);
    }

    public void error(Object message, Throwable t) {
        log(message, t, LogLevel.ERROR);
    }

    public void fatal(Object message) {
        log(message, LogLevel.FATAL);
    }

    public void fatal(Object message, Throwable t) {
        log(message, t, LogLevel.FATAL);
    }

    private void log(Object message, LogLevel logLevel) {
        rootLogger.log(name + ":" + message,logLevel);
    }

    private void log(Object message, Throwable t, LogLevel logLevel) {
        rootLogger.log(name + ":" + message, t, logLevel);
    }

}
