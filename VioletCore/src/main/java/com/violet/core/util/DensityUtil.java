package com.violet.core.util;

import android.util.DisplayMetrics;

import com.violet.core.VioletBaseCore;

public class DensityUtil {
    private static int sDensityDpi = 0;
    private static float sDensity = 0.0f;//屏幕密度
    private static float sScreenHeight = 0;
    private static float sScreenWidth = 0;
    private static float sScreenWidthDpi = 0;
    private static String sScreenSize = "";

    static {
        DisplayMetrics display = VioletBaseCore.getInstance().getContext().getResources()
                .getDisplayMetrics();
        sDensityDpi = display.densityDpi;
        sDensity = display.density;
        sScreenHeight = display.heightPixels;
        sScreenWidth = display.widthPixels;
        sScreenWidthDpi = sScreenWidth / sDensity;
    }

    public static final DisplayMetrics DISPLAY_METRICS = VioletBaseCore.getInstance().getContext()
            .getResources().getDisplayMetrics();

    /**
     * @param dpValue 尺寸dp值
     * @return 像素值
     */
    public static int dip2px(float dpValue) {
        return (int) (dpValue * getDensity() + 0.5F);
    }

    /**
     * @param pxValue 像素尺寸值
     * @return dip值
     */
    public static int px2dip(float pxValue) {
        return (int) (pxValue / getDensity() + 0.5F);
    }

    /**
     * @param spValue sp值
     * @return 像素值
     */
    public static int sp2px(float spValue) {
        return (int) (spValue * getDensity() + 0.5F);
    }

    /**
     * @param dimenId
     * @return  dip值乘以屏幕密度
     */
    public static int dpToPixcel(int dimenId) {
        return (int) VioletBaseCore.getInstance().getContext()
                .getResources().getDimension(dimenId);
    }

    public static int getDensityDpi() {
        return sDensityDpi;
    }

    /**
     * @return 获取屏幕密度
     */
    public static float getDensity() {
        return sDensity;
    }

    /**
     * @return  获取屏幕纵向尺寸
     */
    public static float getScreenPortraitWidth() {
        if (sScreenHeight > sScreenWidth) {
            return sScreenWidth;
        } else {
            return sScreenHeight;
        }
    }

    /**
     * @return 获取屏幕横向尺寸
     */
    public static float getScreenPortraitHeight() {
        if (sScreenHeight > sScreenWidth) {
            return sScreenHeight;
        } else {
            return sScreenWidth;
        }
    }

    /**
     * @return 获取屏幕宽度
     */
    public static float getScreenWidth() {
        return sScreenWidth;
    }

    /**
     * @return 获取屏幕高度
     */
    public static float getScreenHeight() {
        return sScreenHeight;
    }

    /**
     * @return 获取屏幕宽度
     */
    public static float getScreenWidthDpi() {
        return sScreenWidthDpi;
    }

    /**
     * 获取屏幕分辨率
     * @return 字符串形式，"Height * Width"
     */
    public static String getScreenSize() {
        if (SNTextUtils.isEmptyOrBlank(sScreenSize)) {
            int height = (int) sScreenHeight;
            int width = (int) sScreenWidth;
            sScreenSize = height + "*" + width;
        }
        return sScreenSize;
    }
}