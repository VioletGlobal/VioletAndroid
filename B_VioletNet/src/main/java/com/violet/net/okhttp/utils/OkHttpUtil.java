package com.violet.net.okhttp.utils;

import com.violet.net.http.HttpManager;
import com.violet.net.dispatcher.VtCall;
import com.violet.net.http.model.HttpHeaders;
import com.violet.net.http.model.Response;
import com.violet.net.http.request.Request;
import com.violet.net.okhttp.OkHttpConfig;
import com.violet.net.okhttp.adapter.CacheCall;
import com.violet.net.http.request.BodyRequest;
import com.violet.net.okhttp.request.base.ProgressRequestBody;

import java.io.IOException;
import java.util.Set;

import okhttp3.Headers;
import okhttp3.RequestBody;

/**
 * Created by liuqun1 on 2018/2/1.
 */

public class OkHttpUtil {

    public static com.violet.net.http.request.Call adapt(Request request) {
        if (request.getCall() == null) {
            return new CacheCall(request);
        } else {
            return request.getCall();
        }
    }

    public static okhttp3.Request.Builder generateRequestBuilderNoBody(Request request, RequestBody requestBody) {
        request.setUrl(HttpUtils.createUrlFromParams(request.getBaseUrl(), request.getParams().urlParamsMap));
        okhttp3.Request.Builder requestBuilder = new okhttp3.Request.Builder();
        return HttpUtils.appendHeaders(requestBuilder, request.getHeaders());
    }

    public static okhttp3.Request.Builder generateRequestBuilderHasBody(Request request, RequestBody requestBody) {
        try {
            request.headers(HttpHeaders.HEAD_KEY_CONTENT_LENGTH, String.valueOf(requestBody.contentLength()));
        } catch (IOException e) {
            OkLogger.printStackTrace(e);
        }
        okhttp3.Request.Builder requestBuilder = new okhttp3.Request.Builder();
        return HttpUtils.appendHeaders(requestBuilder, request.getHeaders());
    }

    /** 根据不同的请求方式和参数，生成不同的RequestBody */
    public static RequestBody generateRequestBody(Request r) {
        if(!(r instanceof BodyRequest)){
            return null;
        }
        BodyRequest request = (BodyRequest) r;
        if (request.getSpliceUrl()) {
            request.setUrl(HttpUtils.createUrlFromParams(request.getBaseUrl(), request.getParams().urlParamsMap));
        }

//        if (requestBody != null) return requestBody;                                                //自定义的请求体
        okhttp3.MediaType mt = TranseferUtil.transferFileParam(request.getMediaType());
        if (request.content() != null && request.getMediaType() != null) return RequestBody.create(mt, request.content());    //上传字符串数据
        if (request.getBs() != null && request.getMediaType() != null) return RequestBody.create(mt, request.getBs());              //上传字节数组
        if (request.getFile() != null && request.getMediaType() != null) return RequestBody.create(mt, request.getFile());          //上传一个文件
        return HttpUtils.generateMultipartRequestBody(request.getParams(), request.getMultipart());
    }

    /** 根据不同的请求方式，将RequestBody转换成Request对象 */
    public static okhttp3.Request generateRequest(Request r, RequestBody requestBody) {
        okhttp3.Request.Builder requestBuilder = null;
        if(r instanceof BodyRequest){
            requestBuilder = OkHttpUtil.generateRequestBuilderHasBody(r, requestBody);
        } else {
            requestBuilder = OkHttpUtil.generateRequestBuilderNoBody(r, requestBody);
        }
        switch (r.getMethod()){
            case GET:
                requestBuilder.get();
                break;
            case POST:
                requestBuilder.post(requestBody);
                break;
            case HEAD:
                requestBuilder.head();
                break;
            case PUT:
                requestBuilder.put(requestBody);
                break;
            case PATCH:
                requestBuilder.patch(requestBody);
                break;
            case DELETE:
                requestBuilder.delete(requestBody);
                break;
            case TRACE:
                requestBuilder.method("TRACE", requestBody);
                break;
            case OPTIONS:
                requestBuilder.method("OPTIONS", requestBody);
                break;
        }
        return requestBuilder.url(r.getUrl()).tag(r.getTag()).build();
    }

