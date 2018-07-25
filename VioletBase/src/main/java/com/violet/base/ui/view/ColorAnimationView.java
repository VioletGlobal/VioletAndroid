package com.violet.base.ui.view;

import android.animation.Animator;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by kan212 on 2018/4/12.
 * #link https://github.com/TaurusXi/GuideBackgroundColorAnimation
 * viewpager 切换时候背景色的变换
 */

public class ColorAnimationView extends View implements Animator.AnimatorListener, ValueAnimator.AnimatorUpdateListener {

    private static final int RED = 0xffFF8080;
    private static final int BLUE = 0xff8080FF;
    private static final int WHITE = 0xffffffff;
    private static final int GREEN = 0xff80ff80;
    private static final int DISCOVER_BG = 0xff0C0921;
    private static final int DURATION = 3000;

    private PageChangeListener mPageChangeListener;
    ValueAnimator colorAnim = null;
    private OnColorChange mOnColorChange;

    public ColorAnimationView(Context context) {
        this(context,null,0);
    }

    public ColorAnimationView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public ColorAnimationView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mPageChangeListener = new PageChangeListener();
    }

    /**
     * 这是你唯一需要关心的方法
     *
     * @param mViewPager 你必须在设置 Viewpager 的 Adapter 这后，才能调用这个方法。
     * @param count      ,viewpager孩子的数量
     * @param colors     int... colors ，你需要设置的颜色变化值~~ 如何你传人 空，那么触发默认设置的颜色动画
     */
    public void setViewPager(ViewPager mViewPager, int count, int... colors) {
        if (mViewPager.getAdapter() == null) {
            throw new IllegalStateException(
                    "ViewPager does not have adapter instance.");
        }
        mPageChangeListener.setViewPagerChildCount(count);

        mViewPager.setOnPageChangeListener(mPageChangeListener);
        if (colors.length == 0) {
            createDefaultAnimation();
        } else {
            createAnimation(this, colors);
        }
    }

    public void setColors(int... colors) {
        if (colors.length == 0) {
            createDefaultAnimation();
        } else {
            createAnimation(this, colors);
        }

    }

    @Override
    public void onAnimationStart(Animator animation) {

    }

    @Override
    public void onAnimationEnd(Animator animation) {
        mOnColorChange.cancel();
    }

    @Override
    public void onAnimationCancel(Animator animation) {
        mOnColorChange.cancel();
    }

    @Override
    public void onAnimationRepeat(Animator animation) {

    }

    @Override
    public void onAnimationUpdate(ValueAnimator animation) {

    }

    class PageChangeListener implements ViewPager.OnPageChangeListener {

        private int viewPagerChildCount;

        public void setViewPagerChildCount(int viewPagerChildCount) {
            this.viewPagerChildCount = viewPagerChildCount;
        }

        public int getViewPagerChildCount() {
            return viewPagerChildCount;
        }

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            int count = getViewPagerChildCount() - 1;
            if (count != 0) {
                float length = (position + positionOffset) / count;
                int progress = (int) (length * DURATION);
                ColorAnimationView.this.seek(progress);
            }
        }

        @Override
        public void onPageSelected(int position) {

        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    }

    private void seek(long seekTime) {
        if (colorAnim == null) {
            createDefaultAnimation();
        }
        colorAnim.setCurrentPlayTime(seekTime);
    }

    private void createDefaultAnimation() {
        //控制颜色变化，实现view的背景色从透明红色->不透明蓝色变化
        colorAnim = ObjectAnimator.ofInt(this,
                "backgroundColor", WHITE, RED, BLUE, GREEN, DISCOVER_BG);
        colorAnim.setEvaluator(new ArgbEvaluator());
        colorAnim.setDuration(DURATION);
        colorAnim.addUpdateListener(this);
    }

    private void createAnimation(View view, int[] colors) {
        if (null == colorAnim) {
            colorAnim = ObjectAnimator.ofInt(view,
                    "backgroundColor", colors);
            colorAnim.setEvaluator(new ArgbEvaluator());
            colorAnim.setDuration(DURATION);
            colorAnim.setRepeatCount(ValueAnimator.INFINITE);
            colorAnim.setRepeatMode(ValueAnimator.RESTART);
            colorAnim.addUpdateListener(this);
        }
    }

    public void setOnColorChange(OnColorChange onColorChange) {
        mOnColorChange = onColorChange;
    }

    public void start() {
        colorAnim.start();
    }


    public void cancel() {
        colorAnim.cancel();
        mOnColorChange.cancel();
    }

    public interface OnColorChange {
        void onChange(int color);

        void cancel();
    }
}
