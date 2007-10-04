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

package org.apache.tuscany.sca.interfacedef.wsdl.impl;

import java.net.URI;

import org.apache.tuscany.sca.interfacedef.wsdl.XSDefinition;
import org.apache.ws.commons.schema.XmlSchema;
import org.apache.ws.commons.schema.XmlSchemaCollection;
import org.w3c.dom.Document;

/**
 * Represents a XML schema definition.
 *
 * @version $Rev$ $Date$
 */
public class XSDefinitionImpl implements XSDefinition {
    private XmlSchemaCollection schemaCollection = new XmlSchemaCollection();
    private XmlSchema schema;
    private String namespace;
    private URI location;
    private Document document;
    private boolean unresolved;
    
    protected XSDefinitionImpl() {
    }

    public XmlSchema getSchema() {
        return schema;
    }

    public void setSchema(XmlSchema definition) {
        this.schema = definition;
    }

    public boolean isUnresolved() {
        return unresolved;
    }

    public void setUnresolved(boolean undefined) {
        this.unresolved = undefined;
    }
    
    public String getNamespace() {
        if (isUnresolved()) {
            return namespace;
        } else if (schema != null) {
            return schema.getTargetNamespace();
        } else {
            return namespace;
        }
    }
    
    public void setNamespace(String namespace) {
        if (!isUnresolved()) {
            throw new IllegalStateException();
        } else {
            this.namespace = namespace;
        }
    }
    
    /**
     * @return the location
     */
    public URI getLocation() {
        return location;
    }

    /**
     * @param location the location to set
     */
    public void setLocation(URI location) {
        this.location = location;
    }

    /**
     * @return the document
     */
    public Document getDocument() {
        return document;
    }

    /**
     * @param document the document to set
     */
    public void setDocument(Document document) {
        this.document = document;
    }

    /**
     * @return the schemaCollection
     */
    public XmlSchemaCollection getSchemaCollection() {
        return schemaCollection;
    }

    /**
     * @param schemaCollection the schemaCollection to set
     */
    public void setSchemaCollection(XmlSchemaCollection schemaCollection) {
        this.schemaCollection = schemaCollection;
    }

    /**
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((location == null) ? 0 : location.hashCode());
        result = prime * result + ((namespace == null) ? 0 : namespace.hashCode());
        return result;
    }

    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (!(obj instanceof XSDefinitionImpl))
            return false;
        final XSDefinitionImpl other = (XSDefinitionImpl)obj;
        if (location == null) {
            if (other.location != null)
                return false;
        } else if (!location.equals(other.location))
            return false;
        if (namespace == null) {
            if (other.namespace != null)
                return false;
        } else if (!namespace.equals(other.namespace))
            return false;
        return true;
    }
}
