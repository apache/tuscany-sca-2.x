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
package callback.client;

import java.util.Hashtable;


import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

import callback.service.CallbackService;

/**
 * OSGi Callback test client implementation
 */
public class OSGiCallbackClientImpl implements 
        CallbackClient, CallbackCallback, BundleActivator {

    protected CallbackService callbackService;
    
    private static String returnMessage = null;
    private static int callbackCount = 0;
    private static Object monitor = new Object();
    
    private BundleContext bundleContext;

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
        callbackService.knockKnock("Knock Knock");
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

        if (!"Who's There".equals(this.getReturnMessage())) {
           throw new RuntimeException("CallbackITest - test1a");
        }

    }

    private void test1b() {
        callbackService.noCallback("No Reply Desired");

        return;
    }

    private void test1c() {
        callbackService.multiCallback("Call me back 3 times");
        int count = 0;

        // 
        // If we can't get a response in 30 seconds consider this a failure
        // 

        synchronized (monitor) {
            while (this.getCallbackCount() < 3 && count++ < 30) {
                try {
                    monitor.wait(1000L);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        if (this.getCallbackCount() != 3)
            throw new RuntimeException("CallbackITest - test1c");
        return;
    }

    public String getReturnMessage() {
        return returnMessage;
    }

    public void setReturnMessage(String aReturnMessage) {
        returnMessage = aReturnMessage;
    }

    public int getCallbackCount() {
        return callbackCount;
    }

    public void incrementCallbackCount() {
        callbackCount++;
    }

    public void callbackMessage(String aString) {
        System.out.println("Entering callback callbackMessage: " + aString);
        synchronized (monitor) {
            this.setReturnMessage(aString);
            monitor.notify();
        }
    }

    public void callbackIncrement(String aString) {
        System.out.println("Entering callback increment: " + aString);
        synchronized (monitor) {
            this.incrementCallbackCount();
            monitor.notify();
        }
    }

    
    public void start(BundleContext bc) throws Exception {
        
        System.out.println("Started OSGiCallbackClientImpl bundle ");
        
        this.bundleContext = bc;
        
        Hashtable<String, Object> serviceProps = new Hashtable<String, Object>();
        serviceProps.put("component.service.name", "CallbackClient/CallbackClient");

        serviceProps.put("component.name", "CallbackClient");        
        bundleContext.registerService("callback.client.CallbackClient", this, serviceProps);
        
        Hashtable<String, Object> callbackProps = new Hashtable<String, Object>();
        callbackProps.put("component.service.name", "CallbackClient/callbackService");
        callbackProps.put("component.name", "CallbackClient");
        
        bundleContext.registerService("callback.client.CallbackCallback", this, callbackProps);
        
        ServiceReference ref= bundleContext.getServiceReference("callback.service.CallbackService");
        
        if (ref != null)
            callbackService = (callback.service.CallbackService)bundleContext.getService(ref);
        
        
        
      
    }
    
    public void stop(BundleContext bc)  {
    }
    
  
}
