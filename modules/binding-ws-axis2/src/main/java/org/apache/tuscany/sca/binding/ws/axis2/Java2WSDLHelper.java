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
package org.apache.tuscany.sca.binding.ws.axis2;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.List;
import java.util.Map;

import javax.wsdl.Binding;
import javax.wsdl.BindingOperation;
import javax.wsdl.BindingOutput;
import javax.wsdl.Definition;
import javax.wsdl.Input;
import javax.wsdl.Message;
import javax.wsdl.OperationType;
import javax.wsdl.Output;
import javax.wsdl.Part;
import javax.wsdl.Port;
import javax.wsdl.PortType;
import javax.wsdl.Service;
import javax.wsdl.Types;
import javax.wsdl.WSDLException;
import javax.wsdl.extensions.ExtensibilityElement;
import javax.wsdl.extensions.schema.Schema;
import javax.wsdl.xml.WSDLLocator;
import javax.wsdl.xml.WSDLReader;
import javax.xml.namespace.QName;

import org.apache.tuscany.sca.binding.ws.WebServiceBinding;
import org.apache.tuscany.sca.interfacedef.DataType;
import org.apache.tuscany.sca.interfacedef.Operation;
import org.apache.tuscany.sca.interfacedef.java.JavaInterface;
import org.apache.tuscany.sca.interfacedef.java.JavaInterfaceContract;
import org.apache.tuscany.sca.interfacedef.wsdl.DefaultWSDLFactory;
import org.apache.tuscany.sca.interfacedef.wsdl.WSDLDefinition;
import org.apache.tuscany.sca.interfacedef.wsdl.WSDLInterface;
import org.apache.tuscany.sca.interfacedef.wsdl.WSDLInterfaceContract;
import org.apache.tuscany.sca.interfacedef.wsdl.impl.InvalidWSDLException;
import org.apache.tuscany.sca.interfacedef.wsdl.impl.WSDLOperationIntrospectorImpl;
import org.apache.tuscany.sca.interfacedef.wsdl.xml.XMLDocumentHelper;
import org.apache.tuscany.sca.policy.Intent;
import org.apache.tuscany.sca.policy.IntentAttachPoint;
import org.apache.ws.commons.schema.XmlSchemaCollection;
import org.apache.ws.java2wsdl.Java2WSDLBuilder;
import org.osoa.sca.annotations.OneWay;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;

/**
 * Utility methods to create WSDL objects from Java interfaces
 */
public class Java2WSDLHelper {

    /**
     * Create a WSDLInterfaceContract from a JavaInterfaceContract
     */
    public static WSDLInterfaceContract createWSDLInterfaceContract(JavaInterfaceContract contract, WebServiceBinding wsBinding) {
        JavaInterface iface = (JavaInterface)contract.getInterface();
        Definition def = Java2WSDLHelper.createDefinition(iface.getJavaClass(), wsBinding);

        DefaultWSDLFactory wsdlFactory = new DefaultWSDLFactory();

        WSDLInterfaceContract wsdlContract = wsdlFactory.createWSDLInterfaceContract();
        WSDLInterface wsdlInterface = wsdlFactory.createWSDLInterface();

        wsdlContract.setInterface(wsdlInterface);
        WSDLDefinition wsdlDefinition = new DefaultWSDLFactory().createWSDLDefinition();
        wsdlDefinition.setDefinition(def);
        wsdlInterface.setWsdlDefinition(wsdlDefinition);
        wsdlInterface.setRemotable(true);
        wsdlInterface.setConversational(contract.getInterface().isConversational());
        wsdlInterface.setUnresolved(false);
        wsdlInterface.setRemotable(true);
        PortType portType = (PortType)def.getAllPortTypes().values().iterator().next();
        wsdlInterface.setPortType(portType);

        readInlineSchemas(def, wsdlDefinition.getInlinedSchemas());

        try {
            for (Operation op : iface.getOperations()) {
                javax.wsdl.Operation wsdlOp = portType.getOperation(op.getName(), null, null);
                WSDLOperationIntrospectorImpl opx =
                    new WSDLOperationIntrospectorImpl(wsdlFactory, wsdlOp, wsdlDefinition.getInlinedSchemas(), null,
                                                      null);

                Operation clonedOp = (Operation)op.clone();
                clonedOp.setDataBinding(null);

                if (clonedOp.getInputType().getLogical().isEmpty()) {
                    // null args case needs a single input type for the wrapper
                    clonedOp.setInputType(opx.getInputType());
                }
                for (DataType<?> dt : clonedOp.getInputType().getLogical()) {
                    dt.setDataBinding(null);
                }
                
                if (clonedOp.getOutputType() != null ){
                    clonedOp.getOutputType().setDataBinding(null);
                }
                for (DataType<?> dt : clonedOp.getFaultTypes()) {
                    dt.setDataBinding(null);
                }
                clonedOp.setWrapperStyle(true);
                clonedOp.setWrapper(opx.getWrapper().getWrapperInfo());

                wsdlInterface.getOperations().add(clonedOp);
            }
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        } catch (InvalidWSDLException e) {
            throw new RuntimeException(e);
        }

        return wsdlContract;
    }

