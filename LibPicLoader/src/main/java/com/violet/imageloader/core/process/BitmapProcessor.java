package com.violet.imageloader.core.process;

import android.graphics.Bitmap;

/**
 * Created by kan212 on 2018/8/29.
 */

public interface BitmapProcessor {

    /**
     * 图片处理接口。可用于对图片预处理(Pre-process Bitmap)和后处理(Post-process Bitmap)。抽象函数：
     * 用户可以根据自己需求去实现它。比如你想要为你的图片添加一个水印，那么可以自己去实现 BitmapProcessor 接口，
     * 在DisplayImageOptions中配置 Pre-process 阶段预处理图片，这样设置后存储在文件系统以及内存缓存中的图片都是加
     * 了水印后的。如果只希望在显示时改变不动原图片，可以在BitmapDisplayer中处理。
     *
     * @param bitmap
     * @return
     */
    Bitmap process(Bitmap bitmap);
}
