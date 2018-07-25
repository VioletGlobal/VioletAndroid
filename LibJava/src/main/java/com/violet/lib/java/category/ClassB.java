package com.violet.lib.java.category;

/**
 * Created by kan212 on 2018/5/15.
 */


/**
 *
 * 面试题，创建之后的显示顺序
 * Class A static
 * Class B static
 * Class A function
 * Class A constructor
 * Class B function
 * Class B constructor
 */
public class ClassB extends ClassA {

    public ClassB() {
        System.out.println("Class B constructor");
    }

    {
        System.out.println("Class B function");
    }

    static {
        System.out.println("Class B static");
    }



}
