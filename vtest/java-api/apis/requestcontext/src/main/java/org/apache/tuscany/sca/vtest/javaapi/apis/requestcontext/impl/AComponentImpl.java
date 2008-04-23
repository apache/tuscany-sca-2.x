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

package org.apache.tuscany.sca.vtest.javaapi.apis.requestcontext.impl;

import javax.security.auth.Subject;

import org.apache.tuscany.sca.vtest.javaapi.apis.requestcontext.AComponent;
import org.apache.tuscany.sca.vtest.javaapi.apis.requestcontext.BCallback;
import org.apache.tuscany.sca.vtest.javaapi.apis.requestcontext.BComponent;
import org.osoa.sca.CallableReference;
import org.osoa.sca.ComponentContext;
import org.osoa.sca.ServiceReference;
import org.osoa.sca.annotations.Context;
import org.osoa.sca.annotations.Reference;
import org.osoa.sca.annotations.Service;

@Service(AComponent.class)
public class AComponentImpl implements AComponent, BCallback {

    private static Object monitor = new Object();
    private static String returnMessage = null;
    private static CallableReference<BCallback> cbCR;

    protected ComponentContext componentContext;

    @Reference
    protected BComponent bReference;

    public String getName() {
        return "ComponentA";
    }

    @Context
    public void setComponentContext(ComponentContext context) {
        this.componentContext = context;
    }

    public boolean isJAASSubject() {
        return componentContext.getRequestContext().getSecuritySubject() instanceof Subject;
    }

    public String getServiceName() {
        return componentContext.getRequestContext().getServiceName();
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
        cbCR = componentContext.getRequestContext().getServiceReference();
    }

    public String getServiceReferenceName() {
        CallableReference<AComponent> aCR = componentContext.getRequestContext().getServiceReference();
        return aCR.getService().getName();
    }

    public String getCallbackServiceReferenceName() {
        return cbCR.getService().getCallbackName();
    }

    public String getCallbackName() {
        return "CallBackB";
    }

}
