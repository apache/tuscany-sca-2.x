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
package org.apache.tuscany.core.implementation.system.loader;

import javax.xml.namespace.QName;
import javax.xml.stream.Location;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamReader;

import org.apache.tuscany.spi.deployer.DeploymentContext;
import org.apache.tuscany.spi.loader.LoaderRegistry;
import org.apache.tuscany.spi.loader.UnrecognizedElementException;

import junit.framework.TestCase;
import org.apache.tuscany.core.implementation.system.model.SystemImplementation;
import org.easymock.EasyMock;

/**
 * @version $Rev$ $Date$
 */
public class SystemImplementationLoaderTestCase extends TestCase {

    public static final QName SYSTEM_IMPLEMENTATION =
        new QName("http://tuscany.apache.org/xmlns/sca/system/2.0-alpha", "implementation.system");

    public void testLoad() throws Exception {
        LoaderRegistry registry = EasyMock.createNiceMock(LoaderRegistry.class);
        EasyMock.replay(registry);
        DeploymentContext context = EasyMock.createMock(DeploymentContext.class);
        EasyMock.expect(context.getClassLoader()).andReturn(getClass().getClassLoader());
        EasyMock.replay(context);
        XMLStreamReader reader = EasyMock.createMock(XMLStreamReader.class);
        EasyMock.expect(reader.getName()).andReturn(SYSTEM_IMPLEMENTATION);
        EasyMock.expect(reader.getAttributeValue((String) EasyMock.isNull(), EasyMock.eq("class")))
            .andReturn(getClass().getName());
        EasyMock.expect(reader.next()).andReturn(XMLStreamConstants.END_ELEMENT);
        EasyMock.replay(reader);
        SystemImplementationLoader loader = new SystemImplementationLoader(registry);
        SystemImplementation impl = loader.load(null, reader, context);
        assertEquals(getClass(), impl.getImplementationClass());
        EasyMock.verify(reader);
        EasyMock.verify(context);
    }

    public void testUnrecognizedElement() throws Exception {
        LoaderRegistry registry = EasyMock.createNiceMock(LoaderRegistry.class);
        EasyMock.replay(registry);
        DeploymentContext context = EasyMock.createMock(DeploymentContext.class);
        EasyMock.expect(context.getClassLoader()).andReturn(getClass().getClassLoader());
        EasyMock.replay(context);
        XMLStreamReader reader = EasyMock.createMock(XMLStreamReader.class);
        EasyMock.expect(reader.getName()).andReturn(SYSTEM_IMPLEMENTATION).atLeastOnce();
        EasyMock.expect(reader.getAttributeValue((String) EasyMock.isNull(), EasyMock.eq("class")))
            .andReturn(getClass().getName());
        EasyMock.expect(reader.next()).andReturn(XMLStreamConstants.START_ELEMENT);
        EasyMock.expect(reader.getLocation()).andReturn(new MockLocation());
        EasyMock.replay(reader);
        SystemImplementationLoader loader = new SystemImplementationLoader(registry);
        try {
            loader.load(null, reader, context);
            fail();
        } catch (UnrecognizedElementException e) {
            // expected
        }
    }

    private class MockLocation implements Location {

        public int getLineNumber() {
            return 0;
        }

        public int getColumnNumber() {
            return 0;
        }

        public int getCharacterOffset() {
            return 0;
        }

        public String getPublicId() {
            return null;
        }

        public String getSystemId() {
            return null;
        }
    }

}
