package com.violet.imageloader.core.downloader;

import android.annotation.TargetApi;
import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.webkit.MimeTypeMap;

import com.violet.imageloader.core.assist.ContentLengthInputStream;
import com.violet.imageloader.utils.IoUtils;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by kan212 on 2018/8/29.
 * ImageDownloader的具体实现类。得到上面各种Scheme对应的图片 InputStream。
 */

public class BaseImageDownloader implements ImageDownloader {

    /**
     * {@value}
     */
    public static final int DEFAULT_HTTP_CONNECT_TIMEOUT = 5 * 1000; // milliseconds
    /**
     * {@value}
     */
    public static final int DEFAULT_HTTP_READ_TIMEOUT = 20 * 1000; // milliseconds

    /**
     * {@value}
     */
    protected static final int BUFFER_SIZE = 32 * 1024; // 32 Kb
    /**
     * {@value}
     */
    protected static final String ALLOWED_URI_CHARS = "@#&=*+-_.,:!?()/~'%";

    protected static final int MAX_REDIRECT_COUNT = 5;

    protected static final String CONTENT_CONTACTS_URI_PREFIX = "content://com.android.contacts/";

    private static final String ERROR_UNSUPPORTED_SCHEME = "UIL doesn't support scheme(protocol) by default [%s]. " + "You should implement this support yourself (BaseImageDownloader.getStreamFromOtherSource(...))";

    protected final Context context;
    protected final int connectTimeout;
    protected final int readTimeout;


    public BaseImageDownloader(Context context) {
        this(context, DEFAULT_HTTP_CONNECT_TIMEOUT, DEFAULT_HTTP_READ_TIMEOUT);
    }

    public BaseImageDownloader(Context context, int connectTimeout, int readTimeout) {
        this.context = context.getApplicationContext();
        this.connectTimeout = connectTimeout;
        this.readTimeout = readTimeout;
    }

    @Override
    public InputStream getStream(String imageUri, Object extra) throws IOException {
        switch (Scheme.ofUri(imageUri)) {
            case HTTPS:
            case HTTP:
                return getStreamFromNetwork(imageUri, extra);
            case FILE:
                return getStreamFromFile(imageUri, extra);
            case CONTENT:
                return getStreamFromContent(imageUri, extra);
            case ASSETS:
                return getStreamFromAssets(imageUri, extra);
            case DRAWABLE:
                return getStreamFromDrawable(imageUri, extra);
            case UNKNOWN:
            default:
                return getStreamFromOtherSource(imageUri, extra);
        }
    }


    private InputStream getStreamFromFile(String imageUri, Object extra) throws IOException {
        String filePath = Scheme.FILE.crop(imageUri);
        if (isVideoFileUri(imageUri)) {
            return getVideoThumbnailStream(filePath);
        } else {
            BufferedInputStream imageStream = new BufferedInputStream(new FileInputStream(filePath), BUFFER_SIZE);
            return new ContentLengthInputStream(imageStream, (int) new File(filePath).length());
        }
    }

    protected InputStream getStreamFromContent(String imageUri, Object extra) throws FileNotFoundException {
        ContentResolver res = context.getContentResolver();

        Uri uri = Uri.parse(imageUri);
        if (isVideoContentUri(uri)) { // video thumbnail
            Long origId = Long.valueOf(uri.getLastPathSegment());
            Bitmap bitmap = MediaStore.Video.Thumbnails
                    .getThumbnail(res, origId, MediaStore.Images.Thumbnails.MINI_KIND, null);
            if (bitmap != null) {
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 0, bos);
                return new ByteArrayInputStream(bos.toByteArray());
            }
        } else if (imageUri.startsWith(CONTENT_CONTACTS_URI_PREFIX)) { // contacts photo
            return getContactPhotoStream(uri);
        }

