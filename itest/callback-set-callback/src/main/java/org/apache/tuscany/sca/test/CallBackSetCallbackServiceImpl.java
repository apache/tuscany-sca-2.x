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

import java.io.File;

import org.osoa.sca.ComponentContext;
import org.osoa.sca.RequestContext;
import org.osoa.sca.ServiceReference;
import org.osoa.sca.annotations.Callback;
import org.osoa.sca.annotations.Context;
import org.osoa.sca.annotations.Service;

@Service(CallBackSetCalbackService.class)
public class CallBackSetCallbackServiceImpl implements CallBackSetCalbackService {

    @Callback
    protected CallBackSetCallbackCallback callback;
    @Context
    protected ComponentContext context;

    public void knockKnock(String aString) {

        try {
            System.out.println("CallBackBasicServiceImpl message received: " + aString);
            callback.callBackMessage("Who's There");
            System.out.println("CallBackBasicServiceImpl response sent");
            return;
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    public boolean setCallbackIllegally(String aString) {

        System.out.println("CallBackBasicServiceImpl.setCallbackIllegally() message received: " + aString);

        boolean exceptionProduced = false;
        RequestContext requestContext = null;
        ServiceReference serviceRef = null;

        try {
            requestContext = context.getRequestContext();
            serviceRef = (ServiceReference) requestContext.getServiceReference();
            serviceRef.setCallback(serviceRef);
        } catch (ClassCastException goodEx) {
            exceptionProduced = true;
            System.out.println("Test10 appropriate exception caught during setCallback to own service reference");
        } catch (Exception badEx) {
            System.out.println("CallBackBasicServiceImpl.setCallbackIllegally()  " + badEx.toString());
            badEx.printStackTrace();
        }

        // Return a flag indicating whether we got the exception we are looking for
        return exceptionProduced;

    }
}
