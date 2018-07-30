package com.violet.net.sports.request.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by kan212 on 2018/7/27.
 */
@Inherited
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface JsonReaderField {
    /**
     * 默认使用被修饰field的名称作为key值
     *
     * @return json对应key值。默认为"";起始数组为"[]"
     */
    String key() default "";

    boolean map() default false;
}
