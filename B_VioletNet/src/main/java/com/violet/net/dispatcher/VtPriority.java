package com.violet.net.dispatcher;

/**
 * Created by kan212 on 2018/7/25.
 */

public class VtPriority {

    private int mPriority;

    private static final int P_LOW_VALUE = 500;
    private static final int P_MID_VALUE = 1000;
    private static final int P_HIGH_VALUE = 1500;

    public static final VtPriority PRIORITY_LOW = new VtPriority(P_LOW_VALUE);
    public static final VtPriority PRIORITY_MID = new VtPriority(P_MID_VALUE);
    public static final VtPriority PRIORITY_HIGH = new VtPriority(P_HIGH_VALUE);


    public VtPriority(int priority) {
        this.mPriority = priority;
    }

    public int intValue() {
        return mPriority;
    }
}
