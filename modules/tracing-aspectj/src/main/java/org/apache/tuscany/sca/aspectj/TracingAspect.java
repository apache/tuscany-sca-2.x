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

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;

/**
 * @version $Rev$ $Date$
 */

@Aspect
public abstract class TracingAspect {

    @Pointcut("")
    protected abstract void entry();

    /** ignore join points outside this scope - use within(..) */
    @Pointcut("")
    protected abstract void withinScope();

    @Pointcut("call(* java..*.*(..))")
    protected void exit() {
    }

    @Pointcut("entry() && !cflowbelow(entry())")
    void start() {
    }

    // @Pointcut("withinScope() && cflow(entry()) && !cflow(exit()) && !within(org.apache.tuscany.sca.aspectj.*Aspect)")
    @Pointcut("withinScope() && entry() && !within(org.apache.tuscany.sca.aspectj.*Aspect)")
    void trace() {
    }

    @Pointcut("!handler(*) && !preinitialization(new(..))")
    protected void supportsAfterAdvice() {
    }

    @Before("start()")
    public void beforeStart() {
        startLog();
    }

    @Before("trace() && supportsAfterAdvice()")
    public void beforeTrace(JoinPoint jp) {
        logEnter(jp);
    }

    @After("trace() && supportsAfterAdvice()")
    public void afterTrace(JoinPoint jp) {
        logExit(jp);
    }
    
    @AfterReturning(pointcut = "trace() && supportsAfterAdvice()", returning = "result")
    public void afterReturning(JoinPoint jp, Object result) {
        logExit(jp, result);
    }

    @AfterThrowing(pointcut = "trace() && supportsAfterAdvice()", throwing = "e")
    public void afterThrowing(JoinPoint jp, Throwable e) {
        logException(jp, e);
    }

    @After("start()")
    public void afterStart() {
        completeLog();
    }

    protected abstract void logEnter(JoinPoint jp);

    protected abstract void logExit(JoinPoint jp);
    protected abstract void logExit(JoinPoint jp, Object result);
    protected abstract void logException(JoinPoint jp, Throwable throwable);

    protected abstract void startLog();

    protected abstract void completeLog();

}
