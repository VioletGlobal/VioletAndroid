package com.violet.net.sports.request.bean;

import java.io.Serializable;

/**
 * Created by kan212 on 2018/7/27.
 */

public class BaseSportsBean implements Serializable{


    /**
     * 默认ViewHolder类型，默认值为-1
     */
    public static final int VIEW_HOLDER_TYPE_DEFAULT = -1;

    /**
     * 对应ViewHolder的类型属性
     */
    public int mViewHolderType = VIEW_HOLDER_TYPE_DEFAULT;

}
