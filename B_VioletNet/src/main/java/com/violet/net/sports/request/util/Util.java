package com.violet.net.sports.request.util;
import android.text.TextUtils;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

public class Util {

    /**
     * 处理Get请求
     *
     * @param url
     * @param params
     * @return
     */
    public static String dealGetURL(String url, Map<String, String> params) {
        if (!TextUtils.isEmpty(url)
                && url.startsWith("http")
                && null != params
                && params.size() > 0) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(url);
            boolean isFirst = false, isNewAddQuestionMark = false;
            if (!url.contains("?")) {
                stringBuilder.append("?");
                isNewAddQuestionMark = true;
            }
            if (url.endsWith("?") || isNewAddQuestionMark) {
                isFirst = true;
            }
            Iterator<Map.Entry<String, String>> iterator = params.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, String> entry = iterator.next();
                if (isFirst) {
                    isFirst = false;
                    stringBuilder.append(entry.getKey()).append("=").append(entry.getValue());
                } else {
                    stringBuilder.append("&").append(entry.getKey()).append("=").append(entry.getValue());
                }
            }
            return stringBuilder.toString();
        } else {
            return url;
        }
    }

    /**
     * 处理Get请求
     *
     * @param url
     * @param params
     * @return
     */
    public static String dealGetURL(String url, LinkedHashMap<String, String> params) {
        if (!TextUtils.isEmpty(url)
                && url.startsWith("http")
                && null != params
                && params.size() > 0) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(url);
            boolean isFirst = false, isNewAddQuestionMark = false;
            if (!url.contains("?")) {
                stringBuilder.append("?");
                isNewAddQuestionMark = true;
            }
            if (url.endsWith("?") || isNewAddQuestionMark) {
                isFirst = true;
            }
            Iterator<Map.Entry<String, String>> iterator = params.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, String> entry = iterator.next();
                if (isFirst) {
                    isFirst = false;
                    stringBuilder.append(entry.getKey()).append("=").append(entry.getValue());
                } else {
                    stringBuilder.append("&").append(entry.getKey()).append("=").append(entry.getValue());
                }
            }
            return stringBuilder.toString();
        } else {
            return url;
        }
    }

}
