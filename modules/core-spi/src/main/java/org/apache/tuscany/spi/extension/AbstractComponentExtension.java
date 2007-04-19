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
package org.apache.tuscany.spi.extension;

import java.net.URI;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.tuscany.assembly.Property;
import org.apache.tuscany.spi.component.AbstractSCAObject;
import org.apache.tuscany.spi.component.Component;
import org.apache.tuscany.spi.component.DuplicateNameException;
import org.apache.tuscany.spi.component.Reference;
import org.apache.tuscany.spi.component.RegistrationException;
import org.apache.tuscany.spi.component.SCAObject;
import org.apache.tuscany.spi.component.ScopeContainer;
import org.apache.tuscany.spi.component.Service;
import org.osoa.sca.ComponentContext;

/**
 * @version Provides support for property accessors.
 */
public abstract class AbstractComponentExtension extends AbstractSCAObject implements Component {
    protected final List<Service> services = new ArrayList<Service>();
    protected final List<Reference> references = new ArrayList<Reference>();
    protected final Map<String, SCAObject> children = new ConcurrentHashMap<String, SCAObject>();
    protected ScopeContainer scopeContainer;
    private Map<String, Property> properties = new Hashtable<String, Property>();

    /**
     * Initializes component name and parent.
     *
     * @param name Name of the component.
     */
    public AbstractComponentExtension(URI name) {
        super(name);
    }

    public ScopeContainer getScopeContainer() {
        return scopeContainer;
    }

    public void setScopeContainer(ScopeContainer scopeContainer) {
        this.scopeContainer = scopeContainer;
    }

    public Map<String, Property> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, Property> properties) {
        this.properties = properties;
    }

    public boolean isOptimizable() {
        return false;
    }

    public Service getService(String name) {
        if (name == null) {
            if (services.size() == 1) {
                return services.get(0);
            } else {
                return null;
            }
        }
        SCAObject o = children.get(name);
        if (o instanceof Service) {
            return (Service) o;
        }
        return null;
    }

    public Reference getReference(String name) {
        if (name == null) {
            if (references.size() == 1) {
                return references.get(0);
            } else {
                return null;
            }
        }
        SCAObject o = children.get(name);
        if (o instanceof Reference) {
            return (Reference) o;
        }
        return null;
    }

    public void register(Service service) throws RegistrationException {
        String name = service.getUri().getFragment();
        assert name != null;
        if (children.get(name) != null) {
            String uri = service.getUri().toString();
            throw new DuplicateNameException("A service or reference is already registered with the name", uri);
        }
        children.put(name, service);
        synchronized (services) {
            services.add(service);
        }
    }

    public void register(Reference reference) throws RegistrationException {
        String name = reference.getUri().getFragment();
        assert name != null;
        if (children.get(name) != null) {
            String uri = reference.getUri().toString();
            throw new DuplicateNameException("A service or reference is already registered with the name", uri);
        }
        children.put(name, reference);
        synchronized (services) {
            references.add(reference);
        }
    }

    public ComponentContext getComponentContext() {
        // by default, a component will not give out a component context
        return null;
    }
}
