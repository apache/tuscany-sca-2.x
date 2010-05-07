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

package org.apache.tuscany.sca.binding.rest.xml;

import java.io.StringReader;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;

import org.apache.tuscany.sca.assembly.Composite;
import org.apache.tuscany.sca.binding.rest.RESTBinding;
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
public class RESTBindingProcessorTestCase {

    private static final String COMPOSITE =
        "<?xml version=\"1.0\" encoding=\"ASCII\"?>" 
        + "<composite xmlns=\"http://docs.oasis-open.org/ns/opencsa/sca/200912\" xmlns:tuscany=\"http://tuscany.apache.org/xmlns/sca/1.1\" targetNamespace=\"http://binding-rest\" name=\"binding-rest\">"
            + " <component name=\"CustomerService\">"
            + "   <implementation.java class=\"services.customer.CustomerServiceImpl\"/>"
            + "      <service name=\"CustomerService\">"
            + "         <tuscany:binding.rest uri=\"http://localhost:8085/Customer\">"
            + "            <tuscany:wireFormat.xml />"
            + "            <tuscany:operationSelector.jaxrs />"
            + "            <tuscany:http-headers>"
            + "               <tuscany:header name=\"Cache-Control\" value=\"no-cache\"/>"
            + "               <tuscany:header name=\"Expires\" value=\"-1\"/>"
            + "            </tuscany:http-headers>"
            + "            </tuscany:binding.rest>"
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
        RESTBinding binding = (RESTBinding)   composite.getComponents().get(0).getServices().get(0).getBindings().get(0);
        
        Assert.assertNotNull(binding);
        Assert.assertEquals(2, binding.getHttpHeaders().size());
        Assert.assertEquals("Cache-Control", binding.getHttpHeaders().get(0).getName());
        Assert.assertEquals("no-cache", binding.getHttpHeaders().get(0).getValue());
    }
}
