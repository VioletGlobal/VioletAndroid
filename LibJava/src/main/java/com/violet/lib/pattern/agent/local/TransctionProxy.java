package com.violet.lib.pattern.agent.local;

import com.violet.core.util.LogUtil;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.Connection;

/**
 * Created by kan212 on 2018/4/24.
 */

public class TransctionProxy implements InvocationHandler {

    private Object obj = null;

    public Object newProxyInstance(Object obj) {
        this.obj = obj;
        return Proxy.newProxyInstance(obj.getClass().getClassLoader(), obj.getClass().getInterfaces(), this);
    }


    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        // 用于接收参数
        Object param = null;
        LogUtil.d("Method.name : " + method.getName());
        // 如果是以下方法开头,则代理事务
        if (method.getName().startsWith("add")
                || method.getName().startsWith("modify")
                || method.getName().startsWith("find")
                || method.getName().startsWith("del")) {
            Connection connection = ConnectionManager.getConnection();
            try {
                ConnectionManager.benigTransction(connection);
                param = method.invoke(obj,args);
                ConnectionManager.endTransction(connection);
            }catch (Exception e){
                ConnectionManager.rollback(connection);
                if (e instanceof InvocationTargetException) {
                    InvocationTargetException inv =
                            (InvocationTargetException) e;
                    throw inv.getTargetException();
                } else {
                    throw new Exception("操作失败!");
                }
            }finally {
                // 还原状态
                ConnectionManager.recoverTransction(connection);
                ConnectionManager.close();
            }
        }
        return param;
    }

    public static void realCall() throws Exception {
        TransctionProxy transctionProxy = new TransctionProxy();
        // 产生代理对象
        IUserManager userManager =
                (IUserManager) transctionProxy.
                        newProxyInstance(new UserManagerImpl());
        User user = userManager.findUser("110");
        System.out.println("用户名  : " + user.getName());
        System.out.println("用户id: " + user.getId());
        System.out.println("创建日期:" + user.getCreate_date());
    }
}
