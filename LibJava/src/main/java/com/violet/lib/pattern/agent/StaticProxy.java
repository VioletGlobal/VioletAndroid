package com.violet.lib.pattern.agent;

import com.violet.core.util.LogUtil;

import java.util.Date;

/**
 * Created by kan212 on 2018/4/24.
 * 静态代理
 */

public class StaticProxy implements AgentInter{

    private AgentInter mAgentInter;

    public StaticProxy(AgentInter mAgentInter) {
        this.mAgentInter = mAgentInter;
    }

    public void setAgentInter(AgentInter mAgentInter) {
        this.mAgentInter = mAgentInter;
    }


    @Override
    public String echo(String msg) {
        //预处理
        LogUtil.d("before calling echo()");
        //调用被代理的HelloService 实例的echo()方法
        String result=mAgentInter.echo(msg);
        //事后处理
        LogUtil.d("after calling echo()");
        return result;
    }

    @Override
    public Date getTime() {
        //预处理
        LogUtil.d("before calling getTime()");
        //调用被代理的HelloService 实例的echo()方法
        Date date=mAgentInter.getTime();
        //事后处理
        LogUtil.d("after calling getTime()");
        return date;
    }
}
