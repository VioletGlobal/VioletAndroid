package com.violet.lib.fragment.rigger.aop;

import com.violet.lib.fragment.rigger.rigger.Rigger;

import org.aspectj.lang.annotation.Pointcut;

import java.lang.reflect.Method;

/**
 * Using AspectJ tools reach AOP. this class is used to define common method.
 */
class RiggerInjection {

  /**
   * PointCut method.find all classes that is marked by
   * {@link com.violet.lib.fragment.rigger.annotation.Puppet} Annotation.
   */
  @Pointcut("@target(com.violet.lib.fragment.rigger.annotation.Puppet)")
  public void annotatedWithPuppet() {
  }

  //****************Helper************************************

  /**
   * Returns the instance of Rigger class by reflect.
   */
  Rigger getRiggerInstance() throws Exception {
    Class<?> riggerClazz = Class.forName(Rigger.class.getName());
    Method getInstance = riggerClazz.getDeclaredMethod("getInstance");
    getInstance.setAccessible(true);
    return (Rigger) getInstance.invoke(null);
  }

  /**
   * Returns the method object of Rigger by reflect.
   */
  Method getRiggerMethod(String methodName, Class<?>... params) throws Exception {
    Rigger rigger = getRiggerInstance();
    Class<? extends Rigger> clazz = rigger.getClass();
    Method method = clazz.getDeclaredMethod(methodName, params);
    method.setAccessible(true);
    return method;
  }
}
