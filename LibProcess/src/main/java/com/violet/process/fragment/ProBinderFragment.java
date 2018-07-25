package com.violet.process.fragment;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.view.View;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.violet.core.ui.fragment.CoreFragment;
import com.violet.process.IProcessAidl;
import com.violet.process.R;
import com.violet.process.bean.ProcessBean;
import com.violet.process.grape.ProcessRouterUrl;
import com.violet.process.service.IProcessAidlService;

import java.util.List;
import java.util.Random;

import static android.content.Context.BIND_AUTO_CREATE;

/**
 * Created by kan212 on 2018/4/18.
 */

@Route(path = ProcessRouterUrl.ProcessFragmentRouter.FRAGMENT_BINDER)
public class ProBinderFragment extends CoreFragment implements View.OnClickListener{

    private IProcessAidl iProcessAidl;
    private TextView btn_process,tv_process;

    @Override
    protected int getLayoutId() {
        return R.layout.pro_fragment_process;
    }

    @Override
    protected void initView(View parent) {
        btn_process = (TextView) parent.findViewById(R.id.btn_process);
        tv_process = (TextView) parent.findViewById(R.id.tv_process);
        btn_process.setOnClickListener(this);
    }

    @Override
    protected void initData(Intent intent) {
        Intent i = new Intent(getActivity(), IProcessAidlService.class);
        getActivity().bindService(i,mConnection,BIND_AUTO_CREATE);
    }

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            //连接后拿到 Binder，转换成 AIDL，在不同进程会返回个代理
            iProcessAidl = IProcessAidl.Stub.asInterface(service);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            iProcessAidl = null;
        }
    };

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_process){
            Random random = new Random();
            ProcessBean person = new ProcessBean("violet" + random.nextInt(10));

            try {
                iProcessAidl.addProcessBean(person);
                List<ProcessBean> personList = iProcessAidl.getProcessBeanList();
                tv_process.setText(personList.toString());
            } catch (RemoteException e) {
                e.printStackTrace();
            }

        }
    }
}
