package com.violet.base.bean;

import com.violet.base.net.StatusCode;
import com.violet.core.bean.BaseNetBean;

/**
 * Created by kan212 on 2018/4/16.
 */

public class BaseBean<T> extends BaseNetBean{
    public int status;// 状态码   0：正常，非0，即为异常 [ 参考错误代码对照表 ]http://wiki.intra.sina.com.cn/pages/viewpage.action?pageId=145592304
    public String msg; // msg
    public String resTime; //接口执行时间戳
    public String uni;// 唯一值
    public String localUni;

    public T data;
    public SysCmd sysCmd;

    public boolean isStatusOk() {
        return status == StatusCode.CODE_OK;
    }
}
