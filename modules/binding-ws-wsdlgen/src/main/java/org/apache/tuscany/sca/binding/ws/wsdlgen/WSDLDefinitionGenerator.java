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

package org.apache.tuscany.sca.binding.ws.wsdlgen;

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
import javax.wsdl.Types;
import javax.wsdl.WSDLException;
import javax.wsdl.extensions.ExtensibilityElement;
import javax.wsdl.extensions.soap.SOAPAddress;
import javax.wsdl.extensions.soap.SOAPBinding;
import javax.wsdl.extensions.soap.SOAPBody;
import javax.wsdl.extensions.soap.SOAPFault;
import javax.wsdl.extensions.soap.SOAPOperation;
import javax.wsdl.extensions.soap12.SOAP12Address;
import javax.wsdl.extensions.soap12.SOAP12Binding;
import javax.wsdl.extensions.soap12.SOAP12Body;
import javax.wsdl.extensions.soap12.SOAP12Fault;
import javax.wsdl.extensions.soap12.SOAP12Operation;
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
    public static final QName SOAP_ADDRESS = new QName(SOAP_NS, "address");
    private static final QName SOAP_BINDING = new QName(SOAP_NS, "binding");
    private static final QName SOAP_BODY = new QName(SOAP_NS, "body");
    private static final QName SOAP_FAULT = new QName(SOAP_NS, "fault");
    private static final QName SOAP_OPERATION = new QName(SOAP_NS, "operation");
    private static final String SOAP12_NS = "http://schemas.xmlsoap.org/wsdl/soap12/";
    public static final QName SOAP12_ADDRESS = new QName(SOAP12_NS, "address");
    private static final QName SOAP12_BINDING = new QName(SOAP12_NS, "binding");
    private static final QName SOAP12_BODY = new QName(SOAP12_NS, "body");
    private static final QName SOAP12_FAULT = new QName(SOAP12_NS, "fault");
    private static final QName SOAP12_OPERATION = new QName(SOAP12_NS, "operation");

    private static final String BINDING_SUFFIX = "Binding";
    private static final String SERVICE_SUFFIX = "Service";
    private static final String PORT_SUFFIX = "Port";

    private boolean requiresSOAP12;
    private QName soapAddress;
    private QName soapBinding;
    private QName soapBody;
    private QName soapFault;
    private QName soapOperation;

    public WSDLDefinitionGenerator(boolean requiresSOAP12) {
        super();
        this.requiresSOAP12 = requiresSOAP12;
        soapAddress = requiresSOAP12 ? SOAP12_ADDRESS : SOAP_ADDRESS;
        soapBinding = requiresSOAP12 ? SOAP12_BINDING : SOAP_BINDING;
        soapBody = requiresSOAP12 ? SOAP12_BODY : SOAP_BODY;
        soapFault = requiresSOAP12 ? SOAP12_FAULT : SOAP_FAULT;
        soapOperation = requiresSOAP12 ? SOAP12_OPERATION : SOAP_OPERATION;
    }

    public Definition cloneDefinition(WSDLFactory factory, Definition definition) throws WSDLException {
        Element root = definition.getDocumentationElement();
        root = (Element)root.cloneNode(true);
        WSDLReader reader = factory.newWSDLReader();
        return reader.readWSDL(definition.getDocumentBaseURI(), root);
    }
    
    public Types createTypes(Definition definition) {
        Types types = definition.createTypes();
        definition.setTypes(types);
        return types;
    }

    public Binding createBinding(Definition definition, PortType portType) {
        try {
            Binding binding = definition.createBinding();
            binding.setPortType(portType);
            configureBinding(definition, binding, portType);
            ExtensibilityElement bindingExtension =
                definition.getExtensionRegistry().createExtension(Binding.class, soapBinding);
            if (requiresSOAP12) {
                ((SOAP12Binding)bindingExtension).setStyle("document");
                ((SOAP12Binding)bindingExtension).setTransportURI("http://schemas.xmlsoap.org/soap/http");
            } else {
                ((SOAPBinding)bindingExtension).setStyle("document");
                ((SOAPBinding)bindingExtension).setTransportURI("http://schemas.xmlsoap.org/soap/http");
            }
            binding.addExtensibilityElement(bindingExtension);
            return binding;
        } catch (WSDLException e) {
            throw new WSDLGenerationException(e);
        }
    }

    protected void configureBinding(Definition definition, Binding binding, PortType portType) throws WSDLException {
        QName portTypeName = portType.getQName();
        if (portTypeName != null) {
            // Choose <porttype>Binding if available.  If this name is in use, insert
            // separating underscores until there is no clash.
            for (String suffix = BINDING_SUFFIX; ; suffix = "_" + suffix) { 
                QName name = new QName(definition.getTargetNamespace(), portTypeName.getLocalPart() + suffix);
                if (definition.getBinding(name) == null) {
                    binding.setQName(name);
                    break;
                }
            }
        }
    }

    @SuppressWarnings("unchecked")
    public void createBindingOperations(Definition definition, Binding binding, PortType portType) {
        try {
            for (Iterator oi = portType.getOperations().iterator(); oi.hasNext();) {
                Operation operation = (Operation)oi.next();
                BindingOperation bindingOperation =
                    createBindingOperation(definition, operation, "urn:" + operation.getName());
                binding.addBindingOperation(bindingOperation);
            }
        } catch (WSDLException e) {
            throw new WSDLGenerationException(e);
        }
    }

    @SuppressWarnings("unchecked")
    public BindingOperation createBindingOperation(Definition definition, Operation operation, String action)
        throws WSDLException {
        BindingOperation bindingOperation = definition.createBindingOperation();
        bindingOperation.setOperation(operation);
        configureBindingOperation(bindingOperation, operation);
        ExtensibilityElement operationExtension =
            definition.getExtensionRegistry().createExtension(BindingOperation.class, soapOperation);
        if (requiresSOAP12) {
            ((SOAP12Operation)operationExtension).setSoapActionURI(action);
        } else {
            ((SOAPOperation)operationExtension).setSoapActionURI(action);
        }
        bindingOperation.addExtensibilityElement(operationExtension);
        if (operation.getInput() != null) {
            BindingInput bindingInput = definition.createBindingInput();
            configureBindingInput(bindingInput, operation.getInput());
            ExtensibilityElement inputExtension =
                definition.getExtensionRegistry().createExtension(BindingInput.class, soapBody);
            if (requiresSOAP12) {
                ((SOAP12Body)inputExtension).setUse("literal");
            } else {
                ((SOAPBody)inputExtension).setUse("literal");
            }
            bindingInput.addExtensibilityElement(inputExtension);
            bindingOperation.setBindingInput(bindingInput);
        }
        if (operation.getOutput() != null) {
            BindingOutput bindingOutput = definition.createBindingOutput();
            configureBindingOutput(bindingOutput, operation.getOutput());
            ExtensibilityElement outputExtension =
                definition.getExtensionRegistry().createExtension(BindingOutput.class, soapBody);
            if (requiresSOAP12) {
                ((SOAP12Body)outputExtension).setUse("literal");
            } else {
                ((SOAPBody)outputExtension).setUse("literal");
            }
            bindingOutput.addExtensibilityElement(outputExtension);
            bindingOperation.setBindingOutput(bindingOutput);
        }
        for (Iterator fi = operation.getFaults().values().iterator(); fi.hasNext();) {
            Fault fault = (Fault)fi.next();
            BindingFault bindingFault = definition.createBindingFault();
            ExtensibilityElement faultExtension =
                definition.getExtensionRegistry().createExtension(BindingFault.class, soapFault);
            configureBindingFault(bindingFault, faultExtension, fault);
            bindingFault.addExtensibilityElement(faultExtension);
            bindingOperation.addBindingFault(bindingFault);
        }
        return bindingOperation;
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

    protected void configureBindingFault(BindingFault bindingFault,
                                         ExtensibilityElement faultExtension,
                                         Fault fault)
                                     throws WSDLException {
        String faultName = fault.getName();
        bindingFault.setName(faultName);
        if (requiresSOAP12) {
            ((SOAP12Fault)faultExtension).setName(faultName);
            ((SOAP12Fault)faultExtension).setUse("literal");
        } else {
            ((SOAPFault)faultExtension).setName(faultName);
            ((SOAPFault)faultExtension).setUse("literal");
        }
    }

    public Service createService(Definition definition, PortType portType) {
        try {
            Service service = definition.createService();
            configureService(definition, service, portType);
            // createPort(definition, binding, service);
            definition.addService(service);
            return service;
        } catch (WSDLException e) {
            throw new WSDLGenerationException(e);
        }
    }

    public Service createService(Definition definition, Binding binding) {
        try {
            Service service = definition.createService();
            configureService(definition, service, binding.getPortType());
            // createPort(definition, binding, service);
            definition.addService(service);
            return service;
        } catch (WSDLException e) {
            throw new WSDLGenerationException(e);
        }
    }

    protected void configureService(Definition definition, Service service, PortType portType) throws WSDLException {
        QName portTypeName = portType.getQName();
        if (portTypeName != null) {
            // Choose <porttype>Service if available.  If this name is in use, insert
            // separating underscores until there is no clash.
            for (String suffix = SERVICE_SUFFIX; ; suffix = "_" + suffix) {
                QName name = new QName(definition.getTargetNamespace(), portTypeName.getLocalPart() + suffix);
                if (definition.getService(name) == null) {
                    service.setQName(name);
                    break;
                }
            }
        }
    }

    public Port createPort(Definition definition, Binding binding, Service service, String uri) {
        try {
            Port port = definition.createPort();
            port.setBinding(binding);
            configurePort(port, binding);
            if (uri != null) {
                ExtensibilityElement portExtension =
                    definition.getExtensionRegistry().createExtension(Port.class, soapAddress);
                if (requiresSOAP12) {
                    ((SOAP12Address)portExtension).setLocationURI(uri);
                } else {
                    ((SOAPAddress)portExtension).setLocationURI(uri);
                }
                port.addExtensibilityElement(portExtension);
            }
            service.addPort(port);
            return port;
        } catch (WSDLException e) {
            throw new WSDLGenerationException(e);
        }
    }

    protected void configurePort(Port port, Binding binding) throws WSDLException {
        if (binding.getPortType() != null && binding.getPortType().getQName() != null) {
            port.setName(binding.getPortType().getQName().getLocalPart() + PORT_SUFFIX);
        }
    }

}
