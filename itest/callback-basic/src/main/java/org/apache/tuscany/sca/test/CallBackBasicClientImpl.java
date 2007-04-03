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

import org.osoa.sca.annotations.Reference;
import org.osoa.sca.annotations.Service;
import junit.framework.Assert;

@Service(CallBackBasicClient.class)
public class CallBackBasicClientImpl implements CallBackBasicClient, CallBackBasicCallBack {

    @Reference
    protected CallBackBasicService aCallBackService;
    private static String returnMessage = null;
    private static int callBackCount = 0;
    private static Object monitor = new Object();

    public void run() {

        // This tests basic callback patterns.

        // Test1 is the basic callback where the target calls back prior to
        // returning to the client.
        test1a();

        // Test2 is where the target does not call back to the client.
        test1b();

        // Test3 is where the target calls back multiple times to the client.
        test1c();

        return;
    }

    private void test1a() {
        aCallBackService.knockKnock("Knock Knock");
        int count = 0;

        // 
        // If we cant get a response in 30 seconds consider this a failure
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

        Assert.assertEquals("CallBackBasicITest - test1a", "Who's There", this.getReturnMessage());

    }

    private void test1b() {
        aCallBackService.noCallBack("No Reply Desired");
        Assert.assertEquals("CallBackBasicITest - test1b", 1, 1);

        return;
    }

    private void test1c() {
        aCallBackService.multiCallBack("Call me back 3 times");
        int count = 0;

        // 
        // If we cant get a response in 30 seconds consider this a failure
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

        Assert.assertEquals("CallBackBasicITest - test1c", 3, this.getCallBackCount());
        return;
    }

    public String getReturnMessage() {
        return returnMessage;
    }

    public void setReturnMessage(String aReturnMessage) {
        returnMessage = aReturnMessage;
    }

    public int getCallBackCount() {
        return callBackCount;
    }

    public void incrementCallBackCount() {
        callBackCount++;
    }

    public void callBackMessage(String aString) {
        System.out.println("Entering callback callBackMessage: " + aString);
        synchronized (monitor) {
            this.setReturnMessage(aString);
            monitor.notify();
        }
    }

    public void callBackIncrement(String aString) {
        System.out.println("Entering callback increment: " + aString);
        synchronized (monitor) {
            this.incrementCallBackCount();
            monitor.notify();
        }
    }

}
