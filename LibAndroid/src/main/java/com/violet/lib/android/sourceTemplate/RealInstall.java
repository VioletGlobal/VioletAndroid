package com.violet.lib.android.sourceTemplate;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

/**
 * Created by kan212 on 2018/6/12.
 * #link https://blog.csdn.net/mockingbirds/article/details/53048296
 */

public class RealInstall extends InstallTemplate {

    @Override
    public void realInstall(Context context) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setDataAndType(Uri.parse("file://" + "path"),"application/vnd.android.package-archive");
        context.startActivity(intent);
    }

    @Override
    public void copy() {

    }

    @Override
    public void parse() {

    }

    @Override
    public void mainifest() {

    }

    @Override
    public void dex() {

    }

    @Override
    public void register() {

    }

    @Override
    public void broadcast() {

    }

}
