package com.violet.imageloader.core;

import com.violet.imageloader.core.imageAware.ImageAware;

import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by kan212 on 2018/8/29.
 * 任务分发器，负责分发LoadAndDisplayImageTask和ProcessAndDisplayImageTask给具体的线程池去执行
 */

class ImageLoaderEngine {
    //ImageLoader的配置信息，可包括图片最大尺寸、线程池、缓存、下载器、解码器等等。
    final ImageLoaderConfiguration configuration;

    //用于执行从源获取图片任务的 Executor，为configuration中的 taskExecutor，如果为null，
    //则会调用DefaultConfigurationFactory.createExecutor(…)根据配置返回一个默认的线程池。
    private Executor taskExecutor;
    //用于执行从缓存获取图片任务的 Executor，为configuration中的 taskExecutorForCachedImages，
    //如果为null，则会调用DefaultConfigurationFactory.createExecutor(…)根据配置返回一个默认的线程池。
    private Executor taskExecutorForCachedImages;
    //任务分发线程池，任务指LoadAndDisplayImageTask和ProcessAndDisplayImageTask，
    //因为只需要分发给上面的两个 Executor 去执行任务，不存在较耗时或阻塞操作，所以用无并发数(Int 最大值)
    //限制的线程池即可。
    private Executor taskDistributor;
    //ImageAware与内存缓存 key 对应的 map，key 为ImageAware的 id，value 为内存缓存的 key。
    private final Map<Integer, String> cacheKeysForImageAwares = Collections
            .synchronizedMap(new HashMap<Integer, String>());
    //图片正在加载的重入锁 map，key 为图片的 uri，value 为标识其正在加载的重入锁。
    private final Map<String, ReentrantLock> uriLocks = new WeakHashMap<String, ReentrantLock>();

    //是否被暂停。如果为true，则所有新的加载或显示任务都会等待直到取消暂停(为false)。
    private final AtomicBoolean paused = new AtomicBoolean(false);
    //是否不允许访问网络，如果为true，通过ImageLoadingListener.onLoadingFailed(…)获取图片，
    //则所有不在缓存中需要网络访问的请求都会失败，返回失败原因为网络访问被禁止。
    private final AtomicBoolean networkDenied = new AtomicBoolean(false);
    //是否是慢网络情况，如果为true，则自动调用SlowNetworkImageDownloader下载图片。
    private final AtomicBoolean slowNetwork = new AtomicBoolean(false);
    //暂停的等待锁，可在engine被暂停后调用这个锁等待。
    private final Object pauseLock = new Object();

    public ImageLoaderEngine(ImageLoaderConfiguration configuration) {
        this.configuration = configuration;
        this.taskExecutor = configuration.taskExecutor;
        this.taskExecutorForCachedImages = configuration.taskExecutorForCachedImages;
        //为ImageLoaderEngine中的任务分发器taskDistributor提供线程池，该线程池为 normal
        // 优先级的无并发大小限制的线程池。
        this.taskDistributor = DefaultConfigurationFactory.createTaskDistributor();
    }

    /**
     * 添加一个LoadAndDisplayImageTask。直接用taskDistributor执行一个 Runnable，
     * 在 Runnable 内部根据图片是否被磁盘缓存过确定使用taskExecutorForCachedImages还是taskExecutor执行该 task。
     *
     * @param task
     */
    void submit(final LoadAndDisplayImageTask task) {
        taskDistributor.execute(new Runnable() {
            @Override
            public void run() {
                File image = configuration.diskCache.get(task.getLoadingUri());
                boolean isImageCachedOnDisk = image != null && image.exists();
                initExecutorsIfNeed();
                //如果是在disk上保存的就需要用缓存的线程池获取
                if (isImageCachedOnDisk) {
                    taskExecutorForCachedImages.execute(task);
                } else {
                    taskExecutor.execute(task);
                }
            }
        });
    }

    void submit(ProcessAndDisplayImageTask task) {
        initExecutorsIfNeed();
        taskExecutorForCachedImages.execute(task);
    }


    /**
     * 线程池保证在运行
     */
    private void initExecutorsIfNeed() {
        //如果用户没有自定义线程池，而且现在的已经关闭
        if (!configuration.customExecutor && ((ExecutorService) taskExecutor).isShutdown()) {
            taskExecutor = createTaskExecutor();
        }
        //从缓存中获取的线程池关闭了
        if (!configuration.customExecutorForCachedImages && ((ExecutorService) taskExecutorForCachedImages).isShutdown()) {
            taskExecutorForCachedImages = createTaskExecutor();
        }
    }

    /**
     * 如果用户没有自定义线程池，而且现在的已经关闭
     *
     * @return
     */
    private Executor createTaskExecutor() {
        return DefaultConfigurationFactory.createExecutor(configuration.threadPoolSize, configuration.threadPriority, configuration.tasksProcessingType);
    }

    /**
     * taskDistributor立即执行某个任务。
     *
     * @param r
     */
    void fireCallback(Runnable r) {
        taskDistributor.execute(r);
    }

    /**
     * 得到某个imageAware正在加载的图片 uri。
     *
     * @param imageAware
     * @return
     */
    String getLoadingUriForView(ImageAware imageAware) {
        return cacheKeysForImageAwares.get(imageAware.getId());
    }

    AtomicBoolean getPause() {
        return paused;
    }

    Object getPauseLock() {
        return pauseLock;
    }

    boolean isNetworkDenied() {
        return networkDenied.get();
    }

    boolean isSlowNetwork() {
        return slowNetwork.get();
    }

    /**
     * 取消一个显示任务。从cacheKeysForImageAwares中删除ImageAware对应元素。
     *
     * @param imageAware
     */
    public void cancelDisplayTaskFor(ImageAware imageAware) {
        cacheKeysForImageAwares.remove(imageAware.getId());
    }

    /**
     * 准备开始一个Task。向cacheKeysForImageAwares中插入ImageAware的 id 和图片在内存缓存中的 key。
     * @param imageAware
     * @param memoryCacheKey
     */
    public void prepareDisplayTaskFor(ImageAware imageAware, String memoryCacheKey) {
        cacheKeysForImageAwares.put(imageAware.getId(),memoryCacheKey);
    }

    /**
     * 获取图片正在载入的对象锁
     * @param uri
     * @return
     */
    public ReentrantLock getLockForUri(String uri) {
        ReentrantLock lock = uriLocks.get(uri);
        if(null == lock){
            lock = new ReentrantLock();
            uriLocks.put(uri,lock);
        }
        return lock;
    }
}
