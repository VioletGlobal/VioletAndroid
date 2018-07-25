package com.violet.process.grape;

import com.violet.base.grape.router.RouterUrl;

/**
 * Created by kan212 on 2018/4/18.
 */

public class ProcessRouterUrl extends RouterUrl{

    public interface ProcessModelRouter{

//        String PROCESS_MODEL_BINDER = "/process/binder";

        /**
         * 内部包含fragment的类
         */
        String PROCESS_INNERACTIVITY = "/process/inner";

    }

    public interface ProcessFragmentRouter{


        String FRAGMENT_BINDER = "/process/binder";

        String FRAGMENT_PROVIDER = "/process/provider";

    }


}
