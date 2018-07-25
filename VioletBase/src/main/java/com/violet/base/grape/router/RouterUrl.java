package com.violet.base.grape.router;

/**
 * Created by kan212 on 2018/4/13.
 * 路由常量
 * <p/>
 * 每个组件都需要注意: 1.不同组件通过接口来区分路由地址和参数key，2命名全部是大写下划线
 */

public class RouterUrl {


    /**
     * 用于包含了fragment的内部跳转
     */
    public interface InnerRouter{

        String INNER_ACTIVITY_FRAGMENT = "/inner/fragment";

    }


    /**
     * JAVA基本组件的路由地址
     */
    public interface JavaRouter {
        /**
         * java面试跳转url
         */
        String LIB_JAVA_ACTIVITY = "/java/interview";

    }

    public interface AndroidRouter{
        String ANDROID_ACTIVITY = "/android/activity";
    }


    public interface OpSourceRouter{
        String OPEN_SOURCE_MAIN = "/open_source/main";
    }

    /**
     * 进程间组件的路由地址
     */
    public interface ProcessRouter {
        /**
         * 引导页跳转url
         */
        String PROCESS_ACTIVITY = "/process/select";

    }

    /**
     * 引导组件路由地址
     */
    public interface GuiderRouter {
        /**
         * 引导页跳转url
         */
        String GUIDER_ACTIVITY = "/guider/select";
    }

    /**
     * APP相关路由地址
     */
    public interface MainRouter {

        /**
         * MainActivity跳转url
         */
        String MAIN_LAUNCHER = "/app/launcher";

        /**
         * HybridContainerActivity跳转url
         */
        String HYBRID_CONTAINER = "/app/hybridActivity";

        /**
         * 主页的跳转
         */
        String MAIN_ACTIVITY = "/main/main_activity";

        /**
         * dagger相关跳转
         */
        String APP_DAGGER = "/app/dagger";

    }

    public interface MainKey {
        String MAIN_TAB_ID = "main_tab";

        int TAB_MINE = 0;
        int TAB_FEED = 1000;
        int TAB_DISCOVER = 2000;
    }

    /**
     * Feed页相关路由地址
     */
    public interface FeedRouter {
        String FEED_FRAGMENT = "/feed/main";
        String FEED_DATA_SERVICE = "/feed/data";
    }

    public interface RiggerRouter{
        String RIGGER_ACTIVITY= "/rigger/activity";
    }


}
