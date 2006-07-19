/*
 *
 * Copyright 2006 The Apache Software Foundation or its licensors as applicable
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.apache.tuscany.core.loader;

import javax.xml.namespace.QName;
import static javax.xml.stream.XMLStreamConstants.END_ELEMENT;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import junit.framework.TestCase;
import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import org.osoa.sca.Version;

import org.apache.tuscany.spi.loader.LoaderException;
import org.apache.tuscany.spi.model.Include;

/**
 * @version $Rev$ $Date$
 */
public class IncludeLoaderTestCase extends TestCase {
    private static final QName INCLUDE = new QName(Version.XML_NAMESPACE_1_0, "include");

    private IncludeLoader loader;
    private XMLStreamReader reader;

    public void testName() throws LoaderException, XMLStreamException {
        String name = "foo";
        expect(reader.getName()).andReturn(INCLUDE);
        expect(reader.getAttributeValue(null, "name")).andReturn(name);
        expect(reader.getAttributeValue(null, "scdlLocation")).andReturn(null);
        expect(reader.next()).andReturn(END_ELEMENT);
        replay(reader);

        Include include = loader.load(null, reader, null);
        assertEquals(name, include.getName());
        verify(reader);
    }

    protected void setUp() throws Exception {
        super.setUp();
        this.loader = new IncludeLoader(null);
        reader = createMock(XMLStreamReader.class);
    }
}
