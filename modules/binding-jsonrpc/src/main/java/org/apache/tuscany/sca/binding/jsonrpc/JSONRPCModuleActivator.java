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

package org.apache.tuscany.sca.binding.jsonrpc;

import org.apache.tuscany.sca.assembly.Binding;
import org.apache.tuscany.sca.contribution.processor.StAXArtifactProcessor;
import org.apache.tuscany.sca.contribution.processor.StAXArtifactProcessorExtensionPoint;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.core.ModuleActivator;
import org.apache.tuscany.sca.http.ServletHost;
import org.apache.tuscany.sca.provider.BindingProviderFactory;
import org.apache.tuscany.sca.provider.ProviderFactory;
import org.apache.tuscany.sca.provider.ProviderFactoryExtensionPoint;
import org.apache.tuscany.sca.provider.ReferenceBindingProvider;
import org.apache.tuscany.sca.provider.ServiceBindingProvider;
import org.apache.tuscany.sca.runtime.RuntimeComponent;
import org.apache.tuscany.sca.runtime.RuntimeComponentReference;
import org.apache.tuscany.sca.runtime.RuntimeComponentService;

/**
 * A module activator for the JSONRPC binding extension.
 */
public class JSONRPCModuleActivator implements ModuleActivator {
    
    public void start(ExtensionPointRegistry registry) {
        
        StAXArtifactProcessorExtensionPoint processors = registry.getExtensionPoint(StAXArtifactProcessorExtensionPoint.class);
        processors.addArtifactProcessor(new JSONRPCSCDLProcessor());

        final ServletHost servletHost = registry.getExtensionPoint(ServletHost.class);
        
        ProviderFactoryExtensionPoint providerFactories = registry.getExtensionPoint(ProviderFactoryExtensionPoint.class);
        providerFactories.addProviderFactory(new BindingProviderFactory(){
            public ReferenceBindingProvider createReferenceBindingProvider(RuntimeComponent rc, RuntimeComponentReference rcf, Binding b) {
                return null; // TODO: jsonrpc references not yet implemented
            }
            public ServiceBindingProvider createServiceBindingProvider(RuntimeComponent rc, RuntimeComponentService rcs, Binding b) {
                return new JSONRPCServiceBindingProvider(rc, rcs, (JSONRPCBinding) b, servletHost);
            }
            public Class getModelType() {
                return JSONRPCBinding.class;
            }});           
    }

    public void stop(ExtensionPointRegistry registry) {        
        StAXArtifactProcessorExtensionPoint staxProcessors = registry.getExtensionPoint(StAXArtifactProcessorExtensionPoint.class);
        if (staxProcessors != null) {
            StAXArtifactProcessor processor = staxProcessors.getProcessor(JSONRPCSCDLProcessor.class);
            if (processor != null) {
                staxProcessors.removeArtifactProcessor(processor);
            }
        }

        ProviderFactoryExtensionPoint providerFactories = registry.getExtensionPoint(ProviderFactoryExtensionPoint.class);
        if (providerFactories != null) {
            ProviderFactory factory = providerFactories.getProviderFactory(JSONRPCBinding.class);
            if (factory != null) {
                providerFactories.removeProviderFactory(factory);
            }
        }
    }

    public Object[] getExtensionPoints() {
        return null; // No extensionPoints being contributed here
    }
}
