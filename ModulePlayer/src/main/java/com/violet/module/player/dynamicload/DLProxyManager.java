package com.violet.module.player.dynamicload;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by kan212 on 2018/9/5.
 * 目前感觉是所有的控制类的安装和解除安装
 */

public class DLProxyManager {

    private static DLProxyManager instance = new DLProxyManager();

    public static DLProxyManager getInstance(){
        return instance;
    }

    private final HashMap<Class<? extends DLStaticProxyBase>, DLStaticProxyBase> staticProxyPool;

    private final HashMap<Class<? extends IDLProxyBase>, IDLProxyBase> proxyPool;

    public DLProxyManager() {
        staticProxyPool = new HashMap<>();
        proxyPool = new HashMap<>();
    }

    public void put(Class<? extends DLStaticProxyBase> clazz, DLStaticProxyBase instance) {
        synchronized (DLProxyManager.class) {
            staticProxyPool.put(clazz, instance);
        }
    }

    public void put(Class<? extends IDLProxyBase> clazz, IDLProxyBase instance) {
        synchronized (DLProxyManager.class) {
            proxyPool.put(clazz, instance);
        }
    }

    public void install() {
        ArrayList<IDLProxyBase> list;
        synchronized (proxyPool) {
            list = new ArrayList<>(proxyPool.values());
        }
        try {
            if (list.size() > 0) {
                for (IDLProxyBase idlProxyBase : list) {
                    idlProxyBase.install();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void uninstall() {
        staticProxyPool.clear();
        ArrayList<IDLProxyBase> list;
        synchronized (proxyPool) {
            list = new ArrayList<>(proxyPool.values());
        }
        proxyPool.clear();
        if (list.size() > 0) {
            for (IDLProxyBase iProxyBase : list) {
                iProxyBase.uninstall();
            }
        }
        System.gc();
    }

    public <T extends DLStaticProxyBase> T get(Class<T> clazz) {
        return (T) staticProxyPool.get(clazz);
    }
}
