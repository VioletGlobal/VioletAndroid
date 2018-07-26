package com.violet.net.http.convert;


/**
 * Created by liuqun1 on 2018/1/29.
 */

public interface AbsConverter<T, R> {
    /**
     * 拿到响应后，将数据转换成需要的格式，子线程中执行，可以是耗时操作
     *
     * @param response 需要转换的对象
     * @return 转换后的结果
     * @throws Exception 转换过程发生的异常
     */
    T convertResponse(R response) throws Throwable;

    T convertResponse(R response, Class<T> clazz) throws Throwable;
}
