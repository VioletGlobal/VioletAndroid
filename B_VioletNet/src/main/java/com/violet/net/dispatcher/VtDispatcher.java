package com.violet.net.dispatcher;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import okhttp3.internal.Util;

/**
 * Created by kan212 on 2018/7/26.
 */

public class VtDispatcher {

    private int maxRequests = 64;
    private int maxRequestsPerHost = 5;
    private Runnable idleCallback;
    /**
     * 3个优先级队列获取任务时的概率,3个数为自然数，3个数的和必须保证是100
     * 优先级由低到高
     * 例如{10, 20 , 70}
     */
    private volatile int[] priorityPercent;
    private Random random = new Random();

    /**
     * Executes calls. Created lazily.
     */
    private ExecutorService executorService;

    /**
     * High priority ready async calls in the order they'll be run.
     */
    private final Queue<VtRealCall.AsyncCall> highPriorityQueue = new PriorityQueue<>(8, new PriorityCallComparator<VtCall.Priority>());
    /**
     * Middle priority ready async calls in the order they'll be run.
     */
    private final Queue<VtRealCall.AsyncCall> midPriorityQueue = new PriorityQueue<>(8, new PriorityCallComparator<VtCall.Priority>());
    /**
     * Low priority ready async calls in the order they'll be run.
     */
    private final Queue<VtRealCall.AsyncCall> lowPriorityQueue = new PriorityQueue<>(8, new PriorityCallComparator<VtCall.Priority>());


    /**
     * Running asynchronous calls. Includes canceled calls that haven't finished yet.
     */
    private final Queue<VtRealCall.AsyncCall> runningAsyncCalls = new ArrayDeque<>();

    /**
     * Running synchronous calls. Includes canceled calls that haven't finished yet.
     */
    private final Queue<VtRealCall> runningSyncCalls = new ArrayDeque<>();

    public VtDispatcher(ExecutorService executorService) {
        this.executorService = executorService;
    }

    public VtDispatcher(ExecutorService executorService, int[] priorityPercent) {
        this.executorService = executorService;
        this.priorityPercent = priorityPercent;
        checkPriorityPercent();
    }

    public VtDispatcher() {
    }

    public synchronized ExecutorService executorService() {
        if (executorService == null) {
            executorService = new ThreadPoolExecutor(0, Integer.MAX_VALUE, 60, TimeUnit.SECONDS,
                    new SynchronousQueue<Runnable>(), Util.threadFactory("VtDispatcher", false));
        }
        return executorService;
    }

    public void setPriorityPercent(int[] priorityPercent){
        this.priorityPercent = priorityPercent;
        checkPriorityPercent();
    }

    private void checkPriorityPercent(){
        if(priorityPercent != null){
            boolean useDefault = false;
            if(priorityPercent.length != 3){
                useDefault = true;
            } else if(priorityPercent[0]<0 || priorityPercent[0]>100){
                useDefault = true;
            } else if(priorityPercent[1]<0 || priorityPercent[1]>100){
                useDefault = true;
            } else if(priorityPercent[2]<0 || priorityPercent[2]>100){
                useDefault = true;
            } else if(priorityPercent[0] + priorityPercent[1] + priorityPercent[2] != 100) {
                useDefault = true;
            }
            if(useDefault){
                priorityPercent = new int[]{10, 20, 70};
            }
        } else {
            priorityPercent = new int[]{10, 20, 70};
        }
    }

    /**
     * Set the maximum number of requests to execute concurrently. Above this requests queue in
     * memory, waiting for the running calls to complete.
     * <p>
     * <p>If more than {@code maxRequests} requests are in flight when this is invoked, those requests
     * will remain in flight.
     */
    public synchronized void setMaxRequests(int maxRequests) {
        if (maxRequests < 1) {
            throw new IllegalArgumentException("max < 1: " + maxRequests);
        }
        this.maxRequests = maxRequests;
        promoteCalls();
    }

    public synchronized int getMaxRequests() {
        return maxRequests;
    }

    /**
     * Set the maximum number of requests for each host to execute concurrently. This limits requests
     * by the URL's host name. Note that concurrent requests to a single IP address may still exceed
     * this limit: multiple hostnames may share an IP address or be routed through the same HTTP
     * proxy.
     * <p>
     * <p>If more than {@code maxRequestsPerHost} requests are in flight when this is invoked, those
     * requests will remain in flight.
     */
    public synchronized void setMaxRequestsPerHost(int maxRequestsPerHost) {
        if (maxRequestsPerHost < 1) {
            throw new IllegalArgumentException("max < 1: " + maxRequestsPerHost);
        }
        this.maxRequestsPerHost = maxRequestsPerHost;
        promoteCalls();
    }

