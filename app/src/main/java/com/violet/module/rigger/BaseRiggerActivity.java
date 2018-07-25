package com.violet.module.rigger;

import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.violet.R;
import com.violet.lib.fragment.rigger.annotation.Puppet;

/**
 * Created by kan212 on 2018/5/22.
 */
@Puppet(containerViewId = R.id.rigger_content, bondContainerView = true)
public abstract class BaseRiggerActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getContentView());
        init(savedInstanceState);
    }

    @LayoutRes
    protected int getContentView() {
        return R.layout.activity_rigger_content;
    }

    protected abstract void init(Bundle savedInstanceState);
}
