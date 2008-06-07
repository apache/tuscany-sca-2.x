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

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.tuscany.sca.assembly.Binding;
import org.apache.tuscany.sca.assembly.Endpoint;
import org.apache.tuscany.sca.assembly.Implementation;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.extensibility.ServiceDeclaration;
import org.apache.tuscany.sca.extensibility.ServiceDiscovery;
import org.apache.tuscany.sca.runtime.RuntimeComponent;
import org.apache.tuscany.sca.runtime.RuntimeComponentReference;
import org.apache.tuscany.sca.runtime.RuntimeComponentService;

/**
 * Default implementation of a provider factory extension point.
 *
 * @version $Rev$ $Date$
 */
public class DefaultProviderFactoryExtensionPoint implements ProviderFactoryExtensionPoint {

    private ExtensionPointRegistry registry;
    private final Map<Class<?>, ProviderFactory> providerFactories = new HashMap<Class<?>, ProviderFactory>();
    private final List<PolicyProviderFactory> policyProviderFactories = new ArrayList<PolicyProviderFactory>();
    private boolean loaded;

    /**
     * The default constructor. Does nothing.
     *
     */
    public DefaultProviderFactoryExtensionPoint(ExtensionPointRegistry registry) {
        this.registry = registry;
    }

    /**
     * Add a provider factory.
     * 
     * @param providerFactory The provider factory
     */
    public void addProviderFactory(ProviderFactory providerFactory) {
        if(providerFactory instanceof PolicyProviderFactory) {
            policyProviderFactories.add((PolicyProviderFactory)providerFactory);
        } 
        providerFactories.put(providerFactory.getModelType(), providerFactory);
    }

    /**
     * Remove a provider factory.
     * 
     * @param providerFactory The provider factory
     */
    public void removeProviderFactory(ProviderFactory providerFactory) {
        if(providerFactory instanceof PolicyProviderFactory) {
            policyProviderFactories.remove((PolicyProviderFactory)providerFactory);
        }
        providerFactories.remove(providerFactory.getModelType());
    }

    /**
     * Returns the provider factory associated with the given model type.
     * @param modelType A model type
     * @return The provider factory associated with the given model type
     */
    public ProviderFactory getProviderFactory(Class<?> modelType) {
        loadProviderFactories();

        Class<?>[] classes = modelType.getInterfaces();
        for (Class<?> c : classes) {
            ProviderFactory factory = providerFactories.get(c);
            if (factory != null) {
                return factory;
            }
        }
        return providerFactories.get(modelType);
    }

    public List<PolicyProviderFactory> getPolicyProviderFactories() {
        loadProviderFactories();
        return policyProviderFactories;
    }

    /**
     * Load provider factories declared under META-INF/services.
     * @param registry
     */
    private void loadProviderFactories() {
        if (loaded)
            return;

        loadProviderFactories(BindingProviderFactory.class);
        loadProviderFactories(ImplementationProviderFactory.class);
        loadProviderFactories(PolicyProviderFactory.class);

        loaded = true;
    }

    /**
     * Load provider factories declared under META-INF/services.
     * @param registry
     * @param factoryClass
     * @return
     */
    private List<ProviderFactory> loadProviderFactories(Class<?> factoryClass) {

        // Get the provider factory service declarations
        Set<ServiceDeclaration> factoryDeclarations;
        ServiceDiscovery serviceDiscovery = ServiceDiscovery.getInstance();
        try {
            factoryDeclarations = serviceDiscovery.getServiceDeclarations(factoryClass);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }

        // Get the target extension point
        ProviderFactoryExtensionPoint factoryExtensionPoint =
            registry.getExtensionPoint(ProviderFactoryExtensionPoint.class);
        List<ProviderFactory> factories = new ArrayList<ProviderFactory>();

        for (ServiceDeclaration factoryDeclaration : factoryDeclarations) {
            Map<String, String> attributes = factoryDeclaration.getAttributes();

            // Load an implementation provider factory
            if (factoryClass == ImplementationProviderFactory.class) {
                String modelTypeName = attributes.get("model");

                // Create a provider factory wrapper and register it
                ImplementationProviderFactory factory =
                    new LazyImplementationProviderFactory(registry, modelTypeName, factoryDeclaration);
                factoryExtensionPoint.addProviderFactory(factory);
                factories.add(factory);

            } else if (factoryClass == BindingProviderFactory.class) {

                // Load a binding provider factory
                String modelTypeName = attributes.get("model");

                // Create a provider factory wrapper and register it
                BindingProviderFactory factory =
                    new LazyBindingProviderFactory(registry, modelTypeName, factoryDeclaration);
                factoryExtensionPoint.addProviderFactory(factory);
                factories.add(factory);
            } else if (factoryClass == PolicyProviderFactory.class) {
                // Load a policy provider factory
                String modelTypeName = attributes.get("model");

                // Create a provider factory wrapper and register it
                PolicyProviderFactory factory =
                    new LazyPolicyProviderFactory(registry, modelTypeName, factoryDeclaration);
                factoryExtensionPoint.addProviderFactory(factory);
                factories.add(factory);
            }
        }
        return factories;
    }

