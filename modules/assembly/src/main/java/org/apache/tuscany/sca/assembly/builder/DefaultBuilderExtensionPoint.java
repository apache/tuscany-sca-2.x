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

package org.apache.tuscany.sca.assembly.builder;

import java.lang.reflect.Constructor;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

import org.apache.tuscany.sca.assembly.Binding;
import org.apache.tuscany.sca.assembly.Component;
import org.apache.tuscany.sca.assembly.Composite;
import org.apache.tuscany.sca.assembly.Contract;
import org.apache.tuscany.sca.assembly.Implementation;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.core.FactoryExtensionPoint;
import org.apache.tuscany.sca.core.LifeCycleListener;
import org.apache.tuscany.sca.core.UtilityExtensionPoint;
import org.apache.tuscany.sca.definitions.Definitions;
import org.apache.tuscany.sca.extensibility.ServiceDeclaration;
import org.apache.tuscany.sca.extensibility.ServiceDiscovery;
import org.apache.tuscany.sca.interfacedef.InterfaceContractMapper;
import org.apache.tuscany.sca.monitor.Monitor;

/**
 * Default implementation of a provider factory extension point.
 *
 * @version $Rev$ $Date$
 */
public class DefaultBuilderExtensionPoint implements BuilderExtensionPoint, LifeCycleListener {

    private ExtensionPointRegistry registry;
    private final Map<String, CompositeBuilder> builders = new HashMap<String, CompositeBuilder>();
    private final Map<Class<?>, BindingBuilder> bindingBuilders = new HashMap<Class<?>, BindingBuilder>();
    private final Map<Class<?>, ImplementationBuilder> implementationBuilders =
        new HashMap<Class<?>, ImplementationBuilder>();
    private boolean loaded;

    public DefaultBuilderExtensionPoint(ExtensionPointRegistry registry) {
        this.registry = registry;
    }

    public void start() {
    }

    public void stop() {
        builders.clear();
        bindingBuilders.clear();
        implementationBuilders.clear();
        loaded = false;
    }
    
    public void addCompositeBuilder(CompositeBuilder builder) {
        builders.put(builder.getID(), builder);
    }

    public void removeCompositeBuilder(CompositeBuilder builder) {
        builders.remove(builder.getID());
    }

    public CompositeBuilder getCompositeBuilder(String id) {
        loadBuilders();
        return builders.get(id);
    }

    /**
     * Load builders declared under META-INF/services.
     */
    private synchronized void loadBuilders() {
        if (loaded)
            return;

        FactoryExtensionPoint factories = registry.getExtensionPoint(FactoryExtensionPoint.class);

        UtilityExtensionPoint utils = registry.getExtensionPoint(UtilityExtensionPoint.class);
        InterfaceContractMapper mapper = utils.getUtility(InterfaceContractMapper.class);

        // Get the provider factory service declarations
        Collection<ServiceDeclaration> builderDeclarations;
        ServiceDiscovery serviceDiscovery = ServiceDiscovery.getInstance();
        try {
            builderDeclarations = serviceDiscovery.getServiceDeclarations(CompositeBuilder.class.getName());
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }

        for (ServiceDeclaration builderDeclaration : builderDeclarations) {
            Map<String, String> attributes = builderDeclaration.getAttributes();
            String id = attributes.get("id");

            CompositeBuilder builder = new LazyCompositeBuilder(id, builderDeclaration, this, factories, mapper);
            builders.put(id, builder);
        }

        try {
            builderDeclarations = serviceDiscovery.getServiceDeclarations(BindingBuilder.class);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }

        for (ServiceDeclaration builderDeclaration : builderDeclarations) {
            BindingBuilder<?> builder = new LazyBindingBuilder(builderDeclaration);
            bindingBuilders.put(builder.getModelType(), builder);
        }

        try {
            builderDeclarations = serviceDiscovery.getServiceDeclarations(ImplementationBuilder.class);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }

        for (ServiceDeclaration builderDeclaration : builderDeclarations) {
            ImplementationBuilder<?> builder = new LazyImplementationBuilder(builderDeclaration);
            implementationBuilders.put(builder.getModelType(), builder);
        }
        
        loaded = true;

    }

    public void addBindingBuilder(BindingBuilder<?> bindingBuilder) {
        bindingBuilders.put(bindingBuilder.getModelType(), bindingBuilder);
    }

    public void addImplementationBuilder(ImplementationBuilder<?> implementationBuilder) {
        implementationBuilders.put(implementationBuilder.getModelType(), implementationBuilder);
    }

    public <B extends Binding> BindingBuilder<B> getBindingBuilder(Class<B> bindingType) {
        loadBuilders();
        if (bindingType.isInterface()) {
            return (BindingBuilder<B>)bindingBuilders.get(bindingType);
        }
        Class<?>[] classes = bindingType.getInterfaces();
        for (Class<?> i : classes) {
            BindingBuilder<B> builder = (BindingBuilder<B>)bindingBuilders.get(i);
            if (builder != null) {
                return builder;
            }
        }
        return null;
    }

    public <I extends Implementation> ImplementationBuilder<I> getImplementationBuilder(Class<I> implementationType) {
        loadBuilders();
        if (implementationType.isInterface()) {
            return (ImplementationBuilder<I>)implementationBuilders.get(implementationType);
        }
        Class<?>[] classes = implementationType.getInterfaces();
        for (Class<?> i : classes) {
            ImplementationBuilder<I> builder = (ImplementationBuilder<I>)implementationBuilders.get(i);
            if (builder != null) {
                return builder;
            }
        }
        return null;
    }

