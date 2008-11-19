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
    
    // @Around("anyMethodCall() || anyLog()")
    @Around("anyLog()")
    public Object around(ProceedingJoinPoint jp) throws Throwable {
        System.out.println("Around: " + jp);
        long start = System.currentTimeMillis();
        try {
            return jp.proceed();
        } finally {
            long end = System.currentTimeMillis();
            System.out.println("Roundtrip is " + (end - start) + "ms for " + jp.getSignature());
        }

    }

    @Before("anyMethodCall()")
    public void before(JoinPoint jp) {
        System.out.println("Before: " + jp);
        System.out.println("Location: " + jp.getSourceLocation());
        System.out.println("This: " + jp.getThis());
        System.out.println("Target: " + jp.getTarget());
        System.out.println("Input: " + Arrays.asList(jp.getArgs()));
    }

    @After("anyMethodCall()")
    public void after(JoinPoint jp) {
        System.out.println("After: " + jp);
    }

    @AfterReturning(pointcut = "anyMethodCall()", returning = "result")
    public void afterReturning(JoinPoint jp, Object result) {
        System.out.println("After returning: " + jp + " " + result);
    }

    @AfterThrowing(pointcut = "anyMethodCall()", throwing = "e")
    public void afterThrowing(Exception e) {
    }

}
