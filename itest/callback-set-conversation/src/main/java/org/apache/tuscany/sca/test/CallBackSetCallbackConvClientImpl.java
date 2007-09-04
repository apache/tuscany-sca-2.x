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
import org.osoa.sca.NoRegisteredCallbackException;
import org.osoa.sca.ServiceReference;
import org.osoa.sca.annotations.Context;
import org.osoa.sca.annotations.Reference;
import org.osoa.sca.annotations.Scope;
import org.osoa.sca.annotations.Service;

@Service(CallBackSetCallbackConvClient.class)
@Scope("CONVERSATION")
public class CallBackSetCallbackConvClientImpl implements CallBackSetCallbackConvClient,
        //FIXME: remove the following hack, needed to get around current JavaImplementationInvoker limitation
        CallBackSetCallbackConvCallback {

    @Context
    protected ComponentContext componentContext;
    @Reference
    protected CallBackSetCallbackConvService aCallBackService;
    private CallBackSetCallbackConvObjectCallback aCallbackObject = null;

    public void run() {

        // This tests aspects of the setCallback() API within a conversational
        // scope.

        /*
         * test7 The client calls setCallback() with an object that is not a
         * service reference and the callback interface is stateful. Verify
         * successful execution.
         */
        test7();

        /*
         * test8() The client calls setCallback() with an object that does not
         * implement the callback interface. Verify an appropriate exception is
         * thrown. This requires a STATEFUL interface.
         */
        test8();

        /*
         * test9 The client calls setCallback() with an object that is not
         * serializable. Verify an appropriate exception is thrown. This
         * requires a STATEFUL callback interface. Move from the stateless test
         * case.
         */
        test9();

        return;
    }

    private void test7() {

        //
        // This test is to specify an Object that is not a service reference
        // that does implement
        // the callback interface and is Serializeable. Verify successful
        // execution.
        //	

        aCallbackObject = new CallBackSetCallbackConvObjectCallback();
        aCallbackObject.incrementCallBackCount();

        ServiceReference<CallBackSetCallbackConvService> aCallBackServiceRef
                = componentContext.cast(aCallBackService);
        aCallBackServiceRef.setCallback(aCallbackObject);
        //FIXME: remove the following workaround for runtime bug
        aCallBackServiceRef.getService().knockKnock("Knock Knock");
        //aCallBackService.knockKnock("Knock Knock");

        Assert.assertEquals("CallBackSetCallbackConv - Test7", 2, aCallbackObject.getCount());

    }

    private void test8() {

        boolean correctException = false;

        //
        // This test is to specify an Object that is not a service reference
        // that does not implement
        // the callback interface. The expected result is an appropriate
        // exception.
        //

        try {
            ServiceReference<CallBackSetCallbackConvService> aCallBackServiceRef
                    = componentContext.cast(aCallBackService);
            aCallBackServiceRef.setCallback(new CallBackSetCallbackConvBadCallback());
            //FIXME: remove the following workaround for runtime bug
            aCallBackServiceRef.getService().knockKnock("Knock Knock");
            //aCallBackService.knockKnock("Knock Knock");
        }

        //
        // This should catch an appropriate exception.
        // 

        catch (IllegalArgumentException goodEx)
        {
            System.out.println("correct exception " + goodEx);
            correctException = true;
        }

        catch (Exception ex) {
            // This means an inappropriate exception occurred
            ex.printStackTrace();
        }

        Assert.assertEquals("CallBackSetCallbackConv - Test8", true, correctException);

    }

    private void test9() {

        boolean correctException = false;

        //
        // This test is to specify an Object that is not a service reference
        // that does implement
        // the callback interface but does not implement Serializeable. Verify
        // an appropriate exception
        // is thrown.
        //

        try {
            ServiceReference<CallBackSetCallbackConvService> aCallBackServiceRef
                    = componentContext.cast(aCallBackService);
            aCallBackServiceRef.setCallback(new CallBackSetCallbackConvNonSerCallback());
            //FIXME: remove the following workaround for runtime bug
            aCallBackServiceRef.getService().knockKnock("Knock Knock");
            //aCallBackService.knockKnock("Knock Knock");
        }
        //
        // This should catch an appropriate exception.
        //
        catch (IllegalArgumentException goodEx)
        {
            System.out.println("correct exception " + goodEx);
            correctException = true;
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        Assert.assertEquals("CallBackSetCallbackConv - Test9", true, correctException);

    }

    //FIXME: remove the following methods, needed to get around current JavaImplementationInvoker limitation

    public void callBackMessage(String aString) {
        throw new IllegalStateException("CallbackSetCallbackConvClientImpl.callbackMessage called");
    }

    public void callBackIncrement(String aString) {
        throw new IllegalStateException("CallbackSetCallbackConvClientImpl.callbackIncrement called");
    }

}
