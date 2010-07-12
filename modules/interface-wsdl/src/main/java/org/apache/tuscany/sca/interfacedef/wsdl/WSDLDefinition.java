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

package org.apache.tuscany.sca.interfacedef.wsdl;

import java.net.URI;
import java.util.List;
import java.util.Map;

import javax.wsdl.Binding;
import javax.wsdl.Definition;
import javax.wsdl.WSDLElement;
import javax.xml.namespace.QName;

import org.apache.tuscany.sca.assembly.Base;
import org.apache.tuscany.sca.xsd.XSDefinition;
import org.apache.ws.commons.schema.XmlSchemaElement;
import org.apache.ws.commons.schema.XmlSchemaType;

/**
 * Represents a WSDL definition.
 * WSDLDefinition
 *
 * @version $Rev$ $Date$
 */
public interface WSDLDefinition extends Base {

    /**
     * Returns the WSDL definition model, if there are more than one WSDL definition under the 
     * same namespace, the definition will be a facade which imports all the physical WSDL 
     * definitions
     * 
     * @return the WSDL definition model
     */
    Definition getDefinition();

    /**
     * Sets the WSDL definition model
     * @param definition the WSDL definition model
     */
    void setDefinition(Definition definition);

    /**
     * Returns the namespace of this WSDL definition.
     * @return the namespace of this WSDL definition
     */
    String getNamespace();

    /**
     * Sets the namespace of this WSDL definition.
     * @param namespace the namespace of this WSDL definition
     */
    void setNamespace(String namespace);

    /**
     * Get a list of inline XML schema definitions
     * @return A list of inline XML schema definitions
     */
    List<XSDefinition> getXmlSchemas();

    /**
     * Get the location of the WSDL file
     * @return The location of the WSDL file
     */
    URI getLocation();

    /**
     * Set the location of the WSDL file
     * @param url
     */
    void setLocation(URI url);

    /**
     * Get the contribution artifact URI of the WSDL document
     * @return The URI of the WSDL document
     */
    URI getURI();

    /**
     * Set the contribution artifact URI of the WSDL document
     * @param uri
     */
    void setURI(URI uri);

    /**
     * Get the WSDL definitions imported by this definition
     * @return A list of imported WSDL definitions
     */
    List<WSDLDefinition> getImportedDefinitions();

    /**
     * Get an XSD element by QName
     * @param name
     * @return
     */
    XmlSchemaElement getXmlSchemaElement(QName name);

    /**
     * Get an XSD type by QName
     * @param name
     * @return
     */
    XmlSchemaType getXmlSchemaType(QName name);
    
    /**
     * Get the WSDL object by type and name
     * @param <T>
     * @param type javax.wsdl.Service/PortType/Binding/Message.class
     * @param name The QName of the object
     * @return WSDLObject
     */
    <T extends WSDLElement> WSDLObject<T> getWSDLObject(Class<T> type, QName name);

    /**
     * Get the generated binding for a WSDLDefinition
     * @return the WSDL binding
     */
    Binding getBinding();

    /**
     * Set the generated binding for a WSDLDefinition
     * @param binding the WSDL binding
     */
    void setBinding(Binding binding);
    
    /**
     * Retrieves the name of the required port type used during the WSDL resolve process
     * 
     * @return WSDL port type name
     */
    QName getNameOfPortTypeToResolve();
    
    /**
     * Sets the name of the required port type used during the WSDL resolve process
     * 
     * @param nameOfPortTypeToResolve
     */
    void setNameOfPortTypeToResolve(QName nameOfPortTypeToResolve);    
    
    /**
     * Retrieves the name of the required binding used during the WSDL resolve process
     * 
     * @return WSDL binding name
     */
    QName getNameOfBindingToResolve();
    
    /**
     * Sets the name of the required binding used during the WSDL resolve process
     * 
     * @param nameOfBindingToResolve
     */
    void setNameOfBindingToResolve(QName nameOfBindingToResolve);
    
    /**
     * Retrieves the name of the required service used during the WSDL resolve process
     * 
     * @return WSDL service name
     */
    QName getNameOfServiceToResolve();
    
    /**
     * Sets the name of the required service used during the WSDL resolve process
     * 
     * @param nameOfBindingToResolve
     */
    void setNameOfServiceToResolve(QName nameOfServiceToResolve);

    /**
     * Gets the wsdli:location attribute namespace mappings
     * @return a Map with key being namespace and value the location
     */
    Map<String, String> getWsdliLocations();    

}
