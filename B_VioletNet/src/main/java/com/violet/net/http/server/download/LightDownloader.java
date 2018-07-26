package com.violet.net.http.server.download;

import android.util.Log;

import com.violet.net.http.HttpManager;
import com.violet.net.http.IHttpManager;
import com.violet.net.http.cache.CacheMode;
import com.violet.net.http.callback.CommonCallback;
import com.violet.net.dispatcher.VtPriority;
import com.violet.net.http.model.Response;
import com.violet.net.http.request.GetRequest;
import com.violet.net.http.util.CommonUtil;
import com.violet.net.http.util.IOUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Set;

/**
 * Created by liuqun1 on 2018/3/6.
 */

public class LightDownloader {

    public static void downImage(final String url, Map<String, String> headers, final String saveDir,
                                 boolean onlyCache, final LightDownloadListener listener) {
        if (checkImage(url, saveDir, listener)) {
            return;
        }
        if (onlyCache) {
            if (listener != null) {
                listener.onError();
            }
            return;
        }

        final IHttpManager httpManager = HttpManager.getInstance();
        final GetRequest req = new GetRequest(url);
        req.cacheMode(CacheMode.NO_CACHE);
        req.setResponseClass(okhttp3.Response.class);
        req.priority(VtPriority.PRIORITY_MID);

        if (headers != null) {
            Set<Map.Entry<String, String>> entries = headers.entrySet();
            for (Map.Entry<String, String> entry : entries) {
                req.headers(entry.getKey(), entry.getValue());
            }
        }

        try {
            httpManager.execute(req, new CommonCallback<okhttp3.Response>() {

                @Override
                public void onSuccess(Response<okhttp3.Response> r) {
                    okhttp3.Response response = r.body();
                    InputStream is = null;
                    byte[] buf = new byte[2048];
                    int len = 0;
                    FileOutputStream fos = null;
                    try {
                        // 储存下载文件的目录
                        String savePath = getDirectory(saveDir);
                        String name = getNameFromUrl(url);
                        is = response.body().byteStream();
                        File file = new File(savePath, name + "." + System.currentTimeMillis());
                        fos = new FileOutputStream(file);
                        while ((len = is.read(buf)) != -1) {
                            fos.write(buf, 0, len);
                        }
                        fos.flush();
                        if (!file.renameTo(new File(savePath, name))) {
                            throw new RuntimeException(file.getName() + " rename failed");
                        }
                        String imgPath = file.getAbsolutePath();
                        // 下载完成
                        if (listener != null) {
                            listener.onSuccess(imgPath);
                        }
                    } catch (Exception e) {
                        if (Log.isLoggable(CommonUtil.TAG, Log.ERROR)) {
                            Log.e(CommonUtil.TAG, "Exception: ", e);
                        }
                        if (listener != null) {
                            listener.onError();
                        }
                    } finally {
                        try {
                            if (is != null)
                                is.close();
                        } catch (IOException e) {
                        }
                        try {
                            if (fos != null)
                                fos.close();
                        } catch (IOException e) {
                        }
                    }
                }

                @Override
                public void onError(Response response) {
                    if (listener != null) {
                        listener.onError();
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static boolean checkImage(String url, String saveDir, LightDownloadListener listener) {
        try {
            File file = new File(getDirectory(saveDir), getNameFromUrl(url));
            if (file.exists() && file.isFile()) {
                if (listener != null) {
                    listener.onSuccess(file.getAbsolutePath());
                }
                return true;
            }
        } catch (Exception e) {
            if (Log.isLoggable(CommonUtil.TAG, Log.ERROR)) {
                Log.e(CommonUtil.TAG, "Exception: ", e);
            }
        }
        return false;
    }

    private static String getDirectory(String savePath) throws IOException {
        // 下载位置
        File dir = new File(savePath);
        if (dir.exists()) {
            if (dir.isDirectory()) {
                return dir.getAbsolutePath();
            }
            if (!IOUtils.delFileOrFolder(dir)) {
                throw new IOException("delete file " + savePath + " failed");
            }
        }
        if (!dir.mkdirs()) {
            throw new IOException("make directory " + savePath + " failed");
        }
        return dir.getAbsolutePath();
    }

    /**
     * @param url
     * @return 从下载连接中解析出文件名
     */
    private static String getNameFromUrl(String url) {
        return CommonUtil.md5(url);
    }

}
