package com.violet.net.http.request;

import android.text.TextUtils;

import com.violet.net.http.Config;
import com.violet.net.http.HttpManager;
import com.violet.net.http.UploadInterceptor;
import com.violet.net.http.cache.CacheConst;
import com.violet.net.http.cache.CacheMode;
import com.violet.net.http.callback.Callback;
import com.violet.net.http.convert.AbsConverter;
import com.violet.net.http.model.HttpHeaders;
import com.violet.net.http.model.HttpMethod;
import com.violet.net.http.model.HttpParams;
import com.violet.net.http.util.CommonUtil;
import com.violet.net.http.util.HttpUtils;
import com.violet.net.dispatcher.VtPriority;


import java.io.Serializable;
import java.util.List;
import java.util.Map;


/**
 * 作    者：jeasonlzy
 * 创建日期：2016/1/12
 * 描    述：所有请求的基类，其中泛型 R 主要用于属性设置方法后，返回对应的子类型，以便于实现链式调用
 * modify by liuqun1
 */
public abstract class Request<R extends Request> implements Serializable {
    private static final long serialVersionUID = -7174118653689916252L;

    protected String url;
    protected String baseUrl;
    protected transient Object tag;
    protected int retryCount;
    protected CacheMode cacheMode;
    protected String cacheKey;
    protected long cacheTime;                           //默认缓存的超时时间
    protected HttpParams params = new HttpParams();     //添加的param
    protected HttpHeaders headers = new HttpHeaders();  //添加的header
    protected VtPriority priority;

    protected transient Callback callback;
    protected transient AbsConverter converter;
    protected transient UploadInterceptor uploadInterceptor;
    protected transient Call call;
    protected Class responseClass;

    public Request(String url) {
        this.url = url;
        baseUrl = url;
//        OkGo go = OkGo.getInstance();
        //默认添加 Accept-Language
        String acceptLanguage = HttpHeaders.getAcceptLanguage();
        if (!TextUtils.isEmpty(acceptLanguage)) headers(HttpHeaders.HEAD_KEY_ACCEPT_LANGUAGE, acceptLanguage);
        //默认添加 User-Agent
        String userAgent = HttpHeaders.getUserAgent();
        if (!TextUtils.isEmpty(userAgent)) headers(HttpHeaders.HEAD_KEY_USER_AGENT, userAgent);
        //添加公共请求参数
//        if (go.getCommonParams() != null) params(go.getCommonParams());
//        if (go.getCommonHeaders() != null) headers(go.getCommonHeaders());
        //添加缓存模式
//        retryCount = go.getRetryCount();
//        cacheMode = go.getCacheMode();
//        cacheMode = CacheMode.NO_CACHE;
//        retryCount = 3;
//--------------------------------
        Config config = HttpManager.getInstance().getConfig();
        //添加公共请求参数
        if (config.commonParams() != null) params(config.commonParams());
        if (config.commonHeaders() != null) headers(config.commonHeaders());
        //添加缓存模式
        retryCount = config.retryCount();
        cacheMode = config.cacheMode();
        cacheTime = config.cacheTime();
    }

    public R setUrl(String url){
        this.url = url;
        return (R) this;
    }

    public R setBaseUrl(String baseUrl){
        this.baseUrl = baseUrl;
        return (R) this;
    }

    @SuppressWarnings("unchecked")
    public R tag(Object tag) {
        this.tag = tag;
        return (R) this;
    }

    @SuppressWarnings("unchecked")
    public R priority(VtPriority priority) {
        this.priority = priority;
        return (R) this;
    }

    public VtPriority getPriority(){
        return priority;
    }

    @SuppressWarnings("unchecked")
    public R retryCount(int retryCount) {
        if (retryCount < 0) throw new IllegalArgumentException("retryCount must > 0");
        this.retryCount = retryCount;
        return (R) this;
    }

    @SuppressWarnings("unchecked")
    public R converter(AbsConverter converter) {
        HttpUtils.checkNotNull(converter, "converter == null");

        this.converter = converter;
        return (R) this;
    }

    @SuppressWarnings("unchecked")
    public R call(Call call) {
        CommonUtil.checkNotNull(call, "call == null");

        this.call = call;
        return (R) this;
    }

    public Call getCall(){
        return call;
    }

    public R setResponseClass(Class clazz){
        this.responseClass = clazz;
        if (this.converter == null){
            this.converter = HttpManager.getInstance().getConfig().getConverter();
        }
        return (R) this;
    }

    public Class getResponseClass(){
        return responseClass;
    }

    @SuppressWarnings("unchecked")
    public R uploadInterceptor(UploadInterceptor uploadInterceptor) {
        this.uploadInterceptor = uploadInterceptor;
        return (R) this;
    }

