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

import java.io.StringReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import junit.framework.TestCase;

import org.apache.tuscany.core.deployer.RootDeploymentContext;
import org.apache.tuscany.spi.deployer.DeploymentContext;
import org.apache.tuscany.spi.loader.LoaderException;

/**
 * @version $Rev$ $Date$
 */
public class ImportSDOLoaderTestCase extends TestCase {
    private static boolean inited;

    private ImportSDOLoader loader;
    private XMLInputFactory xmlFactory;
    private DeploymentContext deploymentContext;

    public void testMinimal() throws XMLStreamException, LoaderException {
        String xml = "<import.sdo xmlns='http://www.osoa.org/xmlns/sca/0.9'/>";
        XMLStreamReader reader = getReader(xml);
        assertNull(loader.load(null, reader, null));
    }

    public void testFactory() throws XMLStreamException, LoaderException {
        String xml = "<import.sdo xmlns='http://www.osoa.org/xmlns/sca/0.9' "
                + "factory='org.apache.tuscany.databinding.sdo.ImportSDOLoaderTestCase$MockFactory'/>";
        XMLStreamReader reader = getReader(xml);
        assertFalse(inited);
        assertNull(loader.load(null, reader, deploymentContext));
        assertTrue(inited);
    }

    protected void setUp() throws Exception {
        super.setUp();
        loader = new ImportSDOLoader(null);
        xmlFactory = XMLInputFactory.newInstance();
        deploymentContext = new RootDeploymentContext(getClass().getClassLoader(), xmlFactory, null, null);
    }

    protected XMLStreamReader getReader(String xml) throws XMLStreamException {
        XMLStreamReader reader = xmlFactory.createXMLStreamReader(new StringReader(xml));
        reader.next();
        return reader;
    }

    public static class MockFactory {
        public static final Object INSTANCE = null;

        static {
            ImportSDOLoaderTestCase.inited = true;
        }
    }
}

