package com.violet.process.service;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.annotation.Nullable;

import com.violet.core.service.CoreService;
import com.violet.core.util.LogUtil;

/**
 * Created by kan212 on 2018/4/19.
 */

public class ProMessengerService extends CoreService {

    public static final int MSG_ID_CLIENT = 0x001;
    public static final String MSG_CONTENT = "msg_content";
    public static final int MSG_ID_SERVER = 0x002;


    @SuppressLint("HandlerLeak")
    Messenger mMessenger = new Messenger(new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if (msg != null && msg.arg1 == MSG_ID_CLIENT) {
                if (msg.getData() == null) {
                    return;
                }
                String content = (String) msg.getData().get(MSG_CONTENT);  //接收客户端的消息
                LogUtil.d( "Message from client: " + content);

                //回复消息给客户端
                Message replyMsg = Message.obtain();
                replyMsg.arg1 = MSG_ID_SERVER;
                Bundle bundle = new Bundle();
                bundle.putString(MSG_CONTENT, "听到你的消息了，请说点正经的");
                replyMsg.setData(bundle);

                try {
                    msg.replyTo.send(replyMsg);     //回信
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }
    });

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mMessenger.getBinder();
    }
}
