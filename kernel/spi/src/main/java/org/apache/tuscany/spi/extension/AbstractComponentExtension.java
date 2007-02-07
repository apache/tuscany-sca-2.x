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

import java.util.Map;
import java.net.URI;

import org.osoa.sca.ComponentContext;
import org.osoa.sca.CallableReference;
import org.osoa.sca.ServiceReference;
import org.osoa.sca.RequestContext;

import org.apache.tuscany.spi.component.AbstractSCAObject;
import org.apache.tuscany.spi.component.Component;
import org.apache.tuscany.spi.component.CompositeComponent;
import org.apache.tuscany.spi.component.ScopeContainer;
import org.apache.tuscany.spi.model.PropertyValue;

/**
 * @version Provides support for property accessors.
 */
public abstract class AbstractComponentExtension extends AbstractSCAObject implements Component {
    protected ScopeContainer scopeContainer;
    private final ComponentContext context;
    private Map<String, PropertyValue<?>> defaultPropertyValues;

    /**
     * Initializes component name and parent.
     *
     * @param name   Name of the component.
     * @param parent Parent of the component.
     */
    public AbstractComponentExtension(URI name, CompositeComponent parent) {
        super(name, parent);
        this.context = new ComponentContextImpl();
    }

    public void setScopeContainer(ScopeContainer scopeContainer) {
        this.scopeContainer = scopeContainer;
    }

    public Map<String, PropertyValue<?>> getDefaultPropertyValues() {
        return defaultPropertyValues;
    }

    public void setDefaultPropertyValues(Map<String, PropertyValue<?>> defaultPropertyValues) {
        this.defaultPropertyValues = defaultPropertyValues;
    }

    public boolean isOptimizable() {
        return false;
    }

    public ComponentContext getComponentContext() {
        return context;
    }

    private class ComponentContextImpl implements ComponentContext {

        public String getURI() {
            return AbstractComponentExtension.this.getUri().toString();
        }

        public <B, R extends CallableReference<B>> R cast(B target) throws IllegalArgumentException {
            throw new UnsupportedOperationException();
        }

        public <B> B getService(Class<B> businessInterface, String referenceName) {
            throw new UnsupportedOperationException();
        }

        public <B> ServiceReference<B> getServiceReference(Class<B> businessInterface, String referenceName) {
            throw new UnsupportedOperationException();
        }

        public <B> B getProperty(Class<B> type, String propertyName) {
            throw new UnsupportedOperationException();
        }

        public <B> ServiceReference<B> createSelfReference(Class<B> businessInterface) {
            throw new UnsupportedOperationException();
        }

        public <B> ServiceReference<B> createSelfReference(Class<B> businessInterface, String serviceName) {
            throw new UnsupportedOperationException();
        }

        public RequestContext getRequestContext() {
            throw new UnsupportedOperationException();
        }
    }
}
