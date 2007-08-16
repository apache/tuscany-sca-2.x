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
package org.apache.tuscany.tools.java2wsdl.generate;

import java.io.ByteArrayInputStream;
import java.io.StringWriter;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.om.OMNode;
import org.apache.axiom.om.impl.builder.StAXOMBuilder;
import org.apache.ws.commons.schema.XmlSchema;
import org.codehaus.jam.JMethod;

public class TuscanyJava2OMBuilder implements TuscanyJava2WSDLConstants {

    private TuscanyTypeTable typeTable = null;

    private static int prefixCount = 1;

    private static final String NAMESPACE_PREFIX = "ns";

    private JMethod method[];

    private Collection schemaCollection;

    private GenerationParameters generationParams;

    private OMNamespace ns1;

    private OMNamespace soap;

    private OMNamespace soap12;

    private OMNamespace tns;

    private OMNamespace wsdl;

    private OMNamespace mime;

    private OMNamespace http;

    public TuscanyJava2OMBuilder(JMethod[] method,
                                 Collection schemaCollection,
                                 TuscanyTypeTable typeTab,
                                 GenerationParameters genParams) {
        this.method = method;
        this.schemaCollection = schemaCollection;
        this.typeTable = typeTab;
        this.generationParams = genParams;
    }

    public OMElement generateOM() throws Exception {
        OMFactory fac = OMAbstractFactory.getOMFactory();
        wsdl = fac.createOMNamespace(WSDL_NAMESPACE, DEFAULT_WSDL_NAMESPACE_PREFIX);
        OMElement ele = fac.createOMElement("definitions", wsdl);

        ele.addAttribute("targetNamespace", generationParams.getTargetNamespace(), null);
        generateNamespaces(fac, ele);
        generateTypes(fac, ele);
        generateMessages(fac, ele);
        generatePortType(fac, ele);
        generateBinding(fac, ele);
        generateService(fac, ele);
        return ele;
    }

    private void generateNamespaces(OMFactory fac, OMElement defintions) throws Exception {
        soap = defintions.declareNamespace(URI_WSDL11_SOAP, SOAP11_PREFIX);
        tns =
            defintions.declareNamespace(generationParams.getTargetNamespace(), generationParams
                .getTargetNamespacePrefix());
        soap12 = defintions.declareNamespace(URI_WSDL12_SOAP, SOAP12_PREFIX);
        http = defintions.declareNamespace(HTTP_NAMESPACE, HTTP_PREFIX);
        mime = defintions.declareNamespace(MIME_NAMESPACE, MIME_PREFIX);
    }

    private void generateTypes(OMFactory fac, OMElement defintions) throws Exception {
        OMElement wsdlTypes = fac.createOMElement("types", wsdl);
        StringWriter writer = new StringWriter();

        // wrap the Schema elements with this start and end tags to create a
        // document root
        // under which the schemas can fall into
        writer.write("<xmlSchemas>");
        writeSchemas(writer);
        writer.write("</xmlSchemas>");

        XMLStreamReader xmlReader =
            XMLInputFactory.newInstance().createXMLStreamReader(new ByteArrayInputStream(writer.toString().getBytes()));

        StAXOMBuilder staxOMBuilders = new StAXOMBuilder(fac, xmlReader);
        OMElement documentElement = staxOMBuilders.getDocumentElement();

        Iterator iterator = documentElement.getChildElements();
        while (iterator.hasNext()) {
            wsdlTypes.addChild((OMNode)iterator.next());
        }
        defintions.addChild(wsdlTypes);
    }

    private void writeSchemas(StringWriter writer) {
        Iterator iterator = schemaCollection.iterator();
        XmlSchema xmlSchema = null;

        while (iterator.hasNext()) {
            xmlSchema = (XmlSchema)iterator.next();
            // typeIterator = xmlSchema.getSchemaTypes().getValues();
            /*
             * while (typeIterator.hasNext()) {
             * xmlSchema.getItems().add((XmlSchemaObject) typeIterator.next()); }
             */
            xmlSchema.write(writer);
        }
    }

