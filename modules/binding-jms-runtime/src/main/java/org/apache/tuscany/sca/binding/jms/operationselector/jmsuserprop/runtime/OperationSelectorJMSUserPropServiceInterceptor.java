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
package org.apache.tuscany.sca.binding.jms.operationselector.jmsuserprop.runtime;

import java.util.List;

import javax.jms.JMSException;

import org.apache.tuscany.sca.binding.jms.JMSBinding;
import org.apache.tuscany.sca.binding.jms.JMSBindingException;
import org.apache.tuscany.sca.binding.jms.context.JMSBindingContext;
import org.apache.tuscany.sca.binding.jms.operationselector.OperationSelectorJMSUserProp;
import org.apache.tuscany.sca.core.invocation.InterceptorAsyncImpl;
import org.apache.tuscany.sca.interfacedef.Operation;
import org.apache.tuscany.sca.invocation.Interceptor;
import org.apache.tuscany.sca.invocation.Invoker;
import org.apache.tuscany.sca.invocation.Message;
import org.apache.tuscany.sca.runtime.RuntimeComponentService;
import org.apache.tuscany.sca.runtime.RuntimeEndpoint;

/**
  * Interceptor for user property based operation selection
 * 
 * <operationSelector.jmsUser propertName="MyHeaderProperty"/>
 * 
 */
public class OperationSelectorJMSUserPropServiceInterceptor extends InterceptorAsyncImpl {

    private Invoker next;
    private RuntimeEndpoint endpoint;
    private JMSBinding jmsBinding;
    private OperationSelectorJMSUserProp operationSelector;
    private RuntimeComponentService service;
    private List<Operation> serviceOperations;

    public OperationSelectorJMSUserPropServiceInterceptor(RuntimeEndpoint endpoint) {
        super();
        this.jmsBinding = (JMSBinding) endpoint.getBinding();
        this.operationSelector = (OperationSelectorJMSUserProp)jmsBinding.getOperationSelector();
        this.endpoint = endpoint;
        this.service = (RuntimeComponentService) endpoint.getService();
        this.serviceOperations = service.getInterfaceContract().getInterface().getOperations();
    }

    public Message invoke(Message msg) {
        return next.invoke(invokeRequest(msg));
    }

    public Message invokeRequest(Message msg) {
            // get the jms context
            JMSBindingContext context = msg.getBindingContext();
            javax.jms.Message jmsMsg = context.getJmsMsg();
           
            Operation operation = getTargetOperation(jmsMsg);
            msg.setOperation(operation);

            return msg;
    }

    protected Operation getTargetOperation(javax.jms.Message jmsMsg) {
        String operationName = null;
        String opSelectorPropertyName = operationSelector.getPropertyName();
        
        try {
            operationName = jmsMsg.getStringProperty(opSelectorPropertyName);
        } catch(JMSException e) {
            throw new JMSBindingException(e);
        }
        
        if (operationName == null){
            throw new JMSBindingException("Property " + opSelectorPropertyName + " not found in message header");
        }
        
        for (Operation op : serviceOperations) {
            if (op.getName().equals(operationName)) {
                return op;
            }
        }
        
        throw new JMSBindingException("Can't find operation " + operationName);
    }


    public Invoker getNext() {
        return next;
    }

    public void setNext(Invoker next) {
        this.next = next;
    }

	public Message processRequest(Message msg) {
		return invokeRequest(msg);
	} // end method processRequest

	public Message processResponse(Message msg) {
		return msg;
	} // end method processResponse
}
