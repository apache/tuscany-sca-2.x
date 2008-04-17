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
package org.apache.tuscany.sca.itest.oneway.impl;

import org.apache.tuscany.sca.itest.oneway.OneWayService;

import edu.emory.mathcs.backport.java.util.concurrent.atomic.AtomicInteger;

/**
 * The service for the oneway itest.
 *
 * @version $Rev: 537240 $ $Date: 2007-05-11 18:35:03 +0100 (Fri, 11 May 2007) $
 */


public class OneWayServiceImpl implements OneWayService {

    /**
     * This is the error message that will be in the Exception thrown by
     * the doSomethingWhichThrowsException() method.
     */
    public static final String EXCEPTION_MESSAGE = "Sample RuntimeException from a @OneWay method";

    /**
     * Counts the number of invocations to doSomething().
     */
    public static final AtomicInteger CALL_COUNT = new AtomicInteger();

    /**
     * Counts the number of invocations of the doSomethingWhichThrowsException() method.
     */
    public static final AtomicInteger CALL_COUNT_FOR_THROWS_EXCEPTION_METHOD = new AtomicInteger();

    /**
     * {@inheritDoc}
     */
    public void doSomething(int count) {
        CALL_COUNT.incrementAndGet();

       // System.out.println("Service: doSomething " + count + " callCount = " + callCount);
       // System.out.flush();
    }

    /**
     * {@inheritDoc}
     */
    public void doSomethingWhichThrowsException() {
        System.out.println("OneWay invoked. About to throw an Exception");
        CALL_COUNT_FOR_THROWS_EXCEPTION_METHOD.incrementAndGet();
        throw new NullPointerException(EXCEPTION_MESSAGE);
    }
}
