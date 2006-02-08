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

import java.util.Map;

import org.apache.tuscany.core.builder.ContextCreationException;
import org.apache.tuscany.core.builder.RuntimeConfiguration;
import org.apache.tuscany.core.context.ExternalServiceContext;
import org.apache.tuscany.core.injection.ObjectFactory;
import org.apache.tuscany.core.invocation.spi.ProxyFactory;
import org.apache.tuscany.core.system.context.SystemExternalServiceContext;
import org.apache.tuscany.model.assembly.ScopeEnum;

/**
 * Creates system type external service contexts
 * 
 * @see org.apache.tuscany.core.context.ExternalServiceContext
 * @see org.apache.tuscany.core.system.context.SystemExternalServiceContext
 * 
 * @version $Rev$ $Date$
 */
public class SystemExternalServiceRuntimeConfiguration implements RuntimeConfiguration<ExternalServiceContext> {

    // the name of the external service
    private String name;

    // the factory for returning a reference to the implementation instance of the component represented by the external service
    private ObjectFactory factory;

    // ----------------------------------
    // Constructors
    // ----------------------------------

    public SystemExternalServiceRuntimeConfiguration(String name, ObjectFactory factory) {
        assert (name != null) : "Name was null";
        assert (factory != null) : "Object factory was null";
        this.name = name;
        this.factory = factory;
    }

    // ----------------------------------
    // Methods
    // ----------------------------------

    public int getScope() {
        return ScopeEnum.MODULE;
    }

    public String getName() {
        return name;
    }

    public ExternalServiceContext createInstanceContext() throws ContextCreationException {
        return new SystemExternalServiceContext(name, factory);
    }

    // -- Proxy
    public void prepare() {
    }

    public void addTargetProxyFactory(String serviceName, ProxyFactory factory) {
        throw new UnsupportedOperationException();
    }

    public ProxyFactory getTargetProxyFactory(String serviceName) {
        return null;
    }

    public Map<String, ProxyFactory> getTargetProxyFactories() {
        return null;
    }

    public void addSourceProxyFactory(String referenceName, ProxyFactory factory) {
        throw new UnsupportedOperationException();
    }

    public ProxyFactory getSourceProxyFactory(String referenceName) {
        return null;
    }

    public Map<String, ProxyFactory> getSourceProxyFactories() {
        return null;
    }

}
