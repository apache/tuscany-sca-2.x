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

package org.apache.tuscany.sca.client.impl;

import java.util.Properties;

import org.apache.tuscany.sca.core.DefaultExtensionPointRegistry;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.core.FactoryExtensionPoint;
import org.apache.tuscany.sca.core.ModuleActivatorExtensionPoint;
import org.apache.tuscany.sca.core.UtilityExtensionPoint;
import org.apache.tuscany.sca.core.assembly.RuntimeAssemblyFactory;
import org.apache.tuscany.sca.runtime.DomainRegistry;
import org.apache.tuscany.sca.runtime.DomainRegistryFactory;
import org.apache.tuscany.sca.runtime.ExtensibleDomainRegistryFactory;
import org.apache.tuscany.sca.runtime.RuntimeProperties;
import org.apache.tuscany.sca.work.WorkScheduler;
import org.oasisopen.sca.NoSuchDomainException;

public class RuntimeUtils {

    public static ExtensionPointRegistry createExtensionPointRegistry() throws NoSuchDomainException {
        ExtensionPointRegistry extensionsRegistry = new DefaultExtensionPointRegistry();
        extensionsRegistry.start();

        FactoryExtensionPoint modelFactories = extensionsRegistry.getExtensionPoint(FactoryExtensionPoint.class);
        RuntimeAssemblyFactory assemblyFactory = new RuntimeAssemblyFactory(extensionsRegistry);
        modelFactories.addFactory(assemblyFactory);

        UtilityExtensionPoint utilities = extensionsRegistry.getExtensionPoint(UtilityExtensionPoint.class);
        
        Properties domainConfigProps = new Properties();
        domainConfigProps.setProperty("client", "true");
        utilities.getUtility(RuntimeProperties.class).setProperties(domainConfigProps);

        // TODO: is this needed?
        utilities.getUtility(WorkScheduler.class);

        // Initialize the Tuscany module activators
        // The module activators will be started
        extensionsRegistry.getExtensionPoint(ModuleActivatorExtensionPoint.class);
        
        return extensionsRegistry;
    }

    public static DomainRegistry getClientEndpointRegistry(ExtensionPointRegistry extensionsRegistry, String domainURI) throws NoSuchDomainException {
        DomainRegistryFactory domainRegistryFactory = ExtensibleDomainRegistryFactory.getInstance(extensionsRegistry);
        
        String registryURI = domainURI;

        // TODO: theres better ways to do this but this gets things working for now
        if (registryURI.indexOf(":") == -1) {
            registryURI = "tuscanyclient:" + registryURI;
        }
        if (registryURI.startsWith("uri:")) {
            registryURI = "tuscanyclient:" + registryURI.substring(4);
        }
        if (registryURI.startsWith("tuscany:")) {
            registryURI = "tuscanyclient:" + registryURI.substring(8);
        }

        try {
            return domainRegistryFactory.getEndpointRegistry(registryURI, domainURI);
        } catch (Exception e) {
            throw new NoSuchDomainException(domainURI, e);
        }
    }
    
    public static EndpointFinder getEndpointFinder(ExtensionPointRegistry registry) {
        EndpointFinder endpointFinder = registry.getExtensionPoint(UtilityExtensionPoint.class).getUtility(EndpointFinder.class);
        if (endpointFinder == null) {
            endpointFinder = new DefaultEndpointFinder();
        }
        return endpointFinder;
    }
}
