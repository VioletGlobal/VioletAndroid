package com.violet.center;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by kan212 on 2018/4/13.
 */

public class CenterManager {

    private volatile static CenterManager instance;
    private EventBus mEventBus = null;

    public CenterManager() {
    }

    public static CenterManager getInstance() {
        if (instance == null) {
            synchronized (CenterManager.class) {
                if (instance == null) {
                    instance = new CenterManager();
                }
            }
        }
        return instance;
    }

    public EventBus getBus() {
        if (mEventBus == null) {
            mEventBus = EventBus.getDefault();
        }
        return mEventBus;
    }
    public void register(Object subscriber) {
        getBus().register(subscriber);
    }

    public void unregister(Object subscriber) {
        getBus().unregister(subscriber);
    }

    public void post(Object event) {
        getBus().post(event);
    }

    public void cancelEventDelivery(Object event) {
        getBus().cancelEventDelivery(event);
    }

    public void postSticky(Object event) {
        getBus().postSticky(event);
    }

    public Object getStickyEvent(Class<?> eventType) {
        return getBus().getStickyEvent(eventType);
    }

    public boolean hasSubscriberForEvent(Class<?> eventType) {
        return getBus().hasSubscriberForEvent(eventType);
    }

    public boolean isRegistered(Object subscriber) {
        return getBus().isRegistered(subscriber);
    }

    public void removeAllStickyEvents() {
        getBus().removeAllStickyEvents();
    }

    public boolean removeStickyEvent(Object event) {
        return getBus().removeStickyEvent(event);
    }
}
