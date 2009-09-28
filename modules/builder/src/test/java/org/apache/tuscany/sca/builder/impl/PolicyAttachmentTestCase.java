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

package org.apache.tuscany.sca.builder.impl;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.apache.tuscany.sca.assembly.AssemblyFactory;
import org.apache.tuscany.sca.assembly.Base;
import org.apache.tuscany.sca.assembly.Composite;
import org.apache.tuscany.sca.assembly.builder.BuilderExtensionPoint;
import org.apache.tuscany.sca.assembly.builder.CompositeBuilder;
import org.apache.tuscany.sca.builder.impl.CompositeCloneBuilderImpl;
import org.apache.tuscany.sca.builder.impl.CompositeIncludeBuilderImpl;
import org.apache.tuscany.sca.builder.impl.PolicyAttachmentBuilderImpl;
import org.apache.tuscany.sca.builder.impl.StructuralURIBuilderImpl;
import org.apache.tuscany.sca.contribution.processor.ContributionReadException;
import org.apache.tuscany.sca.contribution.processor.ExtensibleStAXArtifactProcessor;
import org.apache.tuscany.sca.contribution.processor.StAXArtifactProcessor;
import org.apache.tuscany.sca.contribution.processor.StAXArtifactProcessorExtensionPoint;
import org.apache.tuscany.sca.core.DefaultExtensionPointRegistry;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.core.FactoryExtensionPoint;
import org.apache.tuscany.sca.core.UtilityExtensionPoint;
import org.apache.tuscany.sca.definitions.Definitions;
import org.apache.tuscany.sca.monitor.DefaultMonitorFactory;
import org.apache.tuscany.sca.monitor.Monitor;
import org.apache.tuscany.sca.monitor.MonitorFactory;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Test reading SCA XML assembly documents.
 * 
 * @version $Rev$ $Date$
 */
public class PolicyAttachmentTestCase {

    private static StAXArtifactProcessor<Object> staxProcessor;
    private static Monitor monitor;

    private static ExtensionPointRegistry extensionPoints;
    private static XMLInputFactory inputFactory;
    private static AssemblyFactory assemblyFactory;
    private static BuilderExtensionPoint builders;

    @BeforeClass
    public static void init() throws Exception {
        extensionPoints = new DefaultExtensionPointRegistry();
        FactoryExtensionPoint factories = extensionPoints.getExtensionPoint(FactoryExtensionPoint.class);
        assemblyFactory = factories.getFactory(AssemblyFactory.class);
        inputFactory = factories.getFactory(XMLInputFactory.class);
        // Create a monitor
        UtilityExtensionPoint utilities = extensionPoints.getExtensionPoint(UtilityExtensionPoint.class);
        MonitorFactory monitorFactory = new DefaultMonitorFactory();
        if (monitorFactory != null) {
            monitor = monitorFactory.createMonitor();
            utilities.addUtility(monitorFactory);
        }
        StAXArtifactProcessorExtensionPoint staxProcessors =
            extensionPoints.getExtensionPoint(StAXArtifactProcessorExtensionPoint.class);
        staxProcessor = new ExtensibleStAXArtifactProcessor(staxProcessors, inputFactory, null, monitor);
        staxProcessors.addArtifactProcessor(new TestPolicyProcessor());

        builders = extensionPoints.getExtensionPoint(BuilderExtensionPoint.class);
    }

    @Test
    public void testBuild() throws Exception {
        Definitions definitions = load("test_definitions.xml");
        Composite composite = load("Calculator.composite");

        PolicyAttachmentBuilderImpl builder = new PolicyAttachmentBuilderImpl(extensionPoints);
        builder.build(composite, definitions, monitor);
    }

    private <T> T load(String file) throws IOException, XMLStreamException, ContributionReadException {
        URL url = getClass().getResource(file);
        InputStream urlStream = url.openStream();
        XMLStreamReader reader = inputFactory.createXMLStreamReader(urlStream);
        reader.nextTag();

        T model = (T)staxProcessor.read(reader);
        reader.close();
        return model;
    }

    @Test
    public void testComplexBuild() throws Exception {
        Definitions definitions = load("definitions.xml");
        Composite composite1 = load("Composite1.composite");
        Composite composite2 = load("Composite2.composite");
        Composite composite3 = load("Composite3.composite");
        Composite composite4 = load("Composite4.composite");
        composite1.getIncludes().clear();
        composite1.getIncludes().add(composite3);

        composite1.getComponent("Component1B").setImplementation(composite4);
        composite2.getComponent("Component2B").setImplementation(composite4);

        Composite domainComposite = assemblyFactory.createComposite();
        domainComposite.setName(new QName(Base.SCA11_NS, ""));
        domainComposite.setLocal(false);
        domainComposite.getIncludes().add(composite1);
        domainComposite.getIncludes().add(composite2);

        CompositeBuilder includeBuilder = new CompositeIncludeBuilderImpl();
        CompositeBuilder cloneBuilder = new CompositeCloneBuilderImpl();
        CompositeBuilder uriBuilder = new StructuralURIBuilderImpl(extensionPoints);

        /*
        CompositeBuilder includeBuilder =
            builders.getCompositeBuilder("org.apache.tuscany.sca.assembly.builder.CompositeIncludeBuilder");
        CompositeBuilder cloneBuilder =
            builders.getCompositeBuilder("org.apache.tuscany.sca.assembly.builder.CompositeCloneBuilder");
            */

        domainComposite = cloneBuilder.build(domainComposite, definitions, monitor);
        domainComposite = includeBuilder.build(domainComposite, definitions, monitor);
        domainComposite = uriBuilder.build(domainComposite, definitions, monitor);

        PolicyAttachmentBuilderImpl builder = new PolicyAttachmentBuilderImpl(extensionPoints);
        domainComposite = builder.build(domainComposite, definitions, monitor);

    }

}
