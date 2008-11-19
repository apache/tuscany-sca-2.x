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
package org.apache.tuscany.sca.policy.authentication.basic;


import javax.security.auth.Subject;


import org.apache.tuscany.sca.interfacedef.Operation;
import org.apache.tuscany.sca.invocation.Interceptor;
import org.apache.tuscany.sca.invocation.Invoker;
import org.apache.tuscany.sca.invocation.Message;
import org.apache.tuscany.sca.policy.PolicySet;
import org.apache.tuscany.sca.policy.SecurityUtil;

/**
 *
 * @version $Rev$ $Date$
 */
public class BasicAuthenticationReferencePolicyInterceptor implements Interceptor {

    private Invoker next;
    private Operation operation;
    private PolicySet policySet = null;
    private String context;
    private BasicAuthenticationPolicy policy;

    public BasicAuthenticationReferencePolicyInterceptor(String context, Operation operation, PolicySet policySet) {
        super();
        this.operation = operation;
        this.policySet = policySet;
        this.context = context;
        init();
    }

    private void init() {
        if (policySet != null) {
            for (Object policyObject : policySet.getPolicies()){
                if (policyObject instanceof BasicAuthenticationPolicy){
                    policy = (BasicAuthenticationPolicy)policyObject;
                    break;
                }
            }
        }
    }

    public Message invoke(Message msg) {
        
        // get the security context
        Subject subject = SecurityUtil.getSubject(msg);
        BasicAuthenticationPrincipal principal = SecurityUtil.getPrincipal(subject, 
                                                                           BasicAuthenticationPrincipal.class);

        // if no credentials propogated from the reference then use 
        // the ones from the policy
        if (principal == null && 
            policy.getUserName() != null && 
            !policy.getUserName().equals("")) {
            principal = new BasicAuthenticationPrincipal(policy.getUserName(),
                                                         policy.getPassword());
            subject.getPrincipals().add(principal);
        }

        if (principal == null){
            // alternatively we could call out here to some 3rd party system to get credentials
            // or convert from some other security principal
        }
        
        return getNext().invoke(msg);
    }

    public Invoker getNext() {
        return next;
    }

    public void setNext(Invoker next) {
        this.next = next;
    }
}
