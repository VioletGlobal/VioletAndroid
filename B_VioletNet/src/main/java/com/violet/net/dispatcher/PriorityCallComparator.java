package com.violet.net.dispatcher;

import java.util.Comparator;

/**
 * Created by kan212 on 2018/7/26.
 */

public class PriorityCallComparator<T> implements Comparator<T> {
    @Override
    public int compare(T left, T right) {
        if (VtCall.Priority.class.isInstance(left)
                && VtCall.Priority.class.isInstance(right)) {
            VtCall.Priority rp = VtCall.Priority.class.cast(right);
            VtCall.Priority lp = VtCall.Priority.class.cast(left);

            return rp.priority().intValue() - lp.priority().intValue();
        }
        return 0;
    }
}
