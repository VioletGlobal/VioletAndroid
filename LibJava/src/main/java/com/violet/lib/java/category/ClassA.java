package com.violet.lib.java.category;

/**
 * Created by kan212 on 2018/5/15.
 */

/**
 * 类的加载过程：
 * 1、查找ClassA.class，并加载到内存中
 * 2、加载static代码块
 * 3、在堆里开辟内存空间，并分配内存地址
 * 4、在堆内存中建立内存的属性，默认的初始化
 * 5、对属性进行显示初始化
 * 6、对对象进行构造代码的初始化
 * 7、调用对象的构造函数初始化
 * 8、将内存地址赋值给classA变量
 */

public class ClassA {

    public ClassA(){
        System.out.println("Class A constructor");
    }

    {
        System.out.println("Class A function");
    }

    static {
        System.out.println("Class A static");
    }

}
