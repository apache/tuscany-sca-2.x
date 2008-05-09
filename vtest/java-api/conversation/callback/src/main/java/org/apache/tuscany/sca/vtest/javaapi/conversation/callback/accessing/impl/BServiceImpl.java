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

package org.apache.tuscany.sca.vtest.javaapi.conversation.callback.accessing.impl;

import org.apache.tuscany.sca.vtest.javaapi.conversation.callback.accessing.AServiceCallback;
import org.apache.tuscany.sca.vtest.javaapi.conversation.callback.accessing.BService;
import org.osoa.sca.CallableReference;
import org.osoa.sca.ComponentContext;
import org.osoa.sca.annotations.Callback;
import org.osoa.sca.annotations.Context;
import org.osoa.sca.annotations.Scope;
import org.osoa.sca.annotations.Service;

@Service(BService.class)
@Scope("CONVERSATION")
public class BServiceImpl implements BService {

    String someState;

    @Callback
    protected CallableReference<AServiceCallback> callback;
    
    @Context
    protected ComponentContext componentContext;

    public void setState(String someState) {
        this.someState = someState;
    }

    public String getState() {
        return someState;
    }

    public void testCallBack(String someState) {
        callback.getService().callBack(someState);
    }

    public void testCallBack2(String someState) {
        AServiceCallback callback = componentContext.getRequestContext().getCallback();
        callback.callBack(someState);
    }

}