    private void generateMessages(OMFactory fac, OMElement definitions) throws Exception {
        Hashtable namespaceMap = new Hashtable();
        String namespacePrefix = null;
        String namespaceURI = null;
        QName messagePartType = null;
        for (int i = 0; i < method.length; i++) {
            JMethod jmethod = method[i];

            if (jmethod.isPublic()) {
                // Request Message
                OMElement requestMessge = fac.createOMElement(MESSAGE_LOCAL_NAME, wsdl);
                requestMessge.addAttribute(ATTRIBUTE_NAME, jmethod.getSimpleName() + MESSAGE_SUFFIX, null);
                definitions.addChild(requestMessge);

                // only if a type for the message part has already been defined
                if ((messagePartType =
                    typeTable.getComplexSchemaTypeName(generationParams.getSchemaTargetNamespace(), jmethod
                        .getSimpleName())) != null) {
                    namespaceURI = messagePartType.getNamespaceURI();
                    // avoid duplicate namespaces
                    if ((namespacePrefix = (String)namespaceMap.get(namespaceURI)) == null) {
                        namespacePrefix = generatePrefix();
                        namespaceMap.put(namespaceURI, namespacePrefix);
                    }

                    OMElement requestPart = fac.createOMElement(PART_ATTRIBUTE_NAME, wsdl);
                    requestMessge.addChild(requestPart);
                    requestPart.addAttribute(ATTRIBUTE_NAME, "part1", null);

                    requestPart.addAttribute(ELEMENT_ATTRIBUTE_NAME, namespacePrefix + COLON_SEPARATOR
                        + jmethod.getSimpleName(), null);
                }

                // only if a type for the message part has already been defined
                if ((messagePartType =
                    typeTable.getComplexSchemaTypeName(generationParams.getSchemaTargetNamespace(), jmethod
                        .getSimpleName() + RESPONSE)) != null) {
                    namespaceURI = messagePartType.getNamespaceURI();
                    if ((namespacePrefix = (String)namespaceMap.get(namespaceURI)) == null) {
                        namespacePrefix = generatePrefix();
                        namespaceMap.put(namespaceURI, namespacePrefix);
                    }
                    // Response Message
                    OMElement responseMessge = fac.createOMElement(MESSAGE_LOCAL_NAME, wsdl);
                    responseMessge.addAttribute(ATTRIBUTE_NAME, jmethod.getSimpleName() + RESPONSE_MESSAGE, null);
                    definitions.addChild(responseMessge);
                    OMElement responsePart = fac.createOMElement(PART_ATTRIBUTE_NAME, wsdl);
                    responseMessge.addChild(responsePart);
                    responsePart.addAttribute(ATTRIBUTE_NAME, "part1", null);

                    responsePart.addAttribute(ELEMENT_ATTRIBUTE_NAME, namespacePrefix + COLON_SEPARATOR
                        + jmethod.getSimpleName()
                        + RESPONSE, null);
                }
            }
        }

        // now add these unique namespaces to the the definitions element
        Enumeration enumeration = namespaceMap.keys();
        while (enumeration.hasMoreElements()) {
            namespaceURI = (String)enumeration.nextElement();
            definitions.declareNamespace(namespaceURI, (String)namespaceMap.get(namespaceURI));
        }
    }

    /**
     * Generate the porttypes
     */
    private void generatePortType(OMFactory fac, OMElement defintions) {
        JMethod jmethod = null;
        OMElement operation = null;
        OMElement message = null;
        OMElement portType = fac.createOMElement(PORT_TYPE_LOCAL_NAME, wsdl);
        defintions.addChild(portType);
        // changed default PortType name to match Java interface name
        // instead of appending "PortType".
        portType.addAttribute(ATTRIBUTE_NAME, generationParams.getServiceName(), null);
        // adding message refs
        for (int i = 0; i < method.length; i++) {
            jmethod = method[i];

            if (jmethod.isPublic()) {
                operation = fac.createOMElement(OPERATION_LOCAL_NAME, wsdl);
                portType.addChild(operation);
                operation.addAttribute(ATTRIBUTE_NAME, jmethod.getSimpleName(), null);

                message = fac.createOMElement(IN_PUT_LOCAL_NAME, wsdl);
                message.addAttribute(MESSAGE_LOCAL_NAME, tns.getPrefix() + COLON_SEPARATOR
                    + jmethod.getSimpleName()
                    + MESSAGE_SUFFIX, null);
                operation.addChild(message);

                if (!jmethod.getReturnType().isVoidType()) {
                    message = fac.createOMElement(OUT_PUT_LOCAL_NAME, wsdl);
                    message.addAttribute(MESSAGE_LOCAL_NAME, tns.getPrefix() + COLON_SEPARATOR
                        + jmethod.getSimpleName()
                        + RESPONSE_MESSAGE, null);
                    operation.addChild(message);
                }
            }
        }

    }

