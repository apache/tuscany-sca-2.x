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

import java.util.List;

import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.login.LoginContext;

import org.apache.tuscany.sca.invocation.Invoker;
import org.apache.tuscany.sca.invocation.Message;
import org.apache.tuscany.sca.invocation.Phase;
import org.apache.tuscany.sca.invocation.PhasedInterceptor;
import org.oasisopen.sca.ServiceRuntimeException;

/**
 * @version $Rev$ $Date$
 */
public class JaasAuthenticationInterceptor implements PhasedInterceptor {
    private List<JaasAuthenticationPolicy> authenticationPolicies;
    private Invoker next;

    public JaasAuthenticationInterceptor(List<JaasAuthenticationPolicy> authenticationPolicies) {
        super();
        this.authenticationPolicies = authenticationPolicies;
    }

    /**
     * @see org.apache.tuscany.sca.invocation.Interceptor#getNext()
     */
    public Invoker getNext() {
        return next;
    }

    /**
     * @see org.apache.tuscany.sca.invocation.Interceptor#setNext(org.apache.tuscany.sca.invocation.Invoker)
     */
    public void setNext(Invoker next) {
        this.next = next;
    }

    /**
     * @see org.apache.tuscany.sca.invocation.Invoker#invoke(org.apache.tuscany.sca.invocation.Message)
     */
    public Message invoke(Message msg) {
        try {
            for (JaasAuthenticationPolicy policy : authenticationPolicies) {
                CallbackHandler callbackHandler =
                    (CallbackHandler)policy.getCallbackHandlerClass().newInstance();
                LoginContext lc = new LoginContext(policy.getConfigurationName(), callbackHandler);
                lc.login();
                // Subject subject = lc.getSubject();
            }
        } catch (Exception e) {
            throw new ServiceRuntimeException(e);
        }
        return getNext().invoke(msg);
    }
    
    public String getPhase() {
        return Phase.IMPLEMENTATION_POLICY;
    }

}
