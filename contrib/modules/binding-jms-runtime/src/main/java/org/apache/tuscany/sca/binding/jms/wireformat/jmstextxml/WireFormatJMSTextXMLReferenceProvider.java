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

package org.apache.tuscany.sca.binding.jms.wireformat.jmstextxml;

import java.util.List;

import org.apache.axiom.om.OMElement;
import org.apache.tuscany.sca.assembly.Binding;
import org.apache.tuscany.sca.binding.jms.impl.JMSBinding;
import org.apache.tuscany.sca.binding.jms.impl.JMSBindingConstants;
import org.apache.tuscany.sca.binding.ws.WebServiceBinding;
import org.apache.tuscany.sca.binding.ws.WebServiceBindingFactory;
import org.apache.tuscany.sca.binding.ws.wsdlgen.BindingWSDLGenerator;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.interfacedef.InterfaceContract;
import org.apache.tuscany.sca.interfacedef.Operation;
import org.apache.tuscany.sca.invocation.Interceptor;
import org.apache.tuscany.sca.invocation.Phase;
import org.apache.tuscany.sca.policy.PolicySet;
import org.apache.tuscany.sca.policy.PolicySetAttachPoint;
import org.apache.tuscany.sca.policy.util.PolicyHandler;
import org.apache.tuscany.sca.provider.PolicyProvider;
import org.apache.tuscany.sca.provider.WireFormatProvider;
import org.apache.tuscany.sca.runtime.RuntimeComponent;
import org.apache.tuscany.sca.runtime.RuntimeComponentReference;

/**
 * @version $Rev$ $Date$
 */
public class WireFormatJMSTextXMLReferenceProvider implements WireFormatProvider {
    private ExtensionPointRegistry registry;
    private RuntimeComponent component;
    private RuntimeComponentReference reference;
    private JMSBinding binding;
    private InterfaceContract interfaceContract; 

    public WireFormatJMSTextXMLReferenceProvider(ExtensionPointRegistry registry,
                                                 RuntimeComponent component,
                                                 RuntimeComponentReference reference,
                                                 Binding binding) {
        super();
        this.registry = registry;
        this.component = component;
        this.reference = reference;
        this.binding = (JMSBinding)binding;
        
        // configure the reference based on this wire format
        
        // currently maintaining the message processor structure which 
        // contains the details of jms message processing so set the message
        // type here if not set explicitly in SCDL
        if (this.binding.getRequestMessageProcessorName().equals(JMSBindingConstants.XML_MP_CLASSNAME) ){
            this.binding.setRequestMessageProcessorName(JMSBindingConstants.XML_MP_CLASSNAME);
            this.binding.setResponseMessageProcessorName(JMSBindingConstants.XML_MP_CLASSNAME);
        }
        
        // set the binding interface contract to represent the WSDL for the 
        // xml messages that will be sent
        if (reference.getInterfaceContract() != null &&
            !isOnMessage()) {
            WebServiceBindingFactory wsFactory = registry.getExtensionPoint(WebServiceBindingFactory.class);
            WebServiceBinding wsBinding = wsFactory.createWebServiceBinding();
            BindingWSDLGenerator.generateWSDL(component, reference, wsBinding, registry, null);
            interfaceContract = wsBinding.getBindingInterfaceContract();
            interfaceContract.getInterface().resetDataBinding(OMElement.class.getName());  
        } else {
            interfaceContract = reference.getInterfaceContract();
        }
    }
    
    protected boolean isOnMessage() {
        InterfaceContract ic = reference.getInterfaceContract();
        if (ic.getInterface().getOperations().size() != 1) {
            return false;
        }
        return "onMessage".equals(ic.getInterface().getOperations().get(0).getName());
    }
    
    public InterfaceContract getWireFormatInterfaceContract() {
        return interfaceContract;
    }
    
    public Interceptor createInterceptor() {
        return new WireFormatJMSTextXMLReferenceInterceptor((JMSBinding)binding, 
                                                            null, 
                                                            reference.getRuntimeWire(binding));
    }

    public String getPhase() {
        return Phase.REFERENCE_BINDING_WIREFORMAT;
    }

}
