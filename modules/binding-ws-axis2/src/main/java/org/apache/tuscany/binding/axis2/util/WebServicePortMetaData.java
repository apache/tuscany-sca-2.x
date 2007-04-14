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
package org.apache.tuscany.binding.axis2.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.wsdl.Binding;
import javax.wsdl.BindingOperation;
import javax.wsdl.Definition;
import javax.wsdl.Operation;
import javax.wsdl.Port;
import javax.wsdl.PortType;
import javax.wsdl.Service;
import javax.wsdl.extensions.soap.SOAPAddress;
import javax.wsdl.extensions.soap.SOAPBinding;
import javax.xml.namespace.QName;

/**
 * Metadata for a WSDL port
 *
 */
public class WebServicePortMetaData {

    private Service wsdlService;
    private QName wsdlServiceName;
    private Port wsdlPort;
    private Binding wsdlBinding;
    private QName wsdlPortName;
    private PortType wsdlPortType;
    private QName wsdlPortTypeName;
    private String endpoint;
    private boolean managed;
    private List<WebServiceOperationMetaData> allOperationMetaData;

    /**
     * Constructor
     *
     * @param wsdlDefinition
     */
    public WebServicePortMetaData(Definition wsdlDefinition, Port wsdlPort, String endpoint, boolean managed) {

        // Lookup the named port
        this.wsdlPort = wsdlPort;
        wsdlPortName = new QName(wsdlDefinition.getTargetNamespace(), wsdlPort.getName());
        Collection services = wsdlDefinition.getServices().values();
        for (Object serviceObj : services) {
            Service service = (Service) serviceObj;
            if (service.getPorts().containsValue(wsdlPort)) {
                wsdlService = service;
                wsdlServiceName = service.getQName();
                break;
            }
        }

        // Save the binding
        wsdlBinding = wsdlPort.getBinding();
        if (wsdlBinding == null) {
            throw new IllegalArgumentException("WSDL binding cannot be found for " + wsdlPortName);
        }

        // Save the portType
        wsdlPortType = wsdlBinding.getPortType();
        if (wsdlPortType == null) {
            throw new IllegalArgumentException("WSDL portType cannot be found for " + wsdlPortName);
        }
        wsdlPortTypeName = wsdlPortType.getQName();

        // Save the endpoint
        this.endpoint = endpoint;

        // Track if this endpoint is managed or not
        this.managed = managed;
    }

    /**
     * Constructor
     *
     * @param serviceName
     * @param portName
     * @param portTypeName
     * @param endpoint
     */
    public WebServicePortMetaData(QName serviceName, String portName, QName portTypeName, String endpoint) {
        wsdlServiceName = serviceName;
        wsdlPortName = new QName(serviceName.getNamespaceURI(), portName);
        wsdlPortTypeName = portTypeName;
        this.endpoint = endpoint;
    }

    /**
     * @return Returns the wsdlPort.
     */
    public javax.wsdl.Port getPort() {
        return wsdlPort;
    }

    /**
     * @return Returns the wsdlService.
     */
    public QName getServiceName() {
        return wsdlServiceName;
    }

    /**
     * @return Returns the wsdlService.
     */
    public javax.wsdl.Service getService() {
        return wsdlService;
    }

    /**
     * @return Returns the wsdlPortType.
     */
    public PortType getPortType() {
        return wsdlPortType;
    }

    /**
     * @return Returns the wsdlPortType.
     */
    public QName getPortTypeName() {
        return wsdlPortTypeName;
    }

    /**
     * @return Returns the wsdlBinding.
     */
    public Binding getBinding() {
        return wsdlBinding;
    }

    /**
     * @return Returns the wsdlPortName.
     */
    public QName getPortName() {
        return wsdlPortName;
    }

    /**
     * Returns the endpoint of a given port.
     */
    public String getEndpoint() {

        // Return the specified endpoint
        if (endpoint != null) {
            return endpoint;
        }

        // Find the target endpoint on the port
        if (wsdlPort != null) {
            final List wsdlPortExtensions = wsdlPort.getExtensibilityElements();
            for (final Object extension : wsdlPortExtensions) {
                if (extension instanceof SOAPAddress) {
                    return ((SOAPAddress) extension).getLocationURI();
                }
            }
        }

        return null;
    }

    /**
     * Returns the SOAP binding style.
     */
    public String getStyle() {

        // Find the binding style
        String style = null;
        if (wsdlBinding != null) {
            final List wsdlBindingExtensions = wsdlBinding.getExtensibilityElements();
            SOAPBinding soapBinding = getExtensibilityElement(wsdlBindingExtensions, SOAPBinding.class);
            if (soapBinding != null) {
                style = soapBinding.getStyle();
            }
        }

        // Default to document
        return (style == null) ? "document" : style;
    }

    /**
     * Returns the use attribute
     */
    public String getUse() {
        List<WebServiceOperationMetaData> list = getAllOperationMetaData();
        return list.get(0).getUse();
    }

    /**
     * Returns the encoding attribute
     */
    public String getEncoding() {
        List<WebServiceOperationMetaData> list = getAllOperationMetaData();
        return list.get(0).getEncoding();
    }

