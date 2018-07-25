package com.violet.lib.fragment.swiper.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Support for {@link android.app.Activity}/{@link android.support.v4.app.Fragment} exit page by swipe edge.
 */
@Inherited
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Swiper {

  /**
   * Allow or not to exit {@link android.app.Activity}/{@link android.support.v4.app.Fragment} by swipe edge.
   *
   * @return The default value is true.
   */
  boolean enable() default true;

  /**
   * The edge that can be swiped.
   * The default value is {@link SwipeEdge#LEFT}
   *
   * @return if the value contained {@link SwipeEdge#NONE}, then it is not allowed to swipe.
   */
  SwipeEdge[] enableEdgeSide() default SwipeEdge.LEFT;

  /**
   * Allow or not to show the parallax effect.
   *
   * @return Default value is true.
   */
  boolean parallax() default true;
}
