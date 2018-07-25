package com.violet.lib.opensource.qr.zixin;

/**
 * Created by kan212 on 2018/7/9.
 */

public class FormatException extends ReaderException{
    private static final FormatException INSTANCE = new FormatException();
    static {
        INSTANCE.setStackTrace(NO_TRACE); // since it's meaningless
    }

    private FormatException() {
    }
}

