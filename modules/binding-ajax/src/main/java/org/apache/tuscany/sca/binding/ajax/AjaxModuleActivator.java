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

package org.apache.tuscany.sca.binding.ajax;

import org.apache.tuscany.sca.assembly.Binding;
import org.apache.tuscany.sca.contribution.processor.StAXArtifactProcessor;
import org.apache.tuscany.sca.contribution.processor.StAXArtifactProcessorExtensionPoint;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.core.ModuleActivator;
import org.apache.tuscany.sca.http.ServletHost;
import org.apache.tuscany.sca.provider.BindingProviderFactory;
import org.apache.tuscany.sca.provider.ProviderFactoryExtensionPoint;
import org.apache.tuscany.sca.provider.ReferenceBindingProvider;
import org.apache.tuscany.sca.provider.ServiceBindingProvider;
import org.apache.tuscany.sca.runtime.RuntimeComponent;
import org.apache.tuscany.sca.runtime.RuntimeComponentReference;
import org.apache.tuscany.sca.runtime.RuntimeComponentService;

/**
 * Register the ajax binding SCDL processor and the 
 * ajax service and reference providers with the Tuscany runtime.
 */
public class AjaxModuleActivator implements ModuleActivator {

    public void start(ExtensionPointRegistry registry) {

        // Add the ajax binding SCDL processor to the runtime
        StAXArtifactProcessorExtensionPoint staxProcessors = registry.getExtensionPoint(StAXArtifactProcessorExtensionPoint.class);
        staxProcessors.addArtifactProcessor(new AjaxBindingSCDLProcessor());

        final ServletHost servletHost = registry.getExtensionPoint(ServletHost.class);
        
        // Add a ajax provider factory
        ProviderFactoryExtensionPoint providerFactories = registry.getExtensionPoint(ProviderFactoryExtensionPoint.class);

        providerFactories.addProviderFactory(new BindingProviderFactory() {
            public ReferenceBindingProvider createReferenceBindingProvider(RuntimeComponent rc, RuntimeComponentReference rcr, Binding b) {
                return new AjaxReferenceBindingProvider(rc, rcr, b, servletHost);
            }
            public ServiceBindingProvider createServiceBindingProvider(RuntimeComponent rc, RuntimeComponentService rcs, Binding b) {
                return new AjaxServiceBindingProvider(rc, rcs, b, servletHost);
            }
            public Class getModelType() {
                return AjaxBinding.class;
            }} );           
    }

    public void stop(ExtensionPointRegistry registry) {
        // Remove the ajax binding SCDL processor from the runtime
        StAXArtifactProcessorExtensionPoint staxProcessors = registry.getExtensionPoint(StAXArtifactProcessorExtensionPoint.class);
        staxProcessors.removeArtifactProcessor((StAXArtifactProcessor)staxProcessors.getProcessor(AjaxBindingSCDLProcessor.class));

        // Remove the ajax provider factory from the runtime
        ProviderFactoryExtensionPoint providerFactories = registry.getExtensionPoint(ProviderFactoryExtensionPoint.class);
        providerFactories.removeProviderFactory(providerFactories.getProviderFactory(AjaxBinding.class));
    }

    public Object[] getExtensionPoints() {
        return null; // not needed for ajax binding
    }

}