    /**
     * Generate the service
     */
    public void generateService(OMFactory fac, OMElement defintions) {
        OMElement service = fac.createOMElement(SERVICE_LOCAL_NAME, wsdl);
        defintions.addChild(service);
        // Add "WebService" to the end of WSDL service name
        service.addAttribute(ATTRIBUTE_NAME, generationParams.getServiceName() + WSDL_SERVICE_SUFFIX, null);
        OMElement port = fac.createOMElement(PORT, wsdl);
        service.addChild(port);
        port.addAttribute(ATTRIBUTE_NAME, generationParams.getServiceName() + SOAP11PORT, null);
        port.addAttribute(BINDING_LOCAL_NAME, tns.getPrefix() + COLON_SEPARATOR
            + generationParams.getServiceName()
            + BINDING_NAME_SUFFIX, null);
        addExtensionElement(fac,
                            port,
                            soap,
                            SOAP_ADDRESS,
                            LOCATION,
                            generationParams.getLocationUri() + generationParams.getServiceName());

        port = fac.createOMElement(PORT, wsdl);
        service.addChild(port);
        port.addAttribute(ATTRIBUTE_NAME, generationParams.getServiceName() + SOAP12PORT, null);
        port.addAttribute(BINDING_LOCAL_NAME, tns.getPrefix() + COLON_SEPARATOR
            + generationParams.getServiceName()
            + SOAP12BINDING_NAME_SUFFIX, null);
        addExtensionElement(fac,
                            port,
                            soap12,
                            SOAP_ADDRESS,
                            LOCATION,
                            generationParams.getLocationUri() + generationParams.getServiceName());
    }

    /**
     * Generate the bindings
     */
    private void generateBinding(OMFactory fac, OMElement defintions) throws Exception {
        generateSoap11Binding(fac, defintions);
        generateSoap12Binding(fac, defintions);
    }

    private void generateSoap11Binding(OMFactory fac, OMElement defintions) throws Exception {
        OMElement binding = fac.createOMElement(BINDING_LOCAL_NAME, wsdl);
        defintions.addChild(binding);
        binding.addAttribute(ATTRIBUTE_NAME, generationParams.getServiceName() + BINDING_NAME_SUFFIX, null);
        // changed default PortType name to match Java interface name
        // instead of appending "PortType".
        binding.addAttribute("type", tns.getPrefix() + COLON_SEPARATOR + generationParams.getServiceName(), null);

        addExtensionElement(fac, binding, soap, BINDING_LOCAL_NAME, TRANSPORT, TRANSPORT_URI, STYLE, generationParams
            .getStyle());

        for (int i = 0; i < method.length; i++) {
            JMethod jmethod = method[i];
            if (jmethod.isPublic()) {
                OMElement operation = fac.createOMElement(OPERATION_LOCAL_NAME, wsdl);
                binding.addChild(operation);

                addExtensionElement(fac,
                                    operation,
                                    soap,
                                    OPERATION_LOCAL_NAME,
                                    SOAP_ACTION,
                                    URN_PREFIX + COLON_SEPARATOR + jmethod.getSimpleName(),
                                    STYLE,
                                    generationParams.getStyle());
                operation.addAttribute(ATTRIBUTE_NAME, jmethod.getSimpleName(), null);

                OMElement input = fac.createOMElement(IN_PUT_LOCAL_NAME, wsdl);
                addExtensionElement(fac,
                                    input,
                                    soap,
                                    SOAP_BODY,
                                    SOAP_USE,
                                    generationParams.getUse(),
                                    "namespace",
                                    generationParams.getTargetNamespace());
                operation.addChild(input);

                if (!jmethod.getReturnType().isVoidType()) {
                    OMElement output = fac.createOMElement(OUT_PUT_LOCAL_NAME, wsdl);
                    addExtensionElement(fac,
                                        output,
                                        soap,
                                        SOAP_BODY,
                                        SOAP_USE,
                                        generationParams.getUse(),
                                        "namespace",
                                        generationParams.getTargetNamespace());
                    operation.addChild(output);
                }
            }
        }
    }

