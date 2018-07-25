package com.violet.grape;

import android.app.Application;
import android.content.Context;
import android.content.res.Configuration;
import android.net.Uri;

import com.alibaba.android.arouter.facade.Postcard;
import com.alibaba.android.arouter.launcher.ARouter;
import com.violet.center.CenterManager;
import com.violet.module.IModule;
import com.violet.module.ModuleManager;
import com.violet.service.IService;

import java.util.List;

/**
 * Created by kan212 on 2018/4/13.
 */

public class VioletGrape implements IModule {

    /**
     * 是否开发模式，默认为true：true开发模式、false发布模式
     */
    public static final boolean isDebug = true;

    private static volatile VioletGrape instance = null;
    private ModuleManager mModuleManager = null;//module管理
    private volatile static boolean hasInit = false;//初始化标志位：true已初始化、false未初始化

    public VioletGrape() {
        if (null == mModuleManager) {
            mModuleManager = new ModuleManager();
        }
    }

    /**
     * 初始化，必须放到application.onCreate中进行初始化
     * @param application
     */
    public static void init(Application application) {
        if (!hasInit){
            //arouter初始化
            if (isDebug) {
                ARouter.openLog();     // 打印日志
                ARouter.openDebug();   // 开启调试模式(如果在InstantRun模式下运行，必须开启调试模式！线上版本需要关闭,否则有安全风险)
                ARouter.printStackTrace();
            }
            ARouter.init(application);
        }
    }

    /**
     * 获取{@link VioletGrape}实例，组件间通信、交互等都从这里开始
     *
     * @return VioletGrape对象实例
     */
    public static VioletGrape getInstance() {
        if (instance == null) {
            synchronized (VioletGrape.class) {
                if (instance == null) {
                    instance = new VioletGrape();
                }
            }
        }
        return instance;
    }

    /**
     * 获取ARouter对象实例
     *
     * @return ARouter对象实例
     */
    public ARouter getRouter() {
        return ARouter.getInstance();
    }

    /**
     * 注入参数或服务，{@link ARouter}
     *
     * @param object 当前使用者
     */
    public void inject(Object object) {
        getRouter().inject(object);
    }

    /**
     * 创建组件间通信中介
     *
     * @param path 路由地址
     * @return {@link Postcard}
     */
    public Postcard build(String path) {
        return getRouter().build(path);
    }

    /**
     * 创建组件间通信中介
     *
     * @param url 安卓{@link Uri}
     * @return {@link Postcard}
     */
    public Postcard build(Uri url) {
        return getRouter().build(url);
    }

    /**
     * 查找组件服务
     *
     * @param service    服务class
     * @param <ISERVICE> 实现{@link IService}的对象
     * @return 服务实例
     */
    public <ISERVICE extends IService> ISERVICE findService(Class<? extends ISERVICE> service) {
        return getRouter().navigation(service);
    }

    /**
     * 注册module
     *
     * @param module 业务方组件
     */
    public void registerModule(IModule module) {
        if (module == null) {
            return;
        }
        if (checkModule()) {
            mModuleManager.addModule(module);
        }
    }

    /**
     * 批量注册module
     *
     * @param modules 业务方组件集合
     */
    public void registerModule(List<IModule> modules) {
        if (modules == null || modules.isEmpty()) {
            return;
        }
        if (checkModule()) {
            mModuleManager.addModule(modules);
        }
    }

    /**
     * 获取消息中心管理器
     *
     * @return CenterManager实例
     */
    public CenterManager getCenter() {
        return CenterManager.getInstance();
    }

    /**
     * 在消息中心注册观察者
     *
     * @param subcscriber 观察者对象
     */
    public void registerCenter(Object subcscriber) {
        getCenter().register(subcscriber);
    }

    /**
     * 从消息中心移除观察者
     *
     * @param subcscriber 观察者对象
     */
    public void unregisterCenter(Object subcscriber) {
        getCenter().unregister(subcscriber);
    }


    /**
     * 发布消息
     *
     * @param event 消息体
     */
    public void emitMessage(Object event) {
        getCenter().post(event);
    }

    /**
     * 查询观察者是否已经注册到消息中心
     *
     * @param subcscriber 观察者
     * @return true已注册、false未注册
     */
    public boolean isRegisterCenter(Object subcscriber) {
        return getCenter().isRegistered(subcscriber);
    }

    /**
     * 校验{@link VioletGrape}是否已经初始化
     *
     * @return true已初始化、false未初始化
     */
    private boolean checkInit() {
        return hasInit;
    }

    /**
     * 校验moduleManager是否已经初始化
     *
     * @return true已初始化、false未初始化
     */
    private boolean checkModule() {
        return checkInit() && mModuleManager != null;
    }


    @Override
    public void attach(Context context) {
        if (checkModule()) {
            mModuleManager.attach(context);
        }
    }

    @Override
    public void onCreate() {
        if (checkModule()) {
            mModuleManager.onCreate();
        }
    }

    @Override
    public void onTerminate() {
        if (checkModule()) {
            mModuleManager.onTerminate();
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        if (checkModule()) {
            mModuleManager.onConfigurationChanged(newConfig);
        }
    }

    @Override
    public void onLowMemory() {
        if (checkModule()) {
            mModuleManager.onLowMemory();
        }
    }

    @Override
    public void onTrimMemory(int level) {
        if (checkModule()) {
            mModuleManager.onTrimMemory(level);
        }
    }

    @Override
    public List<Object> registerSMBus() {
        if (checkModule()) {
            mModuleManager.registerSMBus();
        }
        return null;
    }
}
