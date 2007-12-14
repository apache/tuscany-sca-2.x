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

package org.apache.tuscany.sca.policy.security.jaas;

import java.util.Map;

import javax.security.auth.Subject;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.login.LoginException;
import javax.security.auth.spi.LoginModule;

/**
 * @version $Rev$ $Date$
 */
public class JaasLoginModule implements LoginModule {

    private CallbackHandler callbackHandler = null;
    private Subject subject = null;

    public boolean abort() throws LoginException {
        return true;
    }

    
    public boolean commit() throws LoginException {
        return true;
    }

    public void initialize(Subject subject,
                           CallbackHandler callbackHandler,
                           Map<String, ?> sharedState,
                           Map<String, ?> options) {
        this.callbackHandler = callbackHandler;
        this.subject = subject;
    }

    public boolean login() throws LoginException {
        Callback[] callbacks = new Callback[2];
        callbacks[0] = new NameCallback("UserId:");
        callbacks[1] = new PasswordCallback("Password:", false);
        
        try {
            callbackHandler.handle(callbacks);
            String userId = ((NameCallback)callbacks[0]).getName();
            String password = new String(((PasswordCallback)callbacks[1]).getPassword());
            
            if ( userId.equals("CalculatorUser") && password.equals("CalculatorUserPasswd")) {
                System.out.println("Successfully AUTHENTICATED!!");
                return true;
            } else {
                 System.out.println("Incorrect userId / password! AUTHENTICATION FAILED!!");
                return false;
            }
        } catch ( Exception e ) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean logout() throws LoginException {
        return true;
    }

}
