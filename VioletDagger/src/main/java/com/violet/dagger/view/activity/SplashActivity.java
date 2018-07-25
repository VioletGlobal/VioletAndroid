package com.violet.dagger.view.activity;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.violet.base.grape.router.RouterUrl;
import com.violet.base.grape.router.VioletRouter;
import com.violet.base.ui.activity.BaseActivity;
import com.violet.base.ui.view.widget.FixedImageView;
import com.violet.core.util.AppUtil;
import com.violet.dagger.DaggerApplication;
import com.violet.dagger.R;
import com.violet.dagger.presenter.SplashContract;
import com.violet.dagger.presenter.SplashPresenter;
import com.violet.dagger.refer.components.DaggerSplashComponent;
import com.violet.dagger.refer.modules.SplashModule;
import com.violet.dagger.util.tool.FileUtil;
import com.violet.dagger.util.tool.PreferenceUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Random;

import javax.inject.Inject;

/**
 * Created by kan212 on 2018/6/27.
 */
@Route(path = RouterUrl.MainRouter.APP_DAGGER)
public class SplashActivity extends BaseActivity implements SplashContract.View {

    FixedImageView splashImg;
    @Inject
    SplashPresenter presenter;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_violet_dagger_splash;
    }

    @Override
    protected void initView(View parent) {
        splashImg = parent.findViewById(R.id.splash_img);
    }

    @Override
    protected void initToolbar(Toolbar toolbar) {
        toolbar.setVisibility(View.GONE);
    }

    @Override
    protected void initData(Intent intent) {
        DaggerSplashComponent.builder()
                .netComponent(DaggerApplication.mDaggerApplication.mNetComponent)
                .splashModule(new SplashModule(this))
                .build().inject(this);
        delaySplash();
        String deviceId = AppUtil.getDeviceId(this);
        presenter.getSplash(deviceId);
    }

    private void delaySplash() {
        List<String> picList = FileUtil.getAllAD();
        if (picList.size() > 0) {
            Random random = new Random();
            int index = random.nextInt(picList.size());
            int imgIndex = PreferenceUtils.getPrefInt(this, "splash_img_index", 0);
            if (index == imgIndex) {
                if (index >= picList.size()) {
                    index--;
                } else if (imgIndex == 0) {
                    if (index + 1 < picList.size()) {
                        index++;
                    }
                }
            }
            PreferenceUtils.setPrefInt(this, "splash_img_index", index);
            File file = new File(picList.get(index));
            try {
                InputStream fis = new FileInputStream(file);
                splashImg.setImageDrawable(InputStream2Drawable(fis));
                animWelcomeImage();
                fis.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {

            }
        } else {
            try {
                AssetManager assetManager = this.getAssets();
                InputStream in = assetManager.open("welcome_default.jpg");
                splashImg.setImageDrawable(InputStream2Drawable(in));
                animWelcomeImage();
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public Drawable InputStream2Drawable(InputStream is) {
        Drawable drawable = BitmapDrawable.createFromStream(is, "splashImg");
        return drawable;
    }

    private void animWelcomeImage() {
        ObjectAnimator animator = ObjectAnimator.ofFloat(splashImg, "translationX", -100F);
        animator.setDuration(1500L).start();
        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                VioletRouter.startMainActivity(getApplicationContext());
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
    }

}
