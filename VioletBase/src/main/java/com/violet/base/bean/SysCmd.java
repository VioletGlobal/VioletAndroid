package com.violet.base.bean;

import java.util.List;

/**
 * Created by liuqun1 on 2017/10/17.
 */

public class SysCmd {
    public String ver;
    public List<SysCmdItem> list;


    public static class SysCmdItem{
        public String sid;
        public String action;
        public Object data;
    }
}
