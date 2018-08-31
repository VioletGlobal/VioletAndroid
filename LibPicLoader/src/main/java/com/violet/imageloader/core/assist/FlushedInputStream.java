package com.violet.imageloader.core.assist;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by kan212 on 2018/8/29.
 * 为了解决早期 Android 版本BitmapFactory.decodeStream(…)在慢网络情况下 decode image 异常的 Bug。
 * 主要通过重写FilterInputStream的 skip(long n) 函数解决，确保 skip(long n) 始终跳过了 n 个字节。
 * 如果返回结果即跳过的字节数小于 n，则不断循环直到 skip(long n) 跳过 n 字节或到达文件尾
 */

public class FlushedInputStream extends FilterInputStream {

    public FlushedInputStream(InputStream inputStream) {
        super(inputStream);
    }

    @Override
    public long skip(long n) throws IOException {
        long totalBytesSkipped = 0L;
        while (totalBytesSkipped < n) {
            long bytesSkipped = in.skip(n - totalBytesSkipped);
            if (bytesSkipped == 0L) {
                int by_te = read();
                if (by_te < 0) {
                    break; // we reached EOF
                } else {
                    bytesSkipped = 1; // we read one byte
                }
            }
            totalBytesSkipped += bytesSkipped;
        }
        return totalBytesSkipped;
    }
}
