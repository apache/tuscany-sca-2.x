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

package org.apache.tuscany.implementation.java.invocation;

import java.net.URI;

import org.apache.tuscany.assembly.ComponentService;
import org.apache.tuscany.assembly.Service;
import org.apache.tuscany.core.RuntimeComponent;
import org.apache.tuscany.core.RuntimeComponentService;
import org.apache.tuscany.core.scope.CompositeScopeContainer;
import org.apache.tuscany.databinding.DataBindingExtensionPoint;
import org.apache.tuscany.implementation.java.JavaImplementation;
import org.apache.tuscany.implementation.java.context.JavaPropertyValueObjectFactory;
import org.apache.tuscany.implementation.java.impl.JavaImplementationImpl;
import org.apache.tuscany.implementation.java.impl.JavaResourceImpl;
import org.apache.tuscany.implementation.java.injection.ResourceHost;
import org.apache.tuscany.implementation.java.injection.ResourceObjectFactory;
import org.apache.tuscany.interfacedef.InterfaceContract;
import org.apache.tuscany.interfacedef.Operation;
import org.apache.tuscany.invocation.Interceptor;
import org.apache.tuscany.invocation.Invoker;
import org.apache.tuscany.invocation.ProxyFactory;
import org.apache.tuscany.provider.ImplementationActivator;
import org.apache.tuscany.provider.ImplementationProvider;
import org.apache.tuscany.provider.ImplementationProviderFactory;
import org.apache.tuscany.provider.ScopedImplementationProvider;
import org.apache.tuscany.scope.InstanceWrapper;
import org.apache.tuscany.scope.Scope;
import org.apache.tuscany.scope.ScopeContainer;
import org.apache.tuscany.scope.ScopeNotFoundException;
import org.apache.tuscany.scope.ScopeRegistry;
import org.apache.tuscany.spi.ObjectFactory;
import org.apache.tuscany.spi.component.TargetInvokerCreationException;
import org.apache.tuscany.spi.component.WorkContext;
import org.osoa.sca.ComponentContext;

/**
 * @version $Rev$ $Date$
 */
public class JavaImplementationProviderFactory extends JavaImplementationImpl implements JavaImplementation, ImplementationProviderFactory {
    private JavaPropertyValueObjectFactory propertyValueObjectFactory;
    private DataBindingExtensionPoint dataBindingRegistry;
    private ProxyFactory proxyService;
    private WorkContext workContext;

    public JavaImplementationProviderFactory(
                                      ProxyFactory proxyService,
                                      WorkContext workContext,
                                      DataBindingExtensionPoint dataBindingRegistry,
                                      JavaPropertyValueObjectFactory propertyValueObjectFactory) {
        super();
        this.proxyService = proxyService;
        this.workContext = workContext;
        this.dataBindingRegistry = dataBindingRegistry;
        this.propertyValueObjectFactory = propertyValueObjectFactory;
    }

    public ImplementationProvider createImplementationProvider() {
        return new JavaImplementationProvider(this, proxyService, workContext, dataBindingRegistry, propertyValueObjectFactory);
    }
}
