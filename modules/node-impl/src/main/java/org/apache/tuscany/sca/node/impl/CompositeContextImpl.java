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

package org.apache.tuscany.sca.node.impl;

import org.apache.tuscany.sca.assembly.Composite;
import org.apache.tuscany.sca.context.ComponentContextFactory;
import org.apache.tuscany.sca.context.CompositeContext;
import org.apache.tuscany.sca.context.ContextFactoryExtensionPoint;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.runtime.EndpointRegistry;
import org.apache.tuscany.sca.runtime.RuntimeComponent;
import org.apache.tuscany.sca.runtime.RuntimeComponentContext;

/**
 * @version $Rev$ $Date$
 */
public class CompositeContextImpl extends CompositeContext {
    private final ExtensionPointRegistry extensionPointRegistry;
    private final EndpointRegistry endpointRegistry;
    private final ComponentContextFactory componentContextFactory;
    private final Composite domainComposite;

    public CompositeContextImpl(ExtensionPointRegistry registry, EndpointRegistry endpointRegistry, Composite domainComposite) {
        this.extensionPointRegistry = registry;
        this.endpointRegistry = endpointRegistry;
        ContextFactoryExtensionPoint contextFactories = registry.getExtensionPoint(ContextFactoryExtensionPoint.class);
        this.componentContextFactory = contextFactories.getFactory(ComponentContextFactory.class);
        this.domainComposite = domainComposite;
    }

    public ExtensionPointRegistry getExtensionPointRegistry() {
        return extensionPointRegistry;
    }

    public EndpointRegistry getEndpointRegistry() {
        return endpointRegistry;
    }

    public void bindComponent(RuntimeComponent runtimeComponent) {
        RuntimeComponentContext componentContext =
            (RuntimeComponentContext)componentContextFactory.createComponentContext(this, runtimeComponent);
        runtimeComponent.setComponentContext(componentContext);
    }

    public Composite getDomainComposite() {
        return domainComposite;
    }

}
