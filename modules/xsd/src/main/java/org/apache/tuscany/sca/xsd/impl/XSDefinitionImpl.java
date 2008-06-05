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

package org.apache.tuscany.sca.xsd.impl;

import java.net.URI;
import java.util.Iterator;
import java.util.List;

import javax.xml.namespace.QName;

import org.apache.tuscany.sca.xsd.XSDefinition;
import org.apache.ws.commons.schema.XmlSchema;
import org.apache.ws.commons.schema.XmlSchemaCollection;
import org.apache.ws.commons.schema.XmlSchemaElement;
import org.apache.ws.commons.schema.XmlSchemaImport;
import org.apache.ws.commons.schema.XmlSchemaInclude;
import org.apache.ws.commons.schema.XmlSchemaObject;
import org.apache.ws.commons.schema.XmlSchemaType;
import org.w3c.dom.Document;

/**
 * Represents a XML schema definition.
 *
 * @version $Rev: 582399 $ $Date: 2007-10-05 22:28:30 +0100 (Fri, 05 Oct 2007) $
 */
public class XSDefinitionImpl implements XSDefinition {
    private XmlSchemaCollection schemaCollection = new XmlSchemaCollection();
    private XmlSchema schema;
    private String namespace;
    private URI location;
    private Document document;
    private boolean unresolved;
    private List<XSDefinition> definitions;

    public XSDefinitionImpl() {
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

    public static <T extends XmlSchemaObject> T getXmlSchemaObject(XmlSchema schema, QName name, Class<T> type) {
        if (schema != null) {
            XmlSchemaObject object = null;
            if (type == XmlSchemaElement.class) {
                object = schema.getElementByName(name);
            } else if (type == XmlSchemaType.class) {
                object = schema.getTypeByName(name);
            }
            if (object != null) {
                return type.cast(object);
            }
            for (Iterator i = schema.getIncludes().getIterator(); i.hasNext();) {
                XmlSchemaObject obj = (XmlSchemaObject)i.next();
                XmlSchema ext = null;
                if (obj instanceof XmlSchemaInclude) {
                    ext = ((XmlSchemaInclude)obj).getSchema();
                }
                if (obj instanceof XmlSchemaImport) {
                    ext = ((XmlSchemaImport)obj).getSchema();
                }
                object = getXmlSchemaObject(ext, name, type);
                if (object != null) {
                    return type.cast(object);
                }
            }
        }
        return null;
    }

    public XmlSchemaElement getXmlSchemaElement(QName name) {
        if (schema != null) {
            XmlSchemaElement element = getXmlSchemaObject(schema, name, XmlSchemaElement.class);
            if (element != null) {
                return element;
            }
        }

        if (schemaCollection != null) {
            return schemaCollection.getElementByQName(name);
        }
        return null;
    }

    public XmlSchemaType getXmlSchemaType(QName name) {
        if (schema != null) {
            XmlSchemaType type = getXmlSchemaObject(schema, name, XmlSchemaType.class);
            if (type != null) {
                return type;
            }
        }
        if (schemaCollection != null) {
            return schemaCollection.getTypeByQName(name);
        }
        return null;
    }
 
    public List<XSDefinition> getAggregatedDefinitions() {
        return definitions;
    }

    public void setAggregatedDefinitions(List<XSDefinition> definitions) {
        this.definitions = definitions;
    }

}
