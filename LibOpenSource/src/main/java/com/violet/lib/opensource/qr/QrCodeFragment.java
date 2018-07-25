package com.violet.lib.opensource.qr;

import android.content.Intent;
import android.view.View;
import android.widget.ImageView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.violet.base.ui.fragment.BaseFragment;
import com.violet.lib.opensource.OpenSourceRouter;
import com.violet.lib.opensource.R;

/**
 * Created by kan212 on 2018/7/9.
 * 二维码测试demo
 * #link https://github.com/zxing/zxing/blob/master/android-core/pom.xml
 */

@Route(path = OpenSourceRouter.OsInnerRouter.OS_QRCODE)
public class QrCodeFragment extends BaseFragment{

    private ImageView mImageView;

    @Override
    protected int getLayoutId() {
        return R.layout.open_activity_qr_code;
    }

    @Override
    protected void initView(View parent) {
        mImageView = parent.findViewById(R.id.iv);
    }

    @Override
    protected void initData(Intent intent) {
        mImageView.setImageBitmap(QRCodeUtil.createQRCodeBitmap("fuck the world", 400));
    }
}
