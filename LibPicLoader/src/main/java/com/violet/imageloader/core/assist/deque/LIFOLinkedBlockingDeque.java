package com.violet.imageloader.core.assist.deque;

import java.util.concurrent.LinkedBlockingDeque;

/**
 * Created by kan212 on 2018/8/29.
 * 后进先出阻塞队列。重写LinkedBlockingDeque的offer(…)函数如下：
 * 让LinkedBlockingDeque插入总在最前，而remove()本身始终删除第一个元素，所以就变为了后进先出阻塞队列。
 * 实际一般情况只重写offer(…)函数是不够的，但因为ThreadPoolExecutor默认只用到了BlockingQueue的offer(…)函数，
 * 所以这种简单重写后做为ThreadPoolExecutor的任务队列没问题。
 */

public class LIFOLinkedBlockingDeque<T> extends LinkedBlockingDeque<T> {

    private static final long serialVersionUID = -4114786347960826192L;

    @Override
    public boolean offer(T t) {
        return super.offerFirst(t);
    }

    @Override
    public T remove() {
        return super.removeFirst();
    }
}
