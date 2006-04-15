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
package org.apache.tuscany.core.builder.impl;

import org.apache.tuscany.core.builder.ContextCreationException;
import org.apache.tuscany.core.builder.ContextFactory;
import org.apache.tuscany.core.builder.ObjectFactory;
import org.apache.tuscany.core.context.CompositeContext;
import org.apache.tuscany.core.context.ExternalServiceContext;
import org.apache.tuscany.core.context.impl.ExternalServiceContextImpl;
import org.apache.tuscany.core.invocation.ProxyFactory;
import org.apache.tuscany.model.assembly.Scope;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A template implementation that creates instances of {@link org.apache.tuscany.core.context.ExternalServiceContext}
 * configured with the appropriate invocation chains and bindings. This class is intended to be subclassed when
 * contributing new bindings to the runtime. The subclass serves as a marker so the binding {@link org.apache.tuscany.core.builder.WireBuilder
 *
 *
 *
 * }
 * responsible for setting the proper {@link org.apache.tuscany.core.invocation.TargetInvoker} on the invocation chains
 * can be notified.
 * 
 * @version $Rev$ $Date$
 */
public abstract class BaseExternalServiceContextFactory implements ContextFactory<ExternalServiceContext> {

    private String name;

    private ProxyFactory proxyFactory;

    private ObjectFactory objectFactory;

    private String targetServiceName;

    private Map<String,ProxyFactory> targetProxyFactories;

    public BaseExternalServiceContextFactory(String name, ObjectFactory objectFactory) {
        assert (name != null) : "Name was null";
        assert (objectFactory != null) : "Object factory was null";
        this.name = name;
        this.objectFactory = objectFactory;
    }

    public ExternalServiceContext createContext() throws ContextCreationException {
        return new ExternalServiceContextImpl(name, proxyFactory, objectFactory);
    }

    public Scope getScope() {
        return Scope.MODULE;
    }

    public String getName() {
        return name;
    }

    public void prepare() {
    }

    public void addTargetProxyFactory(String serviceName, ProxyFactory factory) {
        assert (serviceName != null) : "No service name specified";
        assert (factory != null) : "Proxy factory was null";
        this.targetServiceName = serviceName; // external services are configured with only one service
        this.proxyFactory = factory;
    }

    public ProxyFactory getTargetProxyFactory(String serviceName) {
        if (this.targetServiceName.equals(serviceName)) {
            return proxyFactory;
        } else {
            return null;
        }
    }

    public Map<String,ProxyFactory> getTargetProxyFactories() {
        if (targetProxyFactories == null) {
            targetProxyFactories = new HashMap<String, ProxyFactory> (1);
            targetProxyFactories.put(targetServiceName, proxyFactory);
        }
        return targetProxyFactories;
    }

    public void addSourceProxyFactory(String referenceName, ProxyFactory factory) {
        // no wires inside a composite from an external service
    }

    public List<ProxyFactory> getSourceProxyFactories() {
        return Collections.emptyList();
    }
    
    public void prepare(CompositeContext parent) {
        //parentContext = parent;
    }

}
