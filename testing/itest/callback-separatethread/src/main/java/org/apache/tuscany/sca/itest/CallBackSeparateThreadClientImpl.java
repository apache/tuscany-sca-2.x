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

package org.apache.tuscany.sca.itest;

import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Assert;
import org.oasisopen.sca.annotation.Reference;
import org.oasisopen.sca.annotation.Service;

/**
 * This is the client implementation for the call backs in a separate thread tests 
 */
@Service(CallBackSeparateThreadClient.class)
public class CallBackSeparateThreadClientImpl implements CallBackSeparateThreadClient, EventProcessorCallBack {
    /**
     * Max time to wait to receive events. If not all the events are received then timeout.
     */
    private static final int TIMEOUT = 30 * 1000;

    /**
     * Counts the number of fast call backs.
     */
    private static final AtomicInteger FAST_CALLBACK_COUNT = new AtomicInteger();

    /**
     * Counts the number of slow call backs.
     */
    private static final AtomicInteger SLOW_CALLBACK_COUNT = new AtomicInteger();

    /**
     * This is our injected reference to the EventProcessorService
     */
    @Reference
    protected EventProcessorService aCallBackService;

    /**
     * This tests call back patterns using separate threads.
     */
    public void runTests() {
    	try {
	        // Register for fast call back
	        registerForFastCallback();
	
	        // Wait for a few fast call backs
	        System.out.println("Waiting for some fast call backs");
	        waitForSomeFastCallbacks();
	
	        try {
		        // Register for slow call back
		        registerForSlowCallback();
		
		        // Wait for a few fast call backs
		        System.out.println("Waiting for some fast calls");
		        waitForSomeFastCallbacks();
		
		        // Wait for a few slow call backs
		        System.out.println("Waiting for some slow calls");
		        waitForSomeSlowCallbacks();
	        } finally {
	        	unregisterForSlowCallback();
	        }
	
	        System.out.println("Done");
    	} finally {
    		unregisterForFastCallback();
    	}
    }

    /**
     * Waits for some fast call backs to be fired
     */
    private void waitForSomeFastCallbacks() {
        // Reset the fast call back count
        FAST_CALLBACK_COUNT.set(0);

        // Wait until we have 10 fast call backs or timeout occurs
        final long start = System.currentTimeMillis();
        do {
            if (FAST_CALLBACK_COUNT.get() >= 10) {
                System.out.println("Received enough fast notifications");
                return;
            }

            try {
                Thread.sleep(5);
            } catch (InterruptedException e) {
                Assert.fail("Unexpeceted exception " + e);
            }
        } while (System.currentTimeMillis() - start < TIMEOUT);

        // If we get to here then we did not receive enough events
        Assert.fail("Did not receive enough fast events");
    }

    /**
     * Waits for some slow call backs to be fired
     */
    private void waitForSomeSlowCallbacks() {
        // Reset the slow call back count
        SLOW_CALLBACK_COUNT.set(0);

        // Wait until we have 4 slow call backs or timeout
        final long start = System.currentTimeMillis();
        do {
            if (SLOW_CALLBACK_COUNT.get() >= 4) {
                System.out.println("Received enough slow notifications");
                return;
            }

            try {
                Thread.sleep(5);
            } catch (InterruptedException e) {
                Assert.fail("Unexpeceted exception " + e);
            }
        } while (System.currentTimeMillis() - start < TIMEOUT);

        // If we get to here then we did not receive enough events
        Assert.fail("Did not receive enough slow events");
    }

    /**
     * Register to receive fast call backs
     */
    private void registerForFastCallback() {
        aCallBackService.registerForEvent("FAST");
    }

    /**
     * Register to receive slow call backs
     */
    private void registerForSlowCallback() {
        aCallBackService.registerForEvent("SLOW");
    }

    /**
     * Unregister to receive fast call backs
     */
    private void unregisterForFastCallback() {
        aCallBackService.unregisterForEvent("FAST");
    }

    /**
     * Unregister to receive slow call backs
     */
    private void unregisterForSlowCallback() {
        aCallBackService.unregisterForEvent("SLOW");
    }

    /**
     * Method that is called when an Event is delivered.
     * 
     * @param aEventName The name of the Event
     * @param aEventData The Event data
     */
    public void eventNotification(String aEventName, Object aEventData) {
        // System.out.println("Received Event : " + aEventName + " " + aEventData);

        if (aEventName.equals("FAST")) {
            final int newValue = FAST_CALLBACK_COUNT.incrementAndGet();
            //System.out.println("Received total of " + newValue + " fast call backs");
        } else if (aEventName.equals("SLOW")) {
            final int newValue = SLOW_CALLBACK_COUNT.incrementAndGet();
            //System.out.println("Received total of " + newValue + " slow call backs");
        } else {
            System.out.println("Unknown event type of " + aEventName);
        }
    }
}
