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

import org.apache.tuscany.sca.assembly.Composite;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.core.FactoryExtensionPoint;
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
public class DefaultCompositeBuilderExtensionPoint implements CompositeBuilderExtensionPoint {

    private ExtensionPointRegistry registry;
    private final Map<String, CompositeBuilder> builders = new HashMap<String, CompositeBuilder>();
    private boolean loaded;

    public DefaultCompositeBuilderExtensionPoint(ExtensionPointRegistry registry) {
        this.registry = registry;
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

            CompositeBuilder builder = new LazyCompositeBuilder(registry, id, builderDeclaration, this, factories, mapper);
            builders.put(id, builder);
        }
    }

    /**
     * A wrapper around a composite builder allowing lazy
     * loading and initialization of implementation providers.
     */
    private static class LazyCompositeBuilder implements CompositeBuilder, CompositeBuilderTmp {

        private ExtensionPointRegistry registry;
        private FactoryExtensionPoint factories;
        private InterfaceContractMapper mapper;
        private String id;
        private ServiceDeclaration builderDeclaration;
        private CompositeBuilder builder;
        private CompositeBuilderExtensionPoint builders;

        private LazyCompositeBuilder(ExtensionPointRegistry registry, String id, ServiceDeclaration factoryDeclaration,
                                     CompositeBuilderExtensionPoint builders, FactoryExtensionPoint factories, InterfaceContractMapper mapper) {
            this.registry = registry;
            this.id = id;
            this.builderDeclaration = factoryDeclaration;
            this.builders = builders;
            this.factories = factories;
            this.mapper = mapper;
        }

        public String getID() {
            return id;
        }

        public void build(Composite composite, Definitions definitions, Monitor monitor) throws CompositeBuilderException {
            getBuilder().build(composite, definitions, monitor);
        }
        
        public void build(Composite composite, Definitions definitions, Map<QName, List<String>> bindingBaseURIs, Monitor monitor)
        		throws CompositeBuilderException {
            ((CompositeBuilderTmp)getBuilder()).build(composite, definitions, bindingBaseURIs, monitor);
        }

        private CompositeBuilder getBuilder() {
            if (builder == null) {
                try {
                    Class<CompositeBuilder> builderClass = (Class<CompositeBuilder>)builderDeclaration.loadClass();
                    try {
                        Constructor<CompositeBuilder> constructor = builderClass.getConstructor(FactoryExtensionPoint.class, InterfaceContractMapper.class);
                        builder = constructor.newInstance(factories, mapper);
                    } catch (NoSuchMethodException e) {
                        try {
                            Constructor<CompositeBuilder> constructor = builderClass.getConstructor(CompositeBuilderExtensionPoint.class, FactoryExtensionPoint.class, InterfaceContractMapper.class);
                            builder = constructor.newInstance(builders, factories, mapper);
                        } catch (NoSuchMethodException ex) {
                            Constructor<CompositeBuilder> constructor = builderClass.getConstructor(ExtensionPointRegistry.class);
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

}
