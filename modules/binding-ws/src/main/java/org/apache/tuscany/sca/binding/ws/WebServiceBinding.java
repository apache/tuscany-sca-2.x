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
package org.apache.tuscany.sca.binding.ws;

import java.util.Map;

import javax.wsdl.Definition;
import javax.wsdl.Port;
import javax.wsdl.Service;
import javax.xml.namespace.QName;

import org.apache.tuscany.sca.assembly.Binding;
import org.apache.tuscany.sca.interfacedef.InterfaceContract;
import org.apache.tuscany.sca.interfacedef.wsdl.WSDLDefinition;
import org.w3c.dom.Element;


/**
 * Represents a WebService binding.
 *
 * @version $Rev$ $Date$
 */
public interface WebServiceBinding extends Binding {
    QName TYPE = new QName(SCA11_NS, "binding.ws");

    /**
     * Sets the WSDL location.
     * @param location the WSDL location
     */
    void setLocation(String location);

    /**
     * Returns the WSDL location
     * @return the WSDL location
     */
    String getLocation();

    /**
     * Returns the wsdli:location attribute namespace mappings
     * @return a Map with key being namespace and value the location
     */
    Map<String, String> getWsdliLocations();

    /**
     * Returns the name of the WSDL service.
     *
     * @return the name of the WSDL service
     */
    QName getServiceName();

    /**
     * Sets the name of the WSDL service.
     *
     * @param serviceName the name of the WSDL service
     */
    void setServiceName(QName serviceName);

    /**
     * Returns the name of the WSDL port.
     *
     * @return the name of the WSDL port
     */
    String getPortName();

    /**
     * Sets the name of the WSDL port.
     *
     * @param portName the name of the WSDL port
     */
    void setPortName(String portName);

    /**
     * Returns the name of the WSDL binding.
     *
     * @return the name of the WSDL binding
     */
    QName getBindingName();

    /**
     * Sets the name of the WSDL binding.
     *
     * @param bindingName the name of the WSDL binding
     */
    void setBindingName(QName bindingName);

    /**
     * Returns the name of the WSDL endpoint.
     *
     * @return the name of the WSDL endpoint
     */
    String getEndpointName();

    /**
     * Sets the name of the WSDL endpoint.
     *
     * @param endpointName the name of the WSDL endpoint
     */
    void setEndpointName(String endpointName);

    /**
     * Returns the WSDL service
     * @return the WSDL service
     */
    Service getService();

    /**
     * Sets the WSDL service.
     * @param service the WSDL service
     */
    void setService(Service service);

    /**
     * Returns the WSDL port
     * @return the WSDL port
     */
    Port getPort();

    /**
     * Sets the WSDL endpoint
     * @param endpoint the WSDL endpoint
     */
    void setEndpoint(Port endpoint);

    /**
     * Returns the WSDL endpoint
     * @return the WSDL endpoint
     */
    Port getEndpoint();

    /**
     * Sets the WSDL port
     * @param port the WSDL port
     */
    void setPort(Port port);

    /**
     * Returns the WSDL binding.
     * @return the WSDL binding
     */
    javax.wsdl.Binding getBinding();

    /**
     * Sets the WSDL binding
     * @param binding the WSDL binding
     */
    void setBinding(javax.wsdl.Binding binding);

    /**
     * Returns the WSDL definition that was specified by the
     * user either via and interface.wsdl or via a wsdlElement 
     * on the binding. This may be empty if no WSDL was specified
     * explicitly in which case the generated WSDL should contain
     * a full WSDL description
     * 
     * @return the WSDL definition
     */
    WSDLDefinition getUserSpecifiedWSDLDefinition();

    /**
     * Sets the WSDL definition if one was specified by the user in the
     * composite file either via and interface.wsdl or via a wsdlElement 
     * on the binding
     * 
     * @param wsdlDefinition the WSDL definition
     */
    void setUserSpecifiedWSDLDefinition(WSDLDefinition wsdlDefinition);

    /**
     * Returns the WSDL namespace.
     * @return the WSDL namespace
     */
    String getNamespace();

    /**
     * Sets the WSDL namespace
     * @param namespace the WSDL namespace
     */
    void setNamespace(String namespace);

    /**
     * Returns true if the model element is unresolved.
     *
     * @return true if the model element is unresolved.
     */
    boolean isUnresolved();

    /**
     * Sets whether the model element is unresolved.
     *
     * @param unresolved whether the model element is unresolved
     */
    void setUnresolved(boolean unresolved);

    InterfaceContract getBindingInterfaceContract();

    void setBindingInterfaceContract(InterfaceContract bindingInterfaceContract);

    Element getEndPointReference();

    void setEndPointReference(Element element);

    /**
     * Returns the generated WSDL definitions document.
     * @return the generated WSDL definitions document
     */
    Definition getGeneratedWSDLDocument();

    /**
     * Sets the generated WSDL definitions document. The WSDL is generated
     * from the component implementation
     * 
     * @param definition the generated WSDL definitions document
     */
    void setGeneratedWSDLDocument(Definition definition);
   
    /**
     * Returns string from the WSDL that represents the SOAP binding transport
     */
    String getBindingTransport();
    
    /**
     * Returns true if the WSDL style is rpc/encoded
     */
    boolean isRpcEncoded();
    
    /**
     * Returns true if the WSDL style is rpc/literal
     */
    boolean isRpcLiteral();
    
    /**
     * Returns true if the WSDL style is doc/encoded 
     */
    boolean isDocEncoded();
    
    /**
     * Returns true is the WSDL style is doc/literal
     */
    boolean isDocLiteralUnwrapped();
    
    /**
     * Returns true if the WSDL style is doc/literal/wrapped
     */
    boolean isDocLiteralWrapped();
    
    /**
     * Returns true if the WSDL style is doc/literal
     * and the mapping to the interface is bare
     */
    boolean isDocLiteralBare(); 
    
    /**
     * Returns true is the WSBinding is configured, via WSDL,
     * to use an HTTP transport
     */
    boolean isHTTPTransport();
    
    /**
     * Returns true is the WSBinding is configured, via WSDL,
     * to use a JMS transport
     */
    boolean isJMSTransport();
}  
