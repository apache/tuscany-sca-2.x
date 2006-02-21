/**
 *
 *  Copyright 2005 The Apache Software Foundation or its licensors, as applicable.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.apache.tuscany.binding.axis.handler;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.wsdl.Binding;
import javax.wsdl.BindingOperation;
import javax.wsdl.Definition;
import javax.wsdl.Port;
import javax.wsdl.PortType;
import javax.wsdl.Service;
import javax.wsdl.extensions.soap.SOAPAddress;
import javax.wsdl.extensions.soap.SOAPBinding;
import javax.xml.namespace.QName;
import javax.xml.soap.Name;
import javax.xml.soap.SOAPBodyElement;
import javax.xml.soap.SOAPElement;

import org.apache.tuscany.model.types.wsdl.WSDLServiceContract;
import org.w3c.dom.Element;

/**
 * Metadata for a WSDL port
 *
 */
public class WebServicePortMetaData {
    private final static String SOAP_ENCODING_URI = "http://schemas.xmlsoap.org/wsdl/soap/";
    private Service wsdlService;

    private QName wsdlServiceName;

    private Port wsdlPort;

    private Binding wsdlBinding;

    private QName wsdlPortName;

    private PortType wsdlPortType;

    private QName wsdlPortTypeName;

    private String endpoint;

    private boolean managed;

    private List allOperationMetaData;

    private WSDLServiceContract interfaceType;

    /**
     * Constructor
     *
     * @param wsdlDefinition
     * @param portName
     */
    public WebServicePortMetaData(Definition wsdlDefinition, Port wsdlPort, String endpoint, boolean managed) {

        // Lookup the named port
        this.wsdlPort=wsdlPort;
        wsdlPortName = new QName(wsdlDefinition.getTargetNamespace(), wsdlPort.getName());
        for (Iterator i = wsdlDefinition.getServices().values().iterator(); i.hasNext() && wsdlPort == null;) {
            wsdlService = (javax.wsdl.Service) i.next();
            if (wsdlService.getPorts().containsValue(wsdlPort)) {
                wsdlServiceName = wsdlService.getQName();
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
     * @param portType
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
     *
     * @param wsdlPort
     * @return
     */
    public String getEndpoint() {

        // Return the specified endpoint
        if (endpoint != null)
            return endpoint;

        // Find the target endpoint on the port
        if (wsdlPort != null) {
            final List wsdlPortExtensions = wsdlPort.getExtensibilityElements();
            for (Iterator i = wsdlPortExtensions.iterator(); i.hasNext();) {
                final Object extension = i.next();
                if (extension instanceof SOAPAddress) {
                    final SOAPAddress address = (SOAPAddress) extension;
                    return address.getLocationURI();
                }
            }
        }

        return null;
    }

    public String getStyle() {

        // Find the binding style
        String style = null;
        if (wsdlBinding != null) {
            final List wsdlBindingExtensions = wsdlBinding.getExtensibilityElements();
            SOAPBinding soapBinding = (SOAPBinding) getExtensibilityElement(wsdlBindingExtensions, SOAPBinding.class);
            if (soapBinding != null)
                style = soapBinding.getStyle();
        }

        // Default to document
        return (style == null) ? "document" : style;
    }

    public String getUse() {
        List list = getAllOperationMetaData();
        WebServiceOperationMetaData operationMetaData = (WebServiceOperationMetaData) list.get(0);
        return operationMetaData.getUse();
    }

    /**
     * @return Returns the managed.
     */
    public boolean isManaged() {
        return managed;
    }

    public static Object getExtensibilityElement(List elements, Class type) {
        for (Iterator i = elements.iterator(); i.hasNext();) {
            Object element = i.next();
            if (type.isInstance(element))
                return element;
        }
        return null;
    }

    public static List getExtensibilityElements(List elements, Class type) {
        List result = new ArrayList();
        for (Iterator i = elements.iterator(); i.hasNext();) {
            Object element = i.next();
            if (type.isInstance(element))
                result.add(element);
        }
        return result;
    }

    /**
     * Get the operation signature from the SOAP Body
     *
     * @param body
     * @return A list of QNames
     */
    public static List getOperationSignature(javax.xml.soap.SOAPBody body) {
        List signature = new ArrayList();
        for (Iterator i = body.getChildElements(); i.hasNext();) {
            Object child = i.next();
            if (child instanceof SOAPBodyElement) {
                Name name = ((SOAPBodyElement) child).getElementName();
                QName qname = new QName(name.getURI(), name.getLocalName(), name.getPrefix());
                signature.add(qname);
            }
        }
        return signature;
    }

    public static List getRPCOperationSignature(javax.xml.soap.SOAPBody body) {
        List signature = new ArrayList();
        for (Iterator i = body.getChildElements(); i.hasNext();) {
            Object child = i.next();
            if (child instanceof SOAPBodyElement) {
                SOAPBodyElement op = ((SOAPBodyElement) child);
                for (Iterator j = op.getChildElements(); j.hasNext();) {
                    Object part = i.next();
                    if (part instanceof SOAPElement) {
                        SOAPElement p = (SOAPElement) part;
                        signature.add(p.getLocalName());
                    }
                }
            }
        }
        return signature;
    }

    public static String getSOAPEncodingAttribute(org.eclipse.wst.wsdl.ExtensibilityElement extensibilityElement, String elementName, String attributeName) {
        Element element = extensibilityElement.getElement();
        if (SOAP_ENCODING_URI.equals(element.getNamespaceURI()) && elementName.equals(element.getLocalName())) {
            return element.getAttribute(attributeName);
        } else
            return null;
    }

    public WebServiceOperationMetaData getOperationMetaData(javax.xml.soap.SOAPBody body) {
        List s1 = getOperationSignature(body);
        // List rpcParts = getRPCOperationSignature(body);
        for (Iterator it = getAllOperationMetaData().iterator(); it.hasNext();) {
            WebServiceOperationMetaData descriptor = (WebServiceOperationMetaData) it.next();

            String style = descriptor.getStyle();

            if (style.equals("document")) {
                List s2 = descriptor.getOperationSignature();
                if (s1.equals(s2))
                    return descriptor;
            } else {
                QName op1 = (QName) s1.get(0);
                QName op2 = descriptor.getRPCOperationName();
                if (op1.equals(op2)) {
                    /*
                          * // FIXME: [rfeng] We don't support method overloading
                          * List partNames = getOperationSignature(binding,
                          * bindingOperation); if (rpcParts.equals(partNames))
                          */
                    return descriptor;
                }
            }
        }
        return null;
    }

    public List getAllOperationMetaData() {
        if (allOperationMetaData == null) {
            allOperationMetaData = new ArrayList();
            for (Iterator it = wsdlBinding.getBindingOperations().iterator(); it.hasNext();) {
                final BindingOperation bindingOperation = (BindingOperation) it.next();
                if (bindingOperation.getOperation() != null)
                    allOperationMetaData.add(new WebServiceOperationMetaData(wsdlBinding, bindingOperation));
            }
        }
        return allOperationMetaData;
    }

    public WebServiceOperationMetaData getOperationMetaData(String operationName) {
        for (Iterator it = getAllOperationMetaData().iterator(); it.hasNext();) {
            WebServiceOperationMetaData descriptor = (WebServiceOperationMetaData) it.next();
            String opName = descriptor.getBindingOperation().getOperation().getName();

            if (opName.equals(operationName))
                return descriptor;
        }
        return null;
    }

    public WSDLServiceContract getInterfaceType() {
        return interfaceType;
	}

}