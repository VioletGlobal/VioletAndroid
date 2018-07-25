package com.violet.base.ui.view.layoutmanager;

public class ItemTransformation {

    final float mScaleX;
    final float mScaleY;
    float mTranslationX;
    float mTranslationY;

    public ItemTransformation(final float scaleX, final float scaleY, final float translationX, final float translationY) {
        mScaleX = scaleX;
        mScaleY = scaleY;
        mTranslationX = translationX;
        mTranslationY = translationY;
    }
}