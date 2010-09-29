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
package sample;

import org.oasisopen.sca.ComponentContext;
import org.oasisopen.sca.ServiceReference;
import org.oasisopen.sca.annotation.Callback;
import org.oasisopen.sca.annotation.Context;
import org.oasisopen.sca.annotation.Service;

@Service(sample.Service.class)
public class ServiceImpl implements sample.Service {

    public static final String MESSAGE_RECEIVED = "ServiceImpl - Received message: ";

    @Context
    protected ComponentContext componentContext;

    @Callback
    protected ServiceReference<CallBack> callbackRef;

    /**
     * This function gets an object of ServiceImpl by calling
     * getCallBackInterface function and calls the callBackMessage function.
     * 
     * @param aString String passed by a function call
     */
    public void knockKnock(String aString) {
        System.out.println(MESSAGE_RECEIVED + aString);
        CallBack callback = this.getCallBackFromComponentContext();
        callback.callBackMessage("Who's There");
    }

    /**
     * This function calls the callBackMessage function. The reference to this
     * function is received from the callback reference to the Service class.
     * 
     * @param aString String passed by a function call
     */
    public void knockKnockByRef(String aString) {
        System.out.println(MESSAGE_RECEIVED + aString);
        callbackRef.getService().callBackMessage("Who's There");
    }

    /**
     * This function gets an object of ServiceImpl by calling
     * getCallBackInterface function. This function then places multiple
     * callbacks using the callbackIncrement function defined in the callback
     * implementation.
     * 
     * @param aString String passed by a function call
     */
    public void multiCallBack(String aString) {
        CallBack callback = this.getCallBackFromComponentContext();
        System.out.println(MESSAGE_RECEIVED + aString);
        callback.callBackIncrement();
        callback.callBackIncrement();
        callback.callBackIncrement();
    }

    /**
     * This function does not callBack any function.
     * 
     * @param aString String passed by a function call
     */
    public void noCallBack(String aString) {
        System.out.println(MESSAGE_RECEIVED + aString);
    }

    /**
     * This function gets an object of ServiceImpl from the present
     * componentContext.
     * 
     * @return the callback
     */
    private CallBack getCallBackFromComponentContext() {
        return componentContext.getRequestContext().getCallback();
    }

}
