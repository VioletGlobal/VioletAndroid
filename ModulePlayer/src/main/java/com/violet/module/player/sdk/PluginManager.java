package com.violet.module.player.sdk;

import android.content.Context;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by kan212 on 2018/9/5.
 * 热更新的相关的，目前有问题，所以先弄成老版本的
 */

public class PluginManager {

    private static PluginManager sPluginManager;
    private Context mContext;
    private ExecutorService mSinglePoolExecutor;

    public PluginManager(Context context) {
        mContext = context;
        sPluginManager = this;
        mSinglePoolExecutor = Executors.newSingleThreadExecutor();
    }

    /**
     * 初始化播放器
     *
     * @param forceReload
     */
    public void initPlayer(boolean forceReload) {
        mSinglePoolExecutor.execute(new InitRunnable(forceReload));
    }

    private static class InitRunnable implements Runnable {

        private boolean mForce = false;

        public InitRunnable() {
        }

        public InitRunnable(boolean foce) {
            this.mForce = foce;
        }

        @Override
        public void run() {
            try{
                sPluginManager.copyMINIcore(mForce);
            }catch (Exception e){

            }
        }
    }

    public void copyMINIcore(boolean forceReload) {

    }
}
