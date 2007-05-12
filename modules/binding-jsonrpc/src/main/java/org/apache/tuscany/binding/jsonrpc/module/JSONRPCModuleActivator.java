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

package org.apache.tuscany.binding.jsonrpc.module;

import java.util.Map;

import org.apache.tuscany.binding.jsonrpc.DefaultJSONRPCBindingFactory;
import org.apache.tuscany.binding.jsonrpc.JSONRPCBindingFactory;
import org.apache.tuscany.binding.jsonrpc.impl.JSONRPCBindingProcessor;
import org.apache.tuscany.binding.jsonrpc.provider.JSONRPCBindingProviderFactory;
import org.apache.tuscany.contribution.processor.StAXArtifactProcessorExtensionPoint;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.core.ModuleActivator;
import org.apache.tuscany.sca.http.ServletHost;
import org.apache.tuscany.sca.provider.ProviderFactoryExtensionPoint;

/**
 * A module activator for the JSONRPC binding extension.
 *
 * @version $Rev$ $Date$
 */
public class JSONRPCModuleActivator implements ModuleActivator {
    
    public Map<Class, Object> getExtensionPoints() {
        // No extensionPoints being contributed here
        return null;
    }

    public void start(ExtensionPointRegistry registry) {
        
        // Create the JSONRPC model factory
        JSONRPCBindingFactory JSONRPCFactory = new DefaultJSONRPCBindingFactory();

        // Add the JSONRPCProcessor extension
        StAXArtifactProcessorExtensionPoint processors = registry.getExtensionPoint(StAXArtifactProcessorExtensionPoint.class);
        JSONRPCBindingProcessor JSONRPCBindingProcessor = new JSONRPCBindingProcessor(JSONRPCFactory);
        processors.addArtifactProcessor(JSONRPCBindingProcessor);
        
        ProviderFactoryExtensionPoint providerFactories = registry.getExtensionPoint(ProviderFactoryExtensionPoint.class);
        ServletHost servletHost = registry.getExtensionPoint(ServletHost.class);
        providerFactories.addProviderFactory(new JSONRPCBindingProviderFactory(servletHost));           
    }

    public void stop(ExtensionPointRegistry registry) {        
    }

}
