package com.violet.core.ui.view.indicator.inter;

import com.violet.core.ui.view.indicator.PositionData;

import java.util.List;

/**
 * 抽象的viewpager指示器，适用于CommonNavigator
 * Created by kan212 on 2018/4/14.
 */

public interface IPagerIndicator {

    void onPageScrolled(int position, float positionOffset, int positionOffsetPixels);

    void onPageSelected(int position);

    void onPageScrollStateChanged(int state);

    void onPositionDataProvide(List<PositionData> dataList);

}
