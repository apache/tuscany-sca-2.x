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


import java.util.Map;

import javax.jms.DeliveryMode;
import javax.jms.JMSException;
import javax.security.auth.Subject;

import org.apache.tuscany.sca.assembly.Binding;
import org.apache.tuscany.sca.binding.jms.impl.JMSBinding;
import org.apache.tuscany.sca.binding.jms.impl.JMSBindingException;
import org.apache.tuscany.sca.binding.jms.policy.JMSBindingDefinitionsProvider;
import org.apache.tuscany.sca.interfacedef.Operation;
import org.apache.tuscany.sca.invocation.Interceptor;
import org.apache.tuscany.sca.invocation.Invoker;
import org.apache.tuscany.sca.invocation.Message;
import org.apache.tuscany.sca.policy.PolicySet;
import org.apache.tuscany.sca.policy.SecurityUtil;
import org.apache.tuscany.sca.policy.authentication.token.TokenPrincipal;
import org.apache.tuscany.sca.runtime.ReferenceParameters;
import org.apache.tuscany.sca.runtime.RuntimeComponent;
import org.apache.tuscany.sca.runtime.RuntimeComponentReference;

/**
 * Policy handler to handle PolicySet related to Logging with the QName
 * {http://tuscany.apache.org/xmlns/sca/1.0/impl/java}LoggingPolicy
 *
 * @version $Rev$ $Date$
 */
public class JMSHeaderReferencePolicyInterceptor implements Interceptor {

    private Invoker next;
    private RuntimeComponent component;
    private RuntimeComponentReference reference;
    private JMSBinding jmsBinding;
    private PolicySet policySet = null;
    private String context;
    private JMSHeaderPolicy jmsHeaderPolicy;

    public JMSHeaderReferencePolicyInterceptor(String context, RuntimeComponent component, RuntimeComponentReference reference, Binding binding, PolicySet policySet) {
        super();
        this.component = component;
        this.reference = reference;
        this.jmsBinding = (JMSBinding)binding;
        this.policySet = policySet;
        this.context = context;
        
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
        try {
            javax.jms.Message jmsMsg = msg.getBody();
            String operationName = msg.getOperation().getName();
    
            if ((jmsHeaderPolicy != null) && 
                (jmsHeaderPolicy.getDeliveryModePersistent() != null)) {
                if (jmsHeaderPolicy.getDeliveryModePersistent()) {
                    jmsMsg.setJMSDeliveryMode(DeliveryMode.PERSISTENT);
                } else {
                    jmsMsg.setJMSDeliveryMode(DeliveryMode.NON_PERSISTENT);
                }
                
            } 
    
            if ((jmsHeaderPolicy != null) && 
                (jmsHeaderPolicy.getJmsCorrelationId() != null)) {
                jmsMsg.setJMSCorrelationID(jmsHeaderPolicy.getJmsCorrelationId());
            } 
    
            if ((jmsHeaderPolicy != null) && 
                (jmsHeaderPolicy.getJmsPriority() != null)) {
                jmsMsg.setJMSPriority(jmsHeaderPolicy.getJmsPriority());
            } 
    
            if ((jmsHeaderPolicy != null) && 
                (jmsHeaderPolicy.getJmsType() != null)) {
                jmsMsg.setJMSType(jmsHeaderPolicy.getJmsType());
            } 
           
            if (jmsHeaderPolicy != null){
                for (String propName : jmsHeaderPolicy.getProperties().keySet()) {
                    jmsMsg.setObjectProperty(propName, jmsHeaderPolicy.getProperties().get(propName));
                }
            }
                        
            return getNext().invoke(msg);
        } catch (JMSException e) {
            throw new JMSBindingException(e);
        } 
    }

    public Invoker getNext() {
        return next;
    }

    public void setNext(Invoker next) {
        this.next = next;
    }
}
