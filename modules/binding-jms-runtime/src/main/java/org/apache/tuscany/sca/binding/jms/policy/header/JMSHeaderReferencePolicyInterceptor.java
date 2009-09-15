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
package org.apache.tuscany.sca.binding.jms.policy.header;


import org.apache.tuscany.sca.assembly.EndpointReference;
import org.apache.tuscany.sca.binding.jms.context.JMSBindingContext;
import org.apache.tuscany.sca.invocation.Invoker;
import org.apache.tuscany.sca.invocation.Message;
import org.apache.tuscany.sca.invocation.PhasedInterceptor;
import org.apache.tuscany.sca.policy.PolicySet;

/**
 * Policy handler to handle PolicySet related to Logging with the QName
 * {http://tuscany.apache.org/xmlns/sca/1.0/impl/java}LoggingPolicy
 *
 * @version $Rev$ $Date$
 */
public class JMSHeaderReferencePolicyInterceptor implements PhasedInterceptor {

    private Invoker next;
    private PolicySet policySet = null;
    private String context;
    private JMSHeaderPolicy jmsHeaderPolicy;
    private String phase;
    private EndpointReference endpointReference;
    
    public JMSHeaderReferencePolicyInterceptor(String context, EndpointReference endpointReference, PolicySet policySet, String phase) {
        super();
        this.endpointReference = endpointReference;
        this.policySet = policySet;
        this.context = context;
        this.phase = phase;
        
        init();
    }

    private void init() {
        if (policySet != null) {
            for (Object policyObject : policySet.getPolicies()){
                if (policyObject instanceof JMSHeaderPolicy){
                    jmsHeaderPolicy = (JMSHeaderPolicy)policyObject;
                    break;
                }
            }
        }
    }

    public Message invoke(Message msg) {
            // get the jms context
            JMSBindingContext context = msg.getBindingContext();

            javax.jms.Message jmsMsg = msg.getBody();
    
            // JMS header attrs set on MessageProducer via interceptors.
                        
            return getNext().invoke(msg);
    }

    public Invoker getNext() {
        return next;
    }

    public void setNext(Invoker next) {
        this.next = next;
    }

    public String getPhase() {
        return phase;
    }
}
