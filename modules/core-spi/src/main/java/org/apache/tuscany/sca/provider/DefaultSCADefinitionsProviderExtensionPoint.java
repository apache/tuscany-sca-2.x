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
import java.util.List;
import java.util.Set;

import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.extensibility.ServiceDeclaration;
import org.apache.tuscany.sca.extensibility.ServiceDiscovery;

/**
 * Concrete Implementation for the SCADefinitionsProviderExtensionPoint
 */
public class DefaultSCADefinitionsProviderExtensionPoint implements
    SCADefinitionsProviderExtensionPoint {

    private ExtensionPointRegistry extensionPointRegistry = null;
    
    private List<SCADefinitionsProvider> scaDefnsProviders = new ArrayList<SCADefinitionsProvider>();
    
    public DefaultSCADefinitionsProviderExtensionPoint(ExtensionPointRegistry extnPtReg) {
        this.extensionPointRegistry = extnPtReg;
    }

    public void addSCADefinitionsProvider(SCADefinitionsProvider provider) {
        scaDefnsProviders.add(provider);
    }

    public void removeSCADefinitionsProvider(SCADefinitionsProvider provider) {
        scaDefnsProviders.remove(provider);
    }

    public List<SCADefinitionsProvider> getSCADefinitionsProviders() {
        if (scaDefnsProviders.isEmpty()) {
            loadProviders();
        }
        return scaDefnsProviders;
    }

    private void loadProviders() {
        // Get the provider service declarations
        Set<ServiceDeclaration> defnProviderDecls;
        SCADefinitionsProvider aProvider = null;
        Class providerClass = null;
        Constructor constructor = null;

        try {
            defnProviderDecls =
                ServiceDiscovery.getInstance().getServiceDeclarations(SCADefinitionsProvider.class);
    
            for (ServiceDeclaration aDefnProviderDecl : defnProviderDecls) {
                providerClass = aDefnProviderDecl.loadClass();
            
                try {
                    constructor = providerClass.getConstructor();
                    aProvider = (SCADefinitionsProvider)constructor.newInstance();
                } catch (NoSuchMethodException e1) {
                        constructor = providerClass.getConstructor(ExtensionPointRegistry.class);
                        aProvider = (SCADefinitionsProvider)constructor.newInstance(extensionPointRegistry);
                } 
                
                scaDefnsProviders.add(aProvider);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }
}
