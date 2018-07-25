package com.violet.lib.pattern.agent;

import com.violet.core.util.LogUtil;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * Created by kan212 on 2018/4/24.
 */

public class DynamicProxy implements InvocationHandler {

    private Object mDynamicProxy;

    public DynamicProxy(Object mDynamicProxy) {
        this.mDynamicProxy = mDynamicProxy;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        LogUtil.d("准备工作之前：");

        //转调具体目标对象的方法
        Object object = method.invoke(mDynamicProxy, args);

        LogUtil.d("工作已经做完了！");
        return object;
    }

    /**
     * 测试动态代理
     */
    public static void realCall() {
        AgentInterImpl real = new AgentInterImpl();
        //该方法用于为指定类装载器、一组接口及调用处理器生成动态代理类实例
        AgentInter proxySubject = (AgentInter) Proxy.newProxyInstance(AgentInter.class.getClassLoader(),
                new Class[]{AgentInter.class},
                new DynamicProxy(real));

        proxySubject.echo("动态代理");
        //该方法用于获取指定代理对象所关联的调用处理器，比如上面代码中的DynamicProxy
        Proxy.getInvocationHandler(new DynamicProxy(real));

        //该方法用于获取关联于指定类装载器和一组接口的动态代理类的类对象
        Proxy.getProxyClass(AgentInter.class.getClassLoader(),
                new Class[]{AgentInter.class});

        //该方法用于判断指定类对象是否是一个动态代理类
        Proxy.isProxyClass(DynamicProxy.class);


    }

    public class InnerProxy extends Proxy {

        /**
         * Constructs a new {@code Proxy} instance from a subclass
         * (typically, a dynamic proxy class) with the specified value
         * for its invocation handler.
         *
         * @param h the invocation handler for this proxy instance
         */
        protected InnerProxy(InvocationHandler h) {
            super(h);
        }
    }

}
