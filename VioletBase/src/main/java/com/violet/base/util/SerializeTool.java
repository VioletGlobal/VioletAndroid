package com.violet.base.util;

import android.content.Context;
import android.text.TextUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * Created by kan212 on 2018/6/28.
 */

public class SerializeTool {

    /**
     * 序列化存储数据到指定文件
     *
     * @param context
     * @param value
     */
    public static void keep(Context context, Serializable value) {
        keep(context, value, null);
    }

    /**
     * 序列化存储数据到指定文件
     *
     * @param context
     * @param value
     * @param name
     */
    public static void keep(Context context, Serializable value, String name) {
        if (null == context) {
            return;
        }
        // 获取缓存文件名称
        String fileName = (TextUtils.isEmpty(name)) ? value.getClass().getSimpleName() : name;
        if (TextUtils.isEmpty(fileName)) {
            return;
        }
        // 重新存储前，将前一次文件删除
        context.deleteFile(fileName);
        if (null == value) {
            return;
        }
        // 存储
        ObjectOutputStream oos = null;
        try {
            FileOutputStream fos = context.openFileOutput(fileName, Context.MODE_PRIVATE);
            GZIPOutputStream gos = new GZIPOutputStream(fos);
            oos = new ObjectOutputStream(gos);
            oos.writeObject(value);
            oos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (null != oos) {
                    oos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 删除序列化文件
     *
     * @param context
     * @param className
     * @return
     */
    public static boolean clear(Context context, Class<? extends Serializable> className) {
        return clear(context, className, null);
    }

    /**
     * 删除序列化文件
     *
     * @param context
     * @param className
     * @param name      序列化文件名称
     * @return
     */
    public static boolean clear(Context context, Class<? extends Serializable> className, String name) {
        if (null == context) {
            return false;
        }
        String fileName = (TextUtils.isEmpty(name)) ? className.getSimpleName() : name;
        if (TextUtils.isEmpty(fileName)) {
            return false;
        }
        return context.deleteFile(fileName);
    }

    /**
     * 读取序列化列数据
     *
     * @param context
     * @param cls
     * @return
     */
    public static Serializable read(Context context, Class<? extends Serializable> cls) {
        return read(context, cls, null);
    }

    /**
     * 读取序列化列数据
     *
     * @param context
     * @param className
     * @param name      序列化文件名称
     * @return
     */
    public static Serializable read(Context context, Class<? extends Serializable> className, String name) {
        Serializable value = null;
        if (context == null) {
            return null;
        }
        String fileName = (TextUtils.isEmpty(name)) ? className.getSimpleName() : name;
        String filepath = context.getFilesDir() + File.separator + fileName;
        if (!new File(filepath).exists()) {
            return null;
        }
        ObjectInputStream ois = null;
        GZIPInputStream gis = null;
        FileInputStream fis = null;
        try {
            fis = context.openFileInput(fileName);
            gis = new GZIPInputStream(fis);
            ois = new ObjectInputStream(gis);
            value = (Serializable) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            try {
                if (null != fis) {
                    fis.close();
                }
                if (null != gis) {
                    gis.close();
                }
                if (null != ois) {
                    ois.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return value;
    }
}
