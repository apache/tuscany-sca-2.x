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
package org.apache.tuscany.interfacedef.wsdl.introspect;

import java.io.IOException;
import java.net.URL;
import java.util.List;

import javax.wsdl.Definition;
import javax.xml.namespace.QName;

import org.apache.ws.commons.schema.XmlSchema;
import org.apache.ws.commons.schema.XmlSchemaElement;
import org.apache.ws.commons.schema.XmlSchemaException;
import org.apache.ws.commons.schema.XmlSchemaType;

/**
 * A service for caching XML Schemas
 *
 * @version $Rev$ $Date$
 */
public interface XMLSchemaRegistry {
    /**
     * Load all inline schemas from the WSDL definition
     * 
     * @param definition The WSDL defintion whose types element contains a list of schemas
     * @return A list of inline schemas
     */
    List<XmlSchema> loadSchemas(Definition definition);
    
    /**
     * Loads and registers a XML schema.
     *
     * @param namespace the expected namespace, or null if any namespace should be allowed
     * @param location  the location to load the schema from
     * @return the loaded Definition
     * @throws IOException   if there was a problem reading the document
     * @throws XmlSchemaException if there was a problem parsing the schema
     */
    XmlSchema loadSchema(String namespace, URL location) throws IOException, XmlSchemaException;

    /**
     * Load and register a XML schema as specified in a XSD schemaLocation attribute.
     *
     * @param schemaLocation the value of the schemaLocation attribute
     * @param classLoader  application classloader used to support relative locations
     * @return the loaded schema
     * @throws IOException   if there was a problem reading the document
     * @throws XmlSchemaException if there was a problem parsing the schema
     */
    XmlSchema loadSchema(String schemaLocation, ClassLoader classLoader) throws IOException, XmlSchemaException;

    /**
     * Returns the XSD Element with the supplied qualified name, or null if no such element has been defined.
     *
     * @param name the qualified name of the XSD element
     * @return the XSD element for the supplied name, or null if none has been defined
     */
    XmlSchemaElement getElement(QName name);

    /**
     * Returns the XmlSchemaType with the supplied qualified name, or null if no such type has been defined.
     *
     * @param name the qualified name of the XSD type
     * @return the XSD type for the supplied name, or null if none has been defined
     */
    XmlSchemaType getType(QName name);



}