    /**
     * A wrapper around an implementation provider factory allowing lazy
     * loading and initialization of implementation providers.
     */
    private static class LazyBindingProviderFactory implements BindingProviderFactory {

        private ExtensionPointRegistry registry;
        private String modelTypeName;
        private ServiceDeclaration factoryDeclaration;
        private BindingProviderFactory factory;
        private Class modelType;

        private LazyBindingProviderFactory(ExtensionPointRegistry registry,
                                           String modelTypeName,
                                           ServiceDeclaration factoryDeclaration) {
            this.registry = registry;
            this.modelTypeName = modelTypeName;
            this.factoryDeclaration = factoryDeclaration;
        }

        @SuppressWarnings("unchecked")
        private BindingProviderFactory getFactory() {
            if (factory == null) {
                try {
                    Class<BindingProviderFactory> factoryClass =
                        (Class<BindingProviderFactory>)factoryDeclaration.loadClass();
                    Constructor<BindingProviderFactory> constructor =
                        factoryClass.getConstructor(ExtensionPointRegistry.class);
                    factory = constructor.newInstance(registry);
                } catch (Exception e) {
                    throw new IllegalStateException(e);
                }
            }
            return factory;
        }

        @SuppressWarnings("unchecked")
        public ReferenceBindingProvider createReferenceBindingProvider(RuntimeComponent component,
                                                                       RuntimeComponentReference reference,
                                                                       Binding binding) {
            return getFactory().createReferenceBindingProvider(component, reference, binding);
        }

        @SuppressWarnings("unchecked")
        public ServiceBindingProvider createServiceBindingProvider(RuntimeComponent component,
                                                                   RuntimeComponentService service,
                                                                   Binding binding) {
            return getFactory().createServiceBindingProvider(component, service, binding);
        }

        public Class getModelType() {
            if (modelType == null) {
                try {
                    modelType = factoryDeclaration.loadClass(modelTypeName);
                } catch (Exception e) {
                    throw new IllegalStateException(e);
                }
            }
            return modelType;
        }

    }

    /**
     * A wrapper around an implementation provider factory allowing lazy
     * loading and initialization of implementation providers.
     */
    private class LazyImplementationProviderFactory implements ImplementationProviderFactory {

        private ExtensionPointRegistry registry;
        private String modelTypeName;
        private ServiceDeclaration providerClass;
        private ImplementationProviderFactory factory;
        private Class modelType;

        private LazyImplementationProviderFactory(ExtensionPointRegistry registry,
                                                  String modelTypeName,
                                                  ServiceDeclaration providerClass) {
            this.registry = registry;
            this.modelTypeName = modelTypeName;
            this.providerClass = providerClass;
        }

        @SuppressWarnings("unchecked")
        private ImplementationProviderFactory getFactory() {
            if (factory == null) {
                try {
                    Class<ImplementationProviderFactory> factoryClass =
                        (Class<ImplementationProviderFactory>)providerClass.loadClass();
                    Constructor<ImplementationProviderFactory> constructor =
                        factoryClass.getConstructor(ExtensionPointRegistry.class);
                    factory = constructor.newInstance(registry);
                } catch (Exception e) {
                    throw new IllegalStateException(e);
                }
            }
            return factory;
        }

        @SuppressWarnings("unchecked")
        public ImplementationProvider createImplementationProvider(RuntimeComponent component,
                                                                   Implementation Implementation) {
            return getFactory().createImplementationProvider(component, Implementation);
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

    /**
     * A wrapper around an policy provider factory allowing lazy
     * loading and initialization of policy providers.
     */
    private class LazyPolicyProviderFactory implements PolicyProviderFactory {
        private ExtensionPointRegistry registry;
        private String modelTypeName;
        private ServiceDeclaration providerClass;
        private PolicyProviderFactory factory;
        private Class modelType;

        private LazyPolicyProviderFactory(ExtensionPointRegistry registry,
                                          String modelTypeName,
                                          ServiceDeclaration providerClass) {
            this.registry = registry;
            this.modelTypeName = modelTypeName;
            this.providerClass = providerClass;
        }

        @SuppressWarnings("unchecked")
        private PolicyProviderFactory getFactory() {
            if (factory == null) {
                try {
                    Class<PolicyProviderFactory> factoryClass = (Class<PolicyProviderFactory>)providerClass.loadClass();
                    Constructor<PolicyProviderFactory> constructor =
                        factoryClass.getConstructor(ExtensionPointRegistry.class);
                    factory = constructor.newInstance(registry);
                } catch (Exception e) {
                    throw new IllegalStateException(e);
                }
            }
            return factory;
        }

        public PolicyProvider createImplementationPolicyProvider(RuntimeComponent component,
                                                                 Implementation implementation) {
            return getFactory().createImplementationPolicyProvider(component, implementation);
        }

        public PolicyProvider createReferencePolicyProvider(RuntimeComponent component,
                                                            RuntimeComponentReference reference,
                                                            Binding binding) {
            return getFactory().createReferencePolicyProvider(component, reference, binding);
        }

        public PolicyProvider createServicePolicyProvider(RuntimeComponent component,
                                                          RuntimeComponentService service,
                                                          Binding binding) {
            return getFactory().createServicePolicyProvider(component, service, binding);
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
