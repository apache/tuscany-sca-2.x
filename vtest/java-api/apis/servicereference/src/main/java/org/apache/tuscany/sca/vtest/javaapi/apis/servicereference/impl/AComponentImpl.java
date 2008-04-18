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

package org.apache.tuscany.sca.vtest.javaapi.apis.servicereference.impl;

import org.apache.tuscany.sca.vtest.javaapi.apis.servicereference.AComponent;
import org.apache.tuscany.sca.vtest.javaapi.apis.servicereference.BCallback;
import org.apache.tuscany.sca.vtest.javaapi.apis.servicereference.BComponent;
import org.apache.tuscany.sca.vtest.javaapi.apis.servicereference.CComponent;
import org.junit.Assert;
import org.osoa.sca.CallableReference;
import org.osoa.sca.ComponentContext;
import org.osoa.sca.ServiceReference;
import org.osoa.sca.annotations.Context;
import org.osoa.sca.annotations.ConversationID;
import org.osoa.sca.annotations.Reference;
import org.osoa.sca.annotations.Scope;
import org.osoa.sca.annotations.Service;

@Service(AComponent.class)
@Scope("CONVERSATION")
public class AComponentImpl implements AComponent, BCallback {

    private static Object monitor = new Object();
    private static String returnMessage = null;

    protected ComponentContext componentContext;
    
    @Reference
    protected CallableReference<BComponent> bReference;

    @Reference
    protected ServiceReference<CComponent> cReference;

    @ConversationID
    protected String cid;

    public String getName() {
        return "ComponentA";
    }

    @Context
    public void setComponentContext(ComponentContext context) {
        this.componentContext = context;
    }

    public void testConversationID() {
        ServiceReference<BComponent> bSR = componentContext.getServiceReference(BComponent.class, "bReference");
        bSR.setConversationID("AConversationID");
        bSR.getService().testConversationID();

        Assert.assertEquals("AConversationID", bSR.getConversation().getConversationID());
    }

    public String getCallbackResult() {
        ServiceReference<BComponent> bSR = componentContext.getServiceReference(BComponent.class, "bReference");
        String cbID = "ComponentACallBack";
        bSR.setCallbackID(cbID);
        bSR.getService().testCallback();

        // Wait for 30s max.
        int count = 0;
        synchronized(monitor) {
            while (returnMessage == null && count++ < 30) {
                try {
                    monitor.wait(1000L);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        return returnMessage;
    }

    public void processResults(String result) {
        returnMessage = result;
    }

    public void redirectCallback() {
        ServiceReference<BComponent> bSR = componentContext.getServiceReference(BComponent.class, "bReference");
        bSR.setCallbackID("RedirectedCallBack");
        bSR.setCallback(cReference);
        bSR.getService().testRedirectedCallback();
    }

    public void testGetCallback() {
        ServiceReference<BComponent> bSR = componentContext.getServiceReference(BComponent.class, "bReference");
        bSR.setCallback(cReference);
        Assert.assertEquals(cReference, bSR.getCallback());

        TestObject aCallback = new TestObject();
        bSR.setCallback(aCallback);
        Assert.assertEquals(aCallback, bSR.getCallback());
        Assert.assertEquals(1, ((TestObject) bSR.getCallback()).getId());

        TestObject bCallback = new TestObject();
        bSR.setCallback(bCallback);
        Assert.assertEquals(bCallback, bSR.getCallback());
        Assert.assertEquals(2, ((TestObject) bSR.getCallback()).getId());
    }

}
