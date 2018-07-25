package com.violet.dagger.refer.modules;

import com.violet.dagger.presenter.SplashContract;

import dagger.Module;
import dagger.Provides;

/**
 * Created by kan212 on 2018/6/27.
 */
@Module
public class SplashModule {

    private SplashContract.View view;

    public SplashModule(SplashContract.View view) {
        this.view = view;
    }
    @Provides
    public SplashContract.View provideView(){
        return view;
    }
}
