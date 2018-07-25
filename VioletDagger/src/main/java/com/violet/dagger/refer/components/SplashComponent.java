package com.violet.dagger.refer.components;

import com.violet.dagger.refer.modules.SplashModule;
import com.violet.dagger.refer.scopes.UserScope;
import com.violet.dagger.view.activity.SplashActivity;

import dagger.Component;

/**
 * Created by kan212 on 2018/6/27.
 */
@UserScope
@Component(modules = SplashModule.class,dependencies = NetComponent.class)
public interface SplashComponent {
    void inject(SplashActivity splashActivity);
}

