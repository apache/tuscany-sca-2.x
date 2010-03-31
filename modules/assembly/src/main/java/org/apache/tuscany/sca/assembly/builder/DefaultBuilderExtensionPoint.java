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
import org.apache.tuscany.sca.assembly.Endpoint;
import org.apache.tuscany.sca.assembly.EndpointReference;
import org.apache.tuscany.sca.assembly.Implementation;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.core.FactoryExtensionPoint;
import org.apache.tuscany.sca.core.LifeCycleListener;
import org.apache.tuscany.sca.core.UtilityExtensionPoint;
import org.apache.tuscany.sca.extensibility.ServiceDeclaration;
import org.apache.tuscany.sca.extensibility.ServiceDeclarationParser;
import org.apache.tuscany.sca.extensibility.ServiceDiscovery;
import org.apache.tuscany.sca.interfacedef.InterfaceContractMapper;

/**
 * Default implementation of a provider factory extension point.
 *
 * @version $Rev$ $Date$
 */
public class DefaultBuilderExtensionPoint implements BuilderExtensionPoint, LifeCycleListener {

    private ExtensionPointRegistry registry;
    private final Map<String, CompositeBuilder> builders = new HashMap<String, CompositeBuilder>();
    private final Map<QName, BindingBuilder> bindingBuilders = new HashMap<QName, BindingBuilder>();
    private final Map<QName, ImplementationBuilder> implementationBuilders =
        new HashMap<QName, ImplementationBuilder>();
    private final Map<QName, PolicyBuilder> policyBuilders = new HashMap<QName, PolicyBuilder>();

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
        ServiceDiscovery serviceDiscovery = registry.getServiceDiscovery();
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
            bindingBuilders.put(builder.getBindingType(), builder);
        }

        try {
            builderDeclarations = serviceDiscovery.getServiceDeclarations(ImplementationBuilder.class);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }

        for (ServiceDeclaration builderDeclaration : builderDeclarations) {
            ImplementationBuilder<?> builder = new LazyImplementationBuilder(builderDeclaration);
            implementationBuilders.put(builder.getImplementationType(), builder);
        }
        
        try {
            builderDeclarations = serviceDiscovery.getServiceDeclarations(PolicyBuilder.class);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }

        for (ServiceDeclaration builderDeclaration : builderDeclarations) {
            PolicyBuilder<?> builder = new LazyPolicyBuilder(builderDeclaration);
            policyBuilders.put(builder.getPolicyType(), builder);
        }


        loaded = true;

    }

    public void addBindingBuilder(BindingBuilder<?> bindingBuilder) {
        bindingBuilders.put(bindingBuilder.getBindingType(), bindingBuilder);
    }
    
    public <B extends Binding> BindingBuilder<B> getBindingBuilder(QName bindingType) {
        loadBuilders();
        return (BindingBuilder<B>)bindingBuilders.get(bindingType);
    }

    public <B extends Binding> void removeBindingBuilder(BindingBuilder<B> builder) {
        bindingBuilders.remove(builder.getBindingType());
    }

    public void addImplementationBuilder(ImplementationBuilder<?> implementationBuilder) {
        implementationBuilders.put(implementationBuilder.getImplementationType(), implementationBuilder);
    }

    public <I extends Implementation> ImplementationBuilder<I> getImplementationBuilder(QName implementationType) {
        loadBuilders();
        return (ImplementationBuilder<I>)implementationBuilders.get(implementationType);
    }

    public <I extends Implementation> void removeImplementationBuilder(ImplementationBuilder<I> builder) {
        implementationBuilders.remove(builder.getImplementationType());
    }

    public void addPolicyBuilder(PolicyBuilder<?> policyBuilder) {
        policyBuilders.put(policyBuilder.getPolicyType(), policyBuilder);
    }

    public <B> PolicyBuilder<B> getPolicyBuilder(QName policyType) {
        loadBuilders();
        return (PolicyBuilder<B>)policyBuilders.get(policyType);
    }
    
    public Collection<PolicyBuilder> getPolicyBuilders() {
        loadBuilders();
        return policyBuilders.values();
    }

    public <B> void removePolicyBuilder(PolicyBuilder<B> builder) {
        policyBuilders.remove(builder.getPolicyType());
    }
    
    /**
     * A wrapper around a composite builder allowing lazy
     * loading and initialization of implementation providers.
     */
    private class LazyCompositeBuilder implements CompositeBuilder {

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

        public Composite build(Composite composite, BuilderContext context)
            throws CompositeBuilderException {
            return getBuilder().build(composite, context);
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
        private BindingBuilder<?> builder;
        private QName qname;;

        /**
         * @param sd
         */
        public LazyBindingBuilder(ServiceDeclaration sd) {
            super();
            this.sd = sd;
            this.qname = ServiceDeclarationParser.getQName(sd.getAttributes().get("qname"));
        }

        public void build(Component component, Contract contract, Binding binding, BuilderContext context) {
            getBuilder().build(component, contract, binding, context);
        }

        public QName getBindingType() {
            return qname;
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
        private ImplementationBuilder<?> builder;
        private QName qname;;

        /**
         * @param sd
         */
        public LazyImplementationBuilder(ServiceDeclaration sd) {
            super();
            this.sd = sd;
            this.qname = ServiceDeclarationParser.getQName(sd.getAttributes().get("qname"));
        }

        public void build(Component component, Implementation implementation, BuilderContext context) {
            getBuilder().build(component, implementation, context);
        }

        public QName getImplementationType() {
            return qname;
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

    private class LazyPolicyBuilder implements PolicyBuilder {
        private ServiceDeclaration sd;
        private PolicyBuilder<?> builder;
        private QName qname;;

        /**
         * @param sd
         */
        public LazyPolicyBuilder(ServiceDeclaration sd) {
            super();
            this.sd = sd;
            this.qname = ServiceDeclarationParser.getQName(sd.getAttributes().get("qname"));
        }

        public boolean build(Component component, Implementation implementation, BuilderContext context) {
            return getBuilder().build(component, implementation, context);
        }

        public QName getPolicyType() {
            return qname;
        }
        
        public List<QName> getSupportedBindings() {
            return getBuilder().getSupportedBindings();
        }
        
        private synchronized PolicyBuilder getBuilder() {
            if (builder == null) {
                try {
                    Class<?> builderClass = sd.loadClass();
                    try {
                        Constructor<?> constructor = builderClass.getConstructor(ExtensionPointRegistry.class);
                        builder = (PolicyBuilder)constructor.newInstance(registry);
                    } catch (NoSuchMethodException e) {
                        Constructor<?> constructor = builderClass.getConstructor();
                        builder = (PolicyBuilder)constructor.newInstance();

                    }
                } catch (Exception e) {
                    throw new IllegalStateException(e);
                }
            }
            return builder;
        }

        public boolean build(Endpoint endpoint, BuilderContext context) {
            return getBuilder().build(endpoint, context);
        }

        public boolean build(org.apache.tuscany.sca.assembly.EndpointReference endpointReference, BuilderContext context) {
            return getBuilder().build(endpointReference, context);
        }

        public boolean build(EndpointReference endpointReference, Endpoint endpoint, BuilderContext context) {
            return getBuilder().build(endpointReference, endpoint, context);
        }      
    }
}
