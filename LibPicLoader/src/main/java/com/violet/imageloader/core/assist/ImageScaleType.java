package com.violet.imageloader.core.assist;

/**
 * Created by kan212 on 2018/8/29.
 */

public enum  ImageScaleType {
    /**
     * 不压缩
     */
    NONE,
    /**
     * 根据需要以整数倍缩小图片，使得其尺寸不超过 Texture 可接受最大尺寸。
     */
    NONE_SAFE,

    /**
     * 根据需要以 2 的 n 次幂缩小图片，使其尺寸不超过目标大小，比较快的缩小方式
     */
    IN_SAMPLE_POWER_OF_2,

    /**
     * 根据需要以整数倍缩小图片，使其尺寸不超过目标大小
     */
    IN_SAMPLE_INT,

    /**
     * 根据需要缩小图片到宽或高有一个与目标尺寸一致
     */
    EXACTLY,

    /**
     * 根据需要缩放图片到宽或高有一个与目标尺寸一致。
     */
    EXACTLY_STRETCHED

}
