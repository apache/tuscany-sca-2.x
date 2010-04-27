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

package org.apache.tuscany.sca.binding.rest.operationselector.jaxrs.impl;

import java.io.ByteArrayOutputStream;
import java.io.StringReader;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamReader;

import org.apache.tuscany.sca.assembly.OperationSelector;
import org.apache.tuscany.sca.binding.rest.RESTBinding;
import org.apache.tuscany.sca.binding.rest.operationselector.jaxrs.JAXRSOperationSelector;
import org.apache.tuscany.sca.contribution.processor.DefaultStAXArtifactProcessorExtensionPoint;
import org.apache.tuscany.sca.contribution.processor.ExtensibleStAXArtifactProcessor;
import org.apache.tuscany.sca.contribution.processor.ProcessorContext;
import org.apache.tuscany.sca.contribution.processor.StAXArtifactProcessorExtensionPoint;
import org.apache.tuscany.sca.core.DefaultExtensionPointRegistry;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * JSON wire format processor tests to verify properly processing of 
 * wireFormat content in binding configuration in the composite file
 * 
 * @version $Rev$ $Date$
 */
public class JAXRSOperationSelectorProcessorTestCase {
    public static final String BINDING_WITH_OPERATION_SELECTOR = 
        "<binding.rest xmlns=\"http://tuscany.apache.org/xmlns/sca/1.1\" uri=\"http://localhost:8080/uri\">"
        +    "<operationSelector.jaxrs />"
        + "</binding.rest>";
    
    private static XMLInputFactory inputFactory;
    private static XMLOutputFactory outputFactory;
    private static ExtensibleStAXArtifactProcessor staxProcessor;
    private static ProcessorContext context;
    
    @BeforeClass
    public static void setUp() throws Exception {
        DefaultExtensionPointRegistry extensionPoints = new DefaultExtensionPointRegistry();
        inputFactory = XMLInputFactory.newInstance();
        outputFactory = XMLOutputFactory.newInstance();
        
        context = new ProcessorContext(extensionPoints);

        StAXArtifactProcessorExtensionPoint staxProcessors = new DefaultStAXArtifactProcessorExtensionPoint(extensionPoints);
        staxProcessor = new ExtensibleStAXArtifactProcessor(staxProcessors, inputFactory, outputFactory);

    }
    
    /**
     * Tests the APIs:
     *     public OperationSelector getRequstOperationSelector();
     * 
     * @throws Exception
     */
    @Test
    public void testWireFormat() throws Exception {
        XMLStreamReader reader = inputFactory.createXMLStreamReader(new StringReader(BINDING_WITH_OPERATION_SELECTOR));
        
        RESTBinding binding = (RESTBinding)staxProcessor.read(reader, context);        
        Assert.assertNotNull(binding);
        
        OperationSelector operationSelector = binding.getOperationSelector();
        Assert.assertEquals(JAXRSOperationSelector.class, operationSelector.getClass().getInterfaces()[0]);        
    }
    
    @Test
    public void testWriteWireFormat() throws Exception {
        XMLStreamReader reader = inputFactory.createXMLStreamReader(new StringReader(BINDING_WITH_OPERATION_SELECTOR));
        
        RESTBinding binding = (RESTBinding)staxProcessor.read(reader, context);
        Assert.assertNotNull(binding);
        reader.close();

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        staxProcessor.write(binding, bos, context);

        // used for debug comparison
        // System.out.println(BINDING_WITH_OPERATION_SELECTOR);
        // System.out.println(bos.toString());

        Assert.assertEquals(BINDING_WITH_OPERATION_SELECTOR, bos.toString());      
        
    }      

}
