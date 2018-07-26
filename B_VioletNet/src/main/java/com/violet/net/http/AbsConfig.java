package com.violet.net.http;

import com.violet.net.http.cache.CacheMode;
import com.violet.net.http.model.HttpHeaders;
import com.violet.net.http.model.HttpParams;

import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.X509TrustManager;

/**
 * Created by liuqun1 on 2018/2/4.
 */

public abstract class AbsConfig<T> implements Config<T> {

    protected volatile boolean mBuilded = false;

    protected long readTimeout;
    protected TimeUnit readTimeoutTimeUnit;
    protected long writeTimeout;
    protected TimeUnit writeTimeoutTimeUnit;
    protected long connectTimeout;
    protected TimeUnit connectTimeoutTimeUnit;
    protected long cacheTime;
    protected int retryCount;
    protected CacheMode cacheMode;

    protected SSLSocketFactory sslSocketFactory;
    protected X509TrustManager trustManager;
    protected HostnameVerifier hostnameVerifier;

    protected int[] priorityPercent;

    protected HttpHeaders commonHttpHeaders;
    protected HttpParams commonHttpParams;

    @Override
    public Config readTimeout(long timeout, TimeUnit unit) {
        this.readTimeout = timeout;
        this.readTimeoutTimeUnit = unit;
        return this;
    }

    @Override
    public Config writeTimeout(long timeout, TimeUnit unit) {
        this.writeTimeout = timeout;
        this.writeTimeoutTimeUnit = unit;
        return this;
    }

    @Override
    public Config connectTimeout(long timeout, TimeUnit unit) {
        this.connectTimeout = timeout;
        this.connectTimeoutTimeUnit = unit;
        return this;
    }

    @Override
    public long readTimeout() {
        return readTimeout;
    }

    @Override
    public TimeUnit readTimeoutTimeUnit() {
        return readTimeoutTimeUnit;
    }

    @Override
    public long writeTimeout() {
        return writeTimeout;
    }

    @Override
    public TimeUnit writeTimeoutTimeUnit() {
        return writeTimeoutTimeUnit;
    }

    @Override
    public long connectTimeout() {
        return connectTimeout;
    }

    @Override
    public TimeUnit connectTimeoutTimeUnit() {
        return connectTimeoutTimeUnit;
    }

    @Override
    public Config sslSocketFactory(SSLSocketFactory sslSocketFactory, X509TrustManager trustManager) {
        this.sslSocketFactory = sslSocketFactory;
        this.trustManager = trustManager;
        return this;
    }

    @Override
    public Config hostnameVerifier(HostnameVerifier hostnameVerifier) {
        this.hostnameVerifier = hostnameVerifier;
        return this;
    }

    @Override
    public CacheMode cacheMode() {
        return cacheMode;
    }

    @Override
    public long cacheTime() {
        return cacheTime;
    }

    @Override
    public int retryCount() {
        return retryCount;
    }

    @Override
    public HttpHeaders commonHeaders() {
        return commonHttpHeaders;
    }

    @Override
    public HttpParams commonParams() {
        return commonHttpParams;
    }

    @Override
    public Config priorityPercent(int[] priorityPercent) {
        this.priorityPercent = priorityPercent;
        return this;
    }

}
