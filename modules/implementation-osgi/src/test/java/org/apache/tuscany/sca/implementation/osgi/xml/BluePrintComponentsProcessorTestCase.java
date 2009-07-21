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
import org.apache.tuscany.sca.core.DefaultExtensionPointRegistry;
import org.apache.tuscany.sca.core.DefaultFactoryExtensionPoint;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * 
 */
public class BluePrintComponentsProcessorTestCase {
    private static final String xml =
        "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + "<components xmlns=\"http://www.osgi.org/xmlns/blueprint/v1.0.0\">"
            + "<component id=\"CalculatorComponent\" class=\"calculator.dosgi.impl.CalculatorServiceImpl\">"
            + "<property name=\"addService\" ref=\"AddService\" />"
            + "<property name=\"subtractService\" ref=\"SubtractService\" />"
            + "<property name=\"multiplyService\" ref=\"MultiplyService\" />"
            + "<property name=\"divideService\" ref=\"DivideService\" />"
            + "</component>"
            + "<service id=\"CalculatorService\" ref=\"CalculatorComponent\" interface=\"calculator.dosgi.CalculatorService\">"
            + "</service>"
            + "<reference id=\"AddService\" interface=\"calculator.dosgi.operations.AddService\">"
            + "</reference>"
            + "<reference id=\"SubtractService\" interface=\"calculator.dosgi.operations.SubtractService\">"
            + "</reference>"
            + "<reference id=\"MultiplyService\" interface=\"calculator.dosgi.operations.MultiplyService\">"
            + "</reference>"
            + "<reference id=\"DivideService\" interface=\"calculator.dosgi.operations.DivideService\">"
            + "</reference>"
            + "</components>";
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
        BluePrintComponentsProcessor processor =
            new BluePrintComponentsProcessor(new DefaultFactoryExtensionPoint(new DefaultExtensionPointRegistry()),
                                             null);
        ComponentType ct = processor.read(reader);
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
