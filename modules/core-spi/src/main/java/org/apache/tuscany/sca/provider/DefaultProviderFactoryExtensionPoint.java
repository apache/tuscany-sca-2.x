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
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.tuscany.sca.assembly.Binding;
import org.apache.tuscany.sca.assembly.Endpoint;
import org.apache.tuscany.sca.assembly.EndpointReference;
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
    private synchronized void loadProviderFactories() {
        if (loaded)
            return;

        loadProviderFactories(BindingProviderFactory.class);
        loadProviderFactories(ImplementationProviderFactory.class);
        loadProviderFactories(PolicyProviderFactory.class);
        loadProviderFactories(WireFormatProviderFactory.class);
        loadProviderFactories(OperationSelectorProviderFactory.class);

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
        Collection<ServiceDeclaration> factoryDeclarations;
        ServiceDiscovery serviceDiscovery = registry.getServiceDiscovery();
        try {
            factoryDeclarations = serviceDiscovery.getServiceDeclarations(factoryClass.getName(), true);
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
            } else if (factoryClass == WireFormatProviderFactory.class) {

                // Load a wire format provider factory
                String modelTypeName = attributes.get("model");

                // Create a provider factory wrapper and register it
                WireFormatProviderFactory factory =
                    new LazyWireFormatProviderFactory(registry, modelTypeName, factoryDeclaration);
                factoryExtensionPoint.addProviderFactory(factory);
                factories.add(factory);
            } else if (factoryClass == OperationSelectorProviderFactory.class) {

                // Load a wire format provider factory
                String modelTypeName = attributes.get("model");

                // Create a provider factory wrapper and register it
                OperationSelectorProviderFactory factory =
                    new LazyOperationSelectorProviderFactory(registry, modelTypeName, factoryDeclaration);
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
        private Class<?> modelType;

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
        public ReferenceBindingProvider createReferenceBindingProvider(EndpointReference endpointReference) {
            return getFactory().createReferenceBindingProvider(endpointReference);
        }

        @SuppressWarnings("unchecked")
        public ServiceBindingProvider createServiceBindingProvider(Endpoint endpoint) {
            return getFactory().createServiceBindingProvider(endpoint);
        }

        public Class<?> getModelType() {
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
        private Class<?> modelType;

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

        public Class<?> getModelType() {
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
        private Class<?> modelType;

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

        public PolicyProvider createImplementationPolicyProvider(RuntimeComponent component) {
            return getFactory().createImplementationPolicyProvider(component);
        }

        public PolicyProvider createReferencePolicyProvider(EndpointReference endpointReference) {
            return getFactory().createReferencePolicyProvider(endpointReference);
        }

        public PolicyProvider createServicePolicyProvider(Endpoint endpoint) {
            return getFactory().createServicePolicyProvider(endpoint);
        }

        public Class<?> getModelType() {
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
     * A wrapper around a wire format provider factory allowing lazy
     * loading and initialization of wire format providers.
     */
    private class LazyWireFormatProviderFactory implements WireFormatProviderFactory {

        private ExtensionPointRegistry registry;
        private String modelTypeName;
        private ServiceDeclaration providerClass;
        private WireFormatProviderFactory factory;
        private Class<?> modelType;

        private LazyWireFormatProviderFactory(ExtensionPointRegistry registry,
                                              String modelTypeName,
                                              ServiceDeclaration providerClass) {
            this.registry = registry;
            this.modelTypeName = modelTypeName;
            this.providerClass = providerClass;
        }

        @SuppressWarnings("unchecked")
        private WireFormatProviderFactory getFactory() {
            if (factory == null) {
                try {
                    Class<WireFormatProviderFactory> factoryClass =
                        (Class<WireFormatProviderFactory>)providerClass.loadClass();
                    Constructor<WireFormatProviderFactory> constructor =
                        factoryClass.getConstructor(ExtensionPointRegistry.class);
                    factory = constructor.newInstance(registry);
                } catch (Exception e) {
                    throw new IllegalStateException(e);
                }
            }
            return factory;
        }

        public WireFormatProvider createReferenceWireFormatProvider(RuntimeComponent component,
                                                                    RuntimeComponentReference reference,
                                                                    Binding binding){
            return getFactory().createReferenceWireFormatProvider(component, reference, binding);
        }

        public WireFormatProvider createServiceWireFormatProvider(RuntimeComponent component,
                                                                  RuntimeComponentService service,
                                                                  Binding binding){
            return getFactory().createServiceWireFormatProvider(component, service, binding);
        }

        public Class<?> getModelType() {
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
     * A wrapper around a operation selector provider factory allowing lazy
     * loading and initialization of operation selector providers.
     */
    private class LazyOperationSelectorProviderFactory implements OperationSelectorProviderFactory {

        private ExtensionPointRegistry registry;
        private String modelTypeName;
        private ServiceDeclaration providerClass;
        private OperationSelectorProviderFactory factory;
        private Class<?> modelType;

        private LazyOperationSelectorProviderFactory(ExtensionPointRegistry registry,
                                                     String modelTypeName,
                                                     ServiceDeclaration providerClass) {
            this.registry = registry;
            this.modelTypeName = modelTypeName;
            this.providerClass = providerClass;
        }

        @SuppressWarnings("unchecked")
        private OperationSelectorProviderFactory getFactory() {
            if (factory == null) {
                try {
                    Class<OperationSelectorProviderFactory> factoryClass =
                        (Class<OperationSelectorProviderFactory>)providerClass.loadClass();
                    Constructor<OperationSelectorProviderFactory> constructor =
                        factoryClass.getConstructor(ExtensionPointRegistry.class);
                    factory = constructor.newInstance(registry);
                } catch (Exception e) {
                    throw new IllegalStateException(e);
                }
            }
            return factory;
        }

        public OperationSelectorProvider createReferenceOperationSelectorProvider(RuntimeComponent component,
                                                                    RuntimeComponentReference reference,
                                                                    Binding binding){
            return getFactory().createReferenceOperationSelectorProvider(component, reference, binding);
        }

        public OperationSelectorProvider createServiceOperationSelectorProvider(RuntimeComponent component,
                                                                  RuntimeComponentService service,
                                                                  Binding binding){
            return getFactory().createServiceOperationSelectorProvider(component, service, binding);
        }

        public Class<?> getModelType() {
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
