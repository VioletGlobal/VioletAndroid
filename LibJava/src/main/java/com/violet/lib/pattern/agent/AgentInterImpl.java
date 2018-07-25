package com.violet.lib.pattern.agent;

import java.util.Date;

/**
 * Created by kan212 on 2018/4/24.
 */

public class AgentInterImpl implements AgentInter{

    @Override
    public String echo(String msg) {
        return "echo:"+msg;
    }

    @Override
    public Date getTime() {
        return new Date();
    }
}
