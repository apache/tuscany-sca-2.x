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

package org.apache.tuscany.sca.binding.sca;



import org.apache.tuscany.sca.assembly.SCABindingFactory;

import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.core.ModelFactoryExtensionPoint;
import org.apache.tuscany.sca.core.ModuleActivator;
import org.apache.tuscany.sca.distributed.assembly.DistributedSCABinding;
import org.apache.tuscany.sca.distributed.core.DistributedSCADomainExtensionPoint;
import org.apache.tuscany.sca.distributed.host.DistributedSCADomain;
import org.apache.tuscany.sca.distributed.host.impl.DistributedSCADomainImpl;
import org.apache.tuscany.sca.provider.BindingProviderFactory;
import org.apache.tuscany.sca.provider.ProviderFactoryExtensionPoint;

/**
 * A module activator for the JMS binding extension.
 *
 * @version $Rev$ $Date$
 */
public class SCABindingModuleActivator implements ModuleActivator {
    
    public Object[] getExtensionPoints() {
        // No extensionPoints being contributed here
        return null;
    }

    public void start(ExtensionPointRegistry registry) {
        
        // get the local domain from the extension registry
        DistributedSCADomain domain = (DistributedSCADomain)registry.getExtensionPoint(DistributedSCADomainExtensionPoint.class);
        
        // Create the SCA binding model factory
        SCABindingFactory bindingFactory = new SCABindingFactoryImpl(domain, registry);
        
        // add binding gactory to the factories list
        ModelFactoryExtensionPoint factories = registry.getExtensionPoint(ModelFactoryExtensionPoint.class);
        factories.addFactory(bindingFactory);

        // Add the SCABindingProviderFactory extension
        BindingProviderFactory<DistributedSCABinding> providerFactory = new SCABindingProviderFactoryImpl(domain, registry);
        
        ProviderFactoryExtensionPoint providerFactories = registry.getExtensionPoint(ProviderFactoryExtensionPoint.class);
        providerFactories.addProviderFactory(providerFactory);
    }

    public void stop(ExtensionPointRegistry registry) {        
    }

}
