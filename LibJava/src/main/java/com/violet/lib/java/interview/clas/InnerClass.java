package com.violet.lib.java.interview.clas;

/**
 * Created by kan212 on 2018/4/23.
 * 内部类的4中形式
 * 1、内部类可以用多个实例，每个实例都有自己的状态信息，并且与其他外围对象的信息相互独立。
 * 2、在单个外围类中，可以让多个内部类以不同的方式实现同一个接口，或者继承同一个类。
 * 3、创建内部类对象的时刻并不依赖于外围类对象的创建。
 * 4、内部类并没有令人迷惑的“is-a”关系，他就是一个独立的实体。
 * 5、内部类提供了更好的封装，除了该外围类，其他类都不能访问。
 */

public class InnerClass {


    abstract class Inner {
        public abstract void print();
    }

    /**
     * 第一：成员内部类中不能存在任何static的变量和方法；
     * 第二：成员内部类是依附于外围类的，所以只有先创建了外围类才能够创建内部类
     * @param s1
     */
    public void test1(final String s1) {// 参数必须是final
        //成员内部类
        Inner c = new Inner() {
            public void print() {
                System.out.println(s1);
            }
        };
        c.print();
    }

    public void test2(final String s2) {// 参数必须是final
        //匿名内部类
        new InnerClass() { //名字可以跟外部类一样
            public void print() {
                System.out.println(s2);
            }
        }.print();
    }

    /**
     * 当主方法结束时，局部变量会被cleaned up 而内部类可能还在运行。当局部变量声明为final时，
     * 当使用已被cleaned up的局部变量时会把局部变量替换成常量：
     */
    public void execute() {
        final int s = 10;
        class Inner {
            public void execute() {
                new Thread() {
                    @Override
                    public void run() {
                        try {
                            Thread.currentThread().sleep(2000);
                            System.out.println(s);
                        } catch (final InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }.start();
            }
        }
        new Inner().execute();
        System.out.println("主方法已经over");
    }


}
