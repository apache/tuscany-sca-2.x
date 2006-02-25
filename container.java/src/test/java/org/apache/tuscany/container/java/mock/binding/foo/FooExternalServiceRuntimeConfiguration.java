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
package org.apache.tuscany.container.java.mock.binding.foo;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.tuscany.core.builder.ContextCreationException;
import org.apache.tuscany.core.builder.ObjectFactory;
import org.apache.tuscany.core.builder.RuntimeConfiguration;
import org.apache.tuscany.core.context.ExternalServiceContext;
import org.apache.tuscany.core.context.impl.ExternalServiceContextImpl;
import org.apache.tuscany.core.invocation.spi.ProxyFactory;
import org.apache.tuscany.model.assembly.Scope;

/**
 * Creates instances of {@link org.apache.tuscany.core.context.ExternalServiceContext} configured with the appropriate
 * invocation chains and bindings
 * 
 * @version $Rev$ $Date$
 */
public class FooExternalServiceRuntimeConfiguration implements RuntimeConfiguration<ExternalServiceContext> {

    private String name;

    private ProxyFactory proxyFactory;

    private ObjectFactory objectFactory;

    private String targetServiceName;

    private Map targetProxyFactories;

    public FooExternalServiceRuntimeConfiguration(String name,ObjectFactory objectFactory) {
        assert (name != null) : "Name was null";
        assert (objectFactory != null) : "Object factory was null";
        this.name = name;
        this.objectFactory = objectFactory;
    }

    public ExternalServiceContext createInstanceContext() throws ContextCreationException {
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
        this.targetServiceName = name; // external services are configured with only one service
        this.proxyFactory = factory;
    }

    public ProxyFactory getTargetProxyFactory(String serviceName) {
        if (this.targetServiceName.equals(serviceName)) {
            return proxyFactory;
        } else {
            return null;
        }
    }

    public Map getTargetProxyFactories() {
        if (targetProxyFactories == null) {
            targetProxyFactories = new HashMap(1);
            targetProxyFactories.put(targetServiceName, proxyFactory);
        }
        return targetProxyFactories;
    }

    public void addSourceProxyFactory(String referenceName, ProxyFactory factory) {
        // no wires inside an aggregate from an external service
    }

    public ProxyFactory getSourceProxyFactory(String referenceName) {
        return null;
    }

    public Map getSourceProxyFactories() {
        return Collections.EMPTY_MAP;
    }

}
