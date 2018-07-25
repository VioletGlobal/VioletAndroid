package com.violet.base.inter;

import android.os.Bundle;
import android.view.View;

/**
 * Created by kan212 on 2018/6/11.
 */

public interface OnViewHolderCallbackListener {
    /**
     * 回馈方法
     *  @param view
     * @param position
     * @param bundle 回馈数据
     */
    public void callback(View view, int position, Bundle bundle);
}
