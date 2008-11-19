/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.    
 */

package org.apache.tuscany.sca.aspectj;

import java.util.Arrays;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;

/**
 * LoggingAspect performs standard logging of method signatures, arguments, and
 * return values. All Tuscany methods, constructors, and statics are logged.
 * @version $Rev$ $Date$
 */
@Aspect
public class LoggingAspect {
    @Pointcut("call(* org.apache.tuscany.sca..*(..)) && (!within(org.apache.tuscany.sca.aspectj.*Aspect))")
    public void anyMethodCall() {
    }
    
    @Pointcut("execution(* org.apache.tuscany.sca..*(..)) && (!within(org.apache.tuscany.sca.aspectj.*Aspect))")
    public void anyMethodExecution() {
    }
    
    @Pointcut("call(* java.util.logging.Logger.info(..))")
    public void anyLogCall() {
    }    
    
    @Pointcut("cflow(anyMethodExecution()) && anyLogCall()")
    public void anyLog() {
    }

    @Pointcut("call(org.apache.tuscany.sca..*.new(..))")
    public void anyConstructor() {
    }

    // e.g. org.apache.tuscany.sca.implementation.java.introspect.impl.JavaIntrospectionHelper
    @Pointcut("staticinitialization(org.apache.tuscany.sca.implementation..*)")
    public void anyStatic() {
    }

    @Before("anyMethodCall()")
    public void before(JoinPoint jp) {
        // System.out.println("Logging anyMethodCall before jp=" + jp);
        // System.out.println("Logging anyMethodCall before jp.getSourceLocation=" + jp.getSourceLocation());
        // System.out.println("Logging anyMethodCall before jp.getThis=" + jp.getThis());
        // System.out.println("Logging anyMethodCall before jp.getTarget=" + jp.getTarget());
        System.out.println("Logging Before anyMethodCall jp.getSignature=" + jp.getSignature());
        java.lang.Object[] args = jp.getArgs();
        if (( args != null ) && ( args.length > 0 )) {
           System.out.println("Logging Before anyMethodCall jp.getArgs=" + Arrays.asList(args));
        }
    }

    @AfterReturning(pointcut = "anyMethodCall()", returning = "result")
    public void afterReturning(JoinPoint jp, Object result) {
        // Note that result is null for methods with void return.
        System.out.println("Logging AfterReturning anyMethodCall jp=" + jp + ", result=" + result);
    }

    @AfterThrowing(pointcut = "anyMethodCall()", throwing = "t")
    public void afterThrowing(JoinPoint jp, Throwable t) {
        System.out.println("Logging AfterThrowing anyMethodCall jp=" + jp + ", t=" + t);
    }

    @Before("anyConstructor()")
    public void beforeConstructor(JoinPoint jp) {
        System.out.println("Logging Before anyConstructor jp.getSignature=" + jp.getSignature());
        java.lang.Object[] args = jp.getArgs();
        if (( args != null ) && ( args.length > 0 )) {
           System.out.println("Logging Before anyConstructor jp.getArgs=" + Arrays.asList(args));
        }
    }

    @Before("anyStatic()")
    public void beforeStatic(JoinPoint jp) {
        System.out.println("Logging Before anyStatic before jp=" + jp);
        System.out.println("Logging anyMethodCall before jp.getSourceLocation=" + jp.getSourceLocation());
    }

}
