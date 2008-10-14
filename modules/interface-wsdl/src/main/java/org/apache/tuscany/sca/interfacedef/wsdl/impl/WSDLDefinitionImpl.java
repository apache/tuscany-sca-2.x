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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.wsdl.Binding;
import javax.wsdl.Definition;
import javax.wsdl.Import;
import javax.wsdl.Message;
import javax.wsdl.PortType;
import javax.wsdl.Service;
import javax.wsdl.WSDLElement;
import javax.xml.namespace.QName;

import org.apache.tuscany.sca.interfacedef.wsdl.WSDLDefinition;
import org.apache.tuscany.sca.interfacedef.wsdl.WSDLObject;
import org.apache.tuscany.sca.xsd.XSDefinition;
import org.apache.ws.commons.schema.XmlSchemaCollection;
import org.apache.ws.commons.schema.XmlSchemaElement;
import org.apache.ws.commons.schema.XmlSchemaType;

/**
 * Represents a WSDL definition.
 *
 * @version $Rev$ $Date$
 */
public class WSDLDefinitionImpl implements WSDLDefinition {

    private Definition definition;
    private String namespace;
    private URI location;
    private URI uri;
    private List<WSDLDefinition> imported = new ArrayList<WSDLDefinition>();
    private List<XSDefinition> schemas = new ArrayList<XSDefinition>();
    private boolean unresolved;
    private Binding binding;

    protected WSDLDefinitionImpl() {
    }

    public Definition getDefinition() {
        return definition;
    }

    public void setDefinition(Definition definition) {
        this.definition = definition;
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
        } else if (definition != null) {
            return definition.getTargetNamespace();
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

    /*
    @Override
    public int hashCode() {
        return String.valueOf(getNamespace()).hashCode();
    }
    
    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        } else if (obj instanceof WSDLDefinition) {
            WSDLDefinition def = (WSDLDefinition)obj;
            if (getNamespace() != null) {
                return getNamespace().equals(def.getNamespace());
            } else {
                return def.getNamespace() == null;
            }
        } else {
            return false;
        }
    }
    */

    /**
     * @see org.apache.tuscany.sca.interfacedef.wsdl.WSDLDefinition#getXmlSchemas()
     */
    public List<XSDefinition> getXmlSchemas() {
        return schemas;
    }

    /**
     * @see org.apache.tuscany.sca.interfacedef.wsdl.WSDLDefinition#getLocation()
     */
    public URI getLocation() {
        return location;
    }

    /**
     * @see org.apache.tuscany.sca.interfacedef.wsdl.WSDLDefinition#setLocation(java.net.URI)
     */
    public void setLocation(URI url) {
        this.location = url;
    }

    /**
     * @see org.apache.tuscany.sca.interfacedef.wsdl.WSDLDefinition#getURI()
     */
    public URI getURI() {
        return uri;
    }

    /**
     * @see org.apache.tuscany.sca.interfacedef.wsdl.WSDLDefinition#setURI(java.net.URI)
     */
    public void setURI(URI uri) {
        this.uri = uri;
    }

    /**
     * @see org.apache.tuscany.sca.interfacedef.wsdl.WSDLDefinition#getImportedDefinitions()
     */
    public List<WSDLDefinition> getImportedDefinitions() {
        return imported;
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
        if (!(obj instanceof WSDLDefinitionImpl))
            return false;
        final WSDLDefinitionImpl other = (WSDLDefinitionImpl)obj;
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

    /**
     * @see org.apache.tuscany.sca.interfacedef.wsdl.WSDLDefinition#getXmlSchemaElement(javax.xml.namespace.QName)
     */
    public XmlSchemaElement getXmlSchemaElement(QName name) {
        XmlSchemaCollection schemaCollection = null;
        for (XSDefinition xsd : schemas) {
            if (schemaCollection == null && xsd.getSchemaCollection() != null) {
                schemaCollection = xsd.getSchemaCollection();
            }
            XmlSchemaElement element = xsd.getXmlSchemaElement(name);
            if (element != null) {
                return element;
            }
        }
        if (schemaCollection != null) {
            return schemaCollection.getElementByQName(name);
        }
        return null;
    }

    /**
     * @see org.apache.tuscany.sca.interfacedef.wsdl.WSDLDefinition#getXmlSchemaType(javax.xml.namespace.QName)
     */
    public XmlSchemaType getXmlSchemaType(QName name) {
        XmlSchemaCollection schemaCollection = null;
        for (XSDefinition xsd : schemas) {
            if (xsd.getSchemaCollection() != null) {
                schemaCollection = xsd.getSchemaCollection();
            }
            XmlSchemaType type = xsd.getXmlSchemaType(name);
            if (type != null) {
                return type;
            }
        }
        if (schemaCollection != null) {
            return schemaCollection.getTypeByQName(name);
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    private static <T extends WSDLElement> WSDLObject<T> getWSDLObject(Definition definition, Class<T> type, QName name) {
        if (definition == null) {
            return null;
        }
        Map<QName, WSDLElement> map = null;
        if (type == PortType.class) {
            map = definition.getPortTypes();
        } else if (type == Service.class) {
            map = definition.getServices();
        } else if (type == Binding.class) {
            map = definition.getBindings();
        } else if (type == Message.class) {
            map = definition.getMessages();
        } else {
            throw new IllegalArgumentException("Invalid type: " + type.getName());
        }
        if (map.containsKey(name)) {
            return (WSDLObject<T>)new WSDLObjectImpl(definition, map.get(name));
        } else {
            for (Object imports : definition.getImports().values()) {
                List<Import> importList = (List<Import>)imports;
                for (Import i : importList) {
                    definition = i.getDefinition();
                    WSDLObject<T> element = getWSDLObject(definition, type, name);
                    if (element != null) {
                        return element;
                    }
                }
            }
        }
        return null;
    }

    public <T extends WSDLElement> WSDLObject<T> getWSDLObject(Class<T> type, QName name) {
        return getWSDLObject(definition, type, name);
    }

    public Binding getBinding() {
        return binding;
    }

    public void setBinding(Binding binding) {
        this.binding = binding;
    }

}
