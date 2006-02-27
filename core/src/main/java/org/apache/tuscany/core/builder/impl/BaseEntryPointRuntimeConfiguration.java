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

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.tuscany.core.builder.ContextCreationException;
import org.apache.tuscany.core.builder.RuntimeConfiguration;
import org.apache.tuscany.core.builder.WireBuilder;
import org.apache.tuscany.core.context.AggregateContext;
import org.apache.tuscany.core.context.EntryPointContext;
import org.apache.tuscany.core.context.impl.EntryPointContextImpl;
import org.apache.tuscany.core.invocation.spi.ProxyFactory;
import org.apache.tuscany.core.message.MessageFactory;
import org.apache.tuscany.model.assembly.Scope;

/**
 * A template implementation that creates instances of {@link org.apache.tuscany.core.context.EntryPointContext}
 * configured with the appropriate invocation chains and bindings. This class is intended to be subclassed when
 * contributing new bindings to the runtime. The subclass serves as a marker so the binding {@link WireBuilder}
 * responsible for setting the proper {@link org.apache.tuscany.core.invocation.TargetInvoker} on the invocation chains
 * can be notified.
 * 
 * @version $Rev$ $Date$
 */
public abstract class BaseEntryPointRuntimeConfiguration implements RuntimeConfiguration<EntryPointContext> {

    private String name;

    private ProxyFactory proxyFactory;

    private String sourceReferenceName;

    private Map sourceProxyFactories;
    
    private AggregateContext parentContext;
    
    private MessageFactory messageFactory;

    public BaseEntryPointRuntimeConfiguration(String name, AggregateContext parentContext, MessageFactory messageFactory) {
        assert (name != null) : "Name was null";
        this.name = name;
        this.parentContext = parentContext;
        this.messageFactory = messageFactory;
    }

    public EntryPointContext createInstanceContext() throws ContextCreationException {
        return new EntryPointContextImpl(name, proxyFactory, parentContext, messageFactory);
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
    }

    public ProxyFactory getTargetProxyFactory(String serviceName) {
        return null;
    }

    public Map getTargetProxyFactories() {
        return Collections.EMPTY_MAP;
    }

    public void addSourceProxyFactory(String referenceName, ProxyFactory factory) {
        this.sourceReferenceName = referenceName;
        this.proxyFactory = factory;
    }

    public ProxyFactory getSourceProxyFactory(String referenceName) {
        if (this.sourceReferenceName.equals(referenceName)) {
            return proxyFactory;
        } else {
            return null;
        }
    }

    public Map getSourceProxyFactories() {
        if (sourceProxyFactories == null) {
            sourceProxyFactories = new HashMap(1);
            sourceProxyFactories.put(sourceReferenceName, proxyFactory);
        }
        return sourceProxyFactories;
    }

}
