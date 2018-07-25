package com.violet.lib.feed.news.fragment;

import android.app.Dialog;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MotionEvent;

/**
 * Created by kan212 on 2018/4/20.
 */

public abstract class BaseNewsFragment extends Fragment implements IFragment{

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        return false;
    }

    @Override
    public Dialog onCreateDialog(int id) {
        return null;
    }

    @Override
    public boolean onCreateOptionMenu(Menu menu) {
        return false;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return false;
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        return false;
    }

    @Override
    public boolean onKeyLongPress(int keyCode, KeyEvent event) {
        return false;
    }
}
