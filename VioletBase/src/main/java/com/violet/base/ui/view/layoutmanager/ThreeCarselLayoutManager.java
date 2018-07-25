package com.violet.base.ui.view.layoutmanager;

/**
 * Created by kan212 on 2018/5/10.
 */

public class ThreeCarselLayoutManager extends CarouselLayoutManager{

    public ThreeCarselLayoutManager(int orientation, boolean circleLayout) {
        super(orientation, circleLayout);
    }

    @Override
    protected int getLayerIndex(int index) {
        return 1;
    }

    @Override
    protected int getLayoutCount(int itemsCount) {
        return 3;
    }
}
