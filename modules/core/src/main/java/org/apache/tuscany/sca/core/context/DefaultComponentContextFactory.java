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

package org.apache.tuscany.sca.core.context;

import org.apache.tuscany.sca.assembly.AssemblyFactory;
import org.apache.tuscany.sca.context.ComponentContextFactory;
import org.apache.tuscany.sca.context.CompositeContext;
import org.apache.tuscany.sca.context.ContextFactoryExtensionPoint;
import org.apache.tuscany.sca.context.PropertyValueFactory;
import org.apache.tuscany.sca.context.RequestContextFactory;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.core.FactoryExtensionPoint;
import org.apache.tuscany.sca.core.UtilityExtensionPoint;
import org.apache.tuscany.sca.core.context.impl.ComponentContextImpl;
import org.apache.tuscany.sca.core.invocation.ExtensibleProxyFactory;
import org.apache.tuscany.sca.interfacedef.java.JavaInterfaceFactory;
import org.apache.tuscany.sca.runtime.CompositeActivator;
import org.apache.tuscany.sca.runtime.EndpointReferenceBinder;
import org.apache.tuscany.sca.runtime.RuntimeComponent;
import org.oasisopen.sca.ComponentContext;

/**
 * @version $Rev$ $Date$
 */
public class DefaultComponentContextFactory implements ComponentContextFactory {
    private final ExtensionPointRegistry registry;
    private AssemblyFactory assemblyFactory;
    private JavaInterfaceFactory javaInterfaceFactory;
    private CompositeActivator compositeActivator;
    private RequestContextFactory requestContextFactory;
    private PropertyValueFactory propertyFactory;
    private EndpointReferenceBinder eprBinder;
    private ExtensibleProxyFactory proxyFactory;

    public DefaultComponentContextFactory(ExtensionPointRegistry registry) {
        this.registry = registry;
        FactoryExtensionPoint factories = registry.getExtensionPoint(FactoryExtensionPoint.class);
        UtilityExtensionPoint utilities = registry.getExtensionPoint(UtilityExtensionPoint.class);
        this.assemblyFactory = factories.getFactory(AssemblyFactory.class);
        this.javaInterfaceFactory = factories.getFactory(JavaInterfaceFactory.class);
        this.compositeActivator = utilities.getUtility(CompositeActivator.class);
        this.requestContextFactory =
            registry.getExtensionPoint(ContextFactoryExtensionPoint.class).getFactory(RequestContextFactory.class);
        this.propertyFactory = factories.getFactory(PropertyValueFactory.class);
        this.eprBinder = utilities.getUtility(EndpointReferenceBinder.class);
        this.proxyFactory = ExtensibleProxyFactory.getInstance(registry);
    }

    public ComponentContext createComponentContext(CompositeContext compositeContext, RuntimeComponent component) {
        return new ComponentContextImpl(registry, assemblyFactory, javaInterfaceFactory, compositeActivator,
                                        requestContextFactory, propertyFactory, eprBinder, proxyFactory,
                                        compositeContext, component);
    }

}
