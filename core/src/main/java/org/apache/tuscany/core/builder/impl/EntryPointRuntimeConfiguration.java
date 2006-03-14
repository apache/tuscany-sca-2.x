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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.tuscany.core.builder.ContextCreationException;
import org.apache.tuscany.core.builder.RuntimeConfiguration;
import org.apache.tuscany.core.context.AggregateContext;
import org.apache.tuscany.core.context.EntryPointContext;
import org.apache.tuscany.core.context.impl.EntryPointContextImpl;
import org.apache.tuscany.core.invocation.spi.ProxyFactory;
import org.apache.tuscany.core.message.MessageFactory;
import org.apache.tuscany.model.assembly.Scope;

/**
 * Produces entry point contexts
 * 
 * @version $Rev$ $Date$
 */
public abstract class EntryPointRuntimeConfiguration implements RuntimeConfiguration<EntryPointContext> {

    private String name;

    private ProxyFactory proxyFactory;

    private String referenceName;

    private MessageFactory msgFactory;

    private List sourceProxyFactories;
    
    private AggregateContext parentContext;

    public EntryPointRuntimeConfiguration(String name, String referenceName, MessageFactory msgFactory) {
        assert (name != null) : "Entry point name was null";
        assert (msgFactory != null) : "Message factory was null";
        this.name = name;
        this.referenceName = referenceName;
        this.msgFactory = msgFactory;
    }

    public EntryPointContext createInstanceContext() throws ContextCreationException {
        return new EntryPointContextImpl(name, proxyFactory, msgFactory);
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
        // no wires to an entry point from with an aggregate
    }

    public ProxyFactory getTargetProxyFactory(String serviceName) {
        // no wires to an entry point from with an aggregate
        return null;
    }

    public Map<String, ProxyFactory> getTargetProxyFactories() {
        // no wires to an entry point from with an aggregate
        return Collections.EMPTY_MAP;
    }

    public void addSourceProxyFactory(String refName, ProxyFactory factory) {
        assert (refName != null) : "No reference name specified";
        assert (factory != null) : "Proxy factory was null";
        this.referenceName = refName; // entry points are configured with only one reference
        this.proxyFactory = factory;
    }

    public List getSourceProxyFactories() {
        if (sourceProxyFactories == null) {
            sourceProxyFactories = new ArrayList(1);
            sourceProxyFactories.add(proxyFactory);
        }
        return sourceProxyFactories;
    }
    
    public void prepare(AggregateContext parent) {
        parentContext = parent;
    }
}
