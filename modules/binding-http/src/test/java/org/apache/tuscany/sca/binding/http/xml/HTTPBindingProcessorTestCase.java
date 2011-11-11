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

package org.apache.tuscany.sca.binding.http.xml;

import java.io.StringReader;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;

import org.apache.tuscany.sca.assembly.Composite;
import org.apache.tuscany.sca.binding.http.HTTPBinding;
import org.apache.tuscany.sca.contribution.processor.DefaultStAXArtifactProcessorExtensionPoint;
import org.apache.tuscany.sca.contribution.processor.ExtensibleStAXArtifactProcessor;
import org.apache.tuscany.sca.contribution.processor.ProcessorContext;
import org.apache.tuscany.sca.contribution.processor.StAXArtifactProcessor;
import org.apache.tuscany.sca.contribution.processor.StAXArtifactProcessorExtensionPoint;
import org.apache.tuscany.sca.core.DefaultExtensionPointRegistry;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;


/**
 * @version $Rev$ $Date$
 */
public class HTTPBindingProcessorTestCase {

    private static final String COMPOSITE =
        "<?xml version=\"1.0\" encoding=\"ASCII\"?>" 
        + "<composite xmlns=\"http://docs.oasis-open.org/ns/opencsa/sca/200912\" xmlns:tuscany=\"http://tuscany.apache.org/xmlns/sca/1.1\" targetNamespace=\"http://binding-http\" name=\"binding-http\">"
            + " <component name=\"CustomerService\">"
            + "   <implementation.java class=\"services.customer.CustomerServiceImpl\"/>"
            + "      <service name=\"CustomerService\">"
            + "         <tuscany:binding.http uri=\"http://localhost:8085/Customer\">"
            + "            <tuscany:wireFormat.json />"
            + "            <tuscany:operationSelector.default />"
            + "            <tuscany:response>"
            + "                <tuscany:wireFormat.xml />"
            + "            </tuscany:response>"            
            + "            </tuscany:binding.http>"
            + "      </service>"
            + " </component>"
            + "</composite>";

    private static XMLInputFactory inputFactory;
    private static StAXArtifactProcessor<Object> staxProcessor;
    private static ProcessorContext context;

    @BeforeClass
    public static void setUp() throws Exception {
        DefaultExtensionPointRegistry extensionPoints = new DefaultExtensionPointRegistry();
        context = new ProcessorContext(extensionPoints);
        inputFactory = XMLInputFactory.newInstance();
        
        StAXArtifactProcessorExtensionPoint staxProcessors = new DefaultStAXArtifactProcessorExtensionPoint(extensionPoints);
        staxProcessor = new ExtensibleStAXArtifactProcessor(staxProcessors, inputFactory, null);
    }

    /**
     * Test parsing valid composite definition. Valid composite populated with correct values expected.
     * @throws Exception
     */
    @Test
    public void testLoadValidComposite() throws Exception {
        XMLStreamReader reader = inputFactory.createXMLStreamReader(new StringReader(COMPOSITE));
        
        Composite composite = (Composite)staxProcessor.read(reader, context);
        HTTPBinding binding = (HTTPBinding)   composite.getComponents().get(0).getServices().get(0).getBindings().get(0);
        
        Assert.assertNotNull(binding);
        Assert.assertNotNull(binding.getOperationSelector());
        Assert.assertNotNull(binding.getRequestWireFormat());
        Assert.assertNotNull(binding.getResponseWireFormat());

    }
}
