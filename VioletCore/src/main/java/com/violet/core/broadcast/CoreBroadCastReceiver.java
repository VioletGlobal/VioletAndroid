package com.violet.core.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by kan212 on 2018/4/19.
 *
 */

public abstract class CoreBroadCastReceiver extends BroadcastReceiver{

    @Override
    public void onReceive(Context context, Intent intent) {

    }

    /**
     * 最大大小的data：
     * #link https://stackoverflow.com/questions/28729955/max-size-of-string-data-that-can-be-passed-in-intents
     * #link https://blog.csdn.net/aman1111/article/details/50831329
     * 由于是用intent传递数据的所以内部是bundle
     * 数据过大导致2级页面启动的时候缓慢
     */
    protected void maxSize(){
        Intent intent = new Intent();
//            intent.putExtra("extra", new byte[1024 * 1024]); // 1024 KB = 1048576 B, android.os.TransactionTooLargeException
//            intent.putExtra("extra", new byte[1024 * 512]); // 512 KB = 524288 B, android.os.TransactionTooLargeException
//            intent.putExtra("extra", new byte[1024 * 506]); // 506 KB = 518144 B, android.os.TransactionTooLargeException
//            intent.putExtra("extra", new byte[1024 * 505]); // 505 KB = 517120 B, android.os.TransactionTooLargeException
        intent.putExtra("extra", new byte[1024 * 504]); // 504 KB = 516096 B, OK
    }

}
