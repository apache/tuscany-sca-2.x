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
package org.apache.tuscany.core.binding.local;

import java.net.URI;
import javax.xml.stream.XMLStreamReader;

import org.apache.tuscany.spi.loader.LoaderException;

import junit.framework.TestCase;
import org.easymock.EasyMock;

/**
 * @version $Rev$ $Date$
 */
public class LocalBindingLoaderTestCase extends TestCase {
    private LocalBindingLoader loader;

    public void testParse() throws Exception {
        XMLStreamReader reader = EasyMock.createMock(XMLStreamReader.class);
        EasyMock.expect(reader.getAttributeValue(null, "uri")).andReturn("foo");
        EasyMock.replay(reader);
        LocalBindingDefinition definition = loader.load(null, reader, null);
        assertEquals(new URI("foo"), definition.getTargetUri());
        EasyMock.verify(reader);
    }

    public void testNoUri() throws Exception {
        XMLStreamReader reader = EasyMock.createMock(XMLStreamReader.class);
        EasyMock.expect(reader.getAttributeValue(null, "uri")).andReturn(null);
        EasyMock.replay(reader);
        LocalBindingDefinition definition = loader.load(null, reader, null);
        assertNull(definition.getTargetUri());
        EasyMock.verify(reader);
    }

    public void testBadUri() throws Exception {
        XMLStreamReader reader = EasyMock.createMock(XMLStreamReader.class);
        EasyMock.expect(reader.getAttributeValue(null, "uri")).andReturn("foo foo");
        EasyMock.replay(reader);
        try {
            loader.load(null, reader, null);
            fail();
        } catch (LoaderException e) {
            // expected
        }
        EasyMock.verify(reader);
    }

    protected void setUp() throws Exception {
        super.setUp();
        loader = new LocalBindingLoader(null);
    }
}
