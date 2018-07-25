package com.violet.process.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.util.Log;

import com.violet.process.IProcessAidl;
import com.violet.process.bean.ProcessBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kan212 on 2018/4/18.
 */

public class IProcessAidlService extends Service{

    private static final String TAG = "IProcessAidlService: ";
    private List<ProcessBean> list;

    private IBinder mProcessBinder = new IProcessAidl.Stub() {
        @Override
        public void addProcessBean(ProcessBean process) throws RemoteException {
            Log.d(TAG, "addProcessBean: " + process.mName);
            list.add(process);
        }

        @Override
        public List<ProcessBean> getProcessBeanList() throws RemoteException {
            return list;
        }
    };

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        list = new ArrayList<>();
        return mProcessBinder;
    }
}
