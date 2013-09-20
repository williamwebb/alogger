package com.jug6ernaut.android.logging;

/**
 * Created with IntelliJ IDEA.
 * User: williamwebb
 * Date: 8/28/13
 * Time: 10:10 PM
 */
public class ProdLogger extends Logger {

    protected ProdLogger(String name) {
        super(name);
    }

    @Override
    public void trace(Object message) {    }

    @Override
    public void trace(Object message, Throwable t) {    }

    @Override
    public void debug(Object message) {    }

    @Override
    public void debug(Object message, Throwable t) {    }

    @Override
    public void info(Object message) {    }

    @Override
    public void info(Object message, Throwable t) {    }

    @Override
    public void warn(Object message) {    }

    @Override
    public void warn(Object message, Throwable t) {    }

    @Override
    public void error(Object message) {    }

    @Override
    public void error(Object message, Throwable t) {    }

    @Override
    public void fatal(Object message) {    }

    @Override
    public void fatal(Object message, Throwable t) {    }

}
