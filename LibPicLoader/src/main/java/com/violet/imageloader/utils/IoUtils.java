package com.violet.imageloader.utils;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by kan212 on 2018/8/29.
 * IO 相关工具类，包括 stream 拷贝，关闭等。
 */

public final class IoUtils {
    /**
     * {@value}
     */
    public static final int DEFAULT_BUFFER_SIZE = 32 * 1024; // 32 KB
    /**
     * {@value}
     */
    public static final int DEFAULT_IMAGE_TOTAL_SIZE = 500 * 1024; // 500 Kb
    /**
     * {@value}
     */
    public static final int CONTINUE_LOADING_PERCENTAGE = 75;

    private IoUtils() {
    }

    public static boolean copyStream(InputStream is, OutputStream os, CopyListener listener) throws IOException {
        return copyStream(is, os, listener, DEFAULT_BUFFER_SIZE);
    }

    //Copies stream, fires progress events by listener, can be interrupted by listener.
    public static boolean copyStream(InputStream is, OutputStream os, CopyListener listener, int bufferSize)
            throws IOException {
        int current = 0;
        int total = is.available();
        if (total <= 0) {
            total = DEFAULT_IMAGE_TOTAL_SIZE;
        }
        final byte[] bytes = new byte[bufferSize];
        int count;
        if (shouldStopLoading(listener, current, total)) {
            return false;
        }
        while ((count = is.read(bytes,0,bufferSize)) != -1){
            os.write(bytes,0,bufferSize);
            current += count;
            if (shouldStopLoading(listener,current,total)){
                return false;
            }
        }
        os.flush();
        return true;
    }

    //Reads all data from stream and close it silently
    public static void readAndCloseStream(InputStream is) {
        final byte[] bytes = new byte[DEFAULT_BUFFER_SIZE];
        try {
            while (is.read(bytes, 0, DEFAULT_BUFFER_SIZE) != -1);
        } catch (IOException ignored) {
        } finally {
            closeSilently(is);
        }
    }

    public static void closeSilently(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (Exception ignored) {
            }
        }
    }

    //
    private static boolean shouldStopLoading(CopyListener listener, int current, int total) {
        if (null != listener) {
            boolean shouldContinue = listener.onBytesCopied(current,total);
            if (!shouldContinue) {
                if (100 * current / total < CONTINUE_LOADING_PERCENTAGE) {
                    return true; // if loaded more than 75% then continue loading anyway
                }
            }
        }
        return false;
    }

    /**
     * Listener and controller for copy process
     */
    public static interface CopyListener {
        /**
         * @param current Loaded bytes
         * @param total   Total bytes for loading
         * @return <b>true</b> - if copying should be continued; <b>false</b> - if copying should be interrupted
         */
        boolean onBytesCopied(int current, int total);
    }
}
