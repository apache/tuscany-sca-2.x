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

package org.apache.tuscany.sca.xsd;

import java.net.URI;
import java.util.List;

import javax.xml.namespace.QName;

import org.apache.tuscany.sca.assembly.Base;
import org.apache.ws.commons.schema.XmlSchema;
import org.apache.ws.commons.schema.XmlSchemaCollection;
import org.apache.ws.commons.schema.XmlSchemaElement;
import org.apache.ws.commons.schema.XmlSchemaType;
import org.w3c.dom.Document;

/**
 * Represents an XML Schema definition.
 *
 * @version $Rev: 633545 $ $Date: 2008-03-04 16:52:24 +0000 (Tue, 04 Mar 2008) $
 */
public interface XSDefinition extends Base {
    XmlSchemaCollection getSchemaCollection();

    void setSchemaCollection(XmlSchemaCollection schemaCollection);

    /**
     * Returns the XmlSchema definition model
     * @return the XmlSchema definition model
     */
    XmlSchema getSchema();

    /**
     * Sets the XmlSchema definition model
     * @param definition the XmlSchema definition model
     */
    void setSchema(XmlSchema definition);

    /**
     * Returns the namespace of this XmlSchema definition.
     * @return the namespace of this XmlSchema definition
     */
    String getNamespace();

    /**
     * Sets the namespace of this XmlSchema definition.
     * @param namespace the namespace of this XmlSchema definition
     */
    void setNamespace(String namespace);

    /**
     * Get the location of the XSD
     * @return
     */
    URI getLocation();

    /**
     * Set the location of the XSD
     * @param uri
     */
    void setLocation(URI uri);

    /**
     * Get the DOM representation of the XSD
     * @return
     */
    Document getDocument();

    /**
     * Set the DOM representation of the XSD
     * @param document
     */
    void setDocument(Document document);

    /**
     * Get an XSD element by QName
     * @param name The element name
     * @return The XSD element
     */
    XmlSchemaElement getXmlSchemaElement(QName name);

    /**
     * Get an XSD type by QName
     * @param name The type name
     * @return The XSD type
     */
    XmlSchemaType getXmlSchemaType(QName name);

    /**
     * Get the aggregated definitions for a facade XSDefinition
     * @return The aggregated definitions, or null if not a facade
     */
    List<XSDefinition> getAggregatedDefinitions();

    /**
     * Set the aggregated definitions for a facade XSDefinition
     * @param name The aggregated definitions
     */
    void setAggregatedDefinitions(List<XSDefinition> definitions);
}
