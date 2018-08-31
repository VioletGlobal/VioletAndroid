package com.violet.imageloader.core.display;

import android.graphics.Bitmap;

import com.violet.imageloader.core.assist.LoadedFrom;
import com.violet.imageloader.core.imageAware.ImageAware;

/**
 * Created by kan212 on 2018/8/29.
 * 在ImageAware中显示 bitmap 对象的接口。可在实现中对 bitmap 做一些额外处理，比如加圆角、动画效果。
 */

public interface BitmapDisplayer {

     void display(Bitmap bitmap, ImageAware imageAware,LoadedFrom loadedFrom);
}
