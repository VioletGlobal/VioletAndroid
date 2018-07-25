package com.violet.lib.data;

import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;

import java.util.Date;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by kan212 on 2018/4/25.
 * 并发集合
 */

public class ConcurrencyCollection {

    /**
     * 	Vector和CopyOnWriteArrayList是两个线程安全的List，Vector读写操作都用了同步，相对来说更适用于写多读少的场合，
     * 	CopyOnWriteArrayList在写的时候会复制一个副本，对副本写，写完用副本替换原值，读的时候不需要同步，适用于写少读多的场合。
     */

    private void Vector(){
        CopyOnWriteArrayList list = new CopyOnWriteArrayList();
    }

    /**
     * CopyOnWriteArraySet基于CopyOnWriteArrayList来实现的，只是在不允许存在重复的对象这个特性上遍历处理了一下。
     */
    private void Set(){
        CopyOnWriteArraySet set = new CopyOnWriteArraySet();
    }

    /**
     *  ConcurrentHashMap是专用于高并发的Map实现，内部实现进行了锁分离，get操作是无锁的。
     */
    private void map(){
        ConcurrentHashMap map = new ConcurrentHashMap();
    }


    /**
     * 在并发队列上JDK提供了两套实现，一个是以ConcurrentLinkedQueue为代表的高性能队列，
     * 一个是以BlockingQueue接口为代表的阻塞队列。ConcurrentLinkedQueue适用于高并发场景下的队列，
     * 通过无锁的方式实现，通常ConcurrentLinkedQueue的性能要优于BlockingQueue。
     * BlockingQueue的典型应用场景是生产者-消费者模式中，如果生产快于消费，生产队列装满时会阻塞，等待消费
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void Queue(){
        ConcurrentLinkedDeque concurrentLinkedDeque = new ConcurrentLinkedDeque();
        BlockingDeque blockingDeque = new LinkedBlockingDeque();

    }

    /**
     * ReentrantLock是一种互斥锁的实现，就是一次最多只能一个线程拿到锁；
     */
    private void ReentrantLock(){
        ReentrantLock reentrantLock = new ReentrantLock();
    }

    private void ReadWriteLock(){
        ReadWriteLock readWriteLock = new ReadWriteLock() {
            @NonNull
            @Override
            public Lock readLock() {
                return null;
            }

            @NonNull
            @Override
            public Lock writeLock() {
                return null;
            }
        };
    }

    /**
     * 	调用Condition对象的相关方法，可以方便的挂起和唤醒线程。
     */
    private void Condition(){
        class MyCondition implements Condition{

            @Override
            public void await() throws InterruptedException {

            }

            @Override
            public void awaitUninterruptibly() {

            }

            @Override
            public long awaitNanos(long nanosTimeout) throws InterruptedException {
                return 0;
            }

            @Override
            public boolean await(long time, TimeUnit unit) throws InterruptedException {
                return false;
            }

            @Override
            public boolean awaitUntil(@NonNull Date deadline) throws InterruptedException {
                return false;
            }

            @Override
            public void signal() {

            }

            @Override
            public void signalAll() {

            }
        }
    }

}
