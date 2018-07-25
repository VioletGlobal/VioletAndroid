package com.violet.lib.pattern;

import java.io.Serializable;

/**
 * Created by kan212 on 2018/4/24.
 * 单例模式
 * 单例模式保证了全局对象的唯一性
 */

public class SingleTon implements Serializable {

    /**
     * 饿汉式单例在类加载初始化时就创建好一个静态的对象供外部使用，除非系统重启，这个对象不会改变，所以本身就是线程安全的。
     */
    private static SingleTon mSingleTon1 = new SingleTon();

    // 静态工厂方法
    public static SingleTon getInstance() {
        return mSingleTon1;
    }

    /**
     * 该示例虽然用延迟加载方式实现了懒汉式单例，但在多线程环境下会产生多个single对象
     */
    private static SingleTon mSingleTon2;

    public static SingleTon getInstance2() {
        if (mSingleTon2 == null) {
            mSingleTon2 = new SingleTon();
        }
        return mSingleTon2;
    }

    /**
     * 在方法上加synchronized同步锁或是用同步代码块对类加同步锁，此种方式虽然解决了多个实例对象问题，
     * 但是该方式运行效率却很低下，下一个线程想要获取对象，就必须等待上一个线程释放锁之后，才可以继续运行
     *
     * @return
     */
    public static SingleTon getInstance3() {

        // 等同于 synchronized public static Singleton3 getInstance()
        synchronized (SingleTon.class) {
            // 注意：里面的判断是一定要加的，否则出现线程安全问题
            if (mSingleTon2 == null) {
                mSingleTon2 = new SingleTon();
            }
        }
        return mSingleTon2;
    }

    /**
     * 使用双重检查进一步做了优化，可以避免整个方法被锁，只对需要锁的代码部分加锁，可以提高执行效率。
     *
     * @return
     */
    // 双重检查
    public static SingleTon getInstance4() {
        if (mSingleTon2 == null) {
            synchronized (SingleTon.class) {
                if (mSingleTon2 == null) {
                    mSingleTon2 = new SingleTon();
                }
            }
        }
        return mSingleTon2;
    }

    /**
     * 静态内部类虽然保证了单例在多线程并发下的线程安全性，但是在遇到序列化对象时，默认的方式运行得到的结果就是多例的
     * 通过对Singleton的序列化与反序列化得到的对象是一个新的对象，这就破坏了Singleton的单例性。
     */
    // 静态内部类
    private static class InnerObject {
        private static SingleTon single = new SingleTon();
    }

    public static SingleTon getInstance5() {
        return InnerObject.single;
    }

    /**
     * 静态代码初始创建的方式来
     */
    // 静态代码块
    static{
        mSingleTon2 = new SingleTon();
    }

    public static SingleTon getInstance6() {
        return mSingleTon2;
    }

    /**
     * 内部枚举类实现
     */
    // 内部枚举类
    private enum EnmuSingleton{
        Singleton;
        private SingleTon singleton;

        //枚举类的构造方法在类加载是被实例化
        private EnmuSingleton(){
            singleton = new SingleTon();
        }
        public SingleTon getInstance(){
            return singleton;
        }
    }
    public static SingleTon getInstance7() {
        return EnmuSingleton.Singleton.getInstance();
    }


    /**
     * hasReadResolveMethod:如果实现了serializable 或者 externalizable接口的类中包含readResolve则返回true
     * invokeReadResolve:通过反射的方式调用要被反序列化的类的readResolve方法
     * @return
     */
    private Object readResolve() {
        return mSingleTon2;
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }
}
