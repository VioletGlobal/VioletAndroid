package com.violet.dagger.presenter;

import com.violet.base.VioletBaseApplication;
import com.violet.base.util.NetUtil;
import com.violet.core.util.LogUtil;
import com.violet.dagger.model.api.DaggerApiService;
import com.violet.dagger.model.entity.SplashEntity;
import com.violet.dagger.util.tool.OkHttpImageDownloader;

import java.util.List;

import javax.inject.Inject;

import rx.Subscriber;
import rx.schedulers.Schedulers;

/**
 * Created by kan212 on 2018/6/27.
 */

public class SplashPresenter implements SplashContract.Presenter {

    private DaggerApiService apiService;
    private SplashContract.View view;

    @Inject
    public SplashPresenter(SplashContract.View view, DaggerApiService apiService) {
        this.view = view;
        this.apiService = apiService;
        LogUtil.d("apppp:" + apiService);
    }

    @Override
    public void getSplash(String deviceId) {
        String client = "android";
        String version = "1.3.0";
        Long time = System.currentTimeMillis() / 1000;
        apiService.getSplash(client, version, time, deviceId)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(new Subscriber<SplashEntity>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(SplashEntity splashEntity) {
                        if (NetUtil.isWifi(VioletBaseApplication.mInstance)) {
                            if (splashEntity != null) {
                                List<String> imgs = splashEntity.getImages();
                                for (String url : imgs) {
                                    OkHttpImageDownloader.download(url);
                                }
                            }
                        } else {
                            LogUtil.i("不是WIFI环境,就不去下载图片了");
                        }
                    }
                });
    }
}
