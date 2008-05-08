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

import org.apache.tuscany.sca.vtest.javaapi.conversation.callback.AServiceCallback;
import org.apache.tuscany.sca.vtest.javaapi.conversation.callback.BService;
import org.apache.tuscany.sca.vtest.javaapi.conversation.callback.stateless.BServiceCallback;
import org.apache.tuscany.sca.vtest.javaapi.conversation.callback.CService;
import org.apache.tuscany.sca.vtest.javaapi.conversation.callback.Utilities;
import org.junit.Assert;
import org.osoa.sca.RequestContext;
import org.osoa.sca.ServiceReference;
import org.osoa.sca.annotations.Callback;
import org.osoa.sca.annotations.Context;
import org.osoa.sca.annotations.Reference;
import org.osoa.sca.annotations.Scope;
import org.osoa.sca.annotations.Service;

@Service(BService.class)
@Scope("CONVERSATION")
public class BServiceImpl2 implements BService, BServiceCallback {

    String someState;
    
    @Context
    protected RequestContext rc;

    @Callback
    protected AServiceCallback callback;

    @Reference
    protected ServiceReference<CService> c;

    public void setState(String someState) {
        this.someState = someState;
    }

    public String getState() {
        return someState;
    }

    public void callBack(String someState) {
        System.out.println("B-callback called with this state => " + someState);
        this.someState = someState;
        Assert.assertSame("1234", rc.getServiceReference().getCallbackID());
    }

    public void testCallBack(String someState) {

        c.setCallbackID("1234");

        c.getService().testCallBack(someState);
        int count = 4;
        while (this.someState == null && count > 0) {
            Utilities.delayQuarterSecond();
            count--;
        }
        if (this.someState != null)
            Assert.fail("Callback should NOT have been received by this instance");

        callback.callBack(someState);
    }

}
