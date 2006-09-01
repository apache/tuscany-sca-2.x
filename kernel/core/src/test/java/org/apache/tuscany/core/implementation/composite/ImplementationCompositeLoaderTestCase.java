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
package org.apache.tuscany.core.implementation.composite;

import java.net.MalformedURLException;
import java.net.URL;
import javax.xml.namespace.QName;
import static javax.xml.stream.XMLStreamConstants.END_ELEMENT;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import junit.framework.TestCase;
import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.osoa.sca.Version.XML_NAMESPACE_1_0;

import org.apache.tuscany.spi.deployer.DeploymentContext;
import org.apache.tuscany.spi.loader.LoaderException;
import org.apache.tuscany.spi.model.CompositeImplementation;

/**
 * @version $Rev$ $Date$
 */
public class ImplementationCompositeLoaderTestCase extends TestCase {
    private static final QName IMPLEMENTATION_COMPOSITE = new QName(XML_NAMESPACE_1_0, "implementation.composite");

    private ClassLoader cl;
    private ImplementationCompositeLoader loader;
    private XMLStreamReader reader;
    private DeploymentContext context;

    public void testName() throws LoaderException, XMLStreamException, MalformedURLException {
        String name = "foo";
        expect(reader.getName()).andReturn(IMPLEMENTATION_COMPOSITE);
        expect(reader.getAttributeValue(null, "name")).andReturn(name);
        expect(reader.getAttributeValue(null, "scdlLocation")).andReturn(null);
        expect(reader.next()).andReturn(END_ELEMENT);
        replay(reader);

        expect(context.getClassLoader()).andReturn(cl);
        replay(context);

        CompositeImplementation impl = loader.load(null, reader, context);
        verify(reader);
        verify(context);
        assertEquals(name, impl.getName());
        assertNull(impl.getScdlLocation());
        assertSame(cl, impl.getClassLoader());
    }

    public void testWithScdlLocation() throws LoaderException, XMLStreamException, MalformedURLException {
        String name = "foo";
        expect(reader.getName()).andReturn(IMPLEMENTATION_COMPOSITE);
        expect(reader.getAttributeValue(null, "name")).andReturn(name);
        expect(reader.getAttributeValue(null, "scdlLocation")).andReturn("bar.scdl");
        expect(reader.next()).andReturn(END_ELEMENT);
        replay(reader);

        expect(context.getScdlLocation()).andReturn(new URL("http://www.example.com/sca/base.scdl"));
        expect(context.getClassLoader()).andReturn(cl);
        replay(context);

        CompositeImplementation impl = loader.load(null, reader, context);
        verify(reader);
        verify(context);
        assertEquals(name, impl.getName());
        assertEquals(new URL("http://www.example.com/sca/bar.scdl"), impl.getScdlLocation());
        assertSame(cl, impl.getClassLoader());
    }

    protected void setUp() throws Exception {
        super.setUp();
        loader = new ImplementationCompositeLoader(null);
        reader = createMock(XMLStreamReader.class);
        context = createMock(DeploymentContext.class);
        cl = getClass().getClassLoader();
    }
}
