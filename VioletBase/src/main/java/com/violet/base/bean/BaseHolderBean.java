package com.violet.base.bean;

import java.io.Serializable;

/**
 * Created by kan212 on 2018/6/11.
 */

public class BaseHolderBean implements Serializable{

    private static final long serialVersionUID = 4000096879942013198L;

    /**
     * 默认ViewHolder类型，默认值为-1
     */
    public static final int VIEW_HOLDER_TYPE_DEFAULT = -1;

    /**
     * 对应ViewHolder的类型属性
     */
    public int mViewHolderType = VIEW_HOLDER_TYPE_DEFAULT;
}
