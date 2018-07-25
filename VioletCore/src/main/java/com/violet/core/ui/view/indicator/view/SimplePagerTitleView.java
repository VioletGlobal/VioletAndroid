package com.violet.core.ui.view.indicator.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Paint;
import android.graphics.Rect;
import android.text.TextUtils;
import android.view.Gravity;
import android.widget.TextView;

import com.violet.core.ui.view.indicator.inter.IMeasurablePagerTitleView;
import com.violet.core.ui.view.indicator.util.UIUtil;

import static android.view.Gravity.BOTTOM;

/**
 * 带文本的指示器标题
 * Created by kan212 on 2018/4/16.
 */

@SuppressLint("AppCompatCustomView")
public class SimplePagerTitleView extends TextView implements IMeasurablePagerTitleView {

    protected int mSelectedColor;
    protected int mNormalColor;

    public SimplePagerTitleView(Context context) {
        super(context);
        init(context);
    }

    private void init(Context context) {
        setGravity(Gravity.CENTER_HORIZONTAL|BOTTOM);
        int padding = UIUtil.dip2px(context, 5);
//        setPadding(padding, 0, padding, padding);
        setSingleLine();
        setEllipsize(TextUtils.TruncateAt.END);
    }

    @Override
    public int getContentLeft() {
        Rect bound = new Rect();
        getPaint().getTextBounds(getText().toString(), 0, getText().length(), bound);
        int contentWidth = bound.width();
        return getLeft() + getWidth() / 2 - contentWidth / 2;
    }

    @Override
    public int getContentTop() {
        Paint.FontMetrics metrics = getPaint().getFontMetrics();
        float contentHeight = metrics.bottom - metrics.top;
        return (int) (getHeight() / 2 - contentHeight / 2);
    }

    @Override
    public int getContentRight() {
        Rect bound = new Rect();
        getPaint().getTextBounds(getText().toString(), 0, getText().length(), bound);
        int contentWidth = bound.width();
        return getLeft() + getWidth() / 2 + contentWidth / 2;
    }

    @Override
    public int getContentBottom() {
        Paint.FontMetrics metrics = getPaint().getFontMetrics();
        float contentHeight = metrics.bottom - metrics.top;
        return (int) (getHeight() / 2 + contentHeight / 2);
    }

    @Override
    public void onSelected(int index, int totalCount) {
        setTextColor(mSelectedColor);
        getPaint().setFakeBoldText(true);
    }

    @Override
    public void onDeselected(int index, int totalCount) {
        setTextColor(mNormalColor);
        getPaint().setFakeBoldText(false);
    }

    @Override
    public void onLeave(int index, int totalCount, float leavePercent, boolean leftToRight) {

    }

    @Override
    public void onEnter(int index, int totalCount, float enterPercent, boolean leftToRight) {

    }

    public void setSelectedColor(int selectedColor) {
        this.mSelectedColor = selectedColor;
    }

    public void setNormalColor(int normalColor) {
        this.mNormalColor = normalColor;
    }
}
