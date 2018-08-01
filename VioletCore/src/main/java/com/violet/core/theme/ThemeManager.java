package com.violet.core.theme;

import android.app.Activity;
import android.graphics.PixelFormat;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;

import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by kan212 on 2018/8/1.
 */

public class ThemeManager {

    public interface OnThemeChangedListener {
        public boolean dispatchThemeChanged(boolean nightMode);

        public boolean onThemeChanged(boolean nightMode);
    }

    public interface ThemeConfig {
        public void setNightMode(boolean nightMode);

        public boolean isNightMode();
    }

    public interface ThemeChangeNotifier {
        public void notifyThemeChanged(boolean nightMode);
    }

    public final static int NIGHT_MASK_COLOR = 0xA5000000;

    private static ThemeManager sInstance;

    private boolean mNightMode;

    // Activity与其Mask View之间的映射
    private Map<Reference<Activity>, View> mActivityMasks;
    // 当Activity被回收时，其对应的Reference会放到这个Queue里
    private ReferenceQueue<Activity> mEmptyReferences;

    private ThemeConfig mThemeConfig;
    private ThemeChangeNotifier mThemeChangeNotifier;

    public static ThemeManager getInstance() {
        if (sInstance == null) {
            synchronized (ThemeManager.class) {
                if (sInstance == null) {
                    sInstance = new ThemeManager();
                }
            }
        }
        return sInstance;
    }

    private ThemeManager() {
        mNightMode = false;
        mActivityMasks = Collections
                .synchronizedMap(new LinkedHashMap<Reference<Activity>, View>());
        mEmptyReferences = new ReferenceQueue<Activity>();
    }

    public void init(ThemeConfig config, ThemeChangeNotifier notifier) {
        mThemeConfig = config;
        mThemeChangeNotifier = notifier;

        if (mThemeConfig != null) {
            mNightMode = mThemeConfig.isNightMode();
        }
    }

    public void setNightMode(boolean nightMode) {
        if (mNightMode == nightMode) {
            return;
        }

        mNightMode = nightMode;

        if (mThemeConfig != null) {
            mThemeConfig.setNightMode(mNightMode);
        }

        notifyThemeChanged();
    }

    public boolean isNightMode() {
        return mNightMode;
    }

    public void notifyThemeChanged() {
        // EventBus.getDefault().post(new ChangeTheme(mNightMode));
        if (mThemeChangeNotifier != null) {
            mThemeChangeNotifier.notifyThemeChanged(mNightMode);
        }

        if (!mNightMode) {
            // 切换日间模式时清除夜间Mask
            clearNightMask();
        }
    }

    private Reference<Activity> getActivityReference(Activity activity) {
        Reference<? extends Activity> emptyRef = null;
        while ((emptyRef = mEmptyReferences.poll()) != null) {
            mActivityMasks.remove(emptyRef);
        }

        for (Reference<Activity> ref : mActivityMasks.keySet()) {
            if (ref.get() == activity) {
                return ref;
            }
        }

        return new SoftReference<Activity>(activity, mEmptyReferences);
    }

    private void clearNightMask() {
        for (Reference<Activity> ref : mActivityMasks.keySet()) {
            unregisterNightMask(ref.get());
        }
    }

    /**
     * 给当前Activity注册夜间模式Mask，推荐在onCreate事件中setContentView()之后注册 本方法在非夜间模式下不做任何事
     *
     * @param activity
     */
    public void registerNightMask(Activity activity) {
        registerNightMask(activity, LinearLayout.LayoutParams.MATCH_PARENT);
    }

    /**
     * 给当前Activity注册夜间模式Mask，推荐在onCreate事件中setContentView()之后注册 本方法在非夜间模式下不做任何事
     *
     * @param activity
     */
    public void registerNightMask(Activity activity, int maskHeight) {
        if (!isNightMode() || activity == null) {
            return;
        }

        Reference<Activity> actRef = getActivityReference(activity);
        View mask = mActivityMasks.get(actRef);
        if (mask != null) {
            // 当前activity已有mask
            return;
        }

        mask = new View(activity);
        mask.setBackgroundColor(NIGHT_MASK_COLOR);

        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, maskHeight,
                WindowManager.LayoutParams.TYPE_APPLICATION,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                        | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);
        params.gravity = Gravity.BOTTOM;

        activity.getWindowManager().addView(mask, params);
        mActivityMasks.put(actRef, mask);
    }

    /**
     * 给当前Activity取消夜间模式Mask，推荐在onDestory事件中最后一行调用 本方法在非夜间模式下不做任何事
     *
     * @param activity
     */
    public void unregisterNightMask(Activity activity) {
        if (!isNightMode() || activity == null) {
            return;
        }

        Reference<Activity> actRef = getActivityReference(activity);
        View mask = mActivityMasks.get(actRef);
        if (mask == null) {
            // 当前activity没有mask
            return;
        }

        activity.getWindowManager().removeView(mask);
        mActivityMasks.remove(actRef);
    }

}
