package com.violet.net.http.callback;

import com.violet.net.http.cache.CacheMode;
import com.violet.net.http.model.Response;
import com.violet.net.http.request.Request;


/**
 * ================================================
 * 作    者：jeasonlzy（廖子尧）Github地址：https://github.com/jeasonlzy
 * 版   本：1.0
 * 创建日期：2016/1/14
 * 描   述：抽象的回调接口
 * 修订历史：
 * ================================================
 * <p>该类的回调具有如下顺序,虽然顺序写的很复杂,但是理解后,是很简单,并且合情合理的
 * <p>1.无缓存模式{@link CacheMode#NO_CACHE}<br>
 * ---网络请求成功 onStart -> convertResponse -> onSuccess -> onFinish<br>
 * ---网络请求失败 onStart -> onError -> onFinish<br>
 * <p>2.默认缓存模式,遵循304头{@link CacheMode#DEFAULT}<br>
 * ---网络请求成功,服务端返回非304 onStart -> convertResponse -> onSuccess -> onFinish<br>
 * ---网络请求成功服务端返回304 onStart -> onCacheSuccess -> onFinish<br>
 * ---网络请求失败 onStart -> onError -> onFinish<br>
 * <p>3.请求网络失败后读取缓存{@link CacheMode#REQUEST_FAILED_READ_CACHE}<br>
 * ---网络请求成功,不读取缓存 onStart -> convertResponse -> onSuccess -> onFinish<br>
 * ---网络请求失败,读取缓存成功 onStart -> onCacheSuccess -> onFinish<br>
 * ---网络请求失败,读取缓存失败 onStart -> onError -> onFinish<br>
 * <p>4.如果缓存不存在才请求网络，否则使用缓存{@link CacheMode#IF_NONE_CACHE_REQUEST}<br>
 * ---已经有缓存,不请求网络 onStart -> onCacheSuccess -> onFinish<br>
 * ---没有缓存请求网络成功 onStart -> convertResponse -> onSuccess -> onFinish<br>
 * ---没有缓存请求网络失败 onStart -> onError -> onFinish<br>
 * <p>5.先使用缓存，不管是否存在，仍然请求网络{@link CacheMode#FIRST_CACHE_THEN_REQUEST}<br>
 * ---无缓存时,网络请求成功 onStart -> convertResponse -> onSuccess -> onFinish<br>
 * ---无缓存时,网络请求失败 onStart -> onError -> onFinish<br>
 * ---有缓存时,网络请求成功 onStart -> onCacheSuccess -> convertResponse -> onSuccess -> onFinish<br>
 * ---有缓存时,网络请求失败 onStart -> onCacheSuccess -> onError -> onFinish<br>
 */
public interface Callback<T> {

    /** 请求网络开始前， */
    void onStart(Request request);

    /** 对返回数据进行操作的回调，  */
    void onSuccess(Response<T> response);

    /** 缓存成功的回调, */
    void onCacheSuccess(Response<T> response);

    /** 请求失败，响应错误，数据解析错误等，都会回调该方法，  */
    void onError(Response<T> response);

    /** 请求网络结束后， */
    void onFinish();

    /** 上传过程中的进度回调，get请求不回调， */
//    void uploadProgress(Progress progress);

    /** 下载过程中的进度回调， */
//    void downloadProgress(Progress progress);

    /** 控制其他回调方法是否在主线程调用 */
    boolean callOnMainThread();
}
