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
package calculator.security;

import java.io.IOException;

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.UnsupportedCallbackException;

import org.apache.ws.security.WSPasswordCallback;

/**
 * Sample userid passwd generation class
 */
public class ClientPWCBHandler implements CallbackHandler {

    public void handle(Callback[] callbacks) throws IOException,
            UnsupportedCallbackException {
        for (int i = 0; i < callbacks.length; i++) {
        	System.out.println("*** Calling Client UserId/Password Handler .... ");
            WSPasswordCallback pwcb = (WSPasswordCallback)callbacks[i];
            System.out.println("User Id = " + pwcb.getIdentifer());
            System.out.println("Set Password = " + pwcb.getPassword());
            System.out.println("Usage = " + pwcb.getUsage());
            if ( pwcb.getUsage() == WSPasswordCallback.USERNAME_TOKEN ) {
                if ( pwcb.getIdentifer().equals("CalculatorUser")){
                    pwcb.setPassword("CalculatorUserPasswd");
                } else {
                    throw new UnsupportedCallbackException(pwcb, "Authentication Failed : UserId - Password mismatch");
                }
            } else if ( pwcb.getUsage() == WSPasswordCallback.SIGNATURE ) {
                if ( pwcb.getIdentifer().equals("CalculatorUser")) {
                    pwcb.setPassword("CalculatorUserPasswd");
                } else {
                    pwcb.setPassword("CalculatorAdmin");
                }
            }
        }
    }

}
