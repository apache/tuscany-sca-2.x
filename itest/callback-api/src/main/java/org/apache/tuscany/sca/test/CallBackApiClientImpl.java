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
package org.apache.tuscany.sca.test;

import junit.framework.Assert;

import org.osoa.sca.annotations.Reference;
import org.osoa.sca.annotations.Service;

@Service(CallBackApiClient.class)
public class CallBackApiClientImpl implements CallBackApiClient, CallBackApiCallBack {

    @Reference
    protected CallBackApiService aCallBackService;

    private static String returnMessage = null;
    private static int callBackCount = 0;
    private static Object monitor = new Object();

    /**
     * This test calls function test 3a,3a1,3b,3c
     */
    public void run() {

        // This tests basic callback patterns.

        // Test3a is the basic callback where the target calls back prior to
        // returning to the client.
        test3a();

        test3a1();

        // Test3b is where the target does not call back to the client.
        test3b();

        // Test3c is where the target calls back multiple times to the client.
        test3c();

        return;
    }

    /**
     * This test is the basic callback where the target calls back prior to returning to the client.
     */

    private void test3a() {
        aCallBackService.knockKnock("Knock Knock");
        int count = 0;

        // 
        // If we can't get a response in 30 seconds consider this a failure
        // 

        synchronized (monitor) {
            while (returnMessage == null && count++ < 30) {
                try {
                    monitor.wait(1000L);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        Assert.assertEquals("CallBackApiITest - test3a", "Who's There", this.getReturnMessage());

    }

    /**
     * This test is the basic callback where the target calls back prior to returning to the client.
     */
    private void test3a1() {
        aCallBackService.knockKnockByRef("Knock Knock");
        int count = 0;

        // 
        // If we can't get a response in 30 seconds consider this a failure
        // 

        synchronized (monitor) {
            while (returnMessage == null && count++ < 30) {
                try {
                    monitor.wait(1000L);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        Assert.assertEquals("CallBackApiITest - test3a", "Who's There", this.getReturnMessage());

    }

    /**
     * This test is the basic callback where the target does not call back to the client
     */
    private void test3b() {
        aCallBackService.noCallBack("No Reply Desired");
        Assert.assertEquals("CallBackApiITest - test3b", 1, 1);
        return;
    }

    /**
     * This test is the basic callback where the target calls back multiple times to the client
     */

    private void test3c() {
        aCallBackService.multiCallBack("Call me back 3 times");
        int count = 0;

        // 
        // If we can't get a response in 30 seconds consider this a failure
        // 

        synchronized (monitor) {
            while (this.getCallBackCount() < 3 && count++ < 30) {
                try {
                    monitor.wait(1000L);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        Assert.assertEquals("CallBackApiITest - test3c", 3, this.getCallBackCount());
        return;
    }

    /**
     * This function returns a string
     * 
     * @param void
     * @return String result string
     */

    public String getReturnMessage() {
        return returnMessage;
    }

    /**
     * This function set the returnMessage string
     * 
     * @param String
     */
    public void setReturnMessage(String aReturnMessage) {
        returnMessage = aReturnMessage;
    }

    /**
     * This function returns the callBackCount variable
     * 
     * @return Integer callBackCount variable
     */
    public int getCallBackCount() {
        return callBackCount;
    }

    /**
     * This function increments callBackCount variable by 1
     */
    public void incrementCallBackCount() {
        callBackCount++;
    }

    /**
     * This function prints a message synched with setReturnMessage function
     * 
     * @param String
     */
    public void callBackMessage(String aString) {
        System.out.println("Entering callback callBackMessage: " + aString);
        synchronized (monitor) {
            this.setReturnMessage(aString);
            monitor.notify();
        }
    }

    /**
     * This function prints a message synched with incrementCallBackCount function
     * 
     * @param String
     */
    public void callBackIncrement(String aString) {
        System.out.println("Entering callback increment: " + aString);
        synchronized (monitor) {
            this.incrementCallBackCount();
            monitor.notify();
        }
    }

}
