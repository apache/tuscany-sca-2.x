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
package org.apache.tuscany.sca.binding.jms.headers;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.naming.NamingException;

import org.apache.tuscany.sca.binding.jms.JMSBinding;
import org.apache.tuscany.sca.binding.jms.JMSBindingConstants;
import org.apache.tuscany.sca.binding.jms.JMSBindingException;
import org.apache.tuscany.sca.binding.jms.context.JMSBindingContext;
import org.apache.tuscany.sca.binding.jms.provider.JMSBindingServiceBindingProvider;
import org.apache.tuscany.sca.binding.jms.provider.JMSMessageProcessor;
import org.apache.tuscany.sca.binding.jms.provider.JMSMessageProcessorUtil;
import org.apache.tuscany.sca.binding.jms.provider.JMSResourceFactory;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.core.invocation.InterceptorAsyncImpl;
import org.apache.tuscany.sca.interfacedef.Operation;
import org.apache.tuscany.sca.invocation.Interceptor;
import org.apache.tuscany.sca.invocation.Invoker;
import org.apache.tuscany.sca.invocation.Message;
import org.apache.tuscany.sca.runtime.RuntimeEndpoint;
import org.apache.tuscany.sca.runtime.RuntimeEndpointReference;

/**
 *
 * @version $Rev$ $Date$
 */
public class HeaderReferenceInterceptor extends InterceptorAsyncImpl {

    private RuntimeEndpointReference runtimeWire;
    private JMSBinding jmsBinding;
    private JMSMessageProcessor requestMessageProcessor;
    private List<Operation> operations;


    public HeaderReferenceInterceptor(ExtensionPointRegistry extensions, JMSBinding jmsBinding, 
    		JMSResourceFactory jmsResourceFactory, RuntimeEndpointReference runtimeWire) {
        super();
        this.jmsBinding = jmsBinding;
        this.runtimeWire = runtimeWire;      
        this.requestMessageProcessor = JMSMessageProcessorUtil.getRequestMessageProcessor(extensions, jmsBinding);
        this.operations = runtimeWire.getReference().getInterfaceContract().getInterface().getOperations();
    }  // end constructor

    public Message invoke(Message msg) {
        return next.invoke(invokeRequest(msg));
    } // end method invoke
    
    public Message invokeRequest(Message tuscanyMsg) {
        try {
            // Get the JMS context
            JMSBindingContext context = tuscanyMsg.getBindingContext();
            javax.jms.Message jmsMsg = tuscanyMsg.getBody();
            
            Operation operation = tuscanyMsg.getOperation();
            String operationName = operation.getName();
            RuntimeEndpointReference reference = runtimeWire;
            
            // OASIS spec suggests we do not need to do anything with
            // @nativeOperation here on the reference side.
            requestMessageProcessor.setOperationName(operationName, jmsMsg);

            if (jmsBinding.getEffectiveJMSDeliveryMode(operationName) != null) {
            	if (jmsBinding.getEffectiveJMSDeliveryMode(operationName)) {
            		jmsMsg.setJMSDeliveryMode(DeliveryMode.PERSISTENT);
            	} else {
            		jmsMsg.setJMSDeliveryMode(DeliveryMode.NON_PERSISTENT);
            	}
            } 
    
            if (jmsBinding.getEffectiveJMSPriority(operationName) != null) {
            	jmsMsg.setJMSPriority(jmsBinding.getEffectiveJMSPriority(operationName));
            } 
    
            if ( jmsBinding.getEffectiveJMSType(operationName) != null ) {         
            	jmsMsg.setJMSType(jmsBinding.getEffectiveJMSType(operationName));
            } 
            
            if ( jmsBinding.getEffectiveJMSTimeToLive(operationName) != null ) {
            	jmsMsg.setJMSExpiration(jmsBinding.getEffectiveJMSTimeToLive(operationName));
            }
            
            if (jmsBinding.getOperationJMSCorrelationId(operationName) != null) {
                jmsMsg.setJMSCorrelationID(jmsBinding.getOperationJMSCorrelationId(operationName));
            } else if (JMSBindingConstants.CORRELATE_CORRELATION_ID.equals(jmsBinding.getCorrelationScheme())) {
                jmsMsg.setJMSCorrelationID(UUID.randomUUID().toString());
            }
    
            if (tuscanyMsg.getFrom().getCallbackEndpoint() != null) {
    
                String callbackDestName = getCallbackDestinationName(reference);
                if (callbackDestName != null) {
                    jmsMsg.setStringProperty(JMSBindingConstants.CALLBACK_Q_PROPERTY, "jms:jndi:" + callbackDestName);
                    if (operation.isNonBlocking()) {
                    	Destination dest = context.getJmsResourceFactory().lookupDestination(callbackDestName);
						jmsMsg.setJMSReplyTo(dest);
                    }
                }
            }
            
            for (String propName : jmsBinding.getPropertyNames()) {
                Object value = jmsBinding.getProperty(propName);
                jmsMsg.setObjectProperty(propName, value);
            }
    
            Map<String, Object> operationProperties = jmsBinding.getOperationProperties(operationName);
            if (operationProperties != null) {
                for (String propName : operationProperties.keySet()) {
                    Object value = operationProperties.get(propName);
                    jmsMsg.setObjectProperty(propName, value);
                }
            }
            
            if (jmsBinding.getEffectiveJMSTimeToLive(operationName) != null) {
                context.setTimeToLive(jmsBinding.getEffectiveJMSTimeToLive(operationName) * 2);        
            } else {   
            	context.setTimeToLive(JMSBindingConstants.DEFAULT_TIME_TO_LIVE);        
            }
            
            // Adding MESSAGE_ID field to the JMS message, which is used to correlate async callbacks
            String msgID = (String)tuscanyMsg.getHeaders().get("MESSAGE_ID");
            if( msgID != null ) {
            	jmsMsg.setObjectProperty("MESSAGE_ID", msgID);
            } // end if 
            //
            
            return tuscanyMsg;
        } catch (JMSException e) {
            throw new JMSBindingException(e);
        } catch (NamingException e) {
            throw new JMSBindingException(e);
		} 
    } // end method invokeRequest
    
    protected String getCallbackDestinationName(RuntimeEndpointReference reference) {
        RuntimeEndpoint endpoint = (RuntimeEndpoint) reference.getCallbackEndpoint();
        if (endpoint != null) {
            JMSBindingServiceBindingProvider bp = (JMSBindingServiceBindingProvider)endpoint.getBindingProvider();
            return bp.getDestinationName();
        }
        return null;
    }  
      
	public Message processRequest(Message msg) {
		return invokeRequest(msg);
	} // end method processRequest

	public Message processResponse(Message msg) {
		// When the response message arrives, there may be information about the 
		// operation and the related request message ID in the headers - extract it into the
		// Tuscany message
		
		javax.jms.Message responseMsg = msg.getBody();
		try {
			// Operation name...
			String operationName = responseMsg.getStringProperty("scaOperationName");
			for( Operation op : operations ) {
				if( operationName.equals(op.getName())) {
					msg.setOperation(op);
					break;
				} // end if
			} // end for
			
			// Relates to header...
			String relatesTo = responseMsg.getStringProperty("RELATES_TO");
			if( relatesTo != null ) {
				msg.getHeaders().put("RELATES_TO", relatesTo);
			} // end if
		} catch (JMSException e) {
			// TODO Auto-generated catch block
		} // end try
		return msg;
	} // end method processResponse
    
}
