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

package org.apache.tuscany.sca.provider;

import java.util.HashMap;
import java.util.Map;

/**
 * Default implementation of a provider factory extension point.
 *
 * @version $Rev$ $Date$
 */
public class DefaultProviderFactoryExtensionPoint implements ProviderFactoryExtensionPoint {
    
    protected final Map<Class<?>, ProviderFactory> providerFactories = 
        new HashMap<Class<?>, ProviderFactory>();

    /**
     * The default constructor. Does nothing.
     *
     */
    public DefaultProviderFactoryExtensionPoint() {
    }

    /**
     * Add a provider factory.
     * 
     * @param providerFactory The provider factory
     */
    public void addProviderFactory(ProviderFactory providerFactory) {
        providerFactories.put(providerFactory.getModelType(), providerFactory);
    }
    
    /**
     * Remove a provider factory.
     * 
     * @param providerFactory The provider factory
     */
    public void removeProviderFactory(ProviderFactory providerFactory) {
        providerFactories.remove(providerFactory.getModelType());
    }
    
    /**
     * Returns the provider factory associated with the given model type.
     * @param modelType A model type
     * @return The provider factory associated with the given model type
     */
    public ProviderFactory getProviderFactory(Class<?> modelType) {
        Class<?>[] classes = modelType.getInterfaces();
        for (Class<?> c : classes) {
            ProviderFactory factory = providerFactories.get(c);
            if (factory != null) {
                return factory;
            }
        }
        return providerFactories.get(modelType);
    }

}
