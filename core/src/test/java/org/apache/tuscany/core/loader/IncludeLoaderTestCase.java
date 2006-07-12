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
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.jmock.Mock;
import org.jmock.MockObjectTestCase;
import org.osoa.sca.Version;

import org.apache.tuscany.spi.loader.LoaderException;
import org.apache.tuscany.spi.model.Include;

/**
 * @version $Rev$ $Date$
 */
public class IncludeLoaderTestCase extends MockObjectTestCase {
    private static final QName INCLUDE = new QName(Version.XML_NAMESPACE_1_0, "include");

    private IncludeLoader loader;
    private Mock mockReader;

    public void testName() throws LoaderException, XMLStreamException {
        String name = "foo";
        mockReader.expects(once()).method("getName").will(returnValue(IncludeLoaderTestCase.INCLUDE));
        mockReader.expects(atLeastOnce()).method("getAttributeValue")
                .with(ANYTHING, ANYTHING)
                .will(returnValue(name));
        mockReader.expects(once()).method("next").will(returnValue(XMLStreamConstants.END_ELEMENT));
        Include include = loader.load(null, (XMLStreamReader) mockReader.proxy(), null);
        assertEquals(name, include.getName());
    }

    protected void setUp() throws Exception {
        super.setUp();
        this.loader = new IncludeLoader();
        mockReader = mock(XMLStreamReader.class);

    }
}
