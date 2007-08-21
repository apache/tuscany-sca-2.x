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
package echo.provider;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

import org.apache.tuscany.sca.invocation.Invoker;
import org.apache.tuscany.sca.invocation.Message;
import org.apache.tuscany.sca.policy.PolicySet;

import echo.provider.policy.PolicyHandler;
import echo.provider.policy.EncryptionPolicyHandler;

/**
 * Invoker that applies policies before invocation for the sample echo binding.
 */
public class EchoBindingPoliciedInvoker implements Invoker {
    List<PolicySet> policies = null;
    Map<QName, PolicyHandler> policyHandlers = new HashMap<QName, PolicyHandler>();
    
    public EchoBindingPoliciedInvoker(List<PolicySet> policies) {
        this.policies = policies;
        policyHandlers.put(new QName("http://sample/policy","EncryptionPolicy"), 
                           new EncryptionPolicyHandler());
    }

    public Message invoke(Message msg) {
        try {
            Object[] args = msg.getBody();
            
            applyPolicies(args);

            // echo back the first parameter, a real binding would invoke some API for flowing the request
            Object result = args[0];
                                 
            msg.setBody(result);
            
        } catch (Exception e) {
            msg.setFaultBody(e);
        }
        return msg;
    }  
    
    private void applyPolicies(Object[] args) throws Exception {
        for ( PolicySet policySet : policies ) {
            PolicyHandler policyHandler = policyHandlers.get(policySet.getName());
            policyHandler.applyPolicy(args, policySet);
        }
    }

}
