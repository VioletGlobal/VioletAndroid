package com.violet.imageloader.core;

import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.widget.ImageView;

import com.violet.imageloader.core.assist.ImageSize;
import com.violet.imageloader.core.assist.LoadedFrom;
import com.violet.imageloader.core.imageAware.ImageAware;
import com.violet.imageloader.core.imageAware.ImageViewAware;
import com.violet.imageloader.core.listener.ImageLoadingListener;
import com.violet.imageloader.core.listener.ImageLoadingProgressListener;
import com.violet.imageloader.core.listener.SimpleImageLoadingListener;
import com.violet.imageloader.utils.ImageSizeUtils;
import com.violet.imageloader.utils.L;
import com.violet.imageloader.utils.MemoryCacheUtils;

/**
 * Created by kan212 on 2018/8/29.
 */

public class ImageLoader {

    public static final String TAG = ImageLoader.class.getSimpleName();

    static final String LOG_INIT_CONFIG = "Initialize ImageLoader with configuration";
    static final String LOG_DESTROY = "Destroy ImageLoader";
    static final String LOG_LOAD_IMAGE_FROM_MEMORY_CACHE = "Load image from memory cache [%s]";

    private static final String WARNING_RE_INIT_CONFIG = "Try to initialize ImageLoader which had already been initialized before. " + "To re-init ImageLoader with new configuration call ImageLoader.destroy() at first.";
    private static final String ERROR_WRONG_ARGUMENTS = "Wrong arguments were passed to displayImage() method (ImageView reference must not be null)";
    private static final String ERROR_NOT_INIT = "ImageLoader must be init with configuration before using";
    private static final String ERROR_INIT_CONFIG_WITH_NULL = "ImageLoader configuration can not be initialized with null";

    public volatile static ImageLoader instance;

    private ImageLoaderConfiguration configuration;
    private ImageLoadingListener defaultListener = new SimpleImageLoadingListener();

    private ImageLoaderEngine engine;


    public static ImageLoader getInstance(){
        if (null == instance){
            synchronized (ImageLoader.class){
                if (null == instance){
                    instance = new ImageLoader();
                }
            }
        }
        return instance;
    }

    /**
     * 初始化配置参数，参数configuration为ImageLoader的配置信息，
     * 包括图片最大尺寸、任务线程池、磁盘缓存、下载器、解码器等等。
     * @param configuration
     */
    public synchronized void init(ImageLoaderConfiguration configuration){
        if (configuration == null) {
            throw new IllegalArgumentException(ERROR_INIT_CONFIG_WITH_NULL);
        }
        if (this.configuration == null){
            L.d(LOG_INIT_CONFIG);
            engine = new ImageLoaderEngine(configuration);
            this.configuration = configuration;
        }else {
            L.w(WARNING_RE_INIT_CONFIG);
        }
    }

    public void displayImage(String uri, ImageView imageView){
        displayImage(uri,new ImageViewAware(imageView),null,null,null,null);
    }

    public void displayImage(String uri, ImageView imageView,DisplayImageOptions options){
        displayImage(uri,new ImageViewAware(imageView),options,null,null,null);
    }

    public void displayImage(String uri, ImageAware imageAware, DisplayImageOptions options,
                             ImageSize targetSize,ImageLoadingListener listener,ImageLoadingProgressListener progressListener){
        //1、检查参数和部分初始化
        checkConfiguration();
        if (null == imageAware){
            throw new IllegalArgumentException(ERROR_WRONG_ARGUMENTS);
        }
        if (listener == null) {
            listener = defaultListener;
        }
        if (options == null){
            options = configuration.defaultDisplayImageOptions;
        }
        //2、如果uri为空，看是不是需要展示空的默图，不是则显示为null
        if (TextUtils.isEmpty(uri)){
            engine.cancelDisplayTaskFor(imageAware);
            listener.onLoadingStarted(uri,imageAware.getWrappedView());
            if (options.shouldShowImageForEmptyUri()){
                imageAware.setImageDrawable(options.getImageForEmptyUri(configuration.resources));
            }else {
                imageAware.setImageDrawable(null);
            }
            listener.onLoadingComplete(uri,imageAware.getWrappedView(),null);
        }
        if (targetSize == null) {
            targetSize = ImageSizeUtils.defineTargetSizeForView(imageAware, configuration.getMaxImageSize());
        }
        //3、根据uri生成内存中的key值
        String memoryCacheKey = MemoryCacheUtils.generateKey(uri, targetSize);
        //4、向cacheKeysForImageAwares中插入ImageAware的 id 和图片在内存缓存中的 key
        engine.prepareDisplayTaskFor(imageAware,memoryCacheKey);
        Bitmap bitmap = configuration.memoryCache.get(memoryCacheKey);
        if (null != bitmap){
            L.d(LOG_LOAD_IMAGE_FROM_MEMORY_CACHE, memoryCacheKey);
            //5、如果从内存中取出时候需要额外处理图片
            if (options.shouldPostProcess()){
                ImageLoadingInfo imageLoadingInfo = new ImageLoadingInfo(uri,imageAware,targetSize,memoryCacheKey,options,
                        listener,progressListener,engine.getLockForUri(uri));
                ProcessAndDisplayImageTask task = new ProcessAndDisplayImageTask(engine,bitmap,imageLoadingInfo,defineHandler(options));
                //如果需要同步，则任务直接运行
                if (options.isSyncLoading()){
                    task.run();
                }else {
                    //添加到engine
                    engine.submit(task);
                }
            }else {
                options.getDisplayer().display(bitmap,imageAware, LoadedFrom.MEMORY_CACHE);
                listener.onLoadingComplete(uri,imageAware.getWrappedView(),bitmap);
            }
        }
        //6、内存缓存中不存在的情况
        else {
            if (options.shouldShowImageOnLoading()){
                imageAware.setImageDrawable(options.getImageOnLoading(configuration.resources));
            }
            //在加载前是否重置view
            else if (options.isResetViewBeforeLoading()){
                imageAware.setImageDrawable(null);
            }
            ImageLoadingInfo imageLoadingInfo = new ImageLoadingInfo(uri,imageAware,targetSize,memoryCacheKey,options,
                    listener,progressListener,engine.getLockForUri(uri));
            LoadAndDisplayImageTask task = new LoadAndDisplayImageTask(engine,imageLoadingInfo,defineHandler(options));
            if (options.isSyncLoading()){
                task.run();
            }else {
                engine.submit(task);
            }
        }
    }

    /**
     * 生成主线程的handler
     * @param options
     * @return
     */
    private  static Handler defineHandler(DisplayImageOptions options) {
        Handler handler = options.getHandler();
        //是否同步加载
        if (options.isSyncLoading()){
            handler = null;
        }else if(handler == null && Looper.getMainLooper() == Looper.myLooper()){
            handler = new Handler();
        }
        return handler;
    }

    private void checkConfiguration(){
        if (configuration == null) {
            throw new IllegalStateException(ERROR_NOT_INIT);
        }
    }
}
