package com.violet.base.grape.router;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;

import com.alibaba.android.arouter.facade.Postcard;
import com.violet.grape.VioletGrape;

/**
 * Created by kan212 on 2018/4/13.
 */

public class VioletRouter {

    public static final String VIOLET_ROUTER_FRAGMENT_URL = "violet_router_fragment_Url";


    public static void go2ActivityByUrl(String url){
        VioletGrape.getInstance()
                .build(url)
                .navigation();
    }

    public static void go2ActivityWithFragment(String url,String frg){
        VioletGrape.getInstance()
                .build(url)
                .withString(VIOLET_ROUTER_FRAGMENT_URL,frg)
                .navigation();
    }

    public static void startMainActivity(Context context) {
        getMainPostcard().navigation(context);
    }

    public static Postcard getMainPostcard() {
        return VioletGrape.getInstance().build(RouterUrl.MainRouter.MAIN_ACTIVITY)
                .withFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
    }



    /**
     * Fragment根据url生成
     * @param url
     * @return
     */
    public static Fragment go2FragmentByUrl(String url){
        return (Fragment) VioletGrape.getInstance().build(url).navigation();
    }


    /**
     * 跳转GuiderActivity
     */
    public static void go2GuiderActivity() {
        VioletGrape.getInstance()
                .build(RouterUrl.GuiderRouter.GUIDER_ACTIVITY)
                .navigation();
    }
    /**
     * 跳转MainActivity
     */
    public static void go2MainActivity(int tabId) {
        VioletGrape.getInstance()
                .build(RouterUrl.MainRouter.MAIN_LAUNCHER)
                .withInt(RouterUrl.MainKey.MAIN_TAB_ID, tabId)
                .navigation();
    }

    /**
     * 跳转FeedFragment
     */
    public static Fragment go2FeedFragment() {
        Fragment fragment = (Fragment) VioletGrape.getInstance()
                .build(RouterUrl.FeedRouter.FEED_FRAGMENT)
                .navigation();
        return fragment;
    }
}
