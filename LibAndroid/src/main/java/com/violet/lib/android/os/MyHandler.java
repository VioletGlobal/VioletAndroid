package com.violet.lib.android.os;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

/**
 * Created by kan212 on 2018/5/16.
 */

public class MyHandler extends Handler {


    /**
     * 1、 Handler 的生命周期与Activity 不一致
     * 当Android应用启动的时候，会先创建一个UI主线程的Looper对象，Looper实现了一个简单的消息队列，
     * 一个一个的处理里面的Message对象。主线程Looper对象在整个应用生命周期中存在。
     * 当在主线程中初始化Handler时，该Handler和Looper的消息队列关联（没有关联会报错的）。发送到消息队列的
     * Message会引用发送该消息的Handler对象，这样系统可以调用 Handler#handleMessage(Message) 来分发处理该消息。
     * 2、handler 引用 Activity 阻止了GC对Acivity的回收
     * 在Java中，非静态(匿名)内部类会默认隐性引用外部类对象。而静态内部类不会引用外部类对象
     * 如果外部类是Activity，则会引起Activity泄露
     * 当Activity finish后，延时消息会继续存在主线程消息队列中1分钟，然后处理消息。而该消息引用了Activity的
     * Handler对象，然后这个Handler又引用了这个Activity。这些引用对象会保持到该消息被处理完，这样就导致该Activity
     * 对象无法被回收，从而导致了上面说的 Activity泄露
     *
     */
    public MyHandler() {
        super();
    }

    public MyHandler(Looper looper, Callback callback) {
        super(looper, callback);
    }

    @Override
    public void handleMessage(Message msg) {
        super.handleMessage(msg);
    }

    @Override
    public void dispatchMessage(Message msg) {
        super.dispatchMessage(msg);
    }

    @Override
    public String getMessageName(Message message) {
        return super.getMessageName(message);
    }

    @Override
    public boolean sendMessageAtTime(Message msg, long uptimeMillis) {
        return super.sendMessageAtTime(msg, uptimeMillis);
    }

}
