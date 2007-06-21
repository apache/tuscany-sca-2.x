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

package org.apache.tuscany.sca.host.embedded.impl;

import java.lang.ref.WeakReference;
import java.lang.reflect.Constructor;

import org.apache.tuscany.sca.assembly.Binding;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.provider.BindingProviderFactory;
import org.apache.tuscany.sca.provider.ReferenceBindingProvider;
import org.apache.tuscany.sca.provider.ServiceBindingProvider;
import org.apache.tuscany.sca.runtime.RuntimeComponent;
import org.apache.tuscany.sca.runtime.RuntimeComponentReference;
import org.apache.tuscany.sca.runtime.RuntimeComponentService;

/**
 * A wrapper around an implementation provider factory allowing lazy
 * loading and initialization of implementation providers.
 *
 * @version $Rev$ $Date$
 */
public class LazyBindingProviderFactory implements BindingProviderFactory {

    private ExtensionPointRegistry registry;
    private String modelTypeName;
    private WeakReference<ClassLoader> classLoader;
    private String className;
    private BindingProviderFactory factory;
    private Class modelType;
    
    LazyBindingProviderFactory(ExtensionPointRegistry registry, String modelTypeName, ClassLoader classLoader, String className) {
        this.registry = registry;
        this.modelTypeName = modelTypeName;
        this.classLoader = new WeakReference<ClassLoader>(classLoader);
        this.className = className;
    }

    @SuppressWarnings("unchecked")
    private BindingProviderFactory getFactory() {
        if (factory == null) {
            try {
                Class<BindingProviderFactory> factoryClass = (Class<BindingProviderFactory>)Class.forName(className, true, classLoader.get());
                Constructor<BindingProviderFactory> constructor = factoryClass.getConstructor(ExtensionPointRegistry.class);
                factory = constructor.newInstance(registry);
            } catch (Exception e) {
                throw new IllegalStateException(e);
            }
        }
        return factory;
    }

    @SuppressWarnings("unchecked")
    public ReferenceBindingProvider createReferenceBindingProvider(RuntimeComponent component, RuntimeComponentReference reference, Binding binding) {
        return getFactory().createReferenceBindingProvider(component, reference, binding);
    }
    
    @SuppressWarnings("unchecked")
    public ServiceBindingProvider createServiceBindingProvider(RuntimeComponent component, RuntimeComponentService service, Binding binding) {
        return getFactory().createServiceBindingProvider(component, service, binding);
    }
    
    public Class getModelType() {
        if (modelType == null) {
            try {
                modelType = Class.forName(modelTypeName, true, classLoader.get());
            } catch (Exception e) {
                throw new IllegalStateException(e);
            }
        }
        return modelType;
    }

}
