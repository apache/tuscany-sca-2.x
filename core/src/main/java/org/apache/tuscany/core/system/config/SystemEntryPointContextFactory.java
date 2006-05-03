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
import org.apache.tuscany.core.context.CompositeContext;
import org.apache.tuscany.core.context.EntryPointContext;
import org.apache.tuscany.spi.wire.TargetWireFactory;
import org.apache.tuscany.spi.wire.SourceWireFactory;
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

    private CompositeContext parentContext;

    private String targetName;

    private Class serviceInterface;

    public SystemEntryPointContextFactory(String name, String targetName, Class serviceInterface) {
        this.name = name;
        this.targetName = targetName;
        this.serviceInterface = serviceInterface;
    }

    public EntryPointContext createContext() throws ContextCreationException {
        return new SystemEntryPointContext(name, targetName, serviceInterface, this);
    }

    public Scope getScope() {
        return Scope.MODULE;
    }

    public String getName() {
        return name;
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
        throw new UnsupportedOperationException();
    }
    public void addProperty(String propertyName, Object value) {

    }

    public List<SourceWireFactory> getSourceWireFactories() {
        return null;
    }

    public void prepare(CompositeContext parent) {
        this.parentContext = parent;
    }

    public CompositeContext getCurrentContext() {
        return parentContext;
    }

}