    public UploadInterceptor getUploadInterceptor(){
        return uploadInterceptor;
    }

    @SuppressWarnings("unchecked")
    public R cacheMode(CacheMode cacheMode) {
        this.cacheMode = cacheMode;
        return (R) this;
    }

    @SuppressWarnings("unchecked")
    public R cacheKey(String cacheKey) {
        HttpUtils.checkNotNull(cacheKey, "cacheKey == null");

        this.cacheKey = cacheKey;
        return (R) this;
    }

    /** 传入 -1 表示永久有效,默认值即为 -1 */
    @SuppressWarnings("unchecked")
    public R cacheTime(long cacheTime) {
        if (cacheTime <= -1) cacheTime = CacheConst.CACHE_NEVER_EXPIRE;
        this.cacheTime = cacheTime;
        return (R) this;
    }

    @SuppressWarnings("unchecked")
    public R headers(HttpHeaders headers) {
        this.headers.put(headers);
        return (R) this;
    }

    @SuppressWarnings("unchecked")
    public R headers(String key, String value) {
        headers.put(key, value);
        return (R) this;
    }

    @SuppressWarnings("unchecked")
    public R removeHeader(String key) {
        headers.remove(key);
        return (R) this;
    }

    @SuppressWarnings("unchecked")
    public R removeAllHeaders() {
        headers.clear();
        return (R) this;
    }

    @SuppressWarnings("unchecked")
    public R params(HttpParams params) {
        this.params.put(params);
        return (R) this;
    }

    @SuppressWarnings("unchecked")
    public R params(Map<String, String> params, boolean... isReplace) {
        this.params.put(params, isReplace);
        return (R) this;
    }

    @SuppressWarnings("unchecked")
    public R params(String key, String value, boolean... isReplace) {
        params.put(key, value, isReplace);
        return (R) this;
    }

    @SuppressWarnings("unchecked")
    public R params(String key, int value, boolean... isReplace) {
        params.put(key, value, isReplace);
        return (R) this;
    }

    @SuppressWarnings("unchecked")
    public R params(String key, float value, boolean... isReplace) {
        params.put(key, value, isReplace);
        return (R) this;
    }

    @SuppressWarnings("unchecked")
    public R params(String key, double value, boolean... isReplace) {
        params.put(key, value, isReplace);
        return (R) this;
    }

    @SuppressWarnings("unchecked")
    public R params(String key, long value, boolean... isReplace) {
        params.put(key, value, isReplace);
        return (R) this;
    }

    @SuppressWarnings("unchecked")
    public R params(String key, char value, boolean... isReplace) {
        params.put(key, value, isReplace);
        return (R) this;
    }

    @SuppressWarnings("unchecked")
    public R params(String key, boolean value, boolean... isReplace) {
        params.put(key, value, isReplace);
        return (R) this;
    }

    @SuppressWarnings("unchecked")
    public R addUrlParams(String key, List<String> values) {
        params.putUrlParams(key, values);
        return (R) this;
    }

    @SuppressWarnings("unchecked")
    public R removeParam(String key) {
        params.remove(key);
        return (R) this;
    }

    @SuppressWarnings("unchecked")
    public R removeAllParams() {
        params.clear();
        return (R) this;
    }

//    @SuppressWarnings("unchecked")
//    public R uploadInterceptor(ProgressRequestBody.UploadInterceptor uploadInterceptor) {
//        this.uploadInterceptor = uploadInterceptor;
//        return (R) this;
//    }

    /** 默认返回第一个参数 */
    public String getUrlParam(String key) {
        List<String> values = params.urlParamsMap.get(key);
        if (values != null && values.size() > 0) return values.get(0);
        return null;
    }

    /** 默认返回第一个参数 */
    public HttpParams.FileWrapper getFileParam(String key) {
        List<HttpParams.FileWrapper> values = params.fileParamsMap.get(key);
        if (values != null && values.size() > 0) return values.get(0);
        return null;
    }

    public HttpParams getParams() {
        return params;
    }

    public HttpHeaders getHeaders() {
        return headers;
    }

    public String getUrl() {
        return url;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public Object getTag() {
        return tag;
    }

    public CacheMode getCacheMode() {
        return cacheMode;
    }

    public String getCacheKey() {
        return cacheKey;
    }

    public long getCacheTime() {
        return cacheTime;
    }

    public int getRetryCount() {
        return retryCount;
    }

    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    public Callback getCallback(){
        return callback;
    }
    public AbsConverter getConverter() {
        // converter 优先级高于 callback
//        if (converter == null) converter = callback;
        HttpUtils.checkNotNull(converter, "converter == null, do you forget to call Request#converter(AbsConverter) ?");
        return converter;
    }

    public abstract HttpMethod getMethod();

}
