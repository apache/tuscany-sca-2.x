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

package org.apache.tuscany.sca.node.configuration.xml;

import java.io.InputStream;
import java.io.StringWriter;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import org.apache.tuscany.sca.contribution.processor.ProcessorContext;
import org.apache.tuscany.sca.contribution.processor.StAXArtifactProcessor;
import org.apache.tuscany.sca.contribution.processor.StAXArtifactProcessorExtensionPoint;
import org.apache.tuscany.sca.core.DefaultExtensionPointRegistry;
import org.apache.tuscany.sca.core.DefaultFactoryExtensionPoint;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.core.FactoryExtensionPoint;
import org.apache.tuscany.sca.node.configuration.NodeConfiguration;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 */
public class NodeConfigurationProcessorTestCase {
    private static FactoryExtensionPoint factories;
    private static StAXArtifactProcessor processor;

    private static ProcessorContext context;

    @BeforeClass
    public static void setUp() throws Exception {
        ExtensionPointRegistry registry = new DefaultExtensionPointRegistry();
        context = new ProcessorContext(registry);

        factories = new DefaultFactoryExtensionPoint(registry);
        StAXArtifactProcessorExtensionPoint processors =
            registry.getExtensionPoint(StAXArtifactProcessorExtensionPoint.class);
        processor = processors.getProcessor(NodeConfiguration.class);
    }

    @Test
    public void testRead() throws Exception {
        InputStream is = getClass().getResourceAsStream("/org/apache/tuscany/sca/node/configuration/node1.xml");
        XMLInputFactory xmlInputFactory = factories.getFactory(XMLInputFactory.class);
        XMLStreamReader reader = xmlInputFactory.createXMLStreamReader(is);
        reader.nextTag();
        NodeConfiguration config = (NodeConfiguration) processor.read(reader, context);
        is.close();        
        StringWriter sw = new StringWriter();
        XMLOutputFactory xmlOutputFactory = factories.getFactory(XMLOutputFactory.class);
        xmlOutputFactory.setProperty(XMLOutputFactory.IS_REPAIRING_NAMESPACES, Boolean.TRUE);
        XMLStreamWriter writer = xmlOutputFactory.createXMLStreamWriter(sw);
        processor.write(config, writer, context);
        writer.flush();
        System.out.println(sw.toString());
    }

    /**
     * @throws java.lang.Exception
     */
    @AfterClass
    public static void tearDownAfterClass() throws Exception {
    }

}
