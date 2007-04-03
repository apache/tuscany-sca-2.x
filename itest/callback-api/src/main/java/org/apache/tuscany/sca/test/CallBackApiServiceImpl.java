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

import org.osoa.sca.annotations.Service;
import org.osoa.sca.annotations.Context;
import org.osoa.sca.CompositeContext;
import org.osoa.sca.RequestContext;
import org.osoa.sca.ServiceReference;

@Service(CallBackApiService.class)
public class CallBackApiServiceImpl implements CallBackApiService {

    @Context
    protected CompositeContext compositeContext;
    protected CallBackApiCallBack callback;

    public void knockKnock(String aString) {

        System.out.println("CallBackApiServiceImpl message received: " + aString);
        callback = this.getCallBackInterface();
        callback.callBackMessage("Who's There");
        System.out.println("CallBackApiServiceImpl response sent");
        return;

    }

    public void multiCallBack(String aString) {

        callback = this.getCallBackInterface();

        System.out.println("CallBackApiServiceImpl message received: " + aString);
        callback.callBackIncrement("Who's There 1");
        System.out.println("CallBackApiServiceImpl response sent");
        callback.callBackIncrement("Who's There 2");
        System.out.println("CallBackApiServiceImpl response sent");
        callback.callBackIncrement("Who's There 3");
        System.out.println("CallBackApiServiceImpl response sent");
        return;

    }

    public void noCallBack(String aString) {

        System.out.println("CallBackApiServiceImpl message received: " + aString);

        return;

    }

    private CallBackApiCallBack getCallBackInterface() {
        System.out.println("CallBackApiServiceImpl getting request context");
        RequestContext rc = compositeContext.getRequestContext();
        System.out.println("CallBackApiServiceImpl getting callback from request context");
        callback = (CallBackApiCallBack) ((ServiceReference) rc.getServiceReference()).getCallback();
        System.out.println("CallBackApiServiceImpl returning callback");
        return callback;

    }

}
