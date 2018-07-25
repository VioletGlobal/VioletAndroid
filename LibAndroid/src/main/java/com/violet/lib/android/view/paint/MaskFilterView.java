package com.violet.lib.android.view.paint;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.EmbossMaskFilter;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.violet.lib.android.R;

/**
 * Created by kan212 on 2018/6/15.
 */

public class MaskFilterView extends View {

    private Paint paint = null;
    private Bitmap bitmap = null;

    public MaskFilterView(Context context) {
        super(context, null, 0);
        setLayerType(LAYER_TYPE_SOFTWARE, null);
    }

    public MaskFilterView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MaskFilterView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setLayerType(LAYER_TYPE_SOFTWARE, null);
        initPaint();
        bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher_round);
    }


    void initPaint() {
        paint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
        /**
         * MaskFilter是用来在绘制之前变化透明度通道值的基类，因此我们可以先获取到图片的alpha通道值并且对图片的alpha通道值进行模糊处理，
         * 然后依次绘制图片的alpha通道值和图片，代码如下：
         */
//        BlurMaskFilter blurMaskFilter = new BlurMaskFilter(30, BlurMaskFilter.Blur.NORMAL);
//        paint.setMaskFilter(blurMaskFilter);

        /**
         * 由上面的代码可知，EmbossMaskFilter通过指定光源的方向、环境光强度、镜面高亮系数和模糊半径实现来实现浮雕效果。
         * EmbossMaskFilter是通过构造方法指定上面所说的4个属性，构造方法参数如下：
         * direction：光源的方向，取值为长度为3的数组[x,y,z]
         * ambient：环境光的强度，取值范围0...1
         * specular: 镜面高亮系数
         * blurRadius：模糊半径
         */
        float[] direction = new float[]{10, 10, 10};
        float ambient = 0.5f;
        float specular = 5;
        float blurRadius = 5;
        EmbossMaskFilter embossMaskFilter = new EmbossMaskFilter(direction, ambient, specular, blurRadius);
        paint.setMaskFilter(embossMaskFilter);


        paint.setColor(Color.BLUE);
        paint.setTextSize(210f);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawText("ANDROID", 100, 300, paint);
        canvas.drawRect(200, 500, 500, 800, paint);
        canvas.translate(200, 1000);
        canvas.drawBitmap(bitmap, 0, 0, paint);
    }

}
