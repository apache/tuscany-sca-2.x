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

import org.apache.tuscany.spi.component.CompositeComponent;
import org.apache.tuscany.spi.extension.ServiceExtension;

/**
 * @version $Rev: 449970 $ $Date: 2006-09-26 06:05:35 -0400 (Tue, 26 Sep 2006) $
 */
public class JMSService extends ServiceExtension {

    private JMSBinding jmsBinding;
    private JMSResourceFactory jmsResourceFactory;
    private MessageConsumer consumer;
    protected OperationAndDataBinding requestOperationAndDataBinding;
    protected OperationAndDataBinding responseOperationAndDataBinding;

    public JMSService(String name,
                      CompositeComponent parent,
                      JMSBinding jmsBinding,
                      JMSResourceFactory jmsResourceFactory,
                      OperationAndDataBinding requestOperationAndDataBinding,
                      OperationAndDataBinding responseOperationAndDataBinding,
                      Class<?> service) {
        super(name, parent);

        this.jmsBinding = jmsBinding;
        this.jmsResourceFactory = jmsResourceFactory;
        this.requestOperationAndDataBinding = requestOperationAndDataBinding;
        this.responseOperationAndDataBinding = responseOperationAndDataBinding;
    }

    public void start() {
        super.start();
        try {
            registerListerner();
        } catch (Exception e) {
            throw new JMSBindingException("Error starting JMSService", e);
        }
    }

    public void stop() {

        try {
            consumer.close();
            jmsResourceFactory.closeConnection();
        } catch (Exception e) {
            throw new JMSBindingException("Error stopping JMSService", e);
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
}
