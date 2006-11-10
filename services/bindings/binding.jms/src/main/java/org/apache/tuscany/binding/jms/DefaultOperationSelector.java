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
package org.apache.tuscany.binding.jms;

import javax.jms.JMSException;
import javax.jms.Message;

public class DefaultOperationSelector implements OperationSelector{
	
	private static final String DEFAULT_PROPERTY_NAME = "scaOperationName";
	private JMSBinding jmsBinding;
		
	public DefaultOperationSelector(JMSBinding jmsBinding){
		this.jmsBinding = jmsBinding;
	}

	public String getOperationName(Message message) throws JMSBindingException {
		try {
			return message.getStringProperty(getPropertyName());
		} catch (JMSException e) {
			throw new JMSBindingException(
					"Error retriving operation name from message", e);
		}
	}

	public void setOperationName(String operationName,Message message) throws JMSBindingException {
		try {
			message.setStringProperty(getPropertyName(),operationName);
		} catch (JMSException e) {
			throw new JMSBindingException(
					"Error setting the operation name as a message header", e);
		}		
	}

	private String getPropertyName(){
		String propName = jmsBinding.getOperationSelectorPropertyName();
		if (propName != null && !propName.trim().equals("")){
			return propName;
		}else{
			return DEFAULT_PROPERTY_NAME;
		}
	}
	
}
