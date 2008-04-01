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

package org.apache.tuscany.sca.interfacedef.wsdl.interface2wsdl;

import java.util.Iterator;

import javax.wsdl.Binding;
import javax.wsdl.BindingFault;
import javax.wsdl.BindingInput;
import javax.wsdl.BindingOperation;
import javax.wsdl.BindingOutput;
import javax.wsdl.Definition;
import javax.wsdl.Fault;
import javax.wsdl.Input;
import javax.wsdl.Operation;
import javax.wsdl.Output;
import javax.wsdl.Port;
import javax.wsdl.PortType;
import javax.wsdl.Service;
import javax.wsdl.WSDLException;
import javax.wsdl.extensions.soap.SOAPBinding;
import javax.wsdl.extensions.soap.SOAPBody;
import javax.wsdl.extensions.soap.SOAPOperation;
import javax.wsdl.factory.WSDLFactory;
import javax.wsdl.xml.WSDLReader;
import javax.xml.namespace.QName;

import org.w3c.dom.Element;

/**
 * 
 * @version $Rev$ $Date$
 */
public class WSDLDefinitionGenerator {
    private static final String SOAP_NS = "http://schemas.xmlsoap.org/wsdl/soap/";
    private static final QName SOAP_ADDRESS = new QName(SOAP_NS, "address");
    private static final QName SOAP_BINDING = new QName(SOAP_NS, "binding");
    private static final QName SOAP_BODY = new QName(SOAP_NS, "body");
    private static final QName SOAP_OPERATION = new QName(SOAP_NS, "operation");

    private static final String BINDING_SUFFIX = "__SOAPBinding";
    private static final String SERVICE_SUFFIX = "__Service";
    private static final String PORT_SUFFIX = "__SOAPHTTPPort";

    public Definition cloneDefinition(WSDLFactory factory, Definition definition) throws WSDLException {
        Element root = definition.getDocumentationElement();
        root = (Element)root.cloneNode(true);
        WSDLReader reader = factory.newWSDLReader();
        return reader.readWSDL(definition.getDocumentBaseURI(), root);
    }

    public Binding createBinding(Definition definition, PortType portType) throws WSDLException {
        Binding binding = definition.createBinding();
        binding.setPortType(portType);
        configureBinding(binding, portType);
        SOAPBinding soapBinding =
            (SOAPBinding)definition.getExtensionRegistry().createExtension(Binding.class, SOAP_BINDING);
        soapBinding.setStyle("document");
        soapBinding.setTransportURI("http://schemas.xmlsoap.org/soap/http");
        binding.addExtensibilityElement(soapBinding);

        createBindingOperations(definition, binding, portType);
        binding.setUndefined(false);
        definition.addBinding(binding);
        return binding;
    }

    protected void configureBinding(Binding binding, PortType portType) throws WSDLException {
        QName portTypeName = portType.getQName();
        if (portTypeName != null) {
            binding.setQName(new QName(portTypeName.getNamespaceURI(), portTypeName.getLocalPart() + BINDING_SUFFIX));
        }
    }

    @SuppressWarnings("unchecked")
    protected void createBindingOperations(Definition definition, Binding binding, PortType portType)
        throws WSDLException {
        for (Iterator oi = portType.getOperations().iterator(); oi.hasNext();) {
            Operation operation = (Operation)oi.next();
            BindingOperation bindingOperation = definition.createBindingOperation();
            bindingOperation.setOperation(operation);
            configureBindingOperation(bindingOperation, operation);
            SOAPOperation soapOperation =
                (SOAPOperation)definition.getExtensionRegistry().createExtension(BindingOperation.class, SOAP_OPERATION);
            soapOperation.setSoapActionURI("");
            bindingOperation.addExtensibilityElement(soapOperation);
            if (operation.getInput() != null) {
                BindingInput bindingInput = definition.createBindingInput();
                configureBindingInput(bindingInput, operation.getInput());
                SOAPBody soapBody =
                    (SOAPBody)definition.getExtensionRegistry().createExtension(BindingInput.class, SOAP_BODY);
                soapBody.setUse("literal");
                bindingInput.addExtensibilityElement(soapBody);
                bindingOperation.setBindingInput(bindingInput);
            }
            if (operation.getOutput() != null) {
                BindingOutput bindingOutput = definition.createBindingOutput();
                configureBindingOutput(bindingOutput, operation.getOutput());
                SOAPBody soapBody =
                    (SOAPBody)definition.getExtensionRegistry().createExtension(BindingOutput.class, SOAP_BODY);
                soapBody.setUse("literal");
                bindingOutput.addExtensibilityElement(soapBody);
                bindingOperation.setBindingOutput(bindingOutput);
            }
            for (Iterator fi = operation.getFaults().values().iterator(); fi.hasNext();) {
                Fault fault = (Fault)fi.next();
                BindingFault bindingFault = definition.createBindingFault();
                configureBindingFault(bindingFault, fault);
                bindingOperation.addBindingFault(bindingFault);
            }
            binding.addBindingOperation(bindingOperation);
        }
    }

    protected void configureBindingOperation(BindingOperation bindingOperation, Operation operation)
        throws WSDLException {
        bindingOperation.setName(operation.getName());
    }

    protected void configureBindingInput(BindingInput bindingInput, Input input) throws WSDLException {
        bindingInput.setName(input.getName());
    }

    protected void configureBindingOutput(BindingOutput bindingOutput, Output output) throws WSDLException {
        bindingOutput.setName(output.getName());
    }

    protected void configureBindingFault(BindingFault bindingFault, Fault fault) throws WSDLException {
        bindingFault.setName(fault.getName());
    }

    public Service createService(Definition definition, PortType portType) {
        try {
            Service service = definition.createService();
            configureService(service, portType);
            Binding binding = createBinding(definition, portType);
            createPort(definition, binding, service);
            definition.addService(service);
            return service;
        } catch (WSDLException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public Service createService(Definition definition, Binding binding) {
        try {
            Service service = definition.createService();
            configureService(service, binding.getPortType());
            createPort(definition, binding, service);
            definition.addService(service);
            return service;
        } catch (WSDLException e) {
            throw new IllegalArgumentException(e);
        }
    }

    protected void configureService(Service service, PortType portType) throws WSDLException {
        QName portTypeName = portType.getQName();
        if (portTypeName != null) {
            service.setQName(new QName(portTypeName.getNamespaceURI(), portTypeName.getLocalPart() + SERVICE_SUFFIX));
        }
    }

    protected Port createPort(Definition definition, Binding binding, Service service) throws WSDLException {
        Port port = definition.createPort();
        port.setBinding(binding);
        configurePort(definition, port, binding);
        /*
        ExtensibilityElement soapAddress =
            definition.getExtensionRegistry().createExtension(Port.class, SOAP_ADDRESS);
        ((SOAPAddress)soapAddress).setLocationURI("");
        port.addExtensibilityElement(soapAddress);
        */
        service.addPort(port);
        return port;
    }

    protected void configurePort(Definition definition, Port port, Binding binding) throws WSDLException {
        if (binding.getPortType() != null && binding.getPortType().getQName() != null) {
            port.setName(binding.getPortType().getQName().getLocalPart() + PORT_SUFFIX);
        }
    }

}
