package com.violet.lib.fragment.rigger.rigger;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;

import com.violet.lib.fragment.rigger.exception.UnSupportException;
import com.violet.lib.fragment.rigger.utils.Logger;

/**
 * Activity Rigger.rig the Activity puppet.
 *
 */

final class _ActivityRigger extends _Rigger {

  private AppCompatActivity mActivity;
  //data
  private boolean mIsResumed = false;

  _ActivityRigger(@NonNull AppCompatActivity activity) {
    super(activity);
    this.mActivity = activity;
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    mContext = mActivity;
    if (mRiggerTransaction == null) {
      mRiggerTransaction = new RiggerTransactionImpl(this, mActivity.getSupportFragmentManager());
    }
    mStackManager.bindFragmentManager(mActivity.getSupportFragmentManager());
  }

  @Override
  public void onResumeFragments() {
    mIsResumed = true;
    mRiggerTransaction.commit();
  }

  @Override
  public void onResume() {
    mIsResumed = true;
  }

  @Override
  public void onPause() {
    mIsResumed = false;
  }

  @Override
  public void onSaveInstanceState(Bundle outState) {
    mStackManager.saveInstanceState(outState);
  }

  @Override
  public void onDestroy() {
    mStackManager.onDestroy();
    if (mActivity.isFinishing()) {
      mStackManager.clear();
      if (mRiggerTransaction != null) {
        mRiggerTransaction.removeAll();
      }
    }
  }

  @Override
  public boolean isResumed() {
    return mIsResumed;
  }

  @Override
  public void close() {
    mActivity.finish();
  }

  @Override
  public void setFragmentTag(@NonNull String tag) {
    Logger.w(mActivity.getClass(), "setFragmentTag(String tag) method can only be called by Fragment");
  }

  @Override
  public String getFragmentTAG() {
    Logger.w(mActivity.getClass(), "getFragmentTAG() method can only be called by Fragment");
    return null;
  }

  @Override
  public void setResult(int resultCode, Bundle bundle) {
    throwException(new UnSupportException("setResult() method can only be called by Fragment"));
  }
}
