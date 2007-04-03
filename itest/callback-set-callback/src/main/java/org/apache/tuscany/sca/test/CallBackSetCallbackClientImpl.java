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

import junit.framework.Assert;

import org.osoa.sca.NoRegisteredCallbackException;
import org.osoa.sca.ServiceReference;
import org.osoa.sca.annotations.Reference;
import org.osoa.sca.annotations.Service;

@Service(CallBackSetCallbackClient.class)
public class CallBackSetCallbackClientImpl implements CallBackSetCallbackClient {

    @Reference
    protected CallBackSetCalbackService aCallBackService;
    @Reference
    protected CallBackSetCallbackCallback callBack;

    public void run() {

        // This test various aspects of the setCallback() API in a stateless
        // scope.

        /*
         * test4 Client does not implement the callback interface but calls
         * setCallback with a service reference before invoking the target,
         * Verify successful executon.
         */

        test4();

        /*
         * test5 The client does not implement the callback interface and does
         * not call setCallback() before invoking the target. Verify a
         * NoRegisteredCallbackException is thrown.
         */

        test5();

        /*
         * test6() The client calls setCallback() with an object that is not a
         * service reference and the callback interface is stateless. Verify
         * that an appropriate exception is thrown. When calling setCallback
         * with an object the interface must be stateful. Stateless interfaces
         * require a service Reference.
         */

        test6();

        /*
         * test10 The target calls setCallback() on its own service reference,
         * e.g. getRequestContext().getServiceReference().getCallback(). Verify
         * an appropriate exeception occurs.
         */

        test10();

        return;
    }

    private void test4() {

        //
        // Since callbacks do not synchronously return and this test results in
        // a callback to a component other
        // than this client I am using a marker file to determine the outcome.
        // The presence of the marker
        // file will be used for the Assertion test. If it exists then the
        // callback occurred and all is good.
        //

        // Make sure the marker file is not present before starting the test.
        File aFile = new File("target/test4_marker");
        if (aFile.exists())
            aFile.delete();

        ((ServiceReference)aCallBackService).setCallback(callBack);

        aCallBackService.knockKnock("Knock Knock");

        // Lets give the callback a little time to complete....

        int count = 0;
        long timeout = 1000;

        while (count++ < 30 && (aFile.exists() == false)) {
            try {
                Thread.sleep(timeout);
            } catch (InterruptedException ie) {
            }
        }

        Assert.assertEquals("CallBackSetCallback - Test4", true, aFile.exists());

    }

    private void test5() {

        boolean correctException = false;

        //
        // The backend service is expecting a callback reference to be set. This
        // test will not
        // set one so an exception is expected. According to the spec if a
        // client calls a method on
        // a service reference prior to calling setCallback() then a
        // NoRegisteredCallbackException
        // will be thrown on the client.
        //

        try {
            aCallBackService.knockKnock("Knock Knock");
        } catch (NoRegisteredCallbackException NotRegEx) {
            correctException = true;
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        Assert.assertEquals("CallBackSetCallback - Test5", true, correctException);

    }

    private void test6() {

        boolean correctException = false;

        //
        // This test is to specify an Object that is not a service reference
        // that does impliment
        // the callback interface. However because this callback service is
        // stateless the expected
        // result is an appropriate exception.
        //

        try {
            ((ServiceReference)aCallBackService).setCallback(new CallBackSetCallbackObjectCallback());
            aCallBackService.knockKnock("Knock Knock");
        }
        //
        // This should catch an appropriate exception.
        //
        catch (NoRegisteredCallbackException NotRegEx) {
            correctException = true;
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        Assert.assertEquals("CallBackSetCallback - Test6", true, correctException);

    }

    private void test10() {
        //
        // Since callbacks do not synchronously return and this test results in
        // a failure on the service
        // side of the fence I am using a marker file to determine the outcome.
        // The presence of the marker
        // file will be used for the Assertion test. If it exists then all is
        // good.
        //

        // Make sure the marker file is not present before starting the test.
        File aFile = new File("target/test10_marker");
        if (aFile.exists())
            aFile.delete();

        aCallBackService.setCallbackIllegally("Try to set callback on your own service reference");

        Assert.assertEquals("CallBackSetCallback - Test10", true, aFile.exists());

        return;
    }

}
