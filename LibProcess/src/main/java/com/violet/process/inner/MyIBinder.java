package com.violet.process.inner;

import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;

/**
 * Created by kan212 on 2018/5/15.
 */

public  interface MyIBinder extends IBinder{

    //查看 binder 对应的进程是否存活
    @Override
    boolean pingBinder();

    // 查看 binder 是否存活，需要注意的是，可能在返回的过程中，binder 不可用
    @Override
    boolean isBinderAlive();

    /**
     * 执行一个对象的方法，
     *
     * @param 需要执行的命令
     * @param 传输的命令数据，这里一定不能为空
     * @param 目标 Binder 返回的结果，可能为空
     * @param 操作方式，0 等待 RPC 返回结果，1 单向的命令，最常见的就是 Intent.
     */
    /**
     * 根据Uid 和 Pid （用户id和进程id）进行相应的校验，校验通过后，将相应的数据写入
     * writeTransactionData，其后在 waitForResponse 里面读取前面写入的值，并执行相应的方法，最后返回结果
     * @param code
     * @param data
     * @param reply
     * @param flags
     * @return
     * @throws RemoteException
     */
    @Override
    boolean transact(int code, Parcel data, Parcel reply, int flags) throws RemoteException;



    // 注册对Binder死亡通知的观察者，在其死亡后，会收到相应的通知
    @Override
    void linkToDeath(DeathRecipient recipient, int flags) throws RemoteException;


}
