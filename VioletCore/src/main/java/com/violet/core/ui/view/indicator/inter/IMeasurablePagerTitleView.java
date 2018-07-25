package com.violet.core.ui.view.indicator.inter;

/**
 * 可测量内容区域的指示器标题
 * Created by kan212 on 2018/4/14.
 */

public interface IMeasurablePagerTitleView extends IPagerTitleView{

    int getContentLeft();

    int getContentTop();

    int getContentRight();

    int getContentBottom();
}
