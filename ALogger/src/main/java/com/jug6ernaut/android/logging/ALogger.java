
package com.jug6ernaut.android.logging;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import java.io.*;
import java.lang.Thread.UncaughtExceptionHandler;
import java.util.ArrayList;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.LogRecord;

public class ALogger implements Serializable {

    private static final long serialVersionUID = -5853120689910966036L;
    private static java.util.logging.Logger logger = null;
    private static FileHandler handler = null;
    private static String logFilePath = "";
    private static String mTag = "";
    static ALogger alogger = null;

    private ALogger(){}

    public enum LogLevel {
        TRACE,
        DEBUG,
        INFO,
        WARN,
        ERROR,
        FATAL
    }

    public static void init(Application application, String tag, boolean toLogCat) {
        mTag = tag;

        if (alogger == null) {
            alogger = new ALogger(application, tag);
        }

    }

    protected static synchronized ALogger getRootLogger() {

        if (alogger == null)
            throw new RuntimeException("RootLogger not initialized! Please run ALogger.init() in your root Application.");

        return alogger;
    }

    private ALogger(Context context, String tag) {
        InitializeLogger(context, tag);
    }

    private static void InitializeLogger(Context context, final String tag) {

        logger = java.util.logging.Logger.getLogger(tag);

        logFilePath = context.getFilesDir().getPath().toString() + "/" + tag + ".log";

        handler = getHandler(logFilePath);

        // Add to the desired logger
        logger.setUseParentHandlers(false);
        logger.setLevel(Level.ALL);
        logger.addHandler(handler);
    }

    private static FileHandler getHandler(String filePath) {

        FileHandler handle = null;

        int sizeLimit = 100000;// 10kB
        int fileLimit = 1;

        try {
            handle = new FileHandler(filePath, sizeLimit, fileLimit, true) {
                @Override
                public void publish(LogRecord record) {
                    super.publish(record);

                    logToListeners(LogEntry.fromLogRecord(record));
                }
            };

            handle.setFormatter(new JSONFormatter());
        } catch (IOException e) {
            Log.e(mTag, "Error in getHandler", e);
        }

        return handle;
    }

    public void setHandleUncaughtException(boolean enable) {
        if (enable) {
            dExceptionHandler = Thread.getDefaultUncaughtExceptionHandler();
            Thread.setDefaultUncaughtExceptionHandler(mExceptionHandler);
        } else {
            if (dExceptionHandler != null) {
                Thread.setDefaultUncaughtExceptionHandler(dExceptionHandler);
            }
        }
    }

    UncaughtExceptionHandler dExceptionHandler = null;
    UncaughtExceptionHandler mExceptionHandler = new UncaughtExceptionHandler() {

        @Override
        public void uncaughtException(Thread thread, Throwable ex) {
            alogger.log("UncaughtException", ex,LogLevel.FATAL);
            dExceptionHandler.uncaughtException(thread, ex);
        }

    };

    public File getLogFile() {
        return new File(logFilePath);
    }

    public FileOutputStream getLogFileStream(Context context) {
        FileOutputStream fos = null;
        try {
            fos = context.openFileOutput(logFilePath, Context.MODE_WORLD_READABLE);
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return fos;
    }

    public StringBuilder getLogAsString() {
        StringBuilder sb = new StringBuilder();

        try {
            FileInputStream fstream = new FileInputStream(logFilePath);
            DataInputStream in = new DataInputStream(fstream);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));

            String strLine;

            while ((strLine = br.readLine()) != null) {
                sb.append(strLine);
                sb.append("\n");
            }

            in.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return sb;

    }

    public boolean clearLogFile() {
        File logFile = new File(logFilePath);
        boolean success = logFile.delete();

        if (success) {
            logger.removeHandler(handler);
            logger = java.util.logging.Logger.getLogger(mTag);
            handler = getHandler(logFilePath);
            logger.setUseParentHandlers(false);
            logger.addHandler(handler);
        }

        return success;
    }

    public interface OnLogListener {
        public void onLog(LogEntry log);
    }

