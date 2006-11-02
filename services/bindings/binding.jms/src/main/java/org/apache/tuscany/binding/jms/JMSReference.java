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

import javax.naming.NamingException;

import org.apache.tuscany.spi.component.CompositeComponent;
import org.apache.tuscany.spi.extension.ReferenceExtension;
import org.apache.tuscany.spi.model.Operation;
import org.apache.tuscany.spi.model.ServiceContract;
import org.apache.tuscany.spi.wire.TargetInvoker;
import org.apache.tuscany.spi.wire.WireService;

/**
 * @version $Rev: 449970 $ $Date: 2006-09-26 06:05:35 -0400 (Tue, 26 Sep 2006) $
 */
public class JMSReference<T> extends ReferenceExtension {
	
	private JMSBinding jmsBinding; 
	private JMSResourceFactory jmsResourceFactory;
	private OperationSelector operationSelector;
	
    public JMSReference(String name, 
    		            CompositeComponent parent,
			            WireService wireService, 
			            JMSBinding jmsBinding,			            
			            JMSResourceFactory jmsResourceFactory,
			            OperationSelector operationSelector,
			            Class<?> service) {
    	
		super(name, service, parent, wireService);

		this.jmsBinding = jmsBinding;
		this.jmsResourceFactory = jmsResourceFactory;
		this.operationSelector = operationSelector;
	}

    public TargetInvoker createTargetInvoker(ServiceContract contract, Operation operation) {
		try {
			return new JMSTargetInvoker(jmsResourceFactory, jmsBinding, operation.getName(),operationSelector);
		} catch (NamingException e) {
			throw new RuntimeException("Unable to create JMS resources for the invocation",e);
		}
    }
}
