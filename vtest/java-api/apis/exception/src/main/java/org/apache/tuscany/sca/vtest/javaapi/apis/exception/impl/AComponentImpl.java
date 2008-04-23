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

package org.apache.tuscany.sca.vtest.javaapi.apis.exception.impl;

import org.apache.tuscany.sca.vtest.javaapi.apis.exception.AComponent;
import org.apache.tuscany.sca.vtest.javaapi.apis.exception.BComponent;
import org.apache.tuscany.sca.vtest.javaapi.apis.exception.CComponent;
import org.apache.tuscany.sca.vtest.javaapi.apis.exception.DComponent;
import org.apache.tuscany.sca.vtest.javaapi.apis.exception.DException;
import org.junit.Assert;
import org.osoa.sca.CallableReference;
import org.osoa.sca.ComponentContext;
import org.osoa.sca.ConversationEndedException;
import org.osoa.sca.NoRegisteredCallbackException;
import org.osoa.sca.ServiceReference;
import org.osoa.sca.ServiceRuntimeException;
import org.osoa.sca.annotations.Context;
import org.osoa.sca.annotations.ConversationID;
import org.osoa.sca.annotations.Destroy;
import org.osoa.sca.annotations.Reference;
import org.osoa.sca.annotations.Scope;
import org.osoa.sca.annotations.Service;

@Service(AComponent.class)
@Scope("CONVERSATION")
public class AComponentImpl implements AComponent {

    protected ComponentContext componentContext;
    
    @Reference
    protected CallableReference<BComponent> bReference;

    @Reference
    protected ServiceReference<CComponent> cReference;

    @Reference
    protected ServiceReference<DComponent> dReference;

    @ConversationID
    protected String cid;

    public String getName() {
        return "ComponentA";
    }

    @Context
    public void setComponentContext(ComponentContext context) {
        this.componentContext = context;
    }

    public void testConversation() {
        ServiceReference<CComponent> cSR = componentContext.getServiceReference(CComponent.class, "cReference");
        cSR.setConversationID("AConversationID");
        Assert.assertEquals("ComponentC", cSR.getService().getName());

        try {
            Thread.sleep(1100);
            cSR.getService().testConversation();

            Assert.fail();
        } catch (InterruptedException ie) {
            ie.printStackTrace();
        } catch (ConversationEndedException cee) {
            // Expected
        }
    }

    public void testCallBack() {
        ServiceReference<BComponent> bSR = componentContext.getServiceReference(BComponent.class, "bReference");
        bSR.setCallbackID("ComponentACallBack");

        try {
            bSR.getService().testCallback();
            Assert.fail();
        } catch (NoRegisteredCallbackException e) {
            // Expected
        }
    }

    public boolean testServiceRuntimeException() {
        try {
            componentContext.getServiceReference(BComponent.class, "dummyBReference").getService().getName();
        } catch (ServiceRuntimeException sre) {
            return true;
        }

        return false;
    }

    public boolean testServiceUnavailableException() {
        return false;
    }

    public boolean testCheckedException() {
        try {
            dReference.getService().testException();
        } catch (DException e) {
            if (e.getMessage().equals("ADException")) {
                return true;
            }
        }

        return false;
    }

    @Destroy
    public void destroy() {
        System.out.println("This is a Destroy of AComponent ..");
    }

}
