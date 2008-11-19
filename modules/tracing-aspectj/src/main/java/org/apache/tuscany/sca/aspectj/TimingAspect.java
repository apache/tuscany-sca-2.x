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
 * The TimingAspect is used to perform timing metrics on various calls.
 * The Pointcut "timedCall" is not defined here, but rather in the aop.xml
 * configuration file. You may provide a point cut to state which type
 * of call you would like timed and reported in the output files.
 * 
 * @version $Rev$ $Date$
 */
@Aspect
public abstract class TimingAspect {
	// Abstract pointcut. Pointcut is defined in aop.xml file.
    @Pointcut
    public void timedCall() {
    }    
    	    
    @Around("timedCall()")
    public Object timedSection(ProceedingJoinPoint jp) throws Throwable {
        System.out.println("Timing Around timedSection jp=" + jp);
        long start = System.currentTimeMillis();
        try {
            return jp.proceed();
        } finally {
            long end = System.currentTimeMillis();
            System.out.println("Timing Around timedSection Roundtrip is " + (end - start) + "ms for jp.getSignature=" + jp.getSignature());
        }
    }
}