    /**
     * @return Returns true if this is a managed web service.
     */
    public boolean isManaged() {
        return managed;
    }

    /**
     * Returns the first extensibility element of the given type.
     *
     * @param elements
     * @param type
     */
    public static <T> T getExtensibilityElement(List elements, Class<T> type) {
        for (Object element : elements) {
            if (type.isInstance(element)) {
                return type.cast(element);
            }
        }
        return null;
    }

    /**
     * Returns the extensibility elements of the given type.
     *
     * @param elements
     * @param type
     * @return List
     */
    public static <T> List<T> getExtensibilityElements(List elements, Class<T> type) {
        List<T> result = new ArrayList<T>();
        for (Object element : elements) {
            if (type.isInstance(element)) {
                result.add(type.cast(element));
            }
        }
        return result;
    }

    /**
     * Get the operation signature from the SOAP Body
     *
     * @return A list of QNames
     */
    // public static List getOperationSignature(javax.xml.soap.SOAPBody body) {
    // List signature = new ArrayList();
    // for (Iterator i = body.getChildElements(); i.hasNext();) {
    // Object child = i.next();
    // if (child instanceof SOAPBodyElement) {
    // Name name = ((SOAPBodyElement) child).getElementName();
    // QName qname = new QName(name.getURI(), name.getLocalName(), name.getPrefix());
    // signature.add(qname);
    // }
    // }
    // return signature;
    // }
    // public static List getRPCOperationSignature(javax.xml.soap.SOAPBody body) {
    // List signature = new ArrayList();
    // for (Iterator i = body.getChildElements(); i.hasNext();) {
    // Object child = i.next();
    // if (child instanceof SOAPBodyElement) {
    // SOAPBodyElement op = ((SOAPBodyElement) child);
    // for (Iterator j = op.getChildElements(); j.hasNext();) {
    // Object part = i.next();
    // if (part instanceof SOAPElement) {
    // SOAPElement p = (SOAPElement) part;
    // signature.add(p.getLocalName());
    // }
    // }
    // }
    // }
    // return signature;
    // }
    // public WebServiceOperationMetaData getOperationMetaData(javax.xml.soap.SOAPBody body) {
    // List s1 = getOperationSignature(body);
    // // List rpcParts = getRPCOperationSignature(body);
    // for (Iterator it = getAllOperationMetaData().iterator(); it.hasNext();) {
    // WebServiceOperationMetaData descriptor = (WebServiceOperationMetaData) it.next();
    //
    // String style = descriptor.getStyle();
    //
    // if (style.equals("document")) {
    // List s2 = descriptor.getOperationSignature();
    // if (s1.equals(s2))
    // return descriptor;
    // } else {
    // QName op1 = (QName) s1.get(0);
    // QName op2 = descriptor.getRPCOperationName();
    // if (op1.equals(op2)) {
    // /*
    // * // FIXME: [rfeng] We don't support method overloading
    // * List partNames = getOperationSignature(binding,
    // * bindingOperation); if (rpcParts.equals(partNames))
    // */
    // return descriptor;
    // }
    // }
    // }
    // return null;
    // }
    public List<WebServiceOperationMetaData> getAllOperationMetaData() {
        if (allOperationMetaData == null) {
            allOperationMetaData = new ArrayList<WebServiceOperationMetaData>();
            for (Iterator it = wsdlBinding.getBindingOperations().iterator(); it.hasNext();) {
                final BindingOperation bindingOperation = (BindingOperation) it.next();
                if (bindingOperation.getOperation() != null) {
                    allOperationMetaData.add(new WebServiceOperationMetaData(wsdlBinding, bindingOperation));
                }
            }
        }
        return allOperationMetaData;
    }

    public WebServiceOperationMetaData getOperationMetaData(String operationName) {
        StringBuilder sb = new StringBuilder(operationName);
        sb.setCharAt(0, Character.toUpperCase(sb.charAt(0)));
        String capatalizedOpName = sb.toString();

        for (WebServiceOperationMetaData webServiceOperationMetaData : getAllOperationMetaData()) {
            WebServiceOperationMetaData descriptor = (WebServiceOperationMetaData) webServiceOperationMetaData;
            String opName = descriptor.getBindingOperation().getOperation().getName();

            if (opName.equals(operationName) || opName.equals(capatalizedOpName)) {
                return descriptor;
            }
        }
        return null;
    }

    /**
     * Get the WSDL operation name for a Java method name
     */
    public String getWSDLOperationName(String methodName) {
        StringBuilder sb = new StringBuilder(methodName);
        sb.setCharAt(0, Character.toUpperCase(sb.charAt(0)));
        String capatalizedOpName = sb.toString();
        for (Object o : wsdlPortType.getOperations()) {
            Operation operation = (Operation) o;
            String wsdlOpName = operation.getName();
            if (wsdlOpName.equals(methodName)) {
                return wsdlOpName;
            }
            if (wsdlOpName.equals(capatalizedOpName)) {
                return wsdlOpName;
            }
        }
        return null;
    }

}