    /** 获取okhttp的同步call对象 */
    public static <T> VtCall getRawCall(Request request) {
        //构建请求体，返回call对象
        RequestBody requestBody = OkHttpUtil.generateRequestBody(request);
        okhttp3.Request rawRequest;
        if (requestBody != null) {
            ProgressRequestBody<T> progressRequestBody = new ProgressRequestBody<>(requestBody, request.getCallback());
            progressRequestBody.setInterceptor(request.getUploadInterceptor());
            rawRequest = OkHttpUtil.generateRequest(request ,progressRequestBody);
        } else {
            rawRequest = OkHttpUtil.generateRequest(request ,null);
        }
        OkHttpConfig okHttpConfig = (OkHttpConfig) HttpManager.getInstance().getConfig();
        return okHttpConfig.getSNOkHttpClient().newCall(rawRequest, request.getPriority());
    }

    /** 普通调用，阻塞方法，同步请求执行 */
//    public static Response execute(Request r) throws IOException {
//        return OkHttpUtil.getRawCall(r).execute();
//    }


    public static <T> Response success2(boolean isFromCache, T body, Request request, HttpHeaders aHeaders) {
        Response response = new Response();
        response.setFromCache(isFromCache);
        response.setBody(body);
        response.setHeaders(aHeaders);
        response.setRequest(request);
        return response;
    }

    public static <T> Response success2(boolean isFromCache, T body, Request request, HttpHeaders aHeaders, int code) {
        Response response = new Response();
        response.setFromCache(isFromCache);
        response.setBody(body);
        response.setHeaders(aHeaders);
        response.setRequest(request);
        response.setCode(code);
        return response;
    }


    public static <T> Response success2(boolean isFromCache, T body, Request request, Headers headers, int code) {
        Response response = new Response();
        response.setFromCache(isFromCache);
        response.setBody(body);
        response.setCode(code);
        response.setRequest(request);
        if(headers != null){
            if(headers != null){
                HttpHeaders aHeaders = new HttpHeaders();
                Set<String> names = headers.names();
                for (String name : names) {
                    aHeaders.put(name, headers.get(name));
                }
                response.setHeaders(aHeaders);
            }
        }
        return response;
    }

    // error ---------------------------------------------------------------------


    public static Response error2(boolean isFromCache, Request request, Headers headers, Throwable throwable) {
        Response response = new Response();
        response.setFromCache(isFromCache);
//        response.setRawCall(rawCall);
        response.setRequest(request);
//        response.setRawResponse(rawResponse);
        response.setException(throwable);
        if(headers != null){
            if(headers != null){
                HttpHeaders aHeaders = new HttpHeaders();
                Set<String> names = headers.names();
                for (String name : names) {
                    aHeaders.put(name, headers.get(name));
                }
                response.setHeaders(aHeaders);
            }
        }
        return response;
    }


    public static Response error(boolean isFromCache, Request request, Headers headers, Throwable throwable, int code) {
        Response response = new Response();
        response.setFromCache(isFromCache);
        response.setRequest(request);
        response.setException(throwable);
        response.setCode(code);
        if(headers != null){
            if(headers != null){
                HttpHeaders aHeaders = new HttpHeaders();
                Set<String> names = headers.names();
                for (String name : names) {
                    aHeaders.put(name, headers.get(name));
                }
                response.setHeaders(aHeaders);
            }
        }
        return response;
    }

    public static Response error2(boolean isFromCache, Request request, HttpHeaders aHeaders, Throwable throwable, int code) {
        Response response = new Response();
        response.setFromCache(isFromCache);
        response.setRequest(request);
        response.setException(throwable);
        response.setCode(code);
        response.setHeaders(aHeaders);
        return response;
    }

}
