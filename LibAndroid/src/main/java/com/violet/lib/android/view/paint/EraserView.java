package com.violet.lib.android.view.paint;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;

import com.violet.lib.android.R;

/**
 * Created by kan212 on 2018/6/15.
 * 橡皮擦自定义view
 */

public class EraserView extends View {

    private int touchSlop = 8; // 滑动距离阀值：如果在屏幕上滑动距离小于此值则不会绘制
    private int fgColor = 0xFFAAAAAA; // 前景颜色
    private Bitmap fgBitmap = null; // 前景图片
    private Drawable bgDrawable = null; // 背景图片
    private float lastX; // 记录上一个触摸事件的位置坐标
    private float lastY;
    private Path path = null; // 橡皮擦的摩擦路径
    private Paint paint = null; // 模拟橡皮擦的画笔
    private Canvas pathCanvas = null; // 用于绘制橡皮擦路径的canva

    public EraserView(Context context, int bgResId, int fgColorId) {
        super(context);
        if (bgResId < 0) {
            throw new IllegalArgumentException("EraserView args error!");
        }
        Resources resources = context.getResources();
        bgDrawable = resources.getDrawable(bgResId);
        fgColor = resources.getColor(fgColorId);
        if (null == bgDrawable) {
            throw new IllegalArgumentException("EraserView args error!");
        }
        init();
    }

    public EraserView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public EraserView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.EraserView);
        bgDrawable = ta.getDrawable(R.styleable.EraserView_eraser_view_bg);
        fgColor = ta.getColor(R.styleable.EraserView_eraser_view_fg, 0xFFAAAAAA);
        ta.recycle();

        if (null == bgDrawable) {
            throw new IllegalArgumentException("EraserView args error!");
        }
        bgDrawable.setCallback(this);
        init();
    }

    private void init() {

        ViewConfiguration viewConfiguration = ViewConfiguration.get(getContext());
        touchSlop = viewConfiguration.getScaledTouchSlop();

        path = new Path();
        paint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.TRANSPARENT);
        paint.setStrokeWidth(50);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
        paint.setColor(Color.TRANSPARENT);

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (null != bgDrawable) {
            bgDrawable.setBounds(0, 0, getWidth(), getHeight());
        }
        createFgBitmap();
    }

    private void createFgBitmap() {
        fgBitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
        pathCanvas = new Canvas(fgBitmap);
        pathCanvas.drawColor(fgColor);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                lastX = x;
                lastY = y;
                path.reset();
                path.moveTo(x, y);
                break;
            }
            case MotionEvent.ACTION_MOVE: {
                float offsetX = x - lastX;
                float offsetY = y - lastY;
                if (offsetX >= touchSlop || offsetY >= touchSlop) {
                    path.quadTo(lastX, lastY, x, y);
                    lastX = x;
                    lastY = y;
                }
                break;
            }
            case MotionEvent.ACTION_UP: {
                break;
            }
        }
        invalidate();
        return true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        bgDrawable.draw(canvas);
        canvas.drawBitmap(fgBitmap, 0, 0, null);
        pathCanvas.drawPath(path, paint);
    }

}
