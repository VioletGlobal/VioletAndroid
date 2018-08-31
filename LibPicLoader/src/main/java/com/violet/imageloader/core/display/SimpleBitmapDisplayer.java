package com.violet.imageloader.core.display;

import android.graphics.Bitmap;

import com.violet.imageloader.core.assist.LoadedFrom;
import com.violet.imageloader.core.imageAware.ImageAware;

/**
 * Created by kan212 on 2018/8/29.
 * 纯粹的展现
 */

public class SimpleBitmapDisplayer implements BitmapDisplayer{

    @Override
    public void display(Bitmap bitmap, ImageAware imageAware, LoadedFrom loadedFrom) {
        imageAware.setImageBitmap(bitmap);
    }
}
