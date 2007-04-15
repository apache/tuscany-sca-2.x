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
    private CallBackSetCallbackCallback callback;
    @Context
    private ComponentContext context;

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

    public void setCallbackIllegally(String aString) {

        System.out.println("CallBackBasicServiceImpl.setCallbackIllegally() message received: " + aString);

        boolean exceptionProduced = false;
        RequestContext requestContext = null;
        ServiceReference serviceRef = null;

        // Context is not working properly so we can't trust that this is
        // working.....
        try {
            requestContext = context.getRequestContext();
            serviceRef = (ServiceReference) requestContext.getServiceReference();
        } catch (Exception ex) {
            System.out.println("CallBackBasicServiceImpl.setCallbackIllegally()  " + ex.toString());
            ex.printStackTrace();
            return;
        }

        // Ok, call setCallback with my own service reference.
        try {
            serviceRef.setCallback(serviceRef);
        } catch (NullPointerException npe) // This needs to be removed once
                                            // appropriate exception is
                                            // identified.
        {
            // This is not an appropriate exception.
            System.out.println("Test10 NPE exception during setCallback to own service reference");
            npe.printStackTrace();
            return;
        }
        // This needs to catch the appropriate exception, once we figure out
        // what is needs to be!
        catch (Exception ex) {
            exceptionProduced = true;
            System.out.println("Test10 appropriate exception caught during setCallback to own service reference");
        }
        ;

        // If we get the exception we are looking for then create the marker
        // file.
        if (exceptionProduced == true) {
            File aFile = new File("target/test10_marker");
            try {
                aFile.createNewFile();
            } catch (Exception ex) {
                System.out.println("Error Creating target/test10_marker marker file");
                ex.printStackTrace();
            }
        }

    }
}
