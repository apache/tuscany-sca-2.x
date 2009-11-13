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

package org.apache.tuscany.sca.binding.jms.wireformat.jmsdefault.runtime;

import java.util.HashMap;
import java.util.List;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.tuscany.sca.assembly.Binding;
import org.apache.tuscany.sca.binding.jms.JMSBinding;
import org.apache.tuscany.sca.binding.jms.JMSBindingConstants;
import org.apache.tuscany.sca.binding.jms.provider.JMSResourceFactory;
import org.apache.tuscany.sca.binding.jms.wireformat.WireFormatJMSDefault;
import org.apache.tuscany.sca.binding.ws.WebServiceBinding;
import org.apache.tuscany.sca.binding.ws.WebServiceBindingFactory;
import org.apache.tuscany.sca.binding.ws.wsdlgen.BindingWSDLGenerator;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.interfacedef.DataType;
import org.apache.tuscany.sca.interfacedef.InterfaceContract;
import org.apache.tuscany.sca.interfacedef.Operation;
import org.apache.tuscany.sca.interfacedef.util.ElementInfo;
import org.apache.tuscany.sca.invocation.Interceptor;
import org.apache.tuscany.sca.invocation.Phase;
import org.apache.tuscany.sca.provider.WireFormatProvider;
import org.apache.tuscany.sca.runtime.RuntimeComponent;
import org.apache.tuscany.sca.runtime.RuntimeComponentService;

/**
 * @version $Rev$ $Date$
 */
public class WireFormatJMSDefaultServiceProvider implements WireFormatProvider {
    private ExtensionPointRegistry registry;
    private RuntimeComponent component;
    private RuntimeComponentService service;
    private JMSBinding binding;
    private JMSResourceFactory jmsResourceFactory;
    private InterfaceContract interfaceContract;
    private HashMap<String, OMElement> inputWrapperMap;
    private HashMap<String, Boolean> outputWrapperMap;

    public WireFormatJMSDefaultServiceProvider(ExtensionPointRegistry registry, RuntimeComponent component, RuntimeComponentService service, Binding binding, JMSResourceFactory jmsResourceFactory) {
        super();
        this.component = component;
        this.service = service;
        this.binding = (JMSBinding) binding;
        this.jmsResourceFactory = jmsResourceFactory;

        this.inputWrapperMap = new HashMap<String, OMElement>();
        this.outputWrapperMap = new HashMap<String, Boolean>();

        // configure the service based on this wire format

        // currently maintaining the message processor structure which
        // contains the details of jms message processing so set the message
        // type here if not set explicitly in SCDL
        if (this.binding.getRequestWireFormat() instanceof WireFormatJMSDefault){
            this.binding.setRequestMessageProcessorName(JMSBindingConstants.DEFAULT_MP_CLASSNAME);
        }
        if (this.binding.getResponseWireFormat() instanceof WireFormatJMSDefault){
            this.binding.setResponseMessageProcessorName(JMSBindingConstants.DEFAULT_MP_CLASSNAME);
        }

        List<Operation> opList = service.getService().getInterfaceContract().getInterface().getOperations();

        // Go through each operation and add wrapper info
        OMFactory factory = OMAbstractFactory.getOMFactory();

        // set the binding interface contract to represent the WSDL for the
        // xml messages that will be sent

        // I think we have to check for asIs because the Java2WSDL will blow up when using javax.jms.Message
        if (service.getInterfaceContract() != null && !isAsIs()) {
            WebServiceBindingFactory wsFactory = registry.getExtensionPoint(WebServiceBindingFactory.class);
            WebServiceBinding wsBinding = wsFactory.createWebServiceBinding();
            BindingWSDLGenerator.generateWSDL(component, service, wsBinding, registry, null);
            interfaceContract = wsBinding.getBindingInterfaceContract();
            interfaceContract.getInterface().resetDataBinding(OMElement.class.getName());

            List<Operation> wsdlOpList = interfaceContract.getInterface().getOperations();

            for (Operation op : opList) {
                String name = op.getName();

                Operation matchingWsdlOp = null;

                // find the matching wsdlop
                for (Operation wsdlOp : wsdlOpList) {
                    if (name.equals(wsdlOp.getName())) {
                        matchingWsdlOp = wsdlOp;
                        break;
                    }
                }

                // only add operations that need to be wrapped/unwrapped

                // TODO - not sure we really support viewing the input/output as separately wrapped 
                // like the separate code paths imply.  Not sure how many @OneWay tests we have, this might 
                // not be an issue.  
                if (matchingWsdlOp.isWrapperStyle()) {
                    if (op.getInputType().getLogical().size() == 1) {
                        // we only need to know what the wrapper is on the deserialization
                        // might need to change this when the input/output wrapper style is different
                        ElementInfo ei = op.getWrapper().getInputWrapperElement();
                        String namespace = ei.getQName().getNamespaceURI();
                        String opName = ei.getQName().getLocalPart();
                        OMNamespace ns = factory.createOMNamespace(namespace, "ns1");
                        OMElement wrapper = factory.createOMElement(opName, ns);
                        this.inputWrapperMap.put(name, wrapper);
                    }
                }

                if (matchingWsdlOp.isWrapperStyle()) {
                    this.outputWrapperMap.put(name, true);
                } else {
                    this.outputWrapperMap.put(name, false);
                }

            }

        } else {            
            interfaceContract = service.getService().getInterfaceContract();
        }
    }

    protected boolean isAsIs() {
        InterfaceContract ic = service.getInterfaceContract();
        if (ic.getInterface().getOperations().size() != 1) {
            return false;
        }

        List<DataType> inputDataTypes = ic.getInterface().getOperations().get(0).getInputType().getLogical();

        if (inputDataTypes.size() != 1) {
            return false;
        }

        Class<?> inputType = inputDataTypes.get(0).getPhysical();

        if (javax.jms.Message.class.isAssignableFrom(inputType)) {
            return true;
        }
        return false;
    }

    public InterfaceContract configureWireFormatInterfaceContract(InterfaceContract interfaceContract){
        
        if (this.interfaceContract != null &&
            !isAsIs()) {
            if (this.binding.getRequestWireFormat() instanceof WireFormatJMSDefault){
                // set the request data transformation
                interfaceContract.getInterface().resetInterfaceInputTypes(this.interfaceContract.getInterface());
            }
            if (this.binding.getResponseWireFormat() instanceof WireFormatJMSDefault){
                // set the response data transformation
                interfaceContract.getInterface().resetInterfaceOutputTypes(this.interfaceContract.getInterface());
            }
        }
        
        return interfaceContract;
    }    


    public Interceptor createInterceptor() {
        return new WireFormatJMSDefaultServiceInterceptor(registry, binding, jmsResourceFactory, service.getRuntimeWire(binding), this.inputWrapperMap, this.outputWrapperMap);
    }

    public String getPhase() {
        return Phase.SERVICE_BINDING_WIREFORMAT;
    }

    public InterfaceContract getWireFormatInterfaceContract() {
        return interfaceContract;
    }
}
