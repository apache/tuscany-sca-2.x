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

package org.apache.tuscany.sca.endpointresolver;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.tuscany.sca.assembly.Binding;
import org.apache.tuscany.sca.assembly.Endpoint;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.extensibility.ServiceDeclaration;
import org.apache.tuscany.sca.extensibility.ServiceDiscovery;

/**
 * Default implementation of a provider factory extension point.
 *
 * @version $Rev$ $Date$
 */
public class DefaultEndpointResolverFactoryExtensionPoint implements EndpointResolverFactoryExtensionPoint {

    private ExtensionPointRegistry registry;
    private final Map<Class<?>, EndpointResolverFactory> endpointResolverFactories = new HashMap<Class<?>, EndpointResolverFactory>();
    private boolean loaded;

    /**
     * The default constructor. Does nothing.
     *
     */
    public DefaultEndpointResolverFactoryExtensionPoint(ExtensionPointRegistry registry) {
        this.registry = registry;
    }

    /**
     * Add an endpoint resolver factory.
     * 
     * @param endpointResolverFactory The resolver factory
     */
    public void addEndpointResolverFactory(EndpointResolverFactory endpointResolverFactory){
        endpointResolverFactories.put(endpointResolverFactory.getModelType(), endpointResolverFactory);
    }

    /**
     * Remove a endpoint resolver factory.
     * 
     * @param endpointResolverFactory The endpoint resolver factory
     */
    public void removeEndpointResolverFactory(EndpointResolverFactory endpointResolverFactory){
        endpointResolverFactories.remove(endpointResolverFactory.getModelType());
    }

    /**
     * Returns the provider factory associated with the given model type.
     * @param modelType A model type
     * @return The provider factory associated with the given model type
     */
    public EndpointResolverFactory getEndpointResolverFactory(Class<?> modelType) {
        loadProviderFactories();

        Class<?>[] classes = modelType.getInterfaces();
        for (Class<?> c : classes) {
            EndpointResolverFactory factory = endpointResolverFactories.get(c);
            if (factory != null) {
                return factory;
            }
        }
        return endpointResolverFactories.get(modelType);
    }


    /**
     * Load provider factories declared under META-INF/services.
     * @param registry
     */
    private void loadProviderFactories() {
        if (loaded)
            return;

        // Get the provider factory service declarations
        Set<ServiceDeclaration> factoryDeclarations;
        ServiceDiscovery serviceDiscovery = ServiceDiscovery.getInstance();
        try {
            factoryDeclarations = serviceDiscovery.getServiceDeclarations(EndpointResolverFactory.class);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }

        // Get the extension point
        EndpointResolverFactoryExtensionPoint factoryExtensionPoint =
            registry.getExtensionPoint(EndpointResolverFactoryExtensionPoint.class);
        List<EndpointResolverFactory> factories = new ArrayList<EndpointResolverFactory>();

        for (ServiceDeclaration factoryDeclaration : factoryDeclarations) {
            Map<String, String> attributes = factoryDeclaration.getAttributes();

            // Find the model type that identifies this resolver
            String modelTypeName = attributes.get("model");

            // Create a provider factory wrapper and register it
            EndpointResolverFactory factory =
                new LazyEndpointResolverFactory(registry, modelTypeName, factoryDeclaration);
            factoryExtensionPoint.addEndpointResolverFactory(factory);
            factories.add(factory);
        }

        loaded = true;
    }

    /**
     * A wrapper around an endpoint provider factory allowing lazy
     * loading and initialization of endpoint providers.
     */
    private class LazyEndpointResolverFactory implements EndpointResolverFactory {
        private ExtensionPointRegistry registry;
        private String modelTypeName;
        private ServiceDeclaration providerClass;
        private EndpointResolverFactory factory;
        private Class modelType;

        private LazyEndpointResolverFactory(ExtensionPointRegistry registry,
                                            String modelTypeName,
                                            ServiceDeclaration providerClass) {
            this.registry = registry;
            this.modelTypeName = modelTypeName;
            this.providerClass = providerClass;
        }

        @SuppressWarnings("unchecked")
        private EndpointResolverFactory getFactory() {
            if (factory == null) {
                try {
                    Class<EndpointResolverFactory> factoryClass = (Class<EndpointResolverFactory>)providerClass.loadClass();
                    Constructor<EndpointResolverFactory> constructor =
                        factoryClass.getConstructor(ExtensionPointRegistry.class);
                    factory = constructor.newInstance(registry);
                } catch (Exception e) {
                    throw new IllegalStateException(e);
                }
            }
            return factory;
        }

        public EndpointResolver createEndpointResolver(Endpoint endpoint, Binding binding) {
            return getFactory().createEndpointResolver(endpoint, binding);
        }

        public Class getModelType() {
            if (modelType == null) {
                try {
                    modelType = providerClass.loadClass(modelTypeName);
                } catch (Exception e) {
                    throw new IllegalStateException(e);
                }
            }
            return modelType;
        }

    }    

}
