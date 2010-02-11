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
package org.apache.tuscany.sca.databinding.sdo;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.StringReader;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.apache.tuscany.sca.contribution.processor.ProcessorContext;
import org.apache.tuscany.sca.contribution.resolver.ClassReference;
import org.apache.tuscany.sca.contribution.resolver.ModelResolver;
import org.apache.tuscany.sca.core.DefaultExtensionPointRegistry;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.core.FactoryExtensionPoint;
import org.junit.BeforeClass;
import org.junit.Test;

import com.example.ipo.sdo.SdoFactory;

/**
 * @version $Rev$ $Date$
 */
public class ImportSDOProcessorTestCase {
    private static boolean inited;

    private static ExtensionPointRegistry registry;
    private static ImportSDOProcessor loader;
    private static XMLInputFactory xmlFactory;

    @Test
    public void testMinimal() throws Exception {
        String xml = "<import.sdo xmlns='http://tuscany.apache.org/xmlns/sca/1.1'/>";
        XMLStreamReader reader = getReader(xml);
        assertTrue(loader.read(reader, new ProcessorContext(registry)) instanceof ImportSDO);
    }

    @Test
    public void testLocation() throws Exception {
        String xml = "<import.sdo xmlns='http://tuscany.apache.org/xmlns/sca/1.1' location='ipo.xsd'/>";
        XMLStreamReader reader = getReader(xml);
        assertTrue(loader.read(reader, new ProcessorContext(registry)) instanceof ImportSDO);
    }

    @Test
    public void testFactory() throws Exception {
        String xml = "<import.sdo xmlns='http://tuscany.apache.org/xmlns/sca/1.1' " + "factory='"
                     + MockFactory.class.getName()
                     + "'/>";
        XMLStreamReader reader = getReader(xml);
        assertFalse(inited);
        ProcessorContext context = new ProcessorContext(registry);
        ImportSDO importSDO = loader.read(reader, context);
        assertNotNull(importSDO);
        ModelResolver resolver = new TestModelResolver();
        resolver.addModel(new ClassReference(MockFactory.class), context);
        loader.resolve(importSDO, resolver, context);
        assertTrue(inited);
    }

    @BeforeClass
    public static void setUp() throws Exception {
        registry = new DefaultExtensionPointRegistry();
        FactoryExtensionPoint factories = registry.getExtensionPoint(FactoryExtensionPoint.class);
        loader = new ImportSDOProcessor(factories, null);
        xmlFactory = factories.getFactory(XMLInputFactory.class);
    }

    protected XMLStreamReader getReader(String xml) throws XMLStreamException {
        XMLStreamReader reader = xmlFactory.createXMLStreamReader(new StringReader(xml));
        reader.next();
        return reader;
    }

    public static class MockFactory {
        public static final Object INSTANCE = SdoFactory.INSTANCE;

        static {
            ImportSDOProcessorTestCase.inited = true;
        }
    }
}
