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

package org.apache.tuscany.sca.implementation.osgi.xml;

import java.io.StringReader;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;

import org.apache.tuscany.sca.assembly.ComponentType;
import org.apache.tuscany.sca.contribution.processor.ProcessorContext;
import org.apache.tuscany.sca.core.DefaultExtensionPointRegistry;
import org.apache.tuscany.sca.core.DefaultFactoryExtensionPoint;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * 
 */
public class OSGiComponentProcessorTestCase {
    private static final String xml =
        "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + "<scr:component name=\"CalculatorComponent\""
            + " xmlns:scr=\"http://www.osgi.org/xmlns/scr/v1.0.0\">"
            + "<implementation class=\"calculator.dosgi.CalculatorServiceDSImpl\" />"
            + "<service>"
            + "<provide interface=\"calculator.dosgi.CalculatorService\" />"
            + "</service>"
            + "<reference name=\"addService\" interface=\"calculator.dosgi.operations.AddService\""
            + " bind=\"setAddService\" unbind=\"unsetAddService\" policy=\"dynamic\" />"
            + "<reference name=\"subtractService\" interface=\"calculator.dosgi.operations.SubtractService\""
            + " bind=\"setSubtractService\" unbind=\"unsetSubtractService\" policy=\"dynamic\" />"
            + "<reference name=\"multiplyService\" interface=\"calculator.dosgi.operations.MultiplyService\""
            + " bind=\"setMultiplyService\" unbind=\"unsetMultiplyService\" policy=\"dynamic\" />"
            + "<reference name=\"divideService\" interface=\"calculator.dosgi.operations.DivideService\""
            + " bind=\"setDivideService\" unbind=\"unsetDivideService\" policy=\"dynamic\" />"
            + "</scr:component>";

    private static XMLStreamReader reader;

    /**
     * @throws java.lang.Exception
     */
    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        XMLInputFactory factory = XMLInputFactory.newInstance();
        reader = factory.createXMLStreamReader(new StringReader(xml));
    }

    @Test
    public void testLoad() throws Exception {
        ExtensionPointRegistry registry = new DefaultExtensionPointRegistry();
        OSGiComponentProcessor processor =
            new OSGiComponentProcessor(new DefaultFactoryExtensionPoint(registry));
        ComponentType ct = processor.read(reader, new ProcessorContext(registry));
        Assert.assertEquals(1, ct.getServices().size());
        Assert.assertEquals(4, ct.getReferences().size());
    }

    /**
     * @throws java.lang.Exception
     */
    @AfterClass
    public static void tearDownAfterClass() throws Exception {
        if (reader != null) {
            reader.close();
        }
    }

}
