package com.violet.process.bean;

import android.os.Binder;
import android.os.Parcel;
import android.os.RemoteException;

/**
 * Created by kan212 on 2018/5/15.
 */

public class MyBinder extends Binder{

    /**
     * 就是 IBinder 抽象了远程调用的接口，任何一个可远程调用的对象都应该实现这个接口。由于 IBinder 对象是一个
     * 高度抽象的结构，直接使用这个接口对于应用层的开发者而言学习成本太高，需要涉及到不少本地实现，因而 Android
     * 实现了 Binder 作为 IBinder 的抽象类，提供了一些默认的本地实现，当开发者需要自定义实现的时候，只需要重写
     * Binder 中的onTransact
     * @param code
     * @param data
     * @param reply
     * @param flags
     * @return
     * @throws RemoteException
     */
    @Override
    protected boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
        return super.onTransact(code, data, reply, flags);
    }
}