        return res.openInputStream(uri);
    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    protected InputStream getContactPhotoStream(Uri uri) {
        ContentResolver res = context.getContentResolver();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            return ContactsContract.Contacts.openContactPhotoInputStream(res, uri, true);
        } else {
            return ContactsContract.Contacts.openContactPhotoInputStream(res, uri);
        }
    }

    /**
     * Retrieves {@link InputStream} of image by URI (image is located in assets of application).
     *
     * @param imageUri Image URI
     * @param extra    Auxiliary object which was passed to ; can be null
     * @return {@link InputStream} of image
     * @throws IOException if some I/O error occurs file reading
     */
    protected InputStream getStreamFromAssets(String imageUri, Object extra) throws IOException {
        String filePath = Scheme.ASSETS.crop(imageUri);
        return context.getAssets().open(filePath);
    }

    /**
     * Retrieves {@link InputStream} of image by URI (image is located in drawable resources of application).
     *
     * @param imageUri Image URI
     * @param extra    Auxiliary object which was passed to  can be null
     * @return {@link InputStream} of image
     */
    protected InputStream getStreamFromDrawable(String imageUri, Object extra) {
        String drawableIdString = Scheme.DRAWABLE.crop(imageUri);
        int drawableId = Integer.parseInt(drawableIdString);
        return context.getResources().openRawResource(drawableId);
    }

    protected InputStream getStreamFromOtherSource(String imageUri, Object extra) throws IOException {
        throw new UnsupportedOperationException(String.format(ERROR_UNSUPPORTED_SCHEME, imageUri));
    }

    @TargetApi(Build.VERSION_CODES.FROYO)
    private InputStream getVideoThumbnailStream(String filePath) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO) {
            Bitmap bitmap = ThumbnailUtils
                    .createVideoThumbnail(filePath, MediaStore.Images.Thumbnails.FULL_SCREEN_KIND);
            if (bitmap != null) {
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 0, bos);
                return new ByteArrayInputStream(bos.toByteArray());
            }
        }
        return null;
    }


    private boolean isVideoFileUri(String uri) {
        String extension = MimeTypeMap.getFileExtensionFromUrl(uri);
        String mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
        return mimeType != null && mimeType.startsWith("video/");
    }

    /**
     * 通过HttpURLConnection从网络获取图片的InputStream。支持 response code 为 3xx 的重定向。
     * 在发生异常时会调用conn.getErrorStream()继续读取 Error Stream，这是为了利于网络连接回收及复用。
     * 但有意思的是在 Froyo(2.2) 之前，HttpURLConnection 有个重大 Bug，调用 close() 函数会影响连接池，
     * 导致连接复用失效，不少库通过在 2.3 之前使用 AndroidHttpClient 解决这个问题。
     *
     * @param imageUri
     * @param extra
     * @return
     */
    private InputStream getStreamFromNetwork(String imageUri, Object extra) throws IOException {
        HttpURLConnection conn = createConnection(imageUri, extra);
        int redirectCount = 0;
        while (conn.getResponseCode() / 100 == 3 && redirectCount < MAX_REDIRECT_COUNT) {
            //获取 重定向地址
            conn = createConnection(conn.getHeaderField("Location"), extra);
            redirectCount++;
        }
        InputStream inputStream;
        try {
            inputStream = conn.getInputStream();
        } catch (IOException ex) {
            IoUtils.readAndCloseStream(conn.getErrorStream());
            throw ex;
        }
        if (!shouldBeProcessed(conn)) {
            IoUtils.closeSilently(inputStream);
            throw new IOException("Image request failed with response code " + conn.getResponseCode());
        }
        return new ContentLengthInputStream(new BufferedInputStream(inputStream, BUFFER_SIZE), conn.getContentLength());
    }

    /**
     * 确定是不是成功
     *
     * @param conn
     * @return
     */
    private boolean shouldBeProcessed(HttpURLConnection conn) throws IOException {
        return conn.getResponseCode() == 200;
    }

    private boolean isVideoContentUri(Uri uri) {
        String mimeType = context.getContentResolver().getType(uri);
        return mimeType != null && mimeType.startsWith("video/");
    }

    /**
     * 获取 HttpURLConnection
     *
     * @param imageUri
     * @param extra
     * @return
     */
    protected HttpURLConnection createConnection(String imageUri, Object extra) throws IOException {
        String encodedUrl = Uri.encode(imageUri, ALLOWED_URI_CHARS);
        HttpURLConnection conn = (HttpURLConnection) new URL(encodedUrl).openConnection();
        conn.setConnectTimeout(connectTimeout);
        conn.setReadTimeout(readTimeout);
        return conn;
    }
}
