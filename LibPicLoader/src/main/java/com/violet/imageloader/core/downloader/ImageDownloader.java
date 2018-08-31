package com.violet.imageloader.core.downloader;

import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;

/**
 * Created by kan212 on 2018/8/29.
 * 图片下载接口
 */

public interface ImageDownloader {

    /**
     * 表示通过 uri 得到 InputStream。
     * 通过内部定义的枚举Scheme, 可以看出 UIL 支持哪些图片来源。
     *
     * @param imageUri
     * @param extra
     * @return
     * @throws IOException
     */
    InputStream getStream(String imageUri, Object extra) throws IOException;

    public enum Scheme {
        HTTP("http"), HTTPS("https"), FILE("file"), CONTENT("content"), ASSETS("assets"), DRAWABLE("drawable"), UNKNOWN("");

        private String scheme;
        private String uriPrefix;

        Scheme(String scheme) {
            this.scheme = scheme;
            uriPrefix = scheme + "://";
        }

        public static Scheme ofUri(String uri) {
            if (uri != null) {
                for (Scheme s : values()) {
                    if (s.belongsTo(uri)) {
                        return s;
                    }
                }
            }
            return UNKNOWN;
        }

        private boolean belongsTo(String uri) {
            return uri.toLowerCase(Locale.US).startsWith(uriPrefix);
        }

        /** Appends scheme to incoming path */
        public String wrap(String path) {
            return uriPrefix + path;
        }

        /** Removed scheme part ("scheme://") from incoming URI */
        public String crop(String uri) {
            if (!belongsTo(uri)) {
                throw new IllegalArgumentException(String.format("URI [%1$s] doesn't have expected scheme [%2$s]", uri, scheme));
            }
            return uri.substring(uriPrefix.length());
        }
    }

}
