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

package org.apache.tuscany.sca.binding.jms.provider;

import java.util.ArrayList;
import java.util.List;

import javax.jms.JMSException;

import org.apache.axiom.om.OMElement;
import org.apache.tuscany.sca.binding.jms.impl.JMSBinding;
import org.apache.tuscany.sca.binding.jms.impl.JMSBindingConstants;
import org.apache.tuscany.sca.binding.jms.impl.JMSBindingException;
import org.apache.tuscany.sca.binding.ws.WebServiceBinding;
import org.apache.tuscany.sca.binding.ws.WebServiceBindingFactory;
import org.apache.tuscany.sca.binding.ws.wsdlgen.BindingWSDLGenerator;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.interfacedef.InterfaceContract;
import org.apache.tuscany.sca.interfacedef.Operation;
import org.apache.tuscany.sca.invocation.Invoker;
import org.apache.tuscany.sca.provider.ReferenceBindingProvider;
import org.apache.tuscany.sca.runtime.RuntimeComponent;
import org.apache.tuscany.sca.runtime.RuntimeComponentReference;

/**
 * Implementation of the JMS reference binding provider.
 * 
 * @version $Rev$ $Date$
 */
public class JMSBindingReferenceBindingProvider implements ReferenceBindingProvider {

    private RuntimeComponentReference reference;
    private JMSBinding jmsBinding;
    private List<JMSBindingInvoker> jmsBindingInvokers = new ArrayList<JMSBindingInvoker>();
    private JMSResourceFactory jmsResourceFactory;
    private RuntimeComponent component;
    private InterfaceContract wsdlInterfaceContract; 
    private ExtensionPointRegistry extensions;

    public JMSBindingReferenceBindingProvider(RuntimeComponent component, RuntimeComponentReference reference, JMSBinding binding,  ExtensionPointRegistry extensions) {
        this.reference = reference;
        this.jmsBinding = binding;
        this.extensions = extensions;
        this.component = component;
        jmsResourceFactory = new JMSResourceFactoryImpl(binding.getConnectionFactoryName(), binding.getInitialContextFactoryName(), binding.getJndiURL());

        if (XMLTextMessageProcessor.class.isAssignableFrom(JMSMessageProcessorUtil.getRequestMessageProcessor(jmsBinding).getClass())) {
            setXMLDataBinding(reference);
        }

    }
    
    protected void setXMLDataBinding(RuntimeComponentReference reference) {
        
        WebServiceBindingFactory wsFactory = extensions.getExtensionPoint(WebServiceBindingFactory.class);
        WebServiceBinding wsBinding = wsFactory.createWebServiceBinding();
        BindingWSDLGenerator.generateWSDL(component, reference, wsBinding, extensions, null);
        wsdlInterfaceContract = wsBinding.getBindingInterfaceContract();
        wsdlInterfaceContract.getInterface().resetDataBinding(OMElement.class.getName());
        
        // TODO: TUSCANY-xxx, section 5.2 "Default Data Binding" in the JMS binding spec  
        
//        try {
//            InterfaceContract ic = (InterfaceContract)reference.getInterfaceContract().clone();
//
//            Interface ii = (Interface)ic.getInterface().clone();
//            ii.resetDataBinding("org.apache.axiom.om.OMElement");
//            ic.setInterface(ii);
//            reference.setInterfaceContract(ic);
//
//        } catch (CloneNotSupportedException e) {
//            throw new RuntimeException(e);
//        }
    }

    public Invoker createInvoker(Operation operation) {

        if (jmsBinding.getDestinationName().equals(JMSBindingConstants.DEFAULT_DESTINATION_NAME)) {
            if (!reference.isCallback()) {
                throw new JMSBindingException("No destination specified for reference " + reference.getName());
            }
        }

        JMSBindingInvoker invoker = new JMSBindingInvoker(jmsBinding, operation, jmsResourceFactory, reference);
        jmsBindingInvokers.add(invoker);
        return invoker;
    }

    public boolean supportsOneWayInvocation() {
        return true;
    }

    public InterfaceContract getBindingInterfaceContract() {
        if (wsdlInterfaceContract != null) {
            return wsdlInterfaceContract;
        } else {
            if (reference.getInterfaceContract() == null) {
                return reference.getReference().getInterfaceContract();
            } else {
                return reference.getInterfaceContract();
            }
        }
    }

    public void start() {

    }

    public void stop() {
        try {
            jmsResourceFactory.closeConnection();
        } catch (JMSException e) {
            throw new JMSBindingException(e);
        }
    }

}
