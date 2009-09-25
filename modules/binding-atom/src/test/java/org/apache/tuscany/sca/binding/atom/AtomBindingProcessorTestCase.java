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

package org.apache.tuscany.sca.binding.atom;

import java.io.StringReader;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;

import junit.framework.TestCase;

import org.apache.tuscany.sca.assembly.Composite;
import org.apache.tuscany.sca.contribution.processor.DefaultStAXArtifactProcessorExtensionPoint;
import org.apache.tuscany.sca.contribution.processor.ExtensibleStAXArtifactProcessor;
import org.apache.tuscany.sca.contribution.processor.StAXArtifactProcessor;
import org.apache.tuscany.sca.contribution.processor.StAXArtifactProcessorExtensionPoint;
import org.apache.tuscany.sca.core.DefaultExtensionPointRegistry;
import org.apache.tuscany.sca.core.UtilityExtensionPoint;
import org.apache.tuscany.sca.monitor.Monitor;
import org.apache.tuscany.sca.monitor.MonitorFactory;
import org.apache.tuscany.sca.monitor.impl.DefaultMonitorFactoryImpl;

/**
 * @version $Rev$ $Date$
 */
public class AtomBindingProcessorTestCase extends TestCase {
    
    private static final String COMPOSITE =
        "<?xml version=\"1.0\" encoding=\"ASCII\"?>" 
        + "<composite xmlns=\"http://www.osoa.org/xmlns/sca/1.0\" xmlns:tuscany=\"http://tuscany.apache.org/xmlns/sca/1.0\" targetNamespace=\"http://binding-atom\" name=\"binding-atom\">"
            + " <component name=\"HelloWorldComponent\">"
            + "   <implementation.java class=\"services.HelloWorld\"/>"
            + "      <service name=\"HelloWorldService\">"
            + "          <tuscany:binding.atom uri=\"http://localhost:8080/feed\" title=\"Feed Title\"/>"
            + "      </service>"
            + " </component>"
            + "</composite>";

    private XMLInputFactory inputFactory;
    private StAXArtifactProcessor<Object> staxProcessor;
    private Monitor monitor;

    @Override
    protected void setUp() throws Exception {
        DefaultExtensionPointRegistry extensionPoints = new DefaultExtensionPointRegistry();
        inputFactory = XMLInputFactory.newInstance();
        // Create a monitor
        UtilityExtensionPoint utilities = extensionPoints.getExtensionPoint(UtilityExtensionPoint.class);
        MonitorFactory monitorFactory = new DefaultMonitorFactoryImpl();  
        if (monitorFactory != null) {
        	monitor = monitorFactory.createMonitor();
        	utilities.addUtility(monitorFactory);
        }
        StAXArtifactProcessorExtensionPoint staxProcessors = new DefaultStAXArtifactProcessorExtensionPoint(extensionPoints);
        staxProcessor = new ExtensibleStAXArtifactProcessor(staxProcessors, inputFactory, null, monitor);
    }

    /**
     * Test parsing valid composite definition. Valid composite populated with correct values expected.
     * @throws Exception
     */
    public void testLoadValidComposite() throws Exception {
        XMLStreamReader reader = inputFactory.createXMLStreamReader(new StringReader(COMPOSITE));
        
        Composite composite = (Composite)staxProcessor.read(reader);
        AtomBinding binding = (AtomBinding)   composite.getComponents().get(0).getServices().get(0).getBindings().get(0);
        
        assertNotNull(binding);
        assertEquals("Feed Title", binding.getTitle());
        assertEquals("http://localhost:8080/feed", binding.getURI());
    }
}
