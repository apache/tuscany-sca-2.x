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
package org.apache.tuscany.databinding.sdo;

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;

import java.io.StringReader;
import java.net.URI;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import junit.framework.TestCase;

import org.apache.tuscany.spi.deployer.DeploymentContext;
import org.apache.tuscany.spi.loader.LoaderException;
import org.easymock.EasyMock;

import com.example.ipo.sdo.SdoFactory;

/**
 * @version $Rev$ $Date$
 */
public class ImportSDOLoaderTestCase extends TestCase {
    private static boolean inited;

    private ImportSDOLoader loader;
    private XMLInputFactory xmlFactory;
    private DeploymentContext deploymentContext;

    public void testMinimal() throws XMLStreamException, LoaderException {
        String xml = "<import.sdo xmlns='http://tuscany.apache.org/xmlns/sca/databinding/sdo/1.0'/>";
        XMLStreamReader reader = getReader(xml);
        assertTrue(loader.load(null, reader, deploymentContext) instanceof ImportSDO);
    }

    public void testLocation() throws XMLStreamException, LoaderException {
        String xml = "<import.sdo xmlns='http://tuscany.apache.org/xmlns/sca/databinding/sdo/1.0' location='ipo.xsd'/>";
        XMLStreamReader reader = getReader(xml);
        assertTrue(loader.load(null, reader, deploymentContext) instanceof ImportSDO);
    }

    public void testFactory() throws XMLStreamException, LoaderException {
        String xml = "<import.sdo xmlns='http://tuscany.apache.org/xmlns/sca/databinding/sdo/1.0' " + "factory='org.apache.tuscany.databinding.sdo.ImportSDOLoaderTestCase$MockFactory'/>";
        XMLStreamReader reader = getReader(xml);
        assertFalse(inited);
        assertTrue(loader.load(null, reader, deploymentContext) instanceof ImportSDO);
        assertTrue(inited);
    }

    protected void setUp() throws Exception {
        super.setUp();
        URI id = URI.create("/composite1/");
        loader = new ImportSDOLoader(null, new HelperContextRegistryImpl());
        xmlFactory = XMLInputFactory.newInstance();
        deploymentContext = EasyMock.createMock(DeploymentContext.class);
        expect(deploymentContext.getXmlFactory()).andReturn(xmlFactory).anyTimes();
        expect(deploymentContext.getComponentId()).andReturn(id).anyTimes();
        expect(deploymentContext.getClassLoader()).andReturn(getClass().getClassLoader()).anyTimes();
        replay(deploymentContext);
    }

    protected XMLStreamReader getReader(String xml) throws XMLStreamException {
        XMLStreamReader reader = xmlFactory.createXMLStreamReader(new StringReader(xml));
        reader.next();
        return reader;
    }

    public static class MockFactory {
        public static final Object INSTANCE = SdoFactory.INSTANCE;

        static {
            ImportSDOLoaderTestCase.inited = true;
        }
    }
}
