package com.violet.lib.java.interview.serialize;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by kan212 on 2018/4/23.
 * Parcelable是以Ibinder作为信息载体的.在内存上的开销比较小,因此在内存之间进行数据传递的时候,
 * Android推荐使用Parcelable,既然是内存方面比价有优势,那么自然就要优先选择.
 *
 * Parcelable是在内存中直接进行读写,而Serializable是通过使用IO流的形式将数据读写入在硬盘上
 *
 * Parcelable无法将数据进行持久化,因此在将数据保存在磁盘的时候,仍然需要使用Serializable
 */

public class BaseParcelable implements Parcelable {


    public int userId;
    public String userName;

    public BaseParcelable(int id, String name) {
        this.userId = id;
        this.userName = name;
    }

    /**
     * 从序列化后的对象中创建原始对象
     *
     * @param in
     */
    protected BaseParcelable(Parcel in) {
        /**
         * 如果元素数据是list类型的时候需要： lits = new ArrayList<?> in.readList(list);
         * 否则会出现空指针异常.并且读出和写入的数据类型必须相同.如果不想对部分关键字进行序列化,
         * 可以使用transient关键字来修饰以及static修饰.
         */
        userId = in.readInt();
        userName = in.readString();
    }

    public static final Creator<BaseParcelable> CREATOR = new Creator<BaseParcelable>() {

        /**
         * 从序列化后的对象中创建原始对象
         * @param in
         * @return
         */
        @Override
        public BaseParcelable createFromParcel(Parcel in) {
            return new BaseParcelable(in);
        }

        /**
         * 创建指定长度的原始对象数组
         * @param size
         * @return
         */
        @Override
        public BaseParcelable[] newArray(int size) {
            return new BaseParcelable[size];
        }
    };

    /**
     * 返回当前对象的内容描述，几乎所有情况都返回0，仅在当前对象中存在文件描述符时返回1
     *
     * @return
     */
    @Override
    public int describeContents() {
        return 0;
    }

    /**
     * 将当前对象写入序列化结构中
     *
     * @param dest
     * @param flags
     */
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(userId);
        dest.writeString(userName);
    }
}
