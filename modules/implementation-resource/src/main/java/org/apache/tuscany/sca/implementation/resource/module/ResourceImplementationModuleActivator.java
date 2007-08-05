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

package org.apache.tuscany.sca.implementation.resource.module;

import org.apache.tuscany.sca.assembly.AssemblyFactory;
import org.apache.tuscany.sca.binding.resource.DefaultHTTPResourceBindingFactory;
import org.apache.tuscany.sca.binding.resource.HTTPResourceBindingFactory;
import org.apache.tuscany.sca.binding.resource.impl.HTTPResourceBindingProcessor;
import org.apache.tuscany.sca.binding.resource.provider.HTTPResourceBindingProviderFactory;
import org.apache.tuscany.sca.contribution.ContributionFactory;
import org.apache.tuscany.sca.contribution.ModelFactoryExtensionPoint;
import org.apache.tuscany.sca.contribution.processor.StAXArtifactProcessorExtensionPoint;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.core.ModuleActivator;
import org.apache.tuscany.sca.http.ServletHost;
import org.apache.tuscany.sca.implementation.resource.DefaultResourceImplementationFactory;
import org.apache.tuscany.sca.implementation.resource.ResourceImplementationFactory;
import org.apache.tuscany.sca.implementation.resource.impl.ResourceImplementationProcessor;
import org.apache.tuscany.sca.implementation.resource.provider.ResourceImplementationProviderFactory;
import org.apache.tuscany.sca.interfacedef.java.DefaultJavaInterfaceFactory;
import org.apache.tuscany.sca.interfacedef.java.JavaInterfaceFactory;
import org.apache.tuscany.sca.interfacedef.java.introspect.ExtensibleJavaInterfaceIntrospector;
import org.apache.tuscany.sca.interfacedef.java.introspect.JavaInterfaceIntrospector;
import org.apache.tuscany.sca.interfacedef.java.introspect.JavaInterfaceIntrospectorExtensionPoint;
import org.apache.tuscany.sca.provider.ProviderFactoryExtensionPoint;

/**
 * Implements a module activator for the resource implementation extension module.
 */
public class ResourceImplementationModuleActivator implements ModuleActivator {

    public Object[] getExtensionPoints() {
        // This module extension does not contribute any new extension point
        return null;
    }

    public void start(ExtensionPointRegistry registry) {

        // Create the resource binding factory
        ModelFactoryExtensionPoint factories = registry.getExtensionPoint(ModelFactoryExtensionPoint.class);
        HTTPResourceBindingFactory bindingFactory = new DefaultHTTPResourceBindingFactory();
        factories.addFactory(bindingFactory);

        // Create the resource implementation factory
        AssemblyFactory assemblyFactory = factories.getFactory(AssemblyFactory.class);
        JavaInterfaceFactory javaFactory = new DefaultJavaInterfaceFactory();
        JavaInterfaceIntrospectorExtensionPoint visitors = registry.getExtensionPoint(JavaInterfaceIntrospectorExtensionPoint.class);
        JavaInterfaceIntrospector introspector = new ExtensibleJavaInterfaceIntrospector(javaFactory, visitors);
        ResourceImplementationFactory resourceFactory = new DefaultResourceImplementationFactory(assemblyFactory, javaFactory, introspector, bindingFactory);
        factories.addFactory(resourceFactory);
        
        // Add the resource binding extension to the StAXArtifactProcessor
        // extension point
        StAXArtifactProcessorExtensionPoint processors = registry.getExtensionPoint(StAXArtifactProcessorExtensionPoint.class);
        HTTPResourceBindingProcessor bindingArtifactProcessor = new HTTPResourceBindingProcessor(bindingFactory);
        processors.addArtifactProcessor(bindingArtifactProcessor);
        
        // Add the resource implementation extension to the StAXArtifactProcessor
        // extension point
        ContributionFactory contributionFactory = factories.getFactory(ContributionFactory.class);
        ResourceImplementationProcessor implementationArtifactProcessor = new ResourceImplementationProcessor(resourceFactory, contributionFactory);
        processors.addArtifactProcessor(implementationArtifactProcessor);

        // Add the provider factories to the provider factory extension point
        ProviderFactoryExtensionPoint providerFactories = registry.getExtensionPoint(ProviderFactoryExtensionPoint.class);
        ServletHost servletHost = registry.getExtensionPoint(ServletHost.class);
        providerFactories.addProviderFactory(new HTTPResourceBindingProviderFactory(servletHost));
        providerFactories.addProviderFactory(new ResourceImplementationProviderFactory());
       
    }

    public void stop(ExtensionPointRegistry registry) {
    }
}
