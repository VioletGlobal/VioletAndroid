package com.violet.lib.feed.news.fragment;

import android.app.Dialog;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MotionEvent;

/**
 * Created by kan212 on 2018/4/20.
 */

public interface IFragment {

    public boolean dispatchTouchEvent(MotionEvent event);

    public Dialog onCreateDialog(int id);

    public boolean onCreateOptionMenu(Menu menu);

    public boolean onKeyDown(int keyCode, KeyEvent event);

    public boolean onKeyUp(int keyCode, KeyEvent event);

    public boolean onKeyLongPress(int keyCode, KeyEvent event);

}
