package com.violet.lib.fragment.rigger.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Used to mark a fragment is using lazy load.
 * Lazy load:fragment's container view will be created and init the data when the fragment is showed.
 * Annotation class that can only used on {@link android.support.v4.app.Fragment}.
 *
 */
@Inherited
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface LazyLoad {

  /**
   * Whether to use lazy load.default value is true.
   */
  boolean value() default true;
}
