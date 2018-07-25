package com.violet.lib.java.interview.fragment;

import android.content.Intent;
import android.view.View;
import android.widget.Button;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.violet.base.ui.fragment.BaseFragment;
import com.violet.core.util.LogUtil;
import com.violet.lib.java.R;
import com.violet.lib.java.grape.LibJavaRouter;

import java.util.HashMap;

/**
 * ==和equals的区别，equals和hashCode的区别
 * 加上字节数多少
 * Created by kan212 on 2018/4/20.
 */

@Route(path = LibJavaRouter.libJavaRouter.LIB_JAVA_FRAGMENT)
public class HasCodeFragment extends BaseFragment implements View.OnClickListener {

    private Button btn_basic_data, btn_composite_data, btn_bytes;

    @Override
    protected int getLayoutId() {
        return R.layout.java_fragment_hascode;
    }

    @Override
    protected void initView(View parent) {
        btn_basic_data = (Button) parent.findViewById(R.id.btn_basic_data);
        btn_composite_data = (Button) parent.findViewById(R.id.btn_composite_data);
        btn_bytes = (Button) parent.findViewById(R.id.btn_bytes);
        btn_basic_data.setOnClickListener(this);
        btn_composite_data.setOnClickListener(this);
        btn_bytes.setOnClickListener(this);
    }

    @Override
    protected void initData(Intent intent) {
//        Plate<? super InnerClass> plate = new Plate<>(new InnerClass());
    }


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_composite_data) {
            compareComposite();
        } else if (v.getId() == R.id.btn_basic_data) {
            compareBasic();
        } else if (v.getId() == R.id.btn_bytes) {
            showBytes();
        }
    }


    /**
     * 成员内部类
     * 第一：成员内部类中不能存在任何static的变量和方法；
     * 第二：成员内部类是依附于外围类的，所以只有先创建了外围类才能够创建内部类。
     */
    public class InnerClass extends InnerStaticClass {

        public InnerClass() {

        }

        /**
         * 1.匿名内部类是没有访问修饰符的
         * 2.匿名内部类是没有构造方法的。因为它连名字都没有何来构造方法
         * @param num
         * @return
         */
        public InnerClass getInnerClass(final int num){

            int age_ ;
            String name_;

            return new InnerClass(){
                int number = num + 3;
                public int getNumber(){
                    return number;
                }
            };
        }
    }

    static class InnerStaticClass {

        /**
         * 局部变量声明为final才能在匿名内部类中使用来避免数据不同步的问题
         */
        public interface whyAriableFinal {
            void why();
        }

        public void display() {
            /*
             * 静态内部类只能访问外围类的静态成员变量和方法
             * 不能访问外围类的非静态成员变量和方法
             */
        }
    }


    /**
     * 基本数据类型字节数多少
     * byte 1字节
     * short 2字节
     * int 4字节
     * long 8字节
     * float 4字节
     * double 8字节
     * char 2字节
     */
    private void showBytes() {
        byte mByte;
        LogUtil.d("byte: " + byte.class);

    }

    /**
     * 当他们用（==）进行比较的时候，比较的是他们在内存中的存放地址，所以，除非是同一个new出来的对象，他们的比较后的结果为true，
     * 否则比较后结果为false。 JAVA当中所有的类都是继承于Object这个基类的，在Object中的基类中定义了一个equals的方法，
     * 这个方法的初始行为是比较对象的内存地址，但在一些类库当中这个方法被覆盖掉了，如String,Integer,Date在这些类当中equals有其自身的实现，
     * 而不再是比较类在堆内存中的存放地址了
     */
    private void compareComposite() {
        InnerObjec innerClass1 = new InnerObjec();
        InnerObjec innerClass2 = new InnerObjec();

        LogUtil.d("InnerObjec: " + (innerClass2 == innerClass1));

    }

    /**
     * 基本数据类型，也称原始数据类型。byte,short,char,int,long,float,double,boolean
     * 他们之间的比较，应用双等号（==）,比较的是他们的值
     */
    private void compareBasic() {
        byte mByte1 = Byte.valueOf("byte1");
        byte mByte2 = Byte.valueOf("byte2");
        LogUtil.d(mByte1 == mByte2);
    }

    /**
     * String 是final的，比较的是每一个字符
     * <p>
     * 自反性。对于任何非null的引用值x，x.equals(x)必须返回true。
     * 对称性。对于任何非null的引用值x和y，当且仅当y.equals(x)返回true时，x.equals(y)必须返回true
     * 传递性。对于任何非null的引用值x、y和z，如果x.equals(y)返回true，并且y.equals(z)也返回true，那么x.equals(z)也必须返回true。
     * 一致性。对于任何非null的引用值x和y，只要equals的比较操作在对象中所用的信息没有被修改，多次调用该x.equals(y)就会一直地返回true，或者一致地返回false。
     * 对于任何非null的引用值x，x.equals(null)必须返回false
     */
    class InnerObjec extends Object {

        /**
         * 如果两个对象根据equals()方法比较是相等的，那么调用这两个对象中任意一个对象的hashCode方法都必须产生同样的整数结果。
         * 如果两个对象根据equals()方法比较是不相等的，那么调用这两个对象中任意一个对象的hashCode方法，则不一定要产生相同的整数结果
         *
         * @param obj
         * @return
         */
        @Override
        public boolean equals(Object obj) {
            return super.equals(obj);
        }

        /**
         * 在每个覆盖了equals方法的类中，也必须覆盖hashCode方法
         * 否则导致该类无法结合所有基于散列的集合一起正常运作
         *
         * @return
         */
        @Override
        public int hashCode() {
            return super.hashCode();
        }
    }

    class InnerMap extends HashMap {


        @Override
        public boolean equals(Object o) {
            return super.equals(o);
        }
    }


    /**
     * 1.抽象类和接口都不能直接实例化,必须实现的子类才能实例化
     * 2.接口只能做方法申明，抽象类中可以做方法申明，也可以做方法实现
     * 3.接口里定义的变量只能是公共的静态的常量，抽象类中的变量是普通变量
     * 4.抽象方法只能申明，不能实现。abstract void abc();不能写成abstract void abc(){}
     * 5.抽象类里可以没有抽象方法
     * 6.如果一个类里有抽象方法，那么这个类只能是抽象类
     * 7.接口可继承接口，并可多继承接口，但类只能单根继承
     */
    public static abstract class AbstractClass {

        public String absStr;

        public AbstractClass getAbstractClass(){
            return this;
        }

        interface inter {
        }
    }

    public <T> T getObject(Class<T> c) throws IllegalAccessException, java.lang.InstantiationException {
        T t = c.newInstance();
        return t;
    }

    class Plate<T>{
        private T item;
        public Plate(T t){item=t;}
    }

    /**
     * 静态方法和属性是属于类的,可以直接类调用
     */
    static void staticFunction(){

    }

    final void finalFunction(){

    }
}
