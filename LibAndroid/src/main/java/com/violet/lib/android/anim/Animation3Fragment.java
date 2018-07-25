package com.violet.lib.android.anim;

import android.animation.AnimatorSet;
import android.animation.LayoutTransition;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.TypeEvaluator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.PointF;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;

import com.violet.base.ui.fragment.BaseFragment;

/**
 * Created by kan212 on 2018/5/14.
 * 三种Android的动画
 */

public class Animation3Fragment extends BaseFragment {
    @Override
    protected int getLayoutId() {
        return 0;
    }

    @Override
    protected void initView(View parent) {

    }

    @Override
    protected void initData(Intent intent) {

    }

    /**
     * 视图动画，也叫Tween（补间）动画可以在一个视图容器内执行一系列简单变换（位置、大小、旋转、透明度）
     * TranslateAnimation、ScaleAnimation、RotateAnimation、AlphaAnimation
     */
    private Animation myAnimation_Alpha;
    private Animation myAnimation_Scale;
    private Animation myAnimation_Translate;
    private Animation myAnimation_Rotate;

    private void ViewAnimation() {
        //第一个参数fromAlpha为 动画开始时候透明度
        //第二个参数toAlpha为 动画结束时候透明度
        myAnimation_Alpha = new AlphaAnimation(0.1f, 1.0f);
        //设置时间持续时间为 5000毫秒
        myAnimation_Alpha.setDuration(500);

        //第一个参数fromX为动画起始时 X坐标上的伸缩尺寸
        //第二个参数toX为动画结束时 X坐标上的伸缩尺寸
        //第三个参数fromY为动画起始时Y坐标上的伸缩尺寸
        //第四个参数toY为动画结束时Y坐标上的伸缩尺寸
        // 第五个参数pivotXType为动画在X轴相对于物件位置类型
        //第六个参数pivotXValue为动画相对于物件的X坐标的开始位置
        //第七个参数pivotXType为动画在Y轴相对于物件位置类型
        // 第八个参数pivotYValue为动画相对于物件的Y坐标的开始位置
        /**
         * 以上四种属性值
         * 0.0表示收缩到没有
         * 1.0表示正常无伸缩
         * 值小于1.0表示收缩
         * 值大于1.0表示放大
         */
        myAnimation_Scale = new ScaleAnimation(0.0f, 1.4f, 0.0f, 1.4f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        //第一个参数fromXDelta为动画起始时 X坐标上的移动位置
        //第二个参数toXDelta为动画结束时 X坐标上的移动位置
        //第三个参数fromYDelta为动画起始时Y坐标上的移动位置
        //第四个参数toYDelta为动画结束时Y坐标上的移动位置
        myAnimation_Translate = new TranslateAnimation(30.0f, -80.0f, 30.0f, 300.0f);

        //第一个参数fromDegrees为动画起始时的旋转角度
        //第二个参数toDegrees为动画旋转到的角度
        //第三个参数pivotXType为动画在X轴相对于物件位置类型
        //第四个参数pivotXValue为动画相对于物件的X坐标的开始位置
        //第五个参数pivotXType为动画在Y轴相对于物件位置类型
        //第六个参数pivotYValue为动画相对于物件的Y坐标的开始位置
        myAnimation_Rotate = new RotateAnimation(0.0f, +350.0f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
    }

    /**
     * Time interpolation：时间差值，LinearInterpolator、AccelerateDecelerateInterpolator，定义动画的变化率。
     * Repeat count and behavior：重复次数、以及重复模式；可以定义重复多少次；重复时从头开始，还是反向。
     * Animator sets: 动画集合，你可以定义一组动画，一起执行或者顺序执行。
     * Frame refresh delay：帧刷新延迟，对于你的动画，多久刷新一次帧；默认为10ms，但最终依赖系统的当前状态；基本不用管。
     * ObjectAnimator  动画的执行类，
     * ValueAnimator 动画的执行类，
     * AnimatorSet 用于控制一组动画的执行：线性，一起，每个动画的先后执行等。
     * AnimatorInflater 用户加载属性动画的xml文件
     * TypeEvaluator  类型估值，主要用于设置动画操作属性的值。
     * TimeInterpolator 时间插值
     */
    private void PropertyAnimation(View view) {
        /**
         * 提供了ofInt、ofFloat、ofObject，这几个方法都是设置动画作用的元素、作用的属性、动画开始、结束、以及中间的任意个属性值
         * 当对于属性值，只设置一个的时候，会认为当然对象该属性的值为开始（getPropName反射获取），然后设置的值为终点。
         * 如果设置两个，则一个为开始、一个为结束
         *
         */
        ObjectAnimator
                .ofFloat(view, "rotationX", 0.0F, 360.0F)//
                .setDuration(500)//
                .start();
        propertyValuesHolder(view);
        ValueAnimator(view);
        animationSet(view);
        LayoutAnimator(view);

    }

    /**
     * Property动画系统还提供了对ViewGroup中View添加时的动画功能，我们可以用LayoutTransition对
     * ViewGroup中的View进行动画设置显示。LayoutTransition的动画效果都是设置给ViewGroup，
     * 然后当被设置动画的 ViewGroup中添加删除View时体现出来
     * LayoutTransition.APPEARING：当View出现或者添加的时候View出现的动画。
     * LayoutTransition.CHANGE_APPEARING：当添加View导致布局容器改变的时候整个布局容器的动画。
     * LayoutTransition.DISAPPEARING：当View消失或者隐藏的时候View消失的动画。
     * LayoutTransition.CHANGE_DISAPPEARING：当删除或者隐藏View导致布局容器改变的时候整个布局容器的动画。
     * LayoutTransition.CHANGE：当不是由于View出现或消失造成对其他View位置造成改变的时候整个布局容器的动画。
     *
     * @param view
     */
    private void LayoutAnimator(View view) {
        LayoutTransition mTransitioner = new LayoutTransition();
        @SuppressLint("ObjectAnimatorBinding") ObjectAnimator anim = ObjectAnimator.ofFloat(this, "scaleX", 0, 1);
        mTransitioner.setAnimator(LayoutTransition.APPEARING, anim);
        ((ViewGroup) view).setLayoutTransition(mTransitioner);
    }

    /**
     * 动画集合，提供把多个动画组合成一个组合的机制，并可设置动画的时序关系，如同时播放、顺序播放或延迟播放
     *
     * @param view
     */
    private void animationSet(View view) {
        ObjectAnimator a1 = ObjectAnimator.ofFloat(view, "alpha", 1.0f, 0f);
        ObjectAnimator a2 = ObjectAnimator.ofFloat(view, "translationY", 0f, view.getWidth());
        AnimatorSet animSet = new AnimatorSet();
        animSet.setDuration(5000);
        animSet.setInterpolator(new LinearInterpolator());
        //animSet.playTogether(a1, a2, ...); //两个动画同时执行
        animSet.play(a1).after(a2); //先后执行
        animSet.start();
    }


    /**
     * 抛物线动画
     * ValueAnimator：属性动画中的时间驱动，管理着动画时间的开始、结束属性值，相应时间属性值计算方法等。
     * 包含所有计算动画值的核心函数以及每一个动画时间节点上的信息、一个动画是否重复、是否监听更新事件等，
     * 并且还可以设置自定义的计算类型
     * ValueAnimator只是动画计算管理驱动，设置了作用目标，但没有设置属性，需要通过updateListener里设置属性才会生效
     *
     * @param view
     */
    /**
     * Evaluators
     * Evaluators就是属性动画系统如何去计算一个属性值。它们通过Animator提供的动画的起始和结束值去计算一个动画的属性值
     * IntEvaluator：整数属性值。
     * FloatEvaluator：浮点数属性值。
     * ArgbEvaluator：十六进制color属性值。
     * TypeEvaluator：用户自定义属性值接口，譬如对象属性值类型不是int、float、color类型，
     * 你必须实现这个接口去定义自己的数据类型。
     *
     * @param view
     */
    private void ValueAnimator(final View view) {
        ValueAnimator valueAnimator = new ValueAnimator();
        valueAnimator.setDuration(3000);
        valueAnimator.setObjectValues(new PointF(0, 0));
        valueAnimator.setInterpolator(new LinearInterpolator());
        valueAnimator.setEvaluator(new TypeEvaluator<PointF>() {
            // fraction = t / duration
            @Override
            public PointF evaluate(float fraction, PointF startValue,
                                   PointF endValue) {
                // x方向200px/s ，则y方向0.5 * 10 * t
                PointF point = new PointF();
                point.x = 200 * fraction * 3;
                point.y = 0.5f * 200 * (fraction * 3) * (fraction * 3);
                return point;
            }
        });

        valueAnimator.start();
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                PointF point = (PointF) animation.getAnimatedValue();
                view.setX(point.x);
                view.setY(point.y);
            }
        });
    }

    /**
     * 实现一个动画更改多个效果：使用propertyValuesHolder
     * 多属性动画同时工作管理类。有时候我们需要同时修改多个属性，那就可以用到此类
     *
     * @param view
     */
    public void propertyValuesHolder(View view) {
        PropertyValuesHolder pvhX = PropertyValuesHolder.ofFloat("alpha", 1f,
                0f, 1f);
        PropertyValuesHolder pvhY = PropertyValuesHolder.ofFloat("scaleX", 1f,
                0, 1f);
        PropertyValuesHolder pvhZ = PropertyValuesHolder.ofFloat("scaleY", 1f,
                0, 1f);
        ObjectAnimator.ofPropertyValuesHolder(view, pvhX, pvhY, pvhZ).setDuration(1000).start();
    }


    /**
     * AccelerateDecelerateInterolator：先加速后减速。
     * AccelerateInterpolator：加速。
     * DecelerateInterpolator：减速。
     * AnticipateInterpolator：先向相反方向改变一段再加速播放。
     * AnticipateOvershootInterpolator：先向相反方向改变，再加速播放，会超出目标值然后缓慢移动至目标值，类似于弹簧回弹。
     * BounceInterpolator：快到目标值时值会跳跃。
     * CycleIinterpolator：动画循环一定次数，值的改变为一正弦函数：Math.sin(2 * mCycles * Math.PI * input)。
     * LinearInterpolator：线性均匀改变。
     * OvershottInterpolator：最后超出目标值然后缓慢改变到目标值。
     * TimeInterpolator：一个允许自定义Interpolator的接口，以上都实现了该接口。
     */
    public class AccelerateInterpolator implements Interpolator {
        private final float mFactor;
        private final double mDoubleFactor;

        public AccelerateInterpolator() {
            mFactor = 1.0f;
            mDoubleFactor = 2.0;
        }

        //input  0到1.0。表示动画当前点的值，0表示开头，1表示结尾。
        //return  插值。值可以大于1超出目标值，也可以小于0突破低值。
        @Override
        public float getInterpolation(float input) {
            //实现核心代码块
            if (mFactor == 1.0f) {
                return input * input;
            } else {
                return (float) Math.pow(input, mDoubleFactor);
            }
        }
    }

}
