package com.violet.process.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by kan212 on 2018/4/18.
 */

public class ProcessBean implements Parcelable{

    public String mName;

    public ProcessBean(String name) {
        this.mName = name;
    }

    // 读数据进行恢复
    protected ProcessBean(Parcel in) {
        mName = in.readString();
    }

    // 写数据进行保存
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mName);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    // 用来创建自定义的Parcelable的对象
    public static final Creator<ProcessBean> CREATOR = new Creator<ProcessBean>() {
        @Override
        public ProcessBean createFromParcel(Parcel in) {
            return new ProcessBean(in);
        }

        @Override
        public ProcessBean[] newArray(int size) {
            return new ProcessBean[size];
        }
    };

    @Override
    public String toString() {
        return "Person{" +
                "mName='" + mName + '\'' +
                '}';
    }
}
