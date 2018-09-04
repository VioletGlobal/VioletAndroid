package com.violet.lib.opensource.ultra;

/**
 * Created by kan212 on 2018/9/3.
 * 钩子任务类，实现了 Runnable 接口，可以理解为在原来的操作之间，插入了一段任务去执行。
 * 一个钩子任务只能执行一次，通过调用 takeOver 去执行。执行结束，用户需要调用 resume 方法，去恢复执行原来的操作。
 */

public abstract class PtrUIHandlerHook implements Runnable {

    private Runnable mResumeAction;
    private static final byte STATUS_PREPARE = 0;
    private static final byte STATUS_IN_HOOK = 1;
    private static final byte STATUS_RESUMED = 2;
    private byte mStatus = STATUS_PREPARE;

    public void takeOver() {
        takeOver(null);
    }

    private void takeOver(Runnable resumeAction) {
        if (null != resumeAction) {
            mResumeAction = resumeAction;
        }
        switch (mStatus) {
            case STATUS_PREPARE:
                mStatus = STATUS_IN_HOOK;
                run();
                break;
            case STATUS_IN_HOOK:
                break;
            case STATUS_RESUMED:
                resume();
                break;
        }
    }

    public void reset() {
        mStatus = STATUS_PREPARE;
    }

    protected void resume() {
        if (mResumeAction != null) {
            mResumeAction.run();
        }
        mStatus = STATUS_RESUMED;
    }

    /**
     * Hook should always have a resume action, which is hooked by this hook.
     *
     * @param runnable
     */
    public void setResumeAction(Runnable runnable) {
        mResumeAction = runnable;
    }
}
