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

import javax.wsdl.Definition;

import org.apache.tuscany.sca.interfacedef.wsdl.WSDLDefinition;
import org.apache.tuscany.sca.interfacedef.wsdl.XSDefinition;
import org.apache.ws.commons.schema.XmlSchemaCollection;

/**
 * Represents a WSDL definition.
 *
 * @version $Rev$ $Date$
 */
public class WSDLDefinitionImpl implements WSDLDefinition {
    
    private Definition definition;
    private String namespace;
    private URI location;
    private XmlSchemaCollection inlineSchemas = new XmlSchemaCollection();
    private List<XSDefinition> schemas = new ArrayList<XSDefinition>();
    private boolean unresolved;
    
    protected WSDLDefinitionImpl() {
    }

    public Definition getDefinition() {
        return definition;
    }

    public void setDefinition(Definition definition) {
        this.definition = definition;
    }
    
    public XmlSchemaCollection getInlinedSchemas() {
        return inlineSchemas;
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
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getLocation() == null) ? 0 : getLocation().hashCode());
        result = prime * result + ((getNamespace() == null) ? 0 : getNamespace().hashCode());
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
        if (getLocation() == null) {
            if (other.getLocation() != null)
                return false;
        } else if (!getLocation().equals(other.getLocation()))
            return false;
        if (getNamespace() == null) {
            if (other.getNamespace() != null)
                return false;
        } else if (!getNamespace().equals(other.getNamespace()))
            return false;
        return true;
    }
}
