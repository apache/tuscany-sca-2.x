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

package org.apache.tuscany.sca.runtime;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.core.LifeCycleListener;
import org.apache.tuscany.sca.extensibility.ServiceDeclaration;
import org.apache.tuscany.sca.extensibility.ServiceHelper;
import org.oasisopen.sca.ServiceRuntimeException;

/**
 * Default implementation of DomainRegistryFactoryExtensionPoint
 */
public class DefaultDomainRegistryFactoryExtensionPoint implements DomainRegistryFactoryExtensionPoint,
    LifeCycleListener {
    private ExtensionPointRegistry registry;
    private boolean loaded;
    private List<DomainRegistryFactory> factories = new ArrayList<DomainRegistryFactory>();
    private Map<String, String> domainRegistryMapping = new HashMap<String, String>();

    /**
     * @param registry
     */
    public DefaultDomainRegistryFactoryExtensionPoint(ExtensionPointRegistry registry, Map<String, String> attributes) {
        super();
        this.registry = registry;
        // Populate the domainRegistryMapping
        domainRegistryMapping.putAll(attributes);
        domainRegistryMapping.remove("class");
        domainRegistryMapping.remove("ranking");
    }

    public void addDomainRegistryFactory(DomainRegistryFactory factory) {
        ServiceHelper.start(factory);
        factories.add(factory);
    }

    public List<DomainRegistryFactory> getDomainRegistryFactories() {
        load();
        return factories;
    }

    private synchronized void load() {
        if (loaded) {
            return;
        }
        try {
            Collection<ServiceDeclaration> declarations =
                registry.getServiceDiscovery().getServiceDeclarations(DomainRegistryFactory.class, true);
            for (ServiceDeclaration declaration : declarations) {
                DomainRegistryFactory factory = ServiceHelper.newInstance(registry, declaration);
                addDomainRegistryFactory(factory);
            }
        } catch (Exception e) {
            throw new ServiceRuntimeException(e);
        } finally {
            loaded = true;
        }
    }

    public void removeDomainRegistryFactory(DomainRegistryFactory factory) {
        if (factories.remove(factory)) {
            ServiceHelper.stop(factory);
        }

    }

    public void start() {
        // Empty
    }

    public void stop() {
        ServiceHelper.stop(factories);
    }

    public Map<String, String> getDomainRegistryMapping() {
        return domainRegistryMapping;
    }

}
