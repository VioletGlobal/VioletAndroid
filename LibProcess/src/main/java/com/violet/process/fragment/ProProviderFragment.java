package com.violet.process.fragment;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.os.SystemClock;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.violet.base.ui.fragment.BaseFragment;
import com.violet.core.service.ThreadPoolManager;
import com.violet.core.util.LogUtil;
import com.violet.process.IProcessAidl;
import com.violet.process.R;
import com.violet.process.bean.ProcessBean;
import com.violet.process.grape.ProcessRouterUrl;
import com.violet.process.provider.IPCProcessProvider;
import com.violet.process.service.IProcessAidlService;
import com.violet.process.service.ProMessengerService;
import com.violet.process.service.TCPServerService;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;
import java.util.Random;

import static android.content.Context.BIND_AUTO_CREATE;
import static com.violet.process.service.ProMessengerService.MSG_CONTENT;
import static com.violet.process.service.ProMessengerService.MSG_ID_CLIENT;
import static com.violet.process.service.ProMessengerService.MSG_ID_SERVER;
import static com.violet.process.service.TCPServerService.TEST_SOCKET_PORT;

/**
 * Created by kan212 on 2018/4/18.
 * contentProvider的demo
 */
@Route(path = ProcessRouterUrl.ProcessFragmentRouter.FRAGMENT_PROVIDER)
public class ProProviderFragment extends BaseFragment implements View.OnClickListener{

    TextView mTvResult;
    Button mBtnAddPerson;
    EditText mEtMsgContent;
    Button mBtnSendMsg;
    TextView mTvMessengerServiceState;
    TextView mTvCpResult;
    TextView mTvSocketMessage;
    EditText mEtClientSocket;
    Button mBtSendSocket;

    private IProcessAidl mIProcessAidl;
    private ProSocketHandler mProSocketHandler;
    private Socket mClientSocket;
    private PrintWriter mPrintWriter;

    //服务端的 Messenger
    private Messenger mServerMessenger;


    @Override
    protected int getLayoutId() {
        return R.layout.pro_fragment_provider;
    }

    @Override
    protected void initView(View parent) {
        mTvResult = (TextView) parent.findViewById(R.id.tv_result);
        mBtnAddPerson = (Button) parent.findViewById(R.id.btn_add_person);
        mEtMsgContent = (EditText) parent.findViewById(R.id.et_msg_content);
        mBtnSendMsg = (Button) parent.findViewById(R.id.btn_send_msg);
        mTvMessengerServiceState = (TextView) parent.findViewById(R.id.tv_messenger_service_state);
        mTvCpResult = (TextView) parent.findViewById(R.id.tv_cp_result);
        mTvSocketMessage = (TextView) parent.findViewById(R.id.tv_socket_message);
        mTvSocketMessage = (TextView) parent.findViewById(R.id.tv_socket_message);
        mEtClientSocket = (EditText) parent.findViewById(R.id.et_client_socket);
        mBtSendSocket = (Button) parent.findViewById(R.id.bt_send_socket);
        mBtnAddPerson.setOnClickListener(this);
        mBtnSendMsg.setOnClickListener(this);
        mBtSendSocket.setOnClickListener(this);
    }

    @Override
    protected void initData(Intent intent) {
        bindAIDLService();
        bindMessengerService();
        getContentFromContentProvider();
        bindSocketService();
    }

    private void bindSocketService() {
        Intent intent = new Intent(getActivity(), TCPServerService.class);
        getActivity().startService(intent);

        mProSocketHandler = new ProSocketHandler();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    connectSocketServer();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }

    private int id = 2;

    private void getContentFromContentProvider() {
        Uri uri = IPCProcessProvider.PROCESS_CONTENT_URI;
        ContentValues contentValues = new ContentValues();
        contentValues.put("_id", id ++);
        contentValues.put("name", "violet" + SystemClock.currentThreadTimeMillis());
        contentValues.put("description", "beautiful girl");
        ContentResolver contentResolver = getActivity().getContentResolver();
        contentResolver.insert(uri, contentValues);

        Cursor cursor = contentResolver.query(uri, new String[]{"name", "description"}, null, null, null, null);
        if (cursor == null) {
            return;
        }
        StringBuilder cursorResult = new StringBuilder("DB 查询结果：");
        while (cursor.moveToNext()) {
            String result = cursor.getString(0) + ", " + cursor.getString(1);
            LogUtil.d("DB 查询结果：" + result);
            cursorResult.append("\n").append(result);
        }
        mTvCpResult.setText(cursorResult.toString());
        cursor.close();

    }

    private void bindMessengerService() {
        Intent intent = new Intent(getActivity(), ProMessengerService.class);
        getActivity().bindService(intent, mMessengerConnection, BIND_AUTO_CREATE);
    }