    protected static void readInlineSchemas(Definition definition, XmlSchemaCollection schemaCollection) {
        Types types = definition.getTypes();
        if (types != null) {
            for (Object ext : types.getExtensibilityElements()) {
                if (ext instanceof Schema) {
                    Element element = ((Schema)ext).getElement();
                    schemaCollection.setBaseUri(((Schema)ext).getDocumentBaseURI());
                    schemaCollection.read(element, definition.getDocumentBaseURI());
                }
            }
        }
        for (Object imports : definition.getImports().values()) {
            List<?> impList = (List<?>)imports;
            for (Object i : impList) {
                javax.wsdl.Import anImport = (javax.wsdl.Import)i;
                // Read inline schemas
                if (anImport.getDefinition() != null) {
                    readInlineSchemas(anImport.getDefinition(), schemaCollection);
                }
            }
        }
    }

    /**
     * Create a WSDL4J Definition object from a Java interface
     */
    protected static Definition createDefinition(Class<?> javaInterface, WebServiceBinding wsBinding) {

        String className = javaInterface.getName();
        ClassLoader cl = javaInterface.getClassLoader();
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        Java2WSDLBuilder builder = new Java2WSDLBuilder(os, className, cl);

        try {
            builder.generateWSDL();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        try {

            WSDLReader reader = javax.wsdl.factory.WSDLFactory.newInstance().newWSDLReader();
            reader.setFeature("javax.wsdl.verbose", false);
            reader.setFeature("javax.wsdl.importDocuments", true);

            WSDLLocatorImpl locator = new WSDLLocatorImpl(new ByteArrayInputStream(os.toByteArray()));
            Definition definition = reader.readWSDL(locator);
            
            processSOAPVersion(definition, wsBinding);
            processNoArgAndVoidReturnMethods(definition, javaInterface);

            return definition;

        } catch (WSDLException e) {
            throw new RuntimeException(e);
        }
    }

    private static void processSOAPVersion(Definition definition, WebServiceBinding wsBinding) {
        if (requiresSOAP12(wsBinding)) {
             removePort(definition, "SOAP11port_http");
         } else {
             removePort(definition, "SOAP12port_http");
         }
    }

    private static void removePort(Definition definition, String portNameSuffix) {
        Service service = (Service)definition.getServices().values().iterator().next();
        Map<?,?> ports = service.getPorts();
        for (Object o : ports.keySet()) {
            if (((String)o).endsWith(portNameSuffix)) {
                Port p = (Port) ports.remove(o);
                definition.removeBinding(p.getBinding().getQName());
                break;
            }
        }
    }

    private static final QName SOAP12_INTENT = new QName("http://www.osoa.org/xmlns/sca/1.0", "soap12");
    
    private static boolean requiresSOAP12(WebServiceBinding wsBinding) {
        if (wsBinding instanceof IntentAttachPoint) {
            List<Intent> intents = ((IntentAttachPoint)wsBinding).getRequiredIntents();
            for (Intent intent : intents) {
                if (SOAP12_INTENT.equals(intent.getName())) {
                    return true;
                }
            }
        }
        return false;
    }

    private static void processNoArgAndVoidReturnMethods(Definition definition, Class javaInterface) {
        String namespaceURI = definition.getTargetNamespace();
        String prefix = definition.getPrefix(namespaceURI);
        String xsPrefix = definition.getPrefix("http://www.w3.org/2001/XMLSchema");
        PortType portType = (PortType)definition.getAllPortTypes().values().iterator().next();

        Element schema = null;
        Document document = null;
        Types types = definition.getTypes();
        if (types != null) {
            for (Object ext : types.getExtensibilityElements()) {
                if (ext instanceof Schema) {
                    Element element = ((Schema)ext).getElement();
                    if (element.getAttribute("targetNamespace").equals(namespaceURI)) {
                        schema = element;
                        document = schema.getOwnerDocument();
                        break;
                    }
                }
            }
        }
        if (document == null) {
            return;
        }

        // look at each operation in the port type to see if it needs fixing up
        for (Object oper : portType.getOperations()) {
            javax.wsdl.Operation operation = (javax.wsdl.Operation)oper;
            String opName = operation.getName();

            // if input message has no parts, add one containing an empty wrapper
            Input input = operation.getInput();
            if (input != null) {
                Message inputMsg = input.getMessage();
                if (inputMsg.getParts().isEmpty()) {
                    // create wrapper element and add it to the schema DOM
                    Element wrapper = document.createElementNS("http://www.w3.org/2001/XMLSchema",
                                                               xsPrefix + ":element");
                    wrapper.setAttribute("name", opName);
                    schema.appendChild(wrapper);
                    Element complexType = document.createElementNS("http://www.w3.org/2001/XMLSchema",
                                                                   xsPrefix + ":complexType");
                    wrapper.appendChild(complexType);

                    // create new part for the wrapper and add it to the message
                    Part part = definition.createPart();
                    part.setName("parameters");
                    part.setElementName(new QName(namespaceURI, opName, prefix));
                    inputMsg.addPart(part);
                }
            }

            // if two-way operation has no output message, add one containing an empty wrapper
            if (input != null && operation.getOutput() == null) {
                boolean isOneWay = false;
                Method[] methods = javaInterface.getMethods();
                for (Method method : methods) {
                    if (method.getName().equals(opName) && method.getAnnotation(OneWay.class) != null) {
                        isOneWay = true;
                    }
                }
                if (!isOneWay) {
                    // create wrapper element and add it to the schema DOM
                    String msgName = opName + "Response";
                    Element wrapper = document.createElementNS("http://www.w3.org/2001/XMLSchema",
                                                               xsPrefix + ":element");
                    wrapper.setAttribute("name", msgName);
                    schema.appendChild(wrapper);
                    Element complexType = document.createElementNS("http://www.w3.org/2001/XMLSchema",
                                                                   xsPrefix + ":complexType");
                    wrapper.appendChild(complexType);

                    // create new part for the wrapper
                    Part part = definition.createPart();
                    part.setName("parameters");
                    part.setElementName(new QName(namespaceURI, msgName, prefix));

                    // create new message for the part
                    Message outputMsg = definition.createMessage();
                    outputMsg.setQName(new QName(namespaceURI, msgName, prefix));
                    outputMsg.addPart(part);
                    outputMsg.setUndefined(false);
                    definition.addMessage(outputMsg);

                    // create output element for the operation
                    Output output = definition.createOutput();
                    output.setMessage(outputMsg);
                    output.setExtensionAttribute(new QName("http://www.w3.org/2006/05/addressing/wsdl", "Action"),
                                                 new QName("urn:" + msgName));
                    operation.setOutput(output);
                    operation.setStyle(OperationType.REQUEST_RESPONSE);

                    // add binding output element to bindings for this port type
                    for (Object bindObj : definition.getAllBindings().values()) {
                        Binding binding = (Binding)bindObj;
                        if (binding.getPortType().equals(portType)) {
                            BindingOperation op = binding.getBindingOperation(opName, null, null);
                            if (op != null && op.getBindingInput() != null && op.getBindingOutput() == null) {
                                BindingOutput bindingOut = definition.createBindingOutput();
                                for (Object extObj : op.getBindingInput().getExtensibilityElements()) {
                                    bindingOut.addExtensibilityElement((ExtensibilityElement)extObj);
                                }
                                op.setBindingOutput(bindingOut);
                            }
                        }
                    }
                }
            }
        }

    }

}

class WSDLLocatorImpl implements WSDLLocator {
    private InputStream inputStream;
    private String base = "http://";
    private String latestImportURI;

    public WSDLLocatorImpl(InputStream is) {
        this.inputStream = is;
    }

    public void close() {
        try {
            inputStream.close();
        } catch (IOException e) {
            // Ignore
        }
    }

    public InputSource getBaseInputSource() {
        try {
            return XMLDocumentHelper.getInputSource(new URL(base), inputStream);
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public String getBaseURI() {
        return base;
    }

    public InputSource getImportInputSource(String parentLocation, String importLocation) {
        return null;
    }

    public String getLatestImportURI() {
        return latestImportURI;
    }

}
