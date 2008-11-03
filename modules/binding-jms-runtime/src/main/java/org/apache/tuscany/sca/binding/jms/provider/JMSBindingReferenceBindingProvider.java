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
import org.apache.tuscany.sca.binding.jms.headers.HeaderReferenceInterceptor;
import org.apache.tuscany.sca.binding.jms.impl.JMSBinding;
import org.apache.tuscany.sca.binding.jms.impl.JMSBindingConstants;
import org.apache.tuscany.sca.binding.jms.impl.JMSBindingException;
import org.apache.tuscany.sca.binding.jms.operationselector.jmsdefault.OperationSelectorJMSDefault;
import org.apache.tuscany.sca.binding.jms.transport.TransportReferenceInterceptor;
import org.apache.tuscany.sca.binding.jms.wireformat.jmsdefault.WireFormatJMSDefault;
import org.apache.tuscany.sca.binding.ws.WebServiceBinding;
import org.apache.tuscany.sca.binding.ws.WebServiceBindingFactory;
import org.apache.tuscany.sca.binding.ws.wsdlgen.BindingWSDLGenerator;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.interfacedef.InterfaceContract;
import org.apache.tuscany.sca.interfacedef.Operation;
import org.apache.tuscany.sca.invocation.InvocationChain;
import org.apache.tuscany.sca.invocation.Invoker;
import org.apache.tuscany.sca.invocation.Phase;
import org.apache.tuscany.sca.provider.OperationSelectorProvider;
import org.apache.tuscany.sca.provider.OperationSelectorProviderFactory;
import org.apache.tuscany.sca.provider.ProviderFactoryExtensionPoint;
import org.apache.tuscany.sca.provider.ReferenceBindingProvider;
import org.apache.tuscany.sca.provider.ReferenceBindingProviderRRB;
import org.apache.tuscany.sca.provider.WireFormatProvider;
import org.apache.tuscany.sca.provider.WireFormatProviderFactory;
import org.apache.tuscany.sca.runtime.RuntimeComponent;
import org.apache.tuscany.sca.runtime.RuntimeComponentReference;
import org.apache.tuscany.sca.runtime.RuntimeWire;

/**
 * Implementation of the JMS reference binding provider.
 * 
 * @version $Rev$ $Date$
 */
public class JMSBindingReferenceBindingProvider implements ReferenceBindingProviderRRB {

    private RuntimeComponentReference reference;
    private JMSBinding jmsBinding;
    private List<JMSBindingInvoker> jmsBindingInvokers = new ArrayList<JMSBindingInvoker>();
    private JMSResourceFactory jmsResourceFactory;
    private RuntimeComponent component;
    private InterfaceContract wsdlInterfaceContract; 
    private ExtensionPointRegistry extensions;
    
    private ProviderFactoryExtensionPoint providerFactories;
       
    private WireFormatProviderFactory requestWireFormatProviderFactory;
    private WireFormatProvider requestWireFormatProvider;
    
    private WireFormatProviderFactory responseWireFormatProviderFactory;
    private WireFormatProvider responseWireFormatProvider;

    public JMSBindingReferenceBindingProvider(RuntimeComponent component, RuntimeComponentReference reference, JMSBinding binding,  ExtensionPointRegistry extensions, JMSResourceFactory jmsResourceFactory) {
        this.reference = reference;
        this.jmsBinding = binding;
        this.extensions = extensions;
        this.component = component;
        this.jmsResourceFactory = jmsResourceFactory;

        if (XMLTextMessageProcessor.class.isAssignableFrom(JMSMessageProcessorUtil.getRequestMessageProcessor(jmsBinding).getClass())) {
            setXMLDataBinding(reference);
        }
        
        // Get the factories/providers for operation selection
        
        // if no operation selector is specified then assume the default
        if (jmsBinding.getOperationSelector() == null){
            jmsBinding.setOperationSelector(new OperationSelectorJMSDefault());
        }
        
        this.providerFactories = extensions.getExtensionPoint(ProviderFactoryExtensionPoint.class);
        
        // Get the factories/providers for wire format
        
        // if no request wire format specified then assume the default
        if (jmsBinding.getRequestWireFormat() == null){
            jmsBinding.setRequestWireFormat(new WireFormatJMSDefault());
        }
        
        // if no response wire format specific then assume the default
        if (jmsBinding.getResponseWireFormat() == null){
            jmsBinding.setResponseWireFormat(new WireFormatJMSDefault());
         }
        
        this.requestWireFormatProviderFactory = 
            (WireFormatProviderFactory)providerFactories.getProviderFactory(jmsBinding.getRequestWireFormat().getClass());
        if (this.requestWireFormatProviderFactory != null){
            this.requestWireFormatProvider = requestWireFormatProviderFactory.createReferenceWireFormatProvider(component, reference, jmsBinding);
        }
        
        this.responseWireFormatProviderFactory = 
            (WireFormatProviderFactory)providerFactories.getProviderFactory(jmsBinding.getResponseWireFormat().getClass());
        if (this.responseWireFormatProvider != null){
            this.responseWireFormatProvider = responseWireFormatProviderFactory.createReferenceWireFormatProvider(component, reference, jmsBinding);
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

        /*
         * TODO turn on RRB version of JMS binding
         */
        Invoker invoker = null;
        invoker = new RRBJMSBindingInvoker(jmsBinding, operation, jmsResourceFactory, reference);
        //invoker = new JMSBindingInvoker(jmsBinding, operation, jmsResourceFactory, reference);
        //jmsBindingInvokers.add((JMSBindingInvoker)invoker);
       
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
    
    /*
     * RRB test methods
     */
    public void configureBindingChain(RuntimeWire runtimeWire) {
        
        InvocationChain bindingChain = runtimeWire.getBindingInvocationChain();
        
        // add transport interceptor
        bindingChain.addInterceptor(Phase.REFERENCE_BINDING_TRANSPORT, 
                                    new TransportReferenceInterceptor(jmsBinding,
                                                                      jmsResourceFactory,
                                                                      runtimeWire) );
        
        // add request wire format 
        bindingChain.addInterceptor(requestWireFormatProvider.getPhase(), 
                                    requestWireFormatProvider.createInterceptor());
        
        // add response wire format, but only add it if it's different from the request
        if (!jmsBinding.getRequestWireFormat().equals(jmsBinding.getResponseWireFormat())){
            bindingChain.addInterceptor(responseWireFormatProvider.getPhase(), 
                                        responseWireFormatProvider.createInterceptor());
        }
        
        // add the header processor that comes after the wire formatter
        bindingChain.addInterceptor(Phase.REFERENCE_BINDING_WIREFORMAT, 
                                    new HeaderReferenceInterceptor(jmsBinding,
                                                                   jmsResourceFactory,
                                                                   runtimeWire) );
    }    

}
