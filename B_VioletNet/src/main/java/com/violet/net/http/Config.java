package com.violet.net.http;

import android.app.Application;
import android.content.Context;

import com.violet.net.http.cache.CacheMode;
import com.violet.net.http.convert.AbsConverter;
import com.violet.net.http.model.HttpHeaders;
import com.violet.net.http.model.HttpParams;

import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.X509TrustManager;

/**
 * Created by liuqun1 on 2018/2/1.
 */

public interface Config<T> {
    //get
    long readTimeout();
    TimeUnit readTimeoutTimeUnit();

    long writeTimeout();
    TimeUnit writeTimeoutTimeUnit();

    long connectTimeout();
    TimeUnit connectTimeoutTimeUnit();

    CacheMode cacheMode();

    long cacheTime();

    int retryCount();

    HttpHeaders commonHeaders();

    HttpParams commonParams();

    AbsConverter getConverter();

    void converter(AbsConverter converter);

    //set
    Config readTimeout(long timeout, TimeUnit unit);

    Config writeTimeout(long timeout, TimeUnit unit);

    Config connectTimeout(long timeout, TimeUnit unit);

    Config sslSocketFactory(SSLSocketFactory sslSocketFactory, X509TrustManager trustManager);

    Config hostnameVerifier(HostnameVerifier hostnameVerifier);

    Config setCacheMode(CacheMode cacheMode);

    Config setCacheTime(long cacheTime);

    Config setRetryCount(int retryCount);

    Config addCommonHeaders(HttpHeaders commonHeaders);

    Config addCommonParams(HttpParams commonParams);

    Config context(Application context);

    Config priorityPercent(int[] priorityPercent);

    void onInit(T initializer);

//    void build();

    void buildIfNeeded();

}
