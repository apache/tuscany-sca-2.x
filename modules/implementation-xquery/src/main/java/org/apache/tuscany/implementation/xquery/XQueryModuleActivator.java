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
package org.apache.tuscany.implementation.xquery;

import org.apache.tuscany.implementation.xquery.xml.XQueryArtifactProcessor;
import org.apache.tuscany.sca.assembly.AssemblyFactory;
import org.apache.tuscany.sca.contribution.ModelFactoryExtensionPoint;
import org.apache.tuscany.sca.contribution.processor.StAXArtifactProcessorExtensionPoint;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.core.ModuleActivator;
import org.apache.tuscany.sca.databinding.DataBindingExtensionPoint;
import org.apache.tuscany.sca.databinding.TransformerExtensionPoint;
import org.apache.tuscany.sca.databinding.impl.MediatorImpl;
import org.apache.tuscany.sca.implementation.java.injection.JavaPropertyValueObjectFactory;
import org.apache.tuscany.sca.interfacedef.java.JavaInterfaceFactory;
import org.apache.tuscany.sca.provider.ProviderFactoryExtensionPoint;

/**
 * This class activates the xquery implementation module
 * @version $Rev$ $Date$
 * The following contributions are prvided: XQueryArtifactProcessor and XQueryImplementationProviderFactory
 */
public class XQueryModuleActivator implements ModuleActivator {

    private XQueryArtifactProcessor xqueryArtifactProcessor;

    public XQueryModuleActivator() {
    }

    public Object[] getExtensionPoints() {
        return null;
    }

    public void start(ExtensionPointRegistry registry) {

        ModelFactoryExtensionPoint factories = registry.getExtensionPoint(ModelFactoryExtensionPoint.class);

        DataBindingExtensionPoint dataBindings = registry.getExtensionPoint(DataBindingExtensionPoint.class);
        TransformerExtensionPoint transformers = registry.getExtensionPoint(TransformerExtensionPoint.class);

        // Tools for Java interface handling
        AssemblyFactory assemblyFactory = factories.getFactory(AssemblyFactory.class);
        JavaInterfaceFactory javaFactory = factories.getFactory(JavaInterfaceFactory.class);
        // Create the artifact processor for XQuery artifacts and add to artifact processors
        xqueryArtifactProcessor = new XQueryArtifactProcessor(assemblyFactory, javaFactory);

        StAXArtifactProcessorExtensionPoint staxProcessors =
            registry.getExtensionPoint(StAXArtifactProcessorExtensionPoint.class);
        staxProcessors.addArtifactProcessor(xqueryArtifactProcessor);

        // Create XQueryImplementationProviderFactory and add to provider factories 
        MediatorImpl mediator = new MediatorImpl(dataBindings, transformers);
        JavaPropertyValueObjectFactory javaPropertyValueObjectFactory = new JavaPropertyValueObjectFactory(mediator);
        XQueryImplementationProviderFactory factory =
            new XQueryImplementationProviderFactory(javaPropertyValueObjectFactory);

        ProviderFactoryExtensionPoint providerFactories =
            registry.getExtensionPoint(ProviderFactoryExtensionPoint.class);
        providerFactories.addProviderFactory(factory);
    }

    public void stop(ExtensionPointRegistry registry) {
        StAXArtifactProcessorExtensionPoint staxProcessors =
            registry.getExtensionPoint(StAXArtifactProcessorExtensionPoint.class);
        staxProcessors.removeArtifactProcessor(xqueryArtifactProcessor);
    }

}