    public synchronized int getMaxRequestsPerHost() {
        return maxRequestsPerHost;
    }

    /**
     * Set a callback to be invoked each time the dispatcher becomes idle (when the number of running
     * calls returns to zero).
     * <p>
     * <p>Note: The time at which a {@linkplain VtCall call} is considered idle is different depending
     * on whether it was run {@linkplain VtCall#enqueue(VtCallback) asynchronously} or
     * {@linkplain VtCall#execute() synchronously}. Asynchronous calls become idle after the
     * {@link VtCallback#onResponse onResponse} or {@link VtCallback#onFailure onFailure} callback has
     * returned. Synchronous calls become idle once {@link VtCall#execute() execute()} returns. This
     * means that if you are doing synchronous calls the network layer will not truly be idle until
     * every returned {@link Response} has been closed.
     */
    public synchronized void setIdleCallback(Runnable idleCallback) {
        this.idleCallback = idleCallback;
    }

    synchronized void enqueue(VtRealCall.AsyncCall call) {
        if (runningAsyncCalls.size() < maxRequests ) {
            runningAsyncCalls.add(call);
//            log("enqueue add running queue ----------------enqueue-------  " + runningAsyncCalls.size());
            executorService().execute(call);
        } else {
            enqueuePriorityReadyAsyncQueue(call);
        }
    }

    /**
     * 添加到3个优先级异步等待队列之一
     * @param call
     */
    private void enqueuePriorityReadyAsyncQueue(VtRealCall.AsyncCall call){
        long start = System.currentTimeMillis();
        VtPriority priority = call.priority();
        if(priority == null){
            priority = VtPriority.PRIORITY_MID;
        }
        if(priority.intValue()<= VtPriority.PRIORITY_LOW.intValue()){
            lowPriorityQueue.add(call);
        } else if(priority.intValue()<= VtPriority.PRIORITY_MID.intValue()){
            midPriorityQueue.add(call);
        } else {
            highPriorityQueue.add(call);
        }

//        log(" enqueue Priority cost time  " +(System.currentTimeMillis() - start));
    }


    /**
     * Cancel all calls currently enqueued or executing. Includes calls executed both {@linkplain
     * VtCall#execute() synchronously} and {@linkplain VtCall#enqueue asynchronously}.
     */
    public synchronized void cancelAll() {
        for (VtRealCall.AsyncCall call : lowPriorityQueue) {
            call.get().cancel();
        }

        for (VtRealCall.AsyncCall call : midPriorityQueue) {
            call.get().cancel();
        }

        for (VtRealCall.AsyncCall call : highPriorityQueue) {
            call.get().cancel();
        }

//        for (VtRealCall.AsyncCall call : readyAsyncCalls) {
//            call.get().cancel();
//        }

        for (VtRealCall.AsyncCall call : runningAsyncCalls) {
            call.get().cancel();
        }

        for (VtRealCall call : runningSyncCalls) {
            call.cancel();
        }
    }

    private void promoteCalls() {
        ThreadPoolExecutor exe = (ThreadPoolExecutor) executorService;
        long start = System.currentTimeMillis();
//        log("promoteCalls start = " + start);
        if (runningAsyncCalls.size() >= maxRequests) return; // Already running max capacity.
        if(lowPriorityQueue.isEmpty() && midPriorityQueue.isEmpty() && highPriorityQueue.isEmpty()){
            return;
        }
        Queue<VtRealCall.AsyncCall> selectedQueue;

        while(runningAsyncCalls.size() < maxRequests){
            //选中
            selectedQueue = selectPriorityQueue();
            VtRealCall.AsyncCall call = selectedQueue.poll();
            if(call == null){
                return;
            }
            runningAsyncCalls.add(call);
            long e = System.currentTimeMillis();
//            log("core_size = " + exe.getCorePoolSize() + " max = " + exe.getMaximumPoolSize() + " , active = " + exe.getActiveCount() + " , xx = " + exe.getPoolSize());
            executorService().execute(call);
//            log("promoteCalls delta3 = " + (System.currentTimeMillis()-e));

        }

        long delta = System.currentTimeMillis()-start;
//        log("promoteCalls cost time : " + delta + " , thread = " + Thread.currentThread().getName());
//        log("end promoteCalls low_q_size = " + lowPriorityQueue.size() + ", mid_q_size = " + midPriorityQueue.size() + " , high_q_size = " + highPriorityQueue.size() );
    }


