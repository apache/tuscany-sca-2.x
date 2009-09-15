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

package org.apache.tuscany.sca.policy.builder.impl;

import java.io.InputStream;
import java.net.URL;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;

import org.apache.tuscany.sca.assembly.Composite;
import org.apache.tuscany.sca.contribution.processor.ExtensibleStAXArtifactProcessor;
import org.apache.tuscany.sca.contribution.processor.StAXArtifactProcessor;
import org.apache.tuscany.sca.contribution.processor.StAXArtifactProcessorExtensionPoint;
import org.apache.tuscany.sca.core.DefaultExtensionPointRegistry;
import org.apache.tuscany.sca.core.UtilityExtensionPoint;
import org.apache.tuscany.sca.definitions.Definitions;
import org.apache.tuscany.sca.monitor.DefaultMonitorFactory;
import org.apache.tuscany.sca.monitor.Monitor;
import org.apache.tuscany.sca.monitor.MonitorFactory;
import org.junit.Test;

/**
 * Test reading SCA XML assembly documents.
 * 
 * @version $Rev$ $Date$
 */
public class PolicyAttachmentTestCase {

    private StAXArtifactProcessor<Object> staxProcessor;
    private Monitor monitor;

    @Test
    public void testBuild() throws Exception {
        DefaultExtensionPointRegistry extensionPoints = new DefaultExtensionPointRegistry();
        XMLInputFactory inputFactory = XMLInputFactory.newInstance();
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

        URL url = getClass().getResource("test_definitions.xml");
        InputStream urlStream = url.openStream();
        XMLStreamReader reader = inputFactory.createXMLStreamReader(urlStream);
        reader.nextTag();

        Definitions definitions = (Definitions)staxProcessor.read(reader);
        reader.close();

        url = getClass().getResource("Calculator.composite");
        urlStream = url.openStream();
        reader = inputFactory.createXMLStreamReader(urlStream);
        reader.nextTag();

        Composite composite = (Composite)staxProcessor.read(reader);
        reader.close();

        PolicyAttachmentBuilderImpl builder = new PolicyAttachmentBuilderImpl(extensionPoints);
        builder.build(composite, definitions, monitor);
    }

}
