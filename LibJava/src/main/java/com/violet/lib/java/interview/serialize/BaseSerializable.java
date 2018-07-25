package com.violet.lib.java.interview.serialize;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * Created by kan212 on 2018/4/23.
 * 永久的保存对象数据,将对象数据保存在文件当中,或者是磁盘中
 * 通过序列化操作将对象数据在网络上进行传输
 * 对象数据在进程之间进行传递
 * va平台允许我们在内存中创建可复用的Java对象，但一般情况下，只有当JVM处于运行时，这些对象才可能存在，
 * 即，这些对象的生命周期不会比JVM的生命周期更长（即每个对象都在JVM中）但在现实应用中，就可能要停止JVM运行，
 * 但有要保存某些指定的对象，并在将来重新读取被保存的对象。这是Java对象序列化就能够实现该功能
 * 序列化对象的时候只是针对变量进行序列化,不针对方法进行序列化
 * 在Intent之间,基本的数据类型直接进行相关传递即可,但是一旦数据类型比较复杂的时候,就需要进行序列化操作了
 */

/**
 * 1.静态成员变量属于类不属于对象，所以不参与序列化过程
 * 2.用transient关键字标记的成员变量不参与序列化过程
 */
public class BaseSerializable implements Serializable {


    public int userId;
    public String userName;

    public BaseSerializable(int id, String name) {
        this.userId = id;
        this.userName = name;
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
    }

    public void writeToLocal(BaseSerializable user) {
//        BaseSerializable user = new BaseSerializable(0, "violet");
        ObjectOutputStream out = null;
        try {
            out = new ObjectOutputStream(new FileOutputStream("user.obj"));
            out.writeObject(user);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public BaseSerializable getFromLocal() {
        ObjectInputStream in = null;
        BaseSerializable user = null;
        try {
            in = new ObjectInputStream(new FileInputStream("BaseSerializable.obj"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            user = (BaseSerializable) in.readObject();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            try {
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return user;
    }
}
