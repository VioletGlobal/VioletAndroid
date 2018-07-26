package com.violet.net.http.model;


import com.violet.net.http.request.Request;

/*
* @author liuqun1
* create at 2018/1/30 下午8:23
*
*/
public class Response<T> {

    protected T body;
    protected Throwable throwable;
    protected boolean isFromCache;
    protected int code;
    protected String msg;
//    protected X rawCall;
//    protected R rawResponse;
    protected HttpHeaders headers;


    protected Request mRequest;


    public Response() {
    }

    public Response(T _body) {
        body = _body;
    }

    public void setCode(int _code){
        code = _code;
    }

    public int code(){
        return code;
    }

    public String message(){
        return msg;
    }

//    public Headers headers() {
//        if (rawResponse == null) return null;
//        return rawResponse.headers();
//    }
    public HttpHeaders getHeaders(){
    return headers;
}

    public void setHeaders(HttpHeaders h){
        headers = h;
    }


    public Request getRequest(){
        return mRequest;
    }

    public void setRequest(Request request){
        mRequest = request;
    }


    public boolean isSuccessful() {
        return code >= 200 && code < 300 && throwable == null;
//        return throwable == null;
    }

    public void setBody(T body) {
        this.body = body;
    }

    public T body() {
        return body;
    }

    public Throwable getException() {
        return throwable;
    }

    public void setException(Throwable exception) {
        this.throwable = exception;
    }

    public boolean isFromCache() {
        return isFromCache;
    }

    public void setFromCache(boolean fromCache) {
        isFromCache = fromCache;
    }
}