    private static ArrayList<OnLogListener> listeners = new ArrayList<OnLogListener>();

    private static void logToListeners(LogEntry log) {
        for (OnLogListener listener : listeners) {
            if (listener != null) {
                listener.onLog(log);
            } else {
                synchronized (listeners) {
                    listeners.remove(listener);
                }
            }
        }
    }

    public void setOnLogListener(OnLogListener listener) {
        if (!listeners.contains(listener)) {
            listeners.add(listener);
        }
    }

    public void removeOnLogListener(OnLogListener listener) {
        if (listeners.contains(listener)) {
            listeners.remove(listener);
        }
    }

    private String buildLog(String msg, boolean showTrace) {
        return ((showTrace) ? (getMethod() + ":" + msg) : msg);
    }

    private String getMethod() {

        StackTraceElement[] elements = new Exception().getStackTrace();
        String methodName = "";

        for (StackTraceElement element : elements) {
            if (element.getClassName().equals(ALogger.class.getCanonicalName())) {
                continue;
            } else {

                methodName = element.getClassName().substring(
                        element.getClassName().lastIndexOf('.') + 1,
                        element.getClassName().length())
                        + ":" + element.getMethodName();
                break;
            }
        }
        return methodName;
    }

    private String getClassString() {

        StackTraceElement[] elements = new Exception().getStackTrace();

        String class1 = "";
        String class2 = "";

        for (StackTraceElement element : elements) {
            if (element.getClassName().equals(ALogger.class)) {
                continue;
            } else {
                if (class2.equals("")) {
                    class2 = element.getMethodName();
                    continue;
                } else if (class1.equals("")) {
                    class1 = element.getClassName().substring(
                            element.getClassName().lastIndexOf('.') + 1,
                            element.getClassName().length());
                    class1 += "." + element.getMethodName();

                } else
                    break;
            }
        }

        return class1 + "." + class2;
    }


    public void log(Object message, LogLevel logLevel) {
        String msg = String.valueOf(message);
        switch (logLevel) {
            case TRACE:
                Log.v(mTag, msg);
                logger.log(Level.FINER, msg);
                break;
            case DEBUG:
                Log.d(mTag, msg);
                logger.log(Level.FINE, msg);
                break;
            case INFO:
                Log.i(mTag, msg);
                logger.log(Level.INFO, msg);
                break;
            case WARN:
                Log.w(mTag, msg);
                logger.log(Level.WARNING, msg);
                break;
            case ERROR:
                Log.e(mTag, msg);
                logger.log(Level.SEVERE, msg);
                break;
            case FATAL:
                Log.wtf(mTag, msg);
                logger.log(Level.SEVERE, msg);
                break;
        }
    }

    public void log(Object message, Throwable t, LogLevel logLevel) {
        String msg = String.valueOf(message);
        switch (logLevel) {
            case TRACE:
                Log.v(mTag, msg, t);
                logger.log(Level.FINER, msg);
                logger.log(Level.SEVERE, getStackTrace(t));
                break;
            case DEBUG:
                Log.d(mTag, msg, t);
                logger.log(Level.FINE, msg);
                logger.log(Level.FINE, getStackTrace(t));
                break;
            case INFO:
                Log.i(mTag, msg, t);
                logger.log(Level.INFO, msg);
                logger.log(Level.INFO, getStackTrace(t));
                break;
            case WARN:
                Log.w(mTag, msg, t);
                logger.log(Level.WARNING, msg);
                logger.log(Level.WARNING, getStackTrace(t));
                break;
            case ERROR:
                Log.e(mTag, msg, t);
                logger.log(Level.SEVERE, msg);
                logger.log(Level.SEVERE, getStackTrace(t));
                break;
            case FATAL:
                Log.wtf(mTag, msg, t);
                logger.log(Level.SEVERE, msg);
                logger.log(Level.SEVERE, getStackTrace(t));
                break;
        }
    }

    private static String getStackTrace(Throwable throwable) {
        StringWriter sw = new StringWriter();
        throwable.printStackTrace(new PrintWriter(sw));
        return sw.toString();
    }

}
