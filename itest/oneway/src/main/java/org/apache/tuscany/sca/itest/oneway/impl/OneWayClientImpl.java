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

import org.apache.tuscany.sca.itest.oneway.OneWayClient;
import org.apache.tuscany.sca.itest.oneway.OneWayService;
import org.junit.Assert;
import org.osoa.sca.annotations.Reference;


/**
 * The client for the oneway itest.
 *
 * @version $Rev: 537240 $ $Date: 2007-05-11 18:35:03 +0100 (Fri, 11 May 2007) $
 */
public class OneWayClientImpl implements OneWayClient {
    /**
     * Injected reference to the OneWayService.
     */
    @Reference 
    protected OneWayService oneWayService;

    /**
     * Tracks the number of calls of the doSomething() method on the OneWayService.
     */
    public static int callCount = 0;

    /**
     * {@inheritDoc}
     */
    public void doSomething(int count) {
        callCount = callCount + count;

        for (int loopCount = 0; loopCount < count; loopCount++) {
            //System.out.println("Client: doSomething " + loopCount);
            //System.out.flush();
            oneWayService.doSomething(loopCount);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void doSomethingWhichThrowsException() {
        Assert.assertNotNull(oneWayService);
        oneWayService.doSomethingWhichThrowsException();
    }
}
