package com.violet.lib.opensource.qr.zixin;

/**
 * Created by kan212 on 2018/7/9.
 */

public class ReaderException extends Exception{
    // disable stack traces when not running inside test units
    protected static final boolean isStackTrace =
            System.getProperty("surefire.test.class.path") != null;
    protected static final StackTraceElement[] NO_TRACE = new StackTraceElement[0];

    ReaderException() {
        // do nothing
    }

    // Prevent stack traces from being taken
    @Override
    public final synchronized Throwable fillInStackTrace() {
        return null;
    }
}
