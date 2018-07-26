package com.violet.net.okhttp.utils;

import com.violet.net.http.model.HttpMethod;
import com.violet.net.okhttp.OkGo;
import com.violet.net.http.model.HttpHeaders;
import com.violet.net.http.model.HttpParams;
import com.violet.net.http.request.Request;
import com.violet.net.http.request.PostRequest;

import okhttp3.MediaType;

/**
 * Created by liuqun1 on 2018/1/30.
 */

public class TranseferUtil {

    public static MediaType transferFileParam(com.violet.net.http.model.MediaType _mediaType){
        if(_mediaType == null)
            return null;

        MediaType mediaType = null;
        if("text/plain;charset=utf-8".contains(_mediaType.toString())){
            mediaType = OkGo.OK_MEDIA_TYPE_PLAIN;
        } else if("application/json;charset=utf-8".contains(_mediaType.toString())){
            mediaType = OkGo.OK_MEDIA_TYPE_JSON;
        } else {
            mediaType = OkGo.OK_MEDIA_TYPE_STREAM;
        }
        return mediaType;
    }

    public static void transferRequest(Request request, Request okgoRequest){
        if(okgoRequest != null){
            okgoRequest.tag(request.getTag());
            okgoRequest.retryCount(request.getRetryCount());
            okgoRequest.converter(request.getConverter());

            HttpHeaders headers = request.getHeaders();
            if(headers != null){
//                com.sina.network.model.HttpHeaders okHeaders = new com.sina.network.model.HttpHeaders();
//                okHeaders.setUserAgent(HttpHeaders.getUserAgent());
//                okHeaders.setAcceptLanguage(HttpHeaders.getAcceptLanguage());
//                Set<String> set = headers.getNames();
//                if(set != null){
//                    for(String key:set){
//                        okHeaders.put(key, headers.get(key));
//                    }
//                }
                okgoRequest.headers(headers);
            }

            HttpParams params = request.getParams();
            if(params != null){
//                com.sina.network.model.HttpParams okParams = new com.sina.network.model.HttpParams();
//                okParams.urlParamsMap = params.urlParamsMap;
//                okParams.fileParamsMap = params.fileParamsMap;
                okgoRequest.params(request.getParams());
//                TranseferUtil.transferFileParam(params.fileParamsMap, okParams.fileParamsMap);
            }

            if(request.getMethod() == HttpMethod.POST){
                com.violet.net.http.request.PostRequest post = (com.violet.net.http.request.PostRequest) request;
                PostRequest okPost = (PostRequest) okgoRequest;
//                okPost.upBytes(post.getBs(), post.mediaType());
//                okPost.upFile(post.getFile(), post.mediaType());
//                okPost.upString(post.content(), post.mediaType());
            }
        }
    }

}
