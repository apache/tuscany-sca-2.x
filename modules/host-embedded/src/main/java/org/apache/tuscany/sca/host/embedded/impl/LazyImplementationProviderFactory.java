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

import org.apache.tuscany.sca.assembly.Implementation;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.provider.ImplementationProvider;
import org.apache.tuscany.sca.provider.ImplementationProviderFactory;
import org.apache.tuscany.sca.runtime.RuntimeComponent;

/**
 * A wrapper around an implementation provider factory allowing lazy
 * loading and initialization of implementation providers.
 *
 * @version $Rev$ $Date$
 */
public class LazyImplementationProviderFactory implements ImplementationProviderFactory {

    private ExtensionPointRegistry registry;
    private String modelTypeName;
    private WeakReference<ClassLoader> classLoader;
    private String className;
    private ImplementationProviderFactory factory;
    private Class modelType;
    
    LazyImplementationProviderFactory(ExtensionPointRegistry registry, String modelTypeName, ClassLoader classLoader, String className) {
        this.registry = registry;
        this.modelTypeName = modelTypeName;
        this.classLoader = new WeakReference<ClassLoader>(classLoader);
        this.className = className;
    }

    @SuppressWarnings("unchecked")
    private ImplementationProviderFactory getFactory() {
        if (factory == null) {
            try {
                Class<ImplementationProviderFactory> factoryClass = (Class<ImplementationProviderFactory>)Class.forName(className, true, classLoader.get());
                Constructor<ImplementationProviderFactory> constructor = factoryClass.getConstructor(ExtensionPointRegistry.class);
                factory = constructor.newInstance(registry);
            } catch (Exception e) {
                throw new IllegalStateException(e);
            }
        }
        return factory;
    }

    @SuppressWarnings("unchecked")
    public ImplementationProvider createImplementationProvider(RuntimeComponent component, Implementation Implementation) {
        return getFactory().createImplementationProvider(component, Implementation);
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
