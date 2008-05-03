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

package org.apache.tuscany.sca.host.embedded.test.extension.module;

import org.apache.tuscany.sca.assembly.AssemblyFactory;
import org.apache.tuscany.sca.contribution.ModelFactoryExtensionPoint;
import org.apache.tuscany.sca.contribution.processor.StAXArtifactProcessorExtensionPoint;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.core.ModuleActivator;
import org.apache.tuscany.sca.host.embedded.test.extension.DefaultTestImplementationFactory;
import org.apache.tuscany.sca.host.embedded.test.extension.TestImplementationFactory;
import org.apache.tuscany.sca.host.embedded.test.extension.impl.TestImplementationProcessor;
import org.apache.tuscany.sca.host.embedded.test.extension.provider.TestImplementationProviderFactory;
import org.apache.tuscany.sca.interfacedef.java.JavaInterfaceFactory;
import org.apache.tuscany.sca.provider.ProviderFactoryExtensionPoint;



/**
 * Implements a module activator for the test implementation extension module.
 * 
 * @version $Rev$ $Date$
 */
public class TestModuleActivator implements ModuleActivator {

    public void start(ExtensionPointRegistry registry) {

        // Create the test  implementation factory
        ModelFactoryExtensionPoint modelFactories = registry.getExtensionPoint(ModelFactoryExtensionPoint.class);
        AssemblyFactory assemblyFactory = modelFactories.getFactory(AssemblyFactory.class);
        JavaInterfaceFactory javaFactory = modelFactories.getFactory(JavaInterfaceFactory.class);
        TestImplementationFactory testFactory = new DefaultTestImplementationFactory(assemblyFactory, javaFactory);
        modelFactories.addFactory(testFactory);

        // Add the test implementation extension to the StAXArtifactProcessor
        // extension point
        StAXArtifactProcessorExtensionPoint processors = registry.getExtensionPoint(StAXArtifactProcessorExtensionPoint.class);
        TestImplementationProcessor implementationArtifactProcessor = new TestImplementationProcessor(testFactory);
        processors.addArtifactProcessor(implementationArtifactProcessor);

        // Add the test provider factory to the ProviderFactory extension point
        ProviderFactoryExtensionPoint providerFactories = registry.getExtensionPoint(ProviderFactoryExtensionPoint.class);
        providerFactories.addProviderFactory(new TestImplementationProviderFactory());
    }

    public void stop(ExtensionPointRegistry registry) {
    }
}
