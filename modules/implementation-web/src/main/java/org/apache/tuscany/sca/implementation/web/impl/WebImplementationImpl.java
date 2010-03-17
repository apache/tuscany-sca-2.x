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
package org.apache.tuscany.sca.implementation.web.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.tuscany.sca.assembly.Component;
import org.apache.tuscany.sca.assembly.Property;
import org.apache.tuscany.sca.assembly.Reference;
import org.apache.tuscany.sca.assembly.Service;
import org.apache.tuscany.sca.assembly.impl.ImplementationImpl;
import org.apache.tuscany.sca.implementation.web.WebImplementation;
import org.apache.tuscany.sca.runtime.RuntimeComponent;


/**
 * The model representing an Web implementation in an SCA assembly model.
 */
class WebImplementationImpl extends ImplementationImpl implements WebImplementation {

    private List<Property> properties = new ArrayList<Property>();
    private List<Reference> references = new ArrayList<Reference>();

    private String webURI;
    private boolean jsClient = true;

    /**
     * Constructs a new Web implementation.
     */
    WebImplementationImpl() {
        super(TYPE);
    }

    public List<Property> getProperties() {
        return properties;
    }

    public List<Service> getServices() {
        // The Web implementation does not offer services
        return Collections.emptyList();
    }

    public List<Reference> getReferences() {
        return references;
    }

    public String getWebURI() {
        return webURI;
    }

    public void setWebURI(String webURI) {
        this.webURI = webURI;
    }

    /**
     * Use preProcess to add any references and properties dynamically
     * TODO: also support introspection and handle WEB-INF/web.componentType (spec line 503)
     */
    public void build(Component component) {
        if (!(component instanceof RuntimeComponent)) {
            return;
        }
        RuntimeComponent rtc = (RuntimeComponent) component;

        for (Reference reference : rtc.getReferences()) {
            if (getReference(reference.getName()) == null) {
                getReferences().add(createReference(reference));
            }
        }

        for (Property property : rtc.getProperties()) {
            if (getProperty(property.getName()) == null) {
                getProperties().add(createProperty(property));
            }
        }
    }


    protected Reference createReference(Reference reference) {
        Reference newReference;
        try {
            newReference = (Reference)reference.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError(e); // should not ever happen
        }
        return newReference;
    }


    protected Property createProperty(Property property) {
        Property newProperty;
        try {
            newProperty = (Property)property.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError(e); // should not ever happen
        }
        return newProperty;
    }

    public boolean getJSClient() {
        return jsClient;
    }

    public void setJSClient(boolean jsClient) {
        this.jsClient = jsClient;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((webURI == null) ? 0 : webURI.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!super.equals(obj)) {
            return false;
        }
        if (!(obj instanceof WebImplementationImpl)) {
            return false;
        }
        WebImplementationImpl other = (WebImplementationImpl)obj;
        if (webURI == null) {
            if (other.webURI != null) {
                return false;
            }
        } else if (!webURI.equals(other.webURI)) {
            return false;
        }
        return true;
    }

}
