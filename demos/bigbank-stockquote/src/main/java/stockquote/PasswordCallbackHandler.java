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
package stockquote;

import java.io.IOException;

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.UnsupportedCallbackException;

import org.apache.ws.security.WSPasswordCallback;

/**
 * Sample userid passwd generation class 
 */
public class PasswordCallbackHandler implements CallbackHandler {

    public void handle(Callback[] callbacks) throws IOException,
            UnsupportedCallbackException {
    	for (int i = 0; i < callbacks.length; i++) {
            System.out.println("*** Calling Server User/Passwd Handler...." );
            WSPasswordCallback pwcb = (WSPasswordCallback)callbacks[i];
            System.out.println("*** Getting password for user ...."  + pwcb.getIdentifer() + " & " + pwcb.getKey());
            if ( pwcb.getUsage() == WSPasswordCallback.SIGNATURE ) {
                System.out.println(" Usage is SIGNATURE ... ");
                pwcb.setPassword("sqservice");
            }
        }
    }

}
