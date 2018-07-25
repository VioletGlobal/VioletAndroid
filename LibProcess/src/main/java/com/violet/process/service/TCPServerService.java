package com.violet.process.service;

import android.text.TextUtils;

import com.violet.core.service.CoreService;
import com.violet.core.service.ThreadPoolManager;
import com.violet.core.util.LogUtil;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by kan212 on 2018/4/19.
 * 使用 Socket 进行 IPC 的例子
 */

public class TCPServerService extends CoreService {

    public static final int TEST_SOCKET_PORT = 8688;
    private boolean mIsServiceDisconnected;


    @Override
    public void onCreate() {
        super.onCreate();
        new Thread(new TCPServer()).start();
    }

    class TCPServer implements Runnable {

        @Override
        public void run() {
            ServerSocket serverSocket;

            try {
                serverSocket = new ServerSocket(TEST_SOCKET_PORT);
                LogUtil.d("服务创建");
            } catch (IOException e) {
                e.printStackTrace();
                LogUtil.d("服务创建失败");
                return;
            }

            while (!mIsServiceDisconnected) {
                try {
                    ////接受客户端消息，阻塞直到收到消息
                    Socket client = serverSocket.accept();
                    ThreadPoolManager.getInstance().addTask(responseClient(client));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private Runnable responseClient(final Socket client) {
        return new Runnable() {
            @Override
            public void run() {
                try {
                    //接受消息
                    BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
                    //回复消息
                    PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(client.getOutputStream())), true);
                    LogUtil.d("服务端已经连接");
                    while (!mIsServiceDisconnected) {
                        String inputStr = in.readLine();
                        LogUtil.d("收到客户端的消息：" + inputStr);
                        if (TextUtils.isEmpty(inputStr)){
                            LogUtil.d("收到消息为空，客户端断开连接 ***");
                            break;
                        }
                        out.println("【" + inputStr + "]"+ " fuck the world" );
                    }
                    out.close();
                    in.close();
                    client.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
    }

    @Override
    public void onDestroy() {
        mIsServiceDisconnected = true;
        super.onDestroy();
    }
}
