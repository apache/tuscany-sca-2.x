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
import org.apache.tuscany.core.context.AggregateContext;
import org.apache.tuscany.core.context.EntryPointContext;
import org.apache.tuscany.core.invocation.spi.ProxyFactory;
import org.apache.tuscany.core.system.context.SystemEntryPointContext;
import org.apache.tuscany.model.assembly.Scope;

import java.util.List;
import java.util.Map;

/**
 * Creates {@link SystemEntryPointContext} instances based on an entry point configuration in an assembly model
 * 
 * @version $Rev$ $Date$
 */
public class SystemEntryPointContextFactory implements ContextFactory<EntryPointContext>, ContextResolver {

    // the name of the entry point
    private String name;

    private AggregateContext parentContext;

    private String targetName;

    // ----------------------------------
    // Constructors
    // ----------------------------------

    public SystemEntryPointContextFactory(String name, String targetName) {
        this.name = name;
        this.targetName = targetName;
    }

    // ----------------------------------
    // Methods
    // ----------------------------------

    public EntryPointContext createContext() throws ContextCreationException {
        return new SystemEntryPointContext(name, targetName, this);
    }

    public Scope getScope() {
        return Scope.MODULE;
    }

    public String getName() {
        return name;
    }

    public void addTargetProxyFactory(String serviceName, ProxyFactory pFactory) {
        throw new UnsupportedOperationException();
    }

    public ProxyFactory getTargetProxyFactory(String serviceName) {
        return null;
    }

    public Map<String, ProxyFactory> getTargetProxyFactories() {
        return null;
    }

    public void addSourceProxyFactory(String referenceName, ProxyFactory pFactory) {
        throw new UnsupportedOperationException();
    }

    public List<ProxyFactory> getSourceProxyFactories() {
        return null;
    }

    public void prepare(AggregateContext parent) {
        this.parentContext = parent;
    }

    public AggregateContext getCurrentContext() {
        return parentContext;
    }

}
