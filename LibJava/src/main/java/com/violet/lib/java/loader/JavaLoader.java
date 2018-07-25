package com.violet.lib.java.loader;

/**
 * Created by kan212 on 2018/6/12.
 * 类加载器
 * <p>
 * 启动类加载器（Bootstrap ClassLoader）：负责加载<JAVA_HOME>\lib目录下或者被-Xbootclasspath参数所指定的路径的，
 * 并且是被虚拟机所识别的库到内存中。
 * <p>
 * 扩展类加载器（Extension ClassLoader）：负责加载<JAVA_HOME>\lib\ext目录下或者被java.ext.dirs系统变量所
 * 指定的路径的所有类库到内存中
 * 应用类加载器（Application ClassLoader）：负责加载用户类路径上的指定类库，如果应用程序中没有实现自己的类加载器，
 * 一般就是这个类加载器去加载应用程序中的类库
 */
 class JavaLoader {



}
