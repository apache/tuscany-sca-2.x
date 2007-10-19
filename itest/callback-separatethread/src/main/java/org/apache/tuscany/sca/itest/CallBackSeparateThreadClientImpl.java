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

import junit.framework.Assert;

import org.osoa.sca.annotations.Reference;
import org.osoa.sca.annotations.Service;

/**
 * This is the client implementation for the call backs in a separate thread tests 
 */
@Service(CallBackSeparateThreadClient.class)
public class CallBackSeparateThreadClientImpl implements CallBackSeparateThreadClient, EventProcessorCallBack {
    /**
     * Used to sleep for 60 seconds.
     */
    private static final int SIXTY_SECONDS = 60 * 1000;

    /**
     * Counts the number of one second call backs
     */
    private static final AtomicInteger oneSecondCallbackCount = new AtomicInteger();

    /**
     * Counts the number of five second call backs
     */
    private static final AtomicInteger fiveSecondCallbackCount = new AtomicInteger();

    /**
     * This is our injected reference to the EventProcessorService
     */
    @Reference
    protected EventProcessorService aCallBackService;

    /**
     * This tests call back patterns using separate threads.
     */
    public void runTests() {
        // Register for 1 second call back
        registerFor1SecondCallback();
        
        // Wait for a few 1 second call backs
        System.out.println("Waiting for some 1 second calls");
        waitForSome1SecondCallbacks();

        // Register for 5 second call back
        registerFor5SecondCallback();
        
        // Wait for a few 1 second call backs
        System.out.println("Waiting for some 1 second calls");
        waitForSome1SecondCallbacks();
        
        // Wait for a few 5 second call backs
        System.out.println("Waiting for some 5 second calls");
        waitForSome5SecondCallbacks();
        
        System.out.println("Done");
    }

    /**
     * Waits for some one second call backs to be fired
     */
    private void waitForSome1SecondCallbacks() {
        // Reset the one second call back count
        oneSecondCallbackCount.set(0);
        
        // Wait until we have 10 1 second call backs or 60 seconds has passed
        final long start = System.currentTimeMillis();
        do {
            if (oneSecondCallbackCount.get() >= 10) {
                System.out.println("Received enough 1 second notifications");
                return;
            }
            
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                Assert.fail("Unexpeceted exception " + e);
            }
        }
        while (System.currentTimeMillis() - start < SIXTY_SECONDS);
        
        // If we get to here then we did not receive enough events
        Assert.fail("Did not receive enough 1 second events");
    }

    /**
     * Waits for some five second call backs to be fired
     */
    private void waitForSome5SecondCallbacks() {
        // Reset the five second call back count
        fiveSecondCallbackCount.set(0);
        
        // Wait until we have 4 5 second call backs or 60 seconds has passed
        final long start = System.currentTimeMillis();
        do
        {
            if (fiveSecondCallbackCount.get() >= 4) {
                System.out.println("Received enough 5 second notifications");
                return;
            }
            
            try
            {
                Thread.sleep(500);
            }
            catch (InterruptedException e)
            {
                Assert.fail("Unexpeceted exception " + e);
            }
        }
        while (System.currentTimeMillis() - start < SIXTY_SECONDS);
        
        // If we get to here then we did not receive enough events
        Assert.fail("Did not receive enough 5 second events");
    }
    
    /**
     * Register to receive one second call backs
     */
    private void registerFor1SecondCallback() {
        aCallBackService.registerForEvent("ONE");
        return;
    }

    /**
     * Register to receive five second call backs
     */
    private void registerFor5SecondCallback() {
        aCallBackService.registerForEvent("FIVE");
    }

    /**
     * Method that is called when an Event is delivered.
     * 
     * @param aEventName The name of the Event
     * @param aEventData The Event data
     */
    public void eventNotification(String aEventName, Object aEventData) {
        // System.out.println("Received Event : " + aEventName + " " + aEventData);

        if (aEventName.equals("ONE")) {
            final int newValue = oneSecondCallbackCount.incrementAndGet();
            //System.out.println("Received total of " + newValue + " 1 second call backs");
        } else if (aEventName.equals("FIVE")) {
            final int newValue = fiveSecondCallbackCount.incrementAndGet();
            //System.out.println("Received total of " + newValue + " 5 second call backs");
        }
        else
            System.out.println("Unknown event type of " + aEventName);
    }
}
