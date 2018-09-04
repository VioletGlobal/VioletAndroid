package com.violet.lib.opensource.ultra;

import com.violet.lib.opensource.ultra.indicator.PtrIndicator;

/**
 * Created by kan212 on 2018/9/3.
 * 实现 UI 接口 PtrUIHandler，封装了 PtrUIHandler，并将其组织成链表的形式。之所以封装成链表的目的是作者希望调用者可以像
 * Header 一样去实现 PtrUIHandler，能够捕捉到 onUIReset，onUIRefreshPrepare，onUIRefreshBegin，onUIRefreshComplete
 * 这几个时机去实现自己的逻辑或者 UI 效果，而它们统一由 PtrUIHandlerHolder 来管理，你只需要 通过 addHandler 方法加入到链表中即可，
 * 这一点的抽象为那些希望去做一些处理的开发者还是相当方便的
 */

public class PtrUIHandlerHolder implements PtrUIHandler {

    private PtrUIHandler mHandler;
    private PtrUIHandlerHolder mNext;

    private boolean contains(PtrUIHandler handler) {
        return mHandler != null && mHandler == handler;
    }

    public boolean hasHandler() {
        return mHandler != null;
    }

    public static void addHandler(PtrUIHandlerHolder head, PtrUIHandler handler) {
        if (null == handler) {
            return;
        }
        if (head == null) {
            return;
        }
        if (null == head.mHandler) {
            head.mHandler = handler;
            return;
        }
        PtrUIHandlerHolder current = head;
        for (; ; current = current.mNext) {
            if (current.contains(handler)) {
                return;
            }
            if (current.mNext == null) {
                break;
            }
        }
        PtrUIHandlerHolder newHolder = new PtrUIHandlerHolder();
        newHolder.mHandler = handler;
        current.mNext = newHolder;
    }

    public static PtrUIHandlerHolder removeHandler(PtrUIHandlerHolder head, PtrUIHandler handler) {
        if (head == null || handler == null || null == head.mHandler) {
            return head;
        }
        PtrUIHandlerHolder current = head;
        PtrUIHandlerHolder pre = null;
        do {
            // delete current: link pre to next, unlink next from current;
            // pre will no change, current move to next element;
            if (current.contains(handler)) {
                // current is head
                if (pre == null) {

                    head = current.mNext;
                    current.mNext = null;

                    current = head;
                } else {

                    pre.mNext = current.mNext;
                    current.mNext = null;
                    current = pre.mNext;
                }
            } else {
                pre = current;
                current = current.mNext;
            }
        } while (null != current);

        if (head == null) {
            head = new PtrUIHandlerHolder();
        }
        return head;
    }

    public static PtrUIHandlerHolder create() {
        return new PtrUIHandlerHolder();
    }

    @Override
    public void onUIReset(PtrFrameLayout frame) {
        if (!hasHandler()) {
            return;
        }
        PtrUIHandlerHolder current = this;
        do {
            final PtrUIHandler handler = current.mHandler;
            if (null != mHandler) {
                mHandler.onUIReset(frame);
            }
        } while ((current = current.mNext) != null);
    }

    @Override
    public void onUIRefreshPrepare(PtrFrameLayout frame) {
        if (!hasHandler()) {
            return;
        }
        PtrUIHandlerHolder current = this;
        do {
            final PtrUIHandler handler = current.mHandler;
            if (null != mHandler) {
                mHandler.onUIRefreshPrepare(frame);
            }
        } while ((current = current.mNext) != null);
    }

    @Override
    public void onUIRefreshBegin(PtrFrameLayout frame) {
        if (!hasHandler()) {
            return;
        }
        PtrUIHandlerHolder current = this;
        do {
            final PtrUIHandler handler = current.mHandler;
            if (null != mHandler) {
                mHandler.onUIRefreshBegin(frame);
            }
        } while ((current = current.mNext) != null);
    }

    @Override
    public void onUIRefreshComplete(PtrFrameLayout frame) {
        if (!hasHandler()) {
            return;
        }
        PtrUIHandlerHolder current = this;
        do {
            final PtrUIHandler handler = current.mHandler;
            if (null != mHandler) {
                mHandler.onUIRefreshComplete(frame);
            }
        } while ((current = current.mNext) != null);
    }

    @Override
    public void onUIPositionChange(PtrFrameLayout frame, boolean isUnderTouch, byte status, PtrIndicator ptrIndicator) {
        if (!hasHandler()) {
            return;
        }
        PtrUIHandlerHolder current = this;
        do {
            final PtrUIHandler handler = current.mHandler;
            if (null != mHandler) {
                mHandler.onUIPositionChange(frame,isUnderTouch,status,ptrIndicator);
            }
        } while ((current = current.mNext) != null);
    }
}
