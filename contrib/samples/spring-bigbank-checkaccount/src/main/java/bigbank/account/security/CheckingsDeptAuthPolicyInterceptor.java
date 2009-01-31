package bigbank.account.security;

import java.security.Principal;

import org.apache.tuscany.sca.interfacedef.Operation;
import org.apache.tuscany.sca.invocation.Interceptor;
import org.apache.tuscany.sca.invocation.Invoker;
import org.apache.tuscany.sca.invocation.Message;
import org.apache.tuscany.sca.policy.PolicySet;
import org.apache.tuscany.sca.policy.SecurityUtil;

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

/**
 * Policy handler to handle PolicySet related to Logging with the QName
 * {http://tuscany.apache.org/xmlns/sca/1.0/impl/java}LoggingPolicy
 */
public class CheckingsDeptAuthPolicyInterceptor implements Interceptor {
    private Invoker next;

    public CheckingsDeptAuthPolicyInterceptor(String context, Operation operation, PolicySet policySet) {
        super();
        init();
    }

    private final void init() {
    }

    public Message invoke(Message msg) {
        Object msgBody = msg.getBody();
        if (msgBody instanceof Object[]) {
        	Object args[] = (Object[])msg.getBody();
            Principal principal = SecurityUtil.getPrincipal(msg);
            if (principal != null){
                BigbankCheckingsAcl.authorize(principal,
                                             (String)args[0]);
            }        	
        } 
        
        Message responseMsg = null;
        try {
            responseMsg = getNext().invoke(msg);
            return responseMsg;
        } catch (RuntimeException e) {
            throw e;
        } 
    }

    public Invoker getNext() {
        return next;
    }

    public void setNext(Invoker next) {
        this.next = next;
    }
}
