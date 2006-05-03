/**
 * 
 * Copyright 2005 The Apache Software Foundation or its licensors, as applicable.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the
 * License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS"
 * BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */
package org.apache.tuscany.core.system.config;

import org.apache.tuscany.core.builder.ContextCreationException;
import org.apache.tuscany.core.builder.ContextFactory;
import org.apache.tuscany.core.builder.ContextResolver;
import org.apache.tuscany.spi.ObjectFactory;
import org.apache.tuscany.core.context.CompositeContext;
import org.apache.tuscany.core.context.ExternalServiceContext;
import org.apache.tuscany.core.injection.InterCompositeReferenceFactory;
import org.apache.tuscany.core.system.context.SystemExternalServiceContext;
import org.apache.tuscany.core.system.injection.AutowireObjectFactory;
import org.apache.tuscany.spi.wire.SourceWireFactory;
import org.apache.tuscany.spi.wire.TargetWireFactory;
import org.apache.tuscany.model.assembly.Scope;

import java.util.List;
import java.util.Map;

/**
 * Creates system type external service contexts
 *
 * @version $Rev$ $Date$
 * @see org.apache.tuscany.core.context.ExternalServiceContext
 * @see org.apache.tuscany.core.system.context.SystemExternalServiceContext
 */
public class SystemExternalServiceContextFactory implements ContextFactory<ExternalServiceContext>, ContextResolver {

    // the name of the external service
    private String name;

    // the factory for returning a reference to the implementation instance of the component represented by the external
    // service
    private ObjectFactory factory;

    private CompositeContext parentContext;

    public SystemExternalServiceContextFactory(String name, ObjectFactory factory) {
        assert (name != null) : "Name was null";
        assert (factory != null) : "Object factory was null";
        this.name = name;
        this.factory = factory;
    }

    public Scope getScope() {
        return Scope.MODULE;
    }

    public String getName() {
        return name;
    }

    public void addProperty(String propertyName, Object value) {
        throw new UnsupportedOperationException();
    }

    public ExternalServiceContext createContext() throws ContextCreationException {
        return new SystemExternalServiceContext(name, factory);
    }

    public void addTargetWireFactory(String serviceName, TargetWireFactory pFactory) {
        throw new UnsupportedOperationException();
    }

    public TargetWireFactory getTargetWireFactory(String serviceName) {
        return null;
    }

    public Map<String, TargetWireFactory> getTargetWireFactories() {
        return null;
    }

    public void addSourceWireFactory(String referenceName, SourceWireFactory pFactory) {
        throw new UnsupportedOperationException();
    }

    public void addSourceWireFactories(String referenceName, Class referenceInterface, List<SourceWireFactory> factory, boolean multiplicity) {

    }

    public List<SourceWireFactory> getSourceWireFactories() {
        return null;
    }

    public void prepare(CompositeContext parent) {
        parentContext = parent;
        if (factory instanceof InterCompositeReferenceFactory) {
            ((InterCompositeReferenceFactory) factory).setContextResolver(this);
        } else if (factory instanceof AutowireObjectFactory) {
            ((AutowireObjectFactory) factory).setContextResolver(this);
        }
    }

    public CompositeContext getCurrentContext() {
        return parentContext;
    }

}
