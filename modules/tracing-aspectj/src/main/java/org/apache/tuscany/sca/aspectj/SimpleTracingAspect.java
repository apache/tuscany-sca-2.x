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
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

/**
 * @version $Rev$ $Date$
 */
@Aspect
public class SimpleTracingAspect extends TracingAspect {

    public SimpleTracingAspect() {
        super();
    }

    @Override
    protected void completeLog() {
        System.out.println("completeLog()");
    }

    @Pointcut("execution(public * org.apache.tuscany.sca..*.*(..)) &&!within(java..*)")
    protected void entry() {
    }

    @Pointcut("within(org.apache.tuscany.sca..*) && !within(org.apache.tuscany.sca.aspectj.*Aspect)")
    protected void withinScope() {
    }

    @Override
    protected void logEnter(JoinPoint jp) {
        System.out.println("> " + jp.getSignature());
        if (jp.getArgs().length != 0) {
            System.out.println("Input: " + Arrays.asList(jp.getArgs()));
        }
    }

    @Override
    protected void logExit(JoinPoint jp) {
        // System.out.println("> " + jp.getSignature());
    }

    @Override
    protected void logException(JoinPoint jp, Throwable throwable) {
        System.out.println("! " + jp.getSignature() + " " + throwable.getMessage());
    }

    @Override
    protected void logExit(JoinPoint jp, Object result) {
        System.out.println("< " + jp.getSignature());
        if (!jp.getSignature().toString().startsWith("void ")) {
            System.out.println("Output: " + result);
        }
    }

    @Override
    protected void startLog() {
        System.out.println("startLog()");
    }

}
