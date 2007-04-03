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

import javax.jms.Destination;
import javax.xml.namespace.QName;

import static org.osoa.sca.Version.XML_NAMESPACE_1_0;

import org.apache.tuscany.spi.component.CompositeComponent;
import org.apache.tuscany.spi.extension.ReferenceBindingExtension;
import org.apache.tuscany.spi.model.Operation;
import org.apache.tuscany.spi.model.ServiceContract;
import org.apache.tuscany.spi.wire.TargetInvoker;

/**
 * @version $Rev: 449970 $ $Date: 2006-09-26 06:05:35 -0400 (Tue, 26 Sep 2006) $
 */
public class JMSReferenceBinding extends ReferenceBindingExtension {
    private static final QName BINDING_JMS = new QName(XML_NAMESPACE_1_0, "binding.jms");

    protected JMSBindingDefinition jmsBinding;
    protected JMSResourceFactory jmsResourceFactory;
    protected OperationAndDataBinding requestOperationAndDataBinding;
    protected OperationAndDataBinding responseOperationAndDataBinding;
    protected Destination requestDest;
    protected Destination replyDest;

    public JMSReferenceBinding(String name,
                        CompositeComponent parent,
                        JMSBindingDefinition jmsBinding,
                        JMSResourceFactory jmsResourceFactory,
                        ServiceContract<?> bindingServiceContract,
                        OperationAndDataBinding requestOperationAndDataBinding,
                        OperationAndDataBinding responseOperationAndDataBinding,
                        Destination requestDest,
                        Destination replyDest) {

        super(name, parent);
        this.bindingServiceContract = bindingServiceContract;
        this.jmsBinding = jmsBinding;
        this.jmsResourceFactory = jmsResourceFactory;
        this.requestOperationAndDataBinding = requestOperationAndDataBinding;
        this.responseOperationAndDataBinding = responseOperationAndDataBinding;
        this.requestDest = requestDest;
        this.replyDest = replyDest;
    }

    public QName getBindingType() {
        return BINDING_JMS;
    }

    public TargetInvoker createTargetInvoker(ServiceContract contract, Operation operation) {
        return new JMSTargetInvoker(jmsResourceFactory, jmsBinding, operation.getName(),
                                    requestOperationAndDataBinding, responseOperationAndDataBinding, requestDest,
                                    replyDest);
    }

}