    /**
     * 根据每个队列选中的概率生成的随机数选中3个优先级队列中的一个
     * @return
     */
    private Queue<VtRealCall.AsyncCall> selectPriorityQueue(){
        if(priorityPercent == null){
            if(!highPriorityQueue.isEmpty()){
                return highPriorityQueue;
            } else if(!midPriorityQueue.isEmpty()){
                return midPriorityQueue;
            } else {
                return lowPriorityQueue;
            }
        } else {
            int x1 = priorityPercent[0];
            int x2 = x1 + priorityPercent[1];
            int r = random.nextInt(100)+1; // r in [1, 101)
            if(r<=x1){//选中 低优先级队列
                if(lowPriorityQueue.isEmpty()){
                    if(highPriorityQueue.isEmpty()){
//                        log("selectPriorityQueue random = " + r + " , select 中优先级队列");
                        return midPriorityQueue;
                    } else {
//                        log("selectPriorityQueue random = " + r + " , select 高优先级队列");
                        return highPriorityQueue;
                    }
                } else {
//                    log("selectPriorityQueue random = " + r + " , select 低优先级队列");
                    return lowPriorityQueue;
                }
            } else if(r<=x2){//选中 中优先级队列
                if(midPriorityQueue.isEmpty()){
                    if(highPriorityQueue.isEmpty()){
//                        log("selectPriorityQueue random = " + r + " , select 低优先级队列");
                        return lowPriorityQueue;
                    } else {
//                        log("selectPriorityQueue random = " + r + " , select 高优先级队列");
                        return highPriorityQueue;
                    }
                } else {
//                    log("selectPriorityQueue random = " + r + " , select 中优先级队列");
                    return midPriorityQueue;
                }
            } else {//选中 高优先级队列
                if(highPriorityQueue.isEmpty()){
                    if(midPriorityQueue.isEmpty()){
//                        log("selectPriorityQueue random = " + r + " , select 低优先级队列");
                        return lowPriorityQueue;
                    } else {
//                        log("selectPriorityQueue random = " + r + " , select 中优先级队列");
                        return midPriorityQueue;
                    }
                } else {
//                    log("selectPriorityQueue random = " + r + " , select 高优先级队列");
                    return highPriorityQueue;
                }
            }
        }

    }


    /**
     * Used by {@code Call#execute} to signal it is in-flight.
     */
    synchronized void executed(VtRealCall call) {
        runningSyncCalls.add(call);
    }

    /**
     * Used by {@code AsyncCall#run} to signal completion.
     */
    void finished(VtRealCall.AsyncCall call) {
        finished(runningAsyncCalls, call, true);
    }

    /**
     * Used by {@code Call#execute} to signal completion.
     */
    void finished(VtRealCall call) {
        finished(runningSyncCalls, call, false);
    }

    private <T> void finished(Queue<T> calls, T call, boolean promoteCalls) {
        int runningCallsCount;
        Runnable idleCallback;
        synchronized (this) {
            if (!calls.remove(call)) throw new AssertionError("Call waVt't in-flight!");
            if (promoteCalls) promoteCalls();
            runningCallsCount = runningCallsCount();
            idleCallback = this.idleCallback;
        }

        if (runningCallsCount == 0 && idleCallback != null) {
            idleCallback.run();
        }
    }

    /**
     * Returns a Vtapshot of the calls currently awaiting execution.
     */
    public synchronized List<VtCall> queuedCalls() {
        List<VtCall> result = new ArrayList<>();
//        for (VtRealCall.AsyncCall asyncCall : readyAsyncCalls) {
//            result.add(asyncCall.get());
//        }

        for (VtRealCall.AsyncCall asyncCall : lowPriorityQueue) {
            result.add(asyncCall.get());
        }

        for (VtRealCall.AsyncCall asyncCall : midPriorityQueue) {
            result.add(asyncCall.get());
        }

        for (VtRealCall.AsyncCall asyncCall : highPriorityQueue) {
            result.add(asyncCall.get());
        }
        return Collections.unmodifiableList(result);
    }

    /**
     * Returns a Vtapshot of the calls currently being executed.
     */
    public synchronized List<VtCall> runningCalls() {
        List<VtCall> result = new ArrayList<>();
        result.addAll(runningSyncCalls);
        for (VtRealCall.AsyncCall asyncCall : runningAsyncCalls) {
            result.add(asyncCall.get());
        }
        return Collections.unmodifiableList(result);
    }

    public synchronized int queuedCallsCount() {
        int i = 0;
        for (VtRealCall.AsyncCall asyncCall : lowPriorityQueue) {
            i++;
        }

        for (VtRealCall.AsyncCall asyncCall : midPriorityQueue) {
            i++;
        }

        for (VtRealCall.AsyncCall asyncCall : highPriorityQueue) {
            i++;
        }
        return i;
    }

    public synchronized int runningCallsCount() {
        return runningAsyncCalls.size() + runningSyncCalls.size();
    }
}
