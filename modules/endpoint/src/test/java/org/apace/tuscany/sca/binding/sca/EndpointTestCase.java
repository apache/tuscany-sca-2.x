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
package org.apace.tuscany.sca.binding.sca;


import java.io.File;
import java.net.URI;
import java.net.URL;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;

import org.apache.tuscany.sca.assembly.AssemblyFactory;
import org.apache.tuscany.sca.assembly.ComponentReference;
import org.apache.tuscany.sca.assembly.Composite;
import org.apache.tuscany.sca.assembly.builder.CompositeBuilder;
import org.apache.tuscany.sca.assembly.builder.CompositeBuilderExtensionPoint;
import org.apache.tuscany.sca.contribution.Contribution;
import org.apache.tuscany.sca.contribution.processor.ExtensibleStAXArtifactProcessor;
import org.apache.tuscany.sca.contribution.processor.StAXArtifactProcessor;
import org.apache.tuscany.sca.contribution.processor.StAXArtifactProcessorExtensionPoint;
import org.apache.tuscany.sca.contribution.processor.URLArtifactProcessor;
import org.apache.tuscany.sca.contribution.processor.URLArtifactProcessorExtensionPoint;
import org.apache.tuscany.sca.contribution.resolver.ExtensibleModelResolver;
import org.apache.tuscany.sca.contribution.resolver.ModelResolver;
import org.apache.tuscany.sca.contribution.resolver.ModelResolverExtensionPoint;
import org.apache.tuscany.sca.core.DefaultExtensionPointRegistry;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.core.FactoryExtensionPoint;
import org.apache.tuscany.sca.core.ModuleActivatorExtensionPoint;
import org.apache.tuscany.sca.core.UtilityExtensionPoint;
import org.apache.tuscany.sca.core.assembly.CompositeActivator;
import org.apache.tuscany.sca.core.assembly.RuntimeAssemblyFactory;
import org.apache.tuscany.sca.monitor.Monitor;
import org.apache.tuscany.sca.monitor.MonitorFactory;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

/**
 * EndpointTestCase
 *
 * @version $Rev$ $Date$
 */
public class EndpointTestCase {

    private static URLArtifactProcessor<Contribution> contributionProcessor;
    private static ModelResolverExtensionPoint modelResolvers;
    private static FactoryExtensionPoint modelFactories;
    private static AssemblyFactory assemblyFactory;
    private static XMLOutputFactory outputFactory;
    private static StAXArtifactProcessor<Object> xmlProcessor;
    private static CompositeBuilder compositeBuilder;
    private static ModelResolver modelResolver;
    private static CompositeActivator compositeActivator;
    private static ExtensionPointRegistry extensionPoints;
    private static Monitor monitor;

    @BeforeClass
    public static void init() {

        // Create extension point registry
        extensionPoints = new DefaultExtensionPointRegistry();

        // Create a monitor
        UtilityExtensionPoint utilities = extensionPoints.getExtensionPoint(UtilityExtensionPoint.class);
        MonitorFactory monitorFactory = utilities.getUtility(MonitorFactory.class);
        monitor = monitorFactory.createMonitor();

        modelFactories = extensionPoints.getExtensionPoint(FactoryExtensionPoint.class);

        // Initialize the Tuscany module activators
        extensionPoints.getExtensionPoint(ModuleActivatorExtensionPoint.class);

        // Get XML input/output factories

        XMLInputFactory inputFactory = modelFactories.getFactory(XMLInputFactory.class);
        outputFactory = modelFactories.getFactory(XMLOutputFactory.class);

        // Get contribution workspace and assembly model factories
        assemblyFactory = new RuntimeAssemblyFactory(extensionPoints);
        modelFactories.addFactory(assemblyFactory);

        // Create XML artifact processors
        StAXArtifactProcessorExtensionPoint xmlProcessorExtensions = extensionPoints.getExtensionPoint(StAXArtifactProcessorExtensionPoint.class);
        xmlProcessor = new ExtensibleStAXArtifactProcessor(xmlProcessorExtensions, inputFactory, outputFactory, monitor);

        // Create contribution content processor
        URLArtifactProcessorExtensionPoint docProcessorExtensions = extensionPoints.getExtensionPoint(URLArtifactProcessorExtensionPoint.class);
        contributionProcessor = docProcessorExtensions.getProcessor(Contribution.class);

        // Get the model resolvers
        modelResolvers = extensionPoints.getExtensionPoint(ModelResolverExtensionPoint.class);
        modelResolver = new ExtensibleModelResolver(null, modelResolvers, modelFactories, monitor);

        // Create a composite builder
        compositeBuilder = extensionPoints.getExtensionPoint(CompositeBuilderExtensionPoint.class).getCompositeBuilder("org.apache.tuscany.sca.assembly.builder.CompositeBuilder");



    }

    @Ignore
    @Test
    public void testProvider(){
        try {
            URI calculatorURI = URI.create("calcualtor");
            URL calculatorURL = new File("./target/test-classes").toURI().toURL();
            Contribution contribution = contributionProcessor.read(null, calculatorURI, calculatorURL);

            contributionProcessor.resolve(contribution, modelResolver);

            Composite composite = contribution.getDeployables().get(0);

            compositeBuilder.build(composite, null, monitor);

            ComponentReference ref = (composite.getComponents().get(0).getReferences().get(0));
/* TODO - EPR - convert to new endpoint reference
            Assert.assertEquals(1, ref.getEndpoints().size());

            Endpoint endpoint = ref.getEndpoints().get(0);

            EndpointResolverFactory<Endpoint> factory = new EndpointResolverFactoryImpl(extensionPoints);

            EndpointResolver endpointResolver = factory.createEndpointResolver(endpoint, null);

            Assert.assertNotNull(endpointResolver);
*/


        } catch(Exception ex) {
            ex.printStackTrace();
            System.out.println(ex.toString());
            Assert.fail();
        }
    }


}
