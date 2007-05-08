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

import org.apache.tuscany.core.RuntimeComponent;
import org.apache.tuscany.databinding.DataBindingExtensionPoint;
import org.apache.tuscany.implementation.java.JavaImplementation;
import org.apache.tuscany.implementation.java.context.JavaPropertyValueObjectFactory;
import org.apache.tuscany.implementation.java.impl.JavaImplementationImpl;
import org.apache.tuscany.invocation.ProxyFactory;
import org.apache.tuscany.provider.ImplementationProvider;
import org.apache.tuscany.provider.ImplementationProviderFactory;
import org.apache.tuscany.spi.component.WorkContext;

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

    public ImplementationProvider createImplementationProvider(RuntimeComponent component) {
        return new JavaImplementationProvider(component, this, proxyService, workContext, dataBindingRegistry, propertyValueObjectFactory);
    }
}
