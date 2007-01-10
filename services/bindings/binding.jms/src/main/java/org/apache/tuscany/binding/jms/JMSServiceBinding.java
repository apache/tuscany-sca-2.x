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
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.Session;
import javax.naming.NamingException;
import javax.xml.namespace.QName;

import static org.osoa.sca.Version.XML_NAMESPACE_1_0;

import org.apache.tuscany.spi.component.CompositeComponent;
import org.apache.tuscany.spi.extension.ServiceBindingExtension;
import org.apache.tuscany.spi.model.ServiceContract;

/**
 * @version $Rev: 449970 $ $Date: 2006-09-26 06:05:35 -0400 (Tue, 26 Sep 2006) $
 */
public class JMSServiceBinding extends ServiceBindingExtension {
    private static final QName BINDING_JMS = new QName(XML_NAMESPACE_1_0, "binding.jms");

    private JMSBindingDefinition jmsBinding;
    private JMSResourceFactory jmsResourceFactory;
    private MessageConsumer consumer;
    protected OperationAndDataBinding requestOperationAndDataBinding;
    protected OperationAndDataBinding responseOperationAndDataBinding;

    public JMSServiceBinding(String name,
                      CompositeComponent parent,
                      JMSBindingDefinition jmsBinding,
                      JMSResourceFactory jmsResourceFactory,
                      ServiceContract<?> serviceBindingContract,
                      OperationAndDataBinding requestOperationAndDataBinding,
                      OperationAndDataBinding responseOperationAndDataBinding,
                      Class<?> service) {
        super(name, parent);

        this.jmsBinding = jmsBinding;
        this.jmsResourceFactory = jmsResourceFactory;
        this.bindingServiceContract = serviceBindingContract;
        this.requestOperationAndDataBinding = requestOperationAndDataBinding;
        this.responseOperationAndDataBinding = responseOperationAndDataBinding;
    }

    public void start() {
        super.start();
        try {
            registerListerner();
        } catch (Exception e) {
            throw new JMSBindingException("Error starting JMSServiceBinding", e);
        }
    }

    public void stop() {

        try {
            consumer.close();
            jmsResourceFactory.closeConnection();
        } catch (Exception e) {
            throw new JMSBindingException("Error stopping JMSServiceBinding", e);
        }

        super.stop();
    }

    private void registerListerner() throws NamingException, JMSException {

        Session session = jmsResourceFactory.createSession();
        Destination destination = session.createQueue(jmsBinding.getDestinationName());

        consumer = session.createConsumer(destination);
        consumer.setMessageListener(new JMSProxy(getInboundWire(), jmsResourceFactory, requestOperationAndDataBinding,
                                                 responseOperationAndDataBinding, jmsBinding.getCorrelationScheme()));

        jmsResourceFactory.startConnection();

    }

    public QName getBindingType() {
        return BINDING_JMS;
    }
}