    public <B extends Binding> void removeBindingBuilder(BindingBuilder<B> builder) {
        bindingBuilders.remove(builder.getModelType());
    }

    public <I extends Implementation> void removeImplementationBuilder(ImplementationBuilder<I> builder) {
        implementationBuilders.remove(builder.getModelType());
    }

    /**
     * A wrapper around a composite builder allowing lazy
     * loading and initialization of implementation providers.
     */
    private class LazyCompositeBuilder implements CompositeBuilder, DeployedCompositeBuilder {

        private FactoryExtensionPoint factories;
        private InterfaceContractMapper mapper;
        private String id;
        private ServiceDeclaration builderDeclaration;
        private CompositeBuilder builder;
        private BuilderExtensionPoint builders;

        private LazyCompositeBuilder(String id,
                                     ServiceDeclaration factoryDeclaration,
                                     BuilderExtensionPoint builders,
                                     FactoryExtensionPoint factories,
                                     InterfaceContractMapper mapper) {
            this.id = id;
            this.builderDeclaration = factoryDeclaration;
            this.builders = builders;
            this.factories = factories;
            this.mapper = mapper;
        }

        public String getID() {
            return id;
        }

        public Composite build(Composite composite, Definitions definitions, Monitor monitor)
            throws CompositeBuilderException {
            return getBuilder().build(composite, definitions, monitor);
        }

        public Composite build(Composite composite,
                          Definitions definitions,
                          Map<QName, List<String>> bindingBaseURIs,
                          Monitor monitor) throws CompositeBuilderException {
            return ((DeployedCompositeBuilder)getBuilder()).build(composite, definitions, bindingBaseURIs, monitor);
        }

        private CompositeBuilder getBuilder() {
            if (builder == null) {
                try {
                    Class<CompositeBuilder> builderClass = (Class<CompositeBuilder>)builderDeclaration.loadClass();
                    try {
                        Constructor<CompositeBuilder> constructor =
                            builderClass.getConstructor(FactoryExtensionPoint.class, InterfaceContractMapper.class);
                        builder = constructor.newInstance(factories, mapper);
                    } catch (NoSuchMethodException e) {
                        try {
                            Constructor<CompositeBuilder> constructor =
                                builderClass.getConstructor(BuilderExtensionPoint.class,
                                                            FactoryExtensionPoint.class,
                                                            InterfaceContractMapper.class);
                            builder = constructor.newInstance(builders, factories, mapper);
                        } catch (NoSuchMethodException ex) {
                            Constructor<CompositeBuilder> constructor =
                                builderClass.getConstructor(ExtensionPointRegistry.class);
                            builder = constructor.newInstance(registry);
                        }
                    }
                } catch (Exception e) {
                    throw new IllegalStateException(e);
                }
            }
            return builder;
        }

    }

    private class LazyBindingBuilder implements BindingBuilder {
        private ServiceDeclaration sd;
        private String model;
        private BindingBuilder<?> builder;
        private Class<?> modelType;

        /**
         * @param sd
         */
        public LazyBindingBuilder(ServiceDeclaration sd) {
            super();
            this.sd = sd;
            this.model = sd.getAttributes().get("model");
        }

        public void build(Component component, Contract contract, Binding binding, Monitor monitor) {
            getBuilder().build(component, contract, binding, monitor);
        }

        public Class getModelType() {
            if (modelType == null) {
                try {
                    modelType = sd.loadClass(model);
                } catch (Exception e) {
                    throw new IllegalStateException(e);
                }
            }
            return modelType;
        }

        private synchronized BindingBuilder getBuilder() {
            if (builder == null) {
                try {
                    Class<?> builderClass = sd.loadClass();
                    try {
                        Constructor<?> constructor = builderClass.getConstructor(ExtensionPointRegistry.class);
                        builder = (BindingBuilder)constructor.newInstance(registry);
                    } catch (NoSuchMethodException e) {
                        Constructor<?> constructor = builderClass.getConstructor();
                        builder = (BindingBuilder)constructor.newInstance();

                    }
                } catch (Exception e) {
                    throw new IllegalStateException(e);
                }
            }
            return builder;
        }

    }

    private class LazyImplementationBuilder implements ImplementationBuilder {
        private ServiceDeclaration sd;
        private String model;
        private ImplementationBuilder<?> builder;
        private Class<?> modelType;

        /**
         * @param sd
         */
        public LazyImplementationBuilder(ServiceDeclaration sd) {
            super();
            this.sd = sd;
            this.model = sd.getAttributes().get("model");
        }

        public void build(Component component, Implementation implementation, Monitor monitor) {
            getBuilder().build(component, implementation, monitor);
        }

        public Class getModelType() {
            if (modelType == null) {
                try {
                    modelType = sd.loadClass(model);
                } catch (Exception e) {
                    throw new IllegalStateException(e);
                }
            }
            return modelType;
        }

        private synchronized ImplementationBuilder getBuilder() {
            if (builder == null) {
                try {
                    Class<?> builderClass = sd.loadClass();
                    try {
                        Constructor<?> constructor = builderClass.getConstructor(ExtensionPointRegistry.class);
                        builder = (ImplementationBuilder)constructor.newInstance(registry);
                    } catch (NoSuchMethodException e) {
                        Constructor<?> constructor = builderClass.getConstructor();
                        builder = (ImplementationBuilder)constructor.newInstance();

                    }
                } catch (Exception e) {
                    throw new IllegalStateException(e);
                }
            }
            return builder;
        }

    }

}