    private void generateSoap12Binding(OMFactory fac, OMElement defintions) throws Exception {
        OMElement binding = fac.createOMElement(BINDING_LOCAL_NAME, wsdl);
        defintions.addChild(binding);
        binding.addAttribute(ATTRIBUTE_NAME, generationParams.getServiceName() + SOAP12BINDING_NAME_SUFFIX, null);
        // changed default PortType name to match Java interface name
        // instead of appending "PortType".
        binding.addAttribute("type", tns.getPrefix() + COLON_SEPARATOR + generationParams.getServiceName(), null);

        addExtensionElement(fac, binding, soap12, BINDING_LOCAL_NAME, TRANSPORT, TRANSPORT_URI, STYLE, generationParams
            .getStyle());

        for (int i = 0; i < method.length; i++) {
            JMethod jmethod = method[i];

            if (jmethod.isPublic()) {
                OMElement operation = fac.createOMElement(OPERATION_LOCAL_NAME, wsdl);
                binding.addChild(operation);
                operation.declareNamespace(URI_WSDL12_SOAP, SOAP12_PREFIX);

                addExtensionElement(fac,
                                    operation,
                                    soap12,
                                    OPERATION_LOCAL_NAME,
                                    SOAP_ACTION,
                                    URN_PREFIX + COLON_SEPARATOR + jmethod.getSimpleName(),
                                    STYLE,
                                    generationParams.getStyle());
                operation.addAttribute(ATTRIBUTE_NAME, jmethod.getSimpleName(), null);

                OMElement input = fac.createOMElement(IN_PUT_LOCAL_NAME, wsdl);
                addExtensionElement(fac,
                                    input,
                                    soap12,
                                    SOAP_BODY,
                                    SOAP_USE,
                                    generationParams.getUse(),
                                    "namespace",
                                    generationParams.getTargetNamespace());
                operation.addChild(input);

                if (!jmethod.getReturnType().isVoidType()) {
                    OMElement output = fac.createOMElement(OUT_PUT_LOCAL_NAME, wsdl);
                    addExtensionElement(fac,
                                        output,
                                        soap12,
                                        SOAP_BODY,
                                        SOAP_USE,
                                        generationParams.getUse(),
                                        "namespace",
                                        generationParams.getTargetNamespace());
                    operation.addChild(output);
                }
            }
        }
    }

    private void addExtensionElement(OMFactory fac,
                                     OMElement element,
                                     OMNamespace namespace,
                                     String name,
                                     String att1Name,
                                     String att1Value,
                                     String att2Name,
                                     String att2Value) {
        OMElement soapbinding = fac.createOMElement(name, namespace);
        element.addChild(soapbinding);
        soapbinding.addAttribute(att1Name, att1Value, null);
        soapbinding.addAttribute(att2Name, att2Value, null);
    }

    private void addExtensionElement(OMFactory fac,
                                     OMElement element,
                                     OMNamespace namespace,
                                     String name,
                                     String att1Name,
                                     String att1Value) {
        OMElement soapbinding = fac.createOMElement(name, namespace);
        element.addChild(soapbinding);
        soapbinding.addAttribute(att1Name, att1Value, null);
    }

    private String generatePrefix() {
        return NAMESPACE_PREFIX + prefixCount++;
    }
}
