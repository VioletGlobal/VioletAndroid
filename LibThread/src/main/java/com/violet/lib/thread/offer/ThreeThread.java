package com.violet.lib.thread.offer;


import android.support.annotation.NonNull;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Created by kan212 on 2018/4/26.
 * 三种Thread的创建方式
 * @link #http://www.importnew.com/25286.html#
 */

public class ThreeThread {


    /**
     * 继承thread的方式
     */
    private void First() {
        class FirstThreed extends Thread {
            @Override
            public void run() {
                super.run();
            }
        }
        FirstThreed threed = new FirstThreed();
        threed.start();
    }

    /**
     * 继承Runnable的方式
     */
    private void second() {

        class secondRun implements Runnable {

            @Override
            public void run() {

            }
        }
        new Thread(new secondRun()).start();
    }

    /**
     * 线程类只是实现了Runnable接口或Callable接口，还可以继承其他类。
     * 在这种方式下，多个线程可以共享同一个target对象，所以非常适合多个相同线程来处理同一份资源的情况，
     * 从而可以将CPU、代码和数据分开，形成清晰的模型，较好地体现了面向对象的思想。
     */
    private void three() {
        class threeCall implements Callable {
            @Override
            public Object call() throws Exception {
                return null;
            }
        }
        MyFutureTask task = new MyFutureTask(new threeCall());
        new Thread(task).start();
    }

    class MyFutureTask extends FutureTask {

        public MyFutureTask(@NonNull Callable callable) {
            super(callable);
        }

        /**
         * 判断任务是否被取消，如果任务在结束(正常执行结束或者执行异常结束)前被取消则返回true，否则返回false
         * @return
         */
        @Override
        public boolean isCancelled() {
            return super.isCancelled();
        }

        /**
         * 判断任务是否已经完成，如果完成则返回true，否则返回false。需要注意的是：任务执行过程中发生异常、
         * 任务被取消也属于任务已完成，也会返回true
         * @return
         */
        @Override
        public boolean isDone() {
            return super.isDone();
        }

        /**
         * 方法用来取消异步任务的执行。如果异步任务已经完成或者已经被取消，或者由于某些原因不能取消，则会返回false。
         * 如果任务还没有被执行，则会返回true并且异步任务不会被执行。如果任务已经开始执行了但是还没有执行完成，
         * 若mayInterruptIfRunning为true，则会立即中断执行任务的线程并返回true，若mayInterruptIfRunning为false，
         * 则会返回true且不会中断任务执行线程。
         * @param mayInterruptIfRunning
         * @return
         */
        @Override
        public boolean cancel(boolean mayInterruptIfRunning) {
            return super.cancel(mayInterruptIfRunning);
        }

        /**
         * 获取任务执行结果，如果任务还没完成则会阻塞等待直到任务执行完成。如果任务被取消则会抛出CancellationException异常，
         * 如果任务执行过程发生异常则会抛出ExecutionException异常，如果阻塞等待过程中被中断则会抛出InterruptedException异常
         * @return
         * @throws InterruptedException
         * @throws ExecutionException
         */
        @Override
        public Object get() throws InterruptedException, ExecutionException {
            return super.get();
        }

        @Override
        public Object get(long timeout, @NonNull TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
            return super.get(timeout, unit);
        }

        @Override
        protected void done() {
            super.done();
        }

        @Override
        protected void set(Object o) {
            super.set(o);
        }

        @Override
        protected void setException(Throwable t) {
            super.setException(t);
        }

        @Override
        public void run() {
            super.run();
        }

        @Override
        protected boolean runAndReset() {
            return super.runAndReset();
        }
    }
}
