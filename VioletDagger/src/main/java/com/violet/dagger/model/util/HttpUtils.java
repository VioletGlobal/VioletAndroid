package com.violet.dagger.model.util;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;

/**
 * Created by kan212 on 2018/6/29.
 */

public class HttpUtils {

    private HttpUtils() {}
    public static final OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(20, TimeUnit.SECONDS)
            .readTimeout(20,TimeUnit.SECONDS)
//            .addInterceptor(createHttpLoggingInterceptor())
            .build();

//    private static HttpLoggingInterceptor createHttpLoggingInterceptor() {
//        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
//        loggingInterceptor.setLevel(BuildConfig.DEBUG ? HttpLoggingInterceptor.Level.BODY : HttpLoggingInterceptor.Level.NONE);
//        return loggingInterceptor;
//    }

}
