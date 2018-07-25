package com.violet.dagger.refer.components;

import com.violet.dagger.model.api.DaggerApiService;
import com.violet.dagger.refer.modules.NetModule;

import javax.inject.Singleton;

import dagger.Component;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;

/**
 * Created by kan212 on 2018/6/29.
 */
@Component(modules = NetModule.class)
@Singleton
public interface NetComponent {
    DaggerApiService getApiService();
    OkHttpClient getOkHttp();
    Retrofit getRetrofit();
}
