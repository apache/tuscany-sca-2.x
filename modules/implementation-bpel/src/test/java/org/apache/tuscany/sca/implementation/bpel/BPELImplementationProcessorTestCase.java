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

package org.apache.tuscany.sca.implementation.bpel;

import java.io.StringReader;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;

import junit.framework.TestCase;

import org.apache.tuscany.sca.assembly.Composite;
import org.apache.tuscany.sca.contribution.processor.DefaultStAXArtifactProcessorExtensionPoint;
import org.apache.tuscany.sca.contribution.processor.ExtensibleStAXArtifactProcessor;
import org.apache.tuscany.sca.contribution.processor.ProcessorContext;
import org.apache.tuscany.sca.contribution.processor.StAXArtifactProcessor;
import org.apache.tuscany.sca.contribution.processor.StAXArtifactProcessorExtensionPoint;
import org.apache.tuscany.sca.core.DefaultExtensionPointRegistry;
import org.apache.tuscany.sca.core.UtilityExtensionPoint;
import org.apache.tuscany.sca.monitor.Problem;

/**
 * @version $Rev$ $Date$
 */
public class BPELImplementationProcessorTestCase extends TestCase {

    private static final String COMPOSITE =
        "<?xml version=\"1.0\" encoding=\"ASCII\"?>" 
        + "<composite xmlns=\"http://docs.oasis-open.org/ns/opencsa/sca/200903\" xmlns:hns=\"http://tuscany.apache.org/implementation/bpel/example/helloworld\" targetNamespace=\"http://bpel\" name=\"bpel\">"
            + " <component name=\"BPELHelloWorldComponent\">"
            + "   <implementation.bpel process=\"hns:HelloWorld\" />"
            + " </component>"
            + "</composite>";

    private static final String COMPOSITE_INVALID =
        "<?xml version=\"1.0\" encoding=\"ASCII\"?>" 
            + "<composite xmlns=\"http://docs.oasis-open.org/ns/opencsa/sca/200903\" xmlns:hns=\"http://tuscany.apache.org/implementation/bpel/example/helloworld\" targetNamespace=\"http://bpel\" name=\"bpel\">"
            + " <component name=\"BPELHelloWorldComponent\">"
            + "   <implementation.bpel/>"
            + " </component>"
            + "</composite>";

    private XMLInputFactory inputFactory;
    private StAXArtifactProcessor<Object> staxProcessor;
    private ProcessorContext context;

    @Override
    protected void setUp() throws Exception {
        DefaultExtensionPointRegistry extensionPoints = new DefaultExtensionPointRegistry();
        context = new ProcessorContext(extensionPoints);
        inputFactory = XMLInputFactory.newInstance();
        // Create a monitor
        UtilityExtensionPoint utilities = extensionPoints.getExtensionPoint(UtilityExtensionPoint.class);

        StAXArtifactProcessorExtensionPoint staxProcessors = new DefaultStAXArtifactProcessorExtensionPoint(extensionPoints);
        staxProcessor = new ExtensibleStAXArtifactProcessor(staxProcessors, inputFactory, null);
    }

    /**
     * Test parsing valid composite definition. Valid composite populated with correct values expected.
     * @throws Exception
     */
    public void testLoadValidComposite() throws Exception {
        XMLStreamReader reader = inputFactory.createXMLStreamReader(new StringReader(COMPOSITE));
        
        Composite composite = (Composite)staxProcessor.read(reader, context);
        BPELImplementation implementation = (BPELImplementation)composite.getComponents().get(0).getImplementation();
        
        assertNotNull(implementation);
        assertEquals(new QName("http://tuscany.apache.org/implementation/bpel/example/helloworld", "HelloWorld"), implementation.getProcess());
    }

    /**
     * Test parsing invalid composite definition. Exception should be thrown
     * @throws Exception
     */
    public void testLoadInvalidComposite() throws Exception {
        XMLStreamReader reader = inputFactory.createXMLStreamReader(new StringReader(COMPOSITE_INVALID));
        staxProcessor.read(reader, context);
        Problem problem = context.getMonitor().getLastProblem();           
        assertNotNull(problem);
        assertEquals("AttributeProcessMissing", problem.getMessageId());
    }    
}
