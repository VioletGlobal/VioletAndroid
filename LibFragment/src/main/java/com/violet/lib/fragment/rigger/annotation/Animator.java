package com.violet.lib.fragment.rigger.annotation;

import android.support.annotation.AnimRes;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Used to set specific animation resources to run for the fragments that are
 * entering and exiting in this transaction.
 * This annotation can only be effective for fragment.
 */
@Inherited
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Animator {

  /**
   * This animator will worked on the target fragment.
   * This animator will be started when the target fragment is entering.
   */
  @AnimRes
  int enter() default 0;

  /**
   * This animator will worked on the target fragment.
   * This animator will be started when the target fragment is exiting
   */
  @AnimRes
  int exit() default 0;

  /**
   * This animator will worked on the pop fragment that is on target fragment's same level.
   * This animator will be started when the target fragment is exiting and the target fragment's same level pop
   * fragment is showing.
   */
  @AnimRes
  int popEnter() default 0;

  /**
   * This animator will worked on the pop fragment that is on target fragment's same level.
   * This animator will be started when the target fragment is entering and the target fragment's same level pop
   * fragment is exiting.
   */
  @AnimRes
  int popExit() default 0;
}
