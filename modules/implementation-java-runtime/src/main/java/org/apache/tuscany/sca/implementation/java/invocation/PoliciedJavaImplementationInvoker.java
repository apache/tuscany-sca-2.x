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

package org.apache.tuscany.sca.implementation.java.invocation;

import java.lang.reflect.Method;
import java.util.Map;

import org.apache.tuscany.sca.interfacedef.Operation;
import org.apache.tuscany.sca.invocation.Message;
import org.apache.tuscany.sca.policy.PolicySet;
import org.apache.tuscany.sca.policy.util.PolicyHandler;
import org.apache.tuscany.sca.runtime.RuntimeComponent;

/**
 * Responsible for synchronously dispatching an invocation to a Java component
 * implementation instance effecting applicable policies before and after invocation 
 */
public class PoliciedJavaImplementationInvoker extends JavaImplementationInvoker {
    private Map<PolicySet, PolicyHandler> policyHandlers = null;
    
    public PoliciedJavaImplementationInvoker(Operation operation, 
                                             Method method, 
                                             RuntimeComponent component,
                                             Map<PolicySet, PolicyHandler> policyHandlers) {  
        super(operation, method, component);
        this.policyHandlers = policyHandlers;
    }
    
    public Message invoke(Message msg) { 
        applyPreInvocationPolicies(operation, msg);
        msg = super.invoke(msg);
        applyPostInvocationPolices(operation, msg);
        return msg;
    }
    
    private void applyPreInvocationPolicies(Object... context) {
        for ( PolicyHandler policyHandler : policyHandlers.values() ) {
            policyHandler.beforeInvoke(context);
        }
    }
    
    private void applyPostInvocationPolices(Object...  context) {
        for ( PolicyHandler policyHandler : policyHandlers.values() ) {
            policyHandler.afterInvoke(context);
        }
    }
        
}
