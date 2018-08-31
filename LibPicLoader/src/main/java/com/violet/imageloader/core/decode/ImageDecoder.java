package com.violet.imageloader.core.decode;

import android.graphics.Bitmap;

import java.io.IOException;

/**
 * Created by kan212 on 2018/8/29.
 * 图片解码器，负责将图片输入流InputStream转换为Bitmap对象
 */

public interface ImageDecoder {

    Bitmap decode(ImageDecodingInfo imageDecodingInfo) throws IOException;

}
