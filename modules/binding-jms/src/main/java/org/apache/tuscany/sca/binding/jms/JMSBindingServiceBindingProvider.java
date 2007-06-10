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

package org.apache.tuscany.sca.binding.jms;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.Session;
import javax.naming.NamingException;

import org.apache.tuscany.sca.interfacedef.Interface;
import org.apache.tuscany.sca.interfacedef.InterfaceContract;
import org.apache.tuscany.sca.interfacedef.java.JavaInterface;
import org.apache.tuscany.sca.provider.ServiceBindingProvider;
import org.apache.tuscany.sca.runtime.RuntimeComponent;
import org.apache.tuscany.sca.runtime.RuntimeComponentService;

/**
 * Implementation of the JMS service binding provider.
 * 
 * @version $Rev$ $Date$
 */
public class JMSBindingServiceBindingProvider implements ServiceBindingProvider {


    private RuntimeComponent        component;
    private RuntimeComponentService service;
    private JMSBinding              jmsBinding;
    private JMSResourceFactory      jmsResourceFactory; 
    private MessageConsumer         consumer;

    public JMSBindingServiceBindingProvider(RuntimeComponent component,
                                            RuntimeComponentService service,
                                            JMSBinding binding) {
        this.component     = component;
        this.service       = service;
        this.jmsBinding    = binding;
        
        jmsResourceFactory = jmsBinding.getJmsResourceFactory();   
        
        // if the default destination queue names is set
        // set the destinate queue name to the reference name
        // so that any wires can be assured a unique endpoint.
        if (jmsBinding.getDestinationName().equals(JMSBindingConstants.DEFAULT_DESTINATION_NAME)){
            //jmsBinding.setDestinationName(service.getName());
            throw new JMSBindingException("No destination specified for service " +
                                          service.getName());
        }

    }

    public InterfaceContract getBindingInterfaceContract() {
        return service.getInterfaceContract();
    }

    public void start() {

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
    }
    
    private void registerListerner() throws NamingException, JMSException {

        Session session         = jmsResourceFactory.createSession();
        Destination destination = jmsResourceFactory.lookupDestination(jmsBinding.getDestinationName());
        
        if (destination == null){ 
            if (jmsBinding.getDestinationCreate().equals(JMSBindingConstants.CREATE_ALLWAYS)) {
                destination = jmsResourceFactory.createDestination(jmsBinding.getDestinationName());
            } else {
                throw new JMSBindingException("JMS Destination " + 
                                              jmsBinding.getDestinationName() +
                                              "not found while registering service " + 
                                              service.getName() +
                                              " listener");
            }
        }
        
        consumer = session.createConsumer(destination);
        
        // TODO - We assume the target is a Java class here!!!
        //Class<?> aClass = getTargetJavaClass(getBindingInterfaceContract().getInterface());
       // Object instance = component.createSelfReference(aClass).getService();

        consumer.setMessageListener(new JMSBindingListener(jmsBinding, jmsResourceFactory, service));

        jmsResourceFactory.startConnection();

    }    

    private Class<?> getTargetJavaClass(Interface targetInterface) {
        // TODO: right now assume that the target is always a Java
        // Implementation. Need to figure out
        // how to generate Java Interface in cases where the target is not a
        // Java Implementation
        return ((JavaInterface)targetInterface).getJavaClass();
    }
}
