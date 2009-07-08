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
package org.apache.tuscany.sca.binding.ws.axis2.policy.authentication.token;


import java.security.Principal;

import javax.security.auth.Subject;

import org.apache.tuscany.sca.binding.ws.axis2.policy.header.Axis2HeaderPolicyUtil;
import org.apache.tuscany.sca.binding.ws.axis2.policy.header.Axis2SOAPHeaderString;
import org.apache.tuscany.sca.interfacedef.Operation;
import org.apache.tuscany.sca.invocation.Invoker;
import org.apache.tuscany.sca.invocation.Message;
import org.apache.tuscany.sca.invocation.Phase;
import org.apache.tuscany.sca.invocation.PhasedInterceptor;
import org.apache.tuscany.sca.policy.PolicySet;
import org.apache.tuscany.sca.policy.authentication.token.TokenPrincipal;
import org.apache.tuscany.sca.policy.security.SecurityUtil;

/**
 * Policy handler to handle PolicySet related to Logging with the QName
 * {http://tuscany.apache.org/xmlns/sca/1.1/impl/java}LoggingPolicy
 *
 * @version $Rev: 721811 $ $Date: 2008-11-30 13:46:51 +0000 (Sun, 30 Nov 2008) $
 */
public class Axis2TokenAuthenticationServicePolicyInterceptor implements PhasedInterceptor {
    private Invoker next;
    private Operation operation;
    private PolicySet policySet = null;
    private String context;
    private Axis2TokenAuthenticationPolicy policy;

    public Axis2TokenAuthenticationServicePolicyInterceptor(String context, Operation operation, PolicySet policySet) {
        super();
        this.operation = operation;
        this.policySet = policySet;
        this.context = context;
        init();
    }

    private void init() {
        if (policySet != null) {
            for (Object policyObject : policySet.getPolicies()){
                if (policyObject instanceof Axis2TokenAuthenticationPolicy){
                    policy = (Axis2TokenAuthenticationPolicy)policyObject;
                    break;
                }
            }
        }
    }

    public Message invoke(Message msg) {
        
        Axis2SOAPHeaderString header = (Axis2SOAPHeaderString)Axis2HeaderPolicyUtil.getHeader(msg, policy.getTokenName());
        
        if (header != null) {
            System.out.println("Web service received token: " + header.getHeaderString());
            
            // call out here to some 3rd party system to do whatever you 
            // need to turn header credentials into an authenticated principal  
            
            Subject subject = SecurityUtil.getSubject(msg);
            Principal principal = new TokenPrincipal(header.getHeaderString());
            subject.getPrincipals().add(principal);            
        }
    
        return getNext().invoke(msg);
    }

    public Invoker getNext() {
        return next;
    }

    public void setNext(Invoker next) {
        this.next = next;
    }
    
    public String getPhase() {
        return Phase.SERVICE_POLICY;
    }    
}
