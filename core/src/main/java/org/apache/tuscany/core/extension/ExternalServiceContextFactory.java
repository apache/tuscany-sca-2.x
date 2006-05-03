/**
 * 
 * Copyright 2005 The Apache Software Foundation or its licensors, as applicable.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package org.apache.tuscany.core.extension;

import org.apache.tuscany.core.builder.ContextCreationException;
import org.apache.tuscany.core.builder.ContextFactory;
import org.apache.tuscany.core.builder.ObjectFactory;
import org.apache.tuscany.core.context.CompositeContext;
import org.apache.tuscany.core.context.ExternalServiceContext;
import org.apache.tuscany.core.context.impl.ExternalServiceContextImpl;
import org.apache.tuscany.core.wire.TargetWireFactory;
import org.apache.tuscany.core.wire.SourceWireFactory;
import org.apache.tuscany.model.assembly.Scope;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A template implementation that creates instances of {@link org.apache.tuscany.core.context.ExternalServiceContext}
 * configured with the appropriate wire chains and bindings. This class is intended to be subclassed when
 * contributing new bindings to the runtime. The subclass serves as a marker so the binding {@link org.apache.tuscany.core.builder.WireBuilder
 *
 *
 *
 * }
 * responsible for setting the proper {@link org.apache.tuscany.core.wire.TargetInvoker} on the wire chains
 * can be notified.
 * 
 * @version $Rev$ $Date$
 */
public abstract class ExternalServiceContextFactory implements ContextFactory<ExternalServiceContext> {

    private String name;

    private TargetWireFactory targetWireFactory;

    private ObjectFactory objectFactory;

    private String targetServiceName;

    private Map<String, TargetWireFactory> targetProxyFactories;

    public ExternalServiceContextFactory(String name, ObjectFactory objectFactory) {
        assert (name != null) : "Name was null";
        assert (objectFactory != null) : "Object factory was null";
        this.name = name;
        this.objectFactory = objectFactory;
    }

    public ExternalServiceContext createContext() throws ContextCreationException {
        return new ExternalServiceContextImpl(name, targetWireFactory, objectFactory);
    }

    public Scope getScope() {
        return Scope.MODULE;
    }

    public String getName() {
        return name;
    }

    public void prepare() {
    }

    public void addTargetWireFactory(String serviceName, TargetWireFactory factory) {
        assert (serviceName != null) : "No service name specified";
        assert (factory != null) : "Proxy factory was null";
        this.targetServiceName = serviceName; // external services are configured with only one service
        this.targetWireFactory = factory;
    }

    public TargetWireFactory getTargetWireFactory(String serviceName) {
        if (this.targetServiceName.equals(serviceName)) {
            return targetWireFactory;
        } else {
            return null;
        }
    }

    public Map<String,TargetWireFactory> getTargetWireFactories() {
        if (targetProxyFactories == null) {
            targetProxyFactories = new HashMap<String, TargetWireFactory> (1);
            targetProxyFactories.put(targetServiceName, targetWireFactory);
        }
        return targetProxyFactories;
    }

    public void addSourceWireFactory(String referenceName, SourceWireFactory factory) {
        throw new UnsupportedOperationException();
    }

    public void addSourceWireFactories(String referenceName, Class referenceInterface, List<SourceWireFactory> factory, boolean multiplicity) {

    }

    public List<SourceWireFactory> getSourceWireFactories() {
        return Collections.emptyList();
    }

    public void addProperty(String propertyName, Object value) {
        throw new UnsupportedOperationException();
    }

    public void prepare(CompositeContext parent) {
        //parentContext = parent;
    }

}
