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

import org.osoa.sca.ComponentContext;
import org.osoa.sca.RequestContext;
import org.osoa.sca.ServiceReference;
import org.osoa.sca.annotations.Context;
import org.osoa.sca.annotations.Reference;
import org.osoa.sca.annotations.Service;

@Service(CallBackIdClient.class)
public class CallBackIdClientImpl implements CallBackIdClient, CallBackIdCallBack {

    @Context
    protected ComponentContext componentContext;
    @Reference
    protected CallBackIdService aCallBackService;

    private static String returnMessage = null;
    private static Object monitor = new Object();
    private static Object callBackId;

    public void run() {

        // This tests the use of the set/get callbackId API both SCA generated
        // and client specified.

        // Test1 uses a SCA generated callback ID and compare that with the
        // callbackID returned during callback.
        test11a();

        // Test2 uses a Client specified callback ID and compare that with the
        // callbackID returned during callback.
        test11b();

        return;
    }

    private void test11a() {

        // Retrieve this services callback ID and save it. Once the callback is
        // received the callback ID will be compared with the one
        // returned. Equal is good.

        Object origCallBackId = ((ServiceReference)aCallBackService).getCallbackID();
        aCallBackService.knockKnock("Knock Knock - Test1");
        int count = 0;

        // 
        // If we cannot get a response in 30 seconds consider this a failure
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

        Assert.assertEquals("CallBackIdITest - test11a - SCA Generated Id", origCallBackId, this.getCallBackId());

    }

    private void test11b() {

        // Set the services callback ID and save it. Once the callback is
        // received the callback ID will be compared with the one
        // returned. Equal is good.

        String origCallBackId = "CallBackId1";
        ((ServiceReference)aCallBackService).setCallbackID(origCallBackId);

        aCallBackService.knockKnock("Knock Knock - Test2");
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

        Assert
            .assertEquals("CallBackIdITest - 11b - Client Specified Id", origCallBackId, (String)this.getCallBackId());

    }

    public String getReturnMessage() {
        return returnMessage;
    }

    public void setReturnMessage(String aReturnMessage) {
        returnMessage = aReturnMessage;
    }

    public void callBackMessage(String aString) {

        System.out.println("Entering callback callBackMessage: " + aString);
        RequestContext rc = componentContext.getRequestContext();
        Object callBackId = rc.getServiceReference().getCallbackID();

        synchronized (monitor) {
            this.setReturnMessage(aString);
            this.setCallBackId(callBackId);
            monitor.notify();
        }
    }

    protected Object getCallBackId() {
        return callBackId;
    }

    protected void setCallBackId(Object aCallBackId) {
        callBackId = aCallBackId;
    }

}
