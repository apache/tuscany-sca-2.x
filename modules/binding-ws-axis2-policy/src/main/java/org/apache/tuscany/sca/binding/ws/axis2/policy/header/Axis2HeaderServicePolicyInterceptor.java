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
package org.apache.tuscany.sca.binding.ws.axis2.policy.header;


import javax.xml.namespace.QName;

import org.apache.axiom.om.OMElement;
import org.apache.tuscany.sca.assembly.xml.Constants;
import org.apache.tuscany.sca.interfacedef.Operation;
import org.apache.tuscany.sca.invocation.Interceptor;
import org.apache.tuscany.sca.invocation.Invoker;
import org.apache.tuscany.sca.invocation.Message;
import org.apache.tuscany.sca.policy.PolicySet;

/**
 * Policy handler to handle PolicySet related to Logging with the QName
 * {http://tuscany.apache.org/xmlns/sca/1.0/impl/java}LoggingPolicy
 *
 * @version $Rev$ $Date$
 */
public class Axis2HeaderServicePolicyInterceptor implements Interceptor {
    private Invoker next;
    private Operation operation;
    private PolicySet policySet = null;
    private String context;
    private Axis2HeaderPolicy policy;

    public Axis2HeaderServicePolicyInterceptor(String context, Operation operation, PolicySet policySet) {
        super();
        this.operation = operation;
        this.policySet = policySet;
        this.context = context;
        init();
    }

    private void init() {
        if (policySet != null) {
            for (Object policyObject : policySet.getPolicies()){
                if (policyObject instanceof Axis2HeaderPolicy){
                    policy = (Axis2HeaderPolicy)policyObject;
                    break;
                }
            }
        }
    }

    public Message invoke(Message msg) {
        // TODO - not yet implemented       
    
        return getNext().invoke(msg);
    }

    public Invoker getNext() {
        return next;
    }

    public void setNext(Invoker next) {
        this.next = next;
    }
}
