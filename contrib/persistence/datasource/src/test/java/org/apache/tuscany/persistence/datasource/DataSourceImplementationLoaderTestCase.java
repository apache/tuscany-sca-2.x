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
package org.apache.tuscany.persistence.datasource;

import static javax.xml.stream.XMLStreamConstants.END_ELEMENT;
import javax.xml.stream.XMLStreamReader;

import org.apache.tuscany.spi.deployer.DeploymentContext;
import org.apache.tuscany.spi.loader.LoaderException;
import org.apache.tuscany.spi.loader.LoaderRegistry;

import junit.framework.TestCase;
import org.easymock.EasyMock;

/**
 * @version $Rev$ $Date$
 */
public class DataSourceImplementationLoaderTestCase extends TestCase {

    public void testLoadingNoParameters() throws Exception {
        LoaderRegistry registry = EasyMock.createNiceMock(LoaderRegistry.class);
        EasyMock.replay(registry);
        DataSourceImplementationLoader loader = new DataSourceImplementationLoader(registry);
        XMLStreamReader reader = EasyMock.createMock(XMLStreamReader.class);
        EasyMock.expect(reader.getAttributeValue(null, "provider")).andReturn("org.foo.MyDriver");
        EasyMock.expect(reader.next()).andReturn(END_ELEMENT);
        EasyMock.replay(reader);
        DeploymentContext ctx = EasyMock.createMock(DeploymentContext.class);
        EasyMock.expect(ctx.getClassLoader()).andReturn(getClass().getClassLoader());
        EasyMock.replay(ctx);
        DataSourceImplementation implementation = (DataSourceImplementation) loader.load(null, reader, ctx);
        assertEquals("org.foo.MyDriver", implementation.getProviderName());
        assertEquals(getClass().getClassLoader(), implementation.getClassLoader());
        EasyMock.verify(reader);
        EasyMock.verify(ctx);
    }

    public void testNoDriverName() throws Exception {
        LoaderRegistry registry = EasyMock.createNiceMock(LoaderRegistry.class);
        EasyMock.replay(registry);
        DataSourceImplementationLoader loader = new DataSourceImplementationLoader(registry);
        XMLStreamReader reader = EasyMock.createMock(XMLStreamReader.class);
        EasyMock.expect(reader.getAttributeValue(null, "provider")).andReturn(null);
        EasyMock.replay(reader);
        try {
            loader.load(null, reader, null);
            fail();
        } catch (LoaderException e) {
            // expected
        }
        EasyMock.verify(reader);
    }

}
