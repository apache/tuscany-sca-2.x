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

package org.apache.tuscany.sca.vtest.javaapi.conversation.callback.multi.impl;

import org.apache.tuscany.sca.vtest.javaapi.conversation.callback.Utilities;
import org.apache.tuscany.sca.vtest.javaapi.conversation.callback.AService;
import org.apache.tuscany.sca.vtest.javaapi.conversation.callback.multi.AServiceCallback;
import org.apache.tuscany.sca.vtest.javaapi.conversation.callback.multi.AServiceCallback2;
import org.apache.tuscany.sca.vtest.javaapi.conversation.callback.multi.BService;
import org.apache.tuscany.sca.vtest.javaapi.conversation.callback.multi.BService2;
import org.junit.Assert;
import org.osoa.sca.ServiceReference;
import org.osoa.sca.annotations.Reference;
import org.osoa.sca.annotations.Scope;
import org.osoa.sca.annotations.Service;

@Service(AService.class)
@Scope("CONVERSATION")
public class AServiceImpl implements AService, AServiceCallback , AServiceCallback2 {

    @Reference
    protected ServiceReference<BService> b;
    
    @Reference
    protected ServiceReference<BService2> b2;


    private String someState;
    private String someState2;

    public void callBack(String someState) {
        System.out.println("A-callback called with this state => " + someState);
        this.someState = someState;
    }

    public void callBack2(String someState) {
        System.out.println("A-callback2 called with this state => " + someState);
        this.someState2 = someState;    
    }
    
    public void testCallback() {
        b.getService().testCallBack("Some string");
        int count = 4;
        while (someState == null && count > 0) {
            Utilities.delayQuarterSecond();
            count--;
        }
        if (someState == null)
            Assert.fail("Callback not received by this instance");
    }

    public void testCallback2() {
        b2.getService().testCallBack2("Some string");
        int count = 4;
        while (someState2 == null && count > 0) {
            Utilities.delayQuarterSecond();
            count--;
        }
        if (someState2 == null)
            Assert.fail("Callback not received by this instance");
    }
}
