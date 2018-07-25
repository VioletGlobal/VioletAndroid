package com.violet.module.rigger.fragment;

import android.os.Bundle;

import com.violet.R;


/**
 * Created by kan212 on 2018/5/22.
 */

public class RiggerFragment extends BaseRiggerFragment {

    public static RiggerFragment newInstance() {
        Bundle args = new Bundle();
        RiggerFragment fragment = new RiggerFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    protected int getContentView() {
        return R.layout.lib_fragment_frag_rigger;
    }

    @Override
    protected void init(Bundle savedInstanceState) {

    }
}