    private void bindAIDLService() {
        Intent intent = new Intent();
        intent.setComponent(new ComponentName("com.violet.process", "com.violet.process.service.IProcessAidlService"));
        getActivity().bindService(intent, mAidlCon, BIND_AUTO_CREATE);

        Intent intent1 = new Intent(getActivity(), IProcessAidlService.class);
        getActivity().bindService(intent1, mAidlCon, BIND_AUTO_CREATE);
    }

    private void connectSocketServer() throws IOException {
        Socket socket = null;
        while (null == socket) {
            try {
                socket = new Socket("localhost", TEST_SOCKET_PORT);
                mClientSocket = socket;
                mPrintWriter = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
            } catch (IOException ex) {
                SystemClock.sleep(1_000);
            }
        }
        mProSocketHandler.sendEmptyMessage(ProSocketHandler.CODE_SOCKET_CONNECT);

        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        while (null != getActivity() && !getActivity().isFinishing()) {
            final String msg = in.readLine();
            if (!TextUtils.isEmpty(msg)) {
                mProSocketHandler.obtainMessage(ProSocketHandler.CODE_SOCKET_MSG,
                        "\n" + "\nserver : " + msg)
                        .sendToTarget();
            }
            SystemClock.sleep(1_000);
        }

        System.out.println("Client quit....");
        mPrintWriter.close();
        in.close();
        socket.close();
    }

    private ServiceConnection mAidlCon = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            //连接后拿到 Binder，转换成 AIDL，在不同进程会返回个代理
            mIProcessAidl = IProcessAidl.Stub.asInterface(service);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mIProcessAidl = null;
        }
    };

    private ServiceConnection mMessengerConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mServerMessenger = new Messenger(service);
            mTvMessengerServiceState.setText(mTvMessengerServiceState.getText().toString() + " 服务已连接");
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mServerMessenger = null;
            mTvMessengerServiceState.setText(mTvMessengerServiceState.getText().toString() + " 服务断开");
        }
    };

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_add_person){
            addProcess();
        }else if (v.getId() == R.id.btn_send_msg){
            sendMsg();
        }else if (v.getId() == R.id.bt_send_socket){
            sendMsgToSocketServer();
        }
    }

    public void addProcess() {
        Random random = new Random();
        ProcessBean person = new ProcessBean("violet" + random.nextInt(10));

        try {
            mIProcessAidl.addProcessBean(person);
            List<ProcessBean> personList = mIProcessAidl.getProcessBeanList();
            mTvResult.setText(personList.toString());
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void sendMsg() {
        String msgContent = mEtMsgContent.getText().toString();
        msgContent = TextUtils.isEmpty(msgContent) ? "默认消息" : msgContent;

        Message message = Message.obtain();
        message.arg1 = MSG_ID_CLIENT;
        Bundle bundle = new Bundle();
        bundle.putString(MSG_CONTENT, msgContent);
        message.setData(bundle);
        message.replyTo = mClientMessenger;     //指定回信人是客户端定义的

        try {
            mServerMessenger.send(message);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }


    private class ProSocketHandler extends Handler {

        public static final int CODE_SOCKET_CONNECT = 1;
        public static final int CODE_SOCKET_MSG = 2;

        @Override
        public void handleMessage(final Message msg) {
            switch (msg.what) {
                case CODE_SOCKET_CONNECT:
                    mBtSendSocket.setEnabled(true);
                    break;
                case CODE_SOCKET_MSG:
                    mTvSocketMessage.setText(mTvSocketMessage.getText() + (String) msg.obj);
                    break;
            }
        }
    }

    /**
     * 客户端的 Messenger
     */
    @SuppressLint("HandlerLeak")
    Messenger mClientMessenger = new Messenger(new Handler() {
        @Override
        public void handleMessage(final Message msg) {
            if (msg != null && msg.arg1 == MSG_ID_SERVER) {
                if (msg.getData() == null) {
                    return;
                }

                String content = (String) msg.getData().get(MSG_CONTENT);
                LogUtil.d( "Message from server: " + content);
            }
        }
    });

    public void sendMsgToSocketServer() {
        final String msg = mEtClientSocket.getText().toString();
        if (!TextUtils.isEmpty(msg) && mPrintWriter != null) {
            ThreadPoolManager.getInstance().addTask(new Runnable() {
                @Override
                public void run() {
                    mPrintWriter.println(msg);
                }
            });
            mEtClientSocket.setText("");
            mTvSocketMessage.setText(mTvSocketMessage.getText() + "\n" + "\nclient : " + msg);
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unbindService(mAidlCon);
        getActivity().unbindService(mMessengerConnection);
//        stopService(new Intent(this, TCPServerService.class));
        try {
            if (mClientSocket != null) {
                mClientSocket.shutdownInput();
                mClientSocket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
