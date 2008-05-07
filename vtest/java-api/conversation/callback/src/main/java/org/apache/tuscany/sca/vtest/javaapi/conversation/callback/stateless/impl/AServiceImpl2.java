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

package org.apache.tuscany.sca.vtest.javaapi.conversation.callback.stateless.impl;

import org.apache.tuscany.sca.vtest.javaapi.conversation.callback.AService;
import org.apache.tuscany.sca.vtest.javaapi.conversation.callback.BService;
import org.apache.tuscany.sca.vtest.javaapi.conversation.callback.stateless.AServiceCallback;
import org.junit.Assert;
import org.osoa.sca.ServiceReference;
import org.osoa.sca.annotations.Reference;
import org.osoa.sca.annotations.Service;

@Service(AService.class)
public class AServiceImpl2 implements AService, AServiceCallback {

    @Reference
    protected ServiceReference<BService> b;

    private String someState;
    
    private String someKey = "1234";

    public void callBack(String someState) {
        System.out.println("A callback called with this state => " + someState);
        this.someState = someState;
        Assert.assertSame(someKey, b.getCallbackID());
    }  

    public void testCallback() {

        b.setCallbackID("someKey");
        b.getService().testCallBack("Some string");
        int count = 4;
        while (someState == null && count > 0) {
            delayQuarterSecond();
            count--;
        }
        if (someState != null)
            Assert.fail("Same instance received statefull calledback");
    }

    // Utilities
    private void delayQuarterSecond() {
        try {
            Thread.sleep(250);// millisecs
        } catch (InterruptedException ex) {
            throw new Error(ex);
        }
    }

}
