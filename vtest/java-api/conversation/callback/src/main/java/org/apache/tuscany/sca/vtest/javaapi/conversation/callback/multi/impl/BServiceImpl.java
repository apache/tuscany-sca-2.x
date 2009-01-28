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

import org.apache.tuscany.sca.vtest.javaapi.conversation.callback.multi.AServiceCallback;
import org.apache.tuscany.sca.vtest.javaapi.conversation.callback.multi.AServiceCallback2;
import org.apache.tuscany.sca.vtest.javaapi.conversation.callback.multi.BService;
import org.apache.tuscany.sca.vtest.javaapi.conversation.callback.multi.BService2;
import org.junit.Assert;
import org.oasisopen.sca.annotation.Callback;
import org.oasisopen.sca.annotation.Scope;
import org.oasisopen.sca.annotation.Service;

@Service(interfaces= {BService.class,BService2.class})
@Scope("CONVERSATION")
public class BServiceImpl implements BService, BService2 {

    String someState;

    @Callback
    protected AServiceCallback callback;

    @Callback
    protected AServiceCallback2 callback2;
    
    @Callback
    protected AServiceCallback2 callback3;


    public void setState(String someState) {
        this.someState = someState;
    }

    public String getState() {
        return someState;
    }

    public void testCallBack(String someState) {
        callback.callBack(someState);
    }

    public void testCallBack2(String someState) {
        Assert.assertNotNull(callback3); //Spec lines 670,671
        callback2.callBack2(someState);
    }

}
