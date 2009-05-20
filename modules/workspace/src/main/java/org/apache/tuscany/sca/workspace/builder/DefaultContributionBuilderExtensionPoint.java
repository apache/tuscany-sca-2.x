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

package org.apache.tuscany.sca.workspace.builder;

import java.lang.reflect.Constructor;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.tuscany.sca.contribution.Contribution;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.core.FactoryExtensionPoint;
import org.apache.tuscany.sca.extensibility.ServiceDeclaration;
import org.apache.tuscany.sca.extensibility.ServiceDiscovery;
import org.apache.tuscany.sca.monitor.Monitor;
import org.apache.tuscany.sca.workspace.Workspace;

/**
 * Default implementation of a provider factory extension point.
 *
 * @version $Rev$ $Date$
 */
public class DefaultContributionBuilderExtensionPoint implements ContributionBuilderExtensionPoint {

    private ExtensionPointRegistry registry;
    private final Map<String, ContributionBuilder> builders = new HashMap<String, ContributionBuilder>();
    private boolean loaded;

    public DefaultContributionBuilderExtensionPoint(ExtensionPointRegistry registry) {
        this.registry = registry;
    }

    public void addContributionBuilder(ContributionBuilder builder) {
        builders.put(builder.getID(), builder);
    }

    public void removeContributionBuilder(ContributionBuilder builder) {
        builders.remove(builder.getID());
    }

    public ContributionBuilder getContributionBuilder(String id) {
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
        
        // Get the provider factory service declarations
        Collection<ServiceDeclaration> builderDeclarations;
        ServiceDiscovery serviceDiscovery = ServiceDiscovery.getInstance();
        try {
            builderDeclarations = serviceDiscovery.getServiceDeclarations(ContributionBuilder.class.getName());
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }

        for (ServiceDeclaration builderDeclaration : builderDeclarations) {
        Map<String, String> attributes = builderDeclaration.getAttributes();
            String id = attributes.get("id");

            ContributionBuilder builder = new LazyContributionBuilder(id, builderDeclaration, this, factories);
            builders.put(id, builder);
        }
    }

    /**
     * A wrapper around a contribution builder allowing lazy
     * loading and initialization of implementation providers.
     */
    private static class LazyContributionBuilder implements ContributionBuilder {

        private FactoryExtensionPoint factories;
        private String id;
        private ServiceDeclaration builderDeclaration;
        private ContributionBuilder builder;
        private ContributionBuilderExtensionPoint builders;

        private LazyContributionBuilder(String id, ServiceDeclaration factoryDeclaration,
                                     ContributionBuilderExtensionPoint builders, FactoryExtensionPoint factories) {
            this.id = id;
            this.builderDeclaration = factoryDeclaration;
            this.builders = builders;
            this.factories = factories;
        }
        
        public String getID() {
            return id;
        }
        
        public void build(Contribution contribution, Workspace workspace, Monitor monitor) throws ContributionBuilderException {
            getBuilder().build(contribution, workspace, monitor);
        }

        private ContributionBuilder getBuilder() {
            if (builder == null) {
                try {
                    Class<ContributionBuilder> builderClass = (Class<ContributionBuilder>)builderDeclaration.loadClass();
                    try {
                        Constructor<ContributionBuilder> constructor = builderClass.getConstructor(FactoryExtensionPoint.class);
                        builder = constructor.newInstance(factories);
                    } catch (NoSuchMethodException e) {
                        Constructor<ContributionBuilder> constructor = builderClass.getConstructor(ContributionBuilderExtensionPoint.class, FactoryExtensionPoint.class);
                        builder = constructor.newInstance(builders, factories);
                    }
                } catch (Exception e) {
                    throw new IllegalStateException(e);
                }
            }
            return builder;
        }

    }

}
