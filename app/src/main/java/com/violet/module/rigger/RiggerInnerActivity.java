package com.violet.module.rigger;

import android.os.Bundle;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.violet.base.grape.router.RouterUrl;
import com.violet.lib.fragment.rigger.rigger.Rigger;
import com.violet.module.rigger.fragment.RiggerFragment;

/**
 * Created by kan212 on 2018/5/22.
 */
@Route(path = RouterUrl.RiggerRouter.RIGGER_ACTIVITY)
public class RiggerInnerActivity extends BaseRiggerActivity {

    @Override
    protected void init(Bundle savedInstanceState) {
        Rigger.enableDebugLogging(true);
        if (savedInstanceState == null) {
            Rigger.getRigger(this).startFragment(RiggerFragment.newInstance());
        }
    }

}
