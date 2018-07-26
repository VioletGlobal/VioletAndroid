
package com.violet.net.okhttp.convert;

import android.text.TextUtils;

import com.google.gson.stream.JsonReader;
import com.violet.net.http.convert.AbsConverter;
import com.violet.net.okhttp.utils.Convert;
import com.sina.simasdk.event.SIMACommonEvent;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * ================================================
 * 作    者：jeasonlzy（廖子尧）Github地址：https://github.com/jeasonlzy
 * 版    本：1.0
 * 创建日期：16/9/11
 * 描    述：
 * 修订历史：
 * ================================================
 */
public class JsonConvert<T> implements AbsConverter<T, Response> {

    protected Class<T> clazz;

    public JsonConvert() {
    }

    public JsonConvert(Class<T> clazz) {
        this.clazz = clazz;
    }

    /**
     * 该方法是子线程处理，不能做ui相关的工作
     * 主要作用是解析网络返回的 response 对象，生成onSuccess回调中需要的数据对象
     * 这里的解析工作不同的业务逻辑基本都不一样,所以需要自己实现,以下给出的时模板代码,实际使用根据需要修改
     */
    @Override
    public T convertResponse(Response res) throws Throwable {
//        Response response = (Response) res;
        // 重要的事情说三遍，不同的业务，这里的代码逻辑都不一样，如果你不修改，那么基本不可用
        // 重要的事情说三遍，不同的业务，这里的代码逻辑都不一样，如果你不修改，那么基本不可用
        // 重要的事情说三遍，不同的业务，这里的代码逻辑都不一样，如果你不修改，那么基本不可用

        // 如果你对这里的代码原理不清楚，可以看这里的详细原理说明: https://github.com/jeasonlzy/okhttp-OkGo/wiki/JsonCallback
        // 如果你对这里的代码原理不清楚，可以看这里的详细原理说明: https://github.com/jeasonlzy/okhttp-OkGo/wiki/JsonCallback
        // 如果你对这里的代码原理不清楚，可以看这里的详细原理说明: https://github.com/jeasonlzy/okhttp-OkGo/wiki/JsonCallback
//        T result = null;
//        try {
//            result = parseClass(response, clazz);
//        } catch (Exception e){
//
//        }
        return convertResponse(res, this.clazz);
    }

    @Override
    public T convertResponse(Response response, Class<T> clazz) throws Throwable {
        // 重要的事情说三遍，不同的业务，这里的代码逻辑都不一样，如果你不修改，那么基本不可用
        // 重要的事情说三遍，不同的业务，这里的代码逻辑都不一样，如果你不修改，那么基本不可用
        // 重要的事情说三遍，不同的业务，这里的代码逻辑都不一样，如果你不修改，那么基本不可用

        // 如果你对这里的代码原理不清楚，可以看这里的详细原理说明: https://github.com/jeasonlzy/okhttp-OkGo/wiki/JsonCallback
        // 如果你对这里的代码原理不清楚，可以看这里的详细原理说明: https://github.com/jeasonlzy/okhttp-OkGo/wiki/JsonCallback
        // 如果你对这里的代码原理不清楚，可以看这里的详细原理说明: https://github.com/jeasonlzy/okhttp-OkGo/wiki/JsonCallback
        T result = null;
        try {
            result = parseClass(response, clazz);
        } catch (Exception e){
            e.printStackTrace();
            try {
                String className = clazz == null? "" : clazz.getName();
                String responseStr = response == null? "" : (response.body() == null? "" : response.body().string());
                String url = response == null? "":response.request().url().url().toString();
                logError("convertResponseException", className, responseStr, url, e.getMessage());
            } catch (Exception ex){
                ex.printStackTrace();
            }

        }
        return result;
    }

    private T parseClass(Response response, Class<?> rawType) throws Exception {
        if (rawType == null){
            ResponseBody body = response.body();
            String bodyStr = body == null? "" : body.string();
            logError("", bodyStr, response.request().url().url().toString(), "rawType is null");
            return null;
        }
        ResponseBody body = response.body();
        if (body == null) {
            logError(rawType.getName(), "", response.request().url().url().toString(), "response is null");
            return null;
        }

        if (rawType == Response.class) {
            //noinspection unchecked
            return (T) response;
        }
        if(rawType == InputStream.class){
            return (T) body.byteStream();
        }
        if(rawType == Reader.class){
            return (T) body.charStream();
        }
        if (rawType == String.class) {
            //noinspection unchecked
            return (T) body.string();
        } else if (rawType == byte[].class) {
            //noinspection unchecked
            return (T) body.bytes();
        } else if (rawType == JSONObject.class) {
            //noinspection unchecked
            return (T) new JSONObject(body.string());
        } else if (rawType == JSONArray.class) {
            //noinspection unchecked
            return (T) new JSONArray(body.string());
        } else {
            JsonReader jsonReader = new JsonReader(body.charStream());
            T t = Convert.fromJson(jsonReader, rawType);
            if(t == null){
                try{
                    logError(rawType.getName(), body.string(), response.request().url().url().toString(),"convert return null");
                } catch (Exception ex){
                    ex.printStackTrace();
                }
            }
            response.close();
            return t;
        }
    }

    private void logError(String className, String responseStr, String url, String info4){
        logError("convertResponseError", className, responseStr, url, info4);
    }

    private void logError(String subType, String className, String responseStr, String url, String info4){
        try {
            Map<String, Object> attr = new HashMap<>();
            attr.put("type", "httpsdk");
            attr.put("subtype", subType);
            attr.put("info", className);
            attr.put("info2", responseStr);
            attr.put("info3", url);
            if(!TextUtils.isEmpty(info4)){
                attr.put("info4", info4);
            }
            attr.put("stime", String.valueOf(System.currentTimeMillis()));

            SIMACommonEvent event = new SIMACommonEvent("_code", "apm");
            event.setEventMethod("httplib_request")
                    .setCustomAttributes(attr)
                    .sendtoAll();
        } catch (Exception e){
            e.printStackTrace();
        }
    }


}
