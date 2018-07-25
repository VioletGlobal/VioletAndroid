package com.violet.lib.fragment.rigger.aop;

import android.os.Bundle;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

import java.lang.reflect.Method;

/**
 * Using AspectJ tools reach AOP. this class is used to inject
 * {@link com.violet.lib.fragment.rigger.rigger.Rigger} to Activity's lifecycle and other methods.
 */
@Aspect
public class ActivityInjection extends RiggerInjection {

    //****************PointCut***********************************

    @Pointcut("execution(android.support.v4.app.FragmentActivity+.new()) && annotatedWithPuppet()")
    public void construct() {
    }

    @Pointcut("execution(* android.support.v4.app.FragmentActivity+.onCreate(..)) && annotatedWithPuppet()")
    public void onCreate() {
    }

    @Pointcut("execution(* android.support.v4.app.FragmentActivity+.onResumeFragments(..)) && annotatedWithPuppet()")
    public void onResumeFragments() {
    }

    @Pointcut("execution(* android.support.v4.app.FragmentActivity+.onPause(..)) && annotatedWithPuppet()")
    public void onPause() {
    }

    @Pointcut("execution(* android.support.v4.app.FragmentActivity+.onResume(..)) && annotatedWithPuppet()")
    public void onResume() {
    }

    @Pointcut("execution(* android.support.v4.app.FragmentActivity+.onSaveInstanceState(..)) && annotatedWithPuppet()")
    public void onSaveInstanceState() {
    }

    @Pointcut("execution(* android.support.v4.app.FragmentActivity+.onDestroy(..)) && annotatedWithPuppet()")
    public void onDestroy() {
    }

    @Pointcut("execution(* android.support.v4.app.FragmentActivity+.onBackPressed(..)) && annotatedWithPuppet()")
    public void onBackPressed() {
    }

    //****************Process***********************************

    @Around("construct()")
    public Object constructProcess(ProceedingJoinPoint joinPoint) throws Throwable {
        Object result = joinPoint.proceed();
        Object puppet = joinPoint.getTarget();
        //Only inject the class that marked by Puppet annotation.
        Method onAttach = getRiggerMethod("onPuppetConstructor", Object.class);
        onAttach.invoke(getRiggerInstance(), puppet);
        return result;
    }

    @Around("onCreate()")
    public Object onCreateProcess(ProceedingJoinPoint joinPoint) throws Throwable {
        Object result = joinPoint.proceed();
        Object puppet = joinPoint.getTarget();
        //Only inject the class that marked by Puppet annotation.
        Object[] args = joinPoint.getArgs();

        Method onCreate = getRiggerMethod("onCreate", Object.class, Bundle.class);
        onCreate.invoke(getRiggerInstance(), puppet, args[0]);
        return result;
    }

    @Around("onResumeFragments()")
    public Object onResumeFragmentsProcess(ProceedingJoinPoint joinPoint) throws Throwable {
        Object result = joinPoint.proceed();
        Object puppet = joinPoint.getTarget();
        //Only inject the class that marked by Puppet annotation.

        Method onResumeFragments = getRiggerMethod("onResumeFragments", Object.class);
        onResumeFragments.invoke(getRiggerInstance(), puppet);
        return result;
    }

    @Around("onPause()")
    public Object onPauseProcess(ProceedingJoinPoint joinPoint) throws Throwable {
        Object result = joinPoint.proceed();
        Object puppet = joinPoint.getTarget();
        //Only inject the class that marked by Puppet annotation.

        Method onPause = getRiggerMethod("onPause", Object.class);
        onPause.invoke(getRiggerInstance(), puppet);
        return result;
    }

    @Around("onResume()")
    public Object onResumeProcess(ProceedingJoinPoint joinPoint) throws Throwable {
        Object result = joinPoint.proceed();
        Object puppet = joinPoint.getTarget();
        //Only inject the class that marked by Puppet annotation.

        Method onPause = getRiggerMethod("onResume", Object.class);
        onPause.invoke(getRiggerInstance(), puppet);
        return result;
    }

    @Around("onSaveInstanceState()")
    public Object onSaveInstanceStateProcess(ProceedingJoinPoint joinPoint) throws Throwable {
        Object result = joinPoint.proceed();
        Object puppet = joinPoint.getTarget();
        //Only inject the class that marked by Puppet annotation.
        Object[] args = joinPoint.getArgs();

        Method onSaveInstanceState = getRiggerMethod("onSaveInstanceState", Object.class, Bundle.class);
        onSaveInstanceState.invoke(getRiggerInstance(), puppet, args[0]);
        return result;
    }

    @Around("onDestroy()")
    public Object onDestroyProcess(ProceedingJoinPoint joinPoint) throws Throwable {
        Object result = joinPoint.proceed();
        Object puppet = joinPoint.getTarget();
        //Only inject the class that marked by Puppet annotation.

        Method onDestroy = getRiggerMethod("onDestroy", Object.class);
        onDestroy.invoke(getRiggerInstance(), puppet);
        return result;
    }

    @Around("onBackPressed()")
    public Object onBackPressedProcess(ProceedingJoinPoint joinPoint) throws Throwable {
//    Object result = joinPoint.proceed();
        Object puppet = joinPoint.getTarget();
        //Only inject the class that marked by Puppet annotation.

        Method onBackPressed = getRiggerMethod("onBackPressed", Object.class);
        return onBackPressed.invoke(getRiggerInstance(), puppet);
    }
}
