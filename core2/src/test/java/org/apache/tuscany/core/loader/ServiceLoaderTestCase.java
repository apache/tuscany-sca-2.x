/**
 *
 * Copyright 2006 The Apache Software Foundation
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

import static javax.xml.stream.XMLStreamConstants.END_ELEMENT;
import static javax.xml.stream.XMLStreamConstants.START_ELEMENT;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.apache.tuscany.model.Service;
import org.apache.tuscany.model.ServiceContract;
import org.apache.tuscany.spi.loader.LoaderContext;
import org.apache.tuscany.spi.loader.LoaderException;
import org.apache.tuscany.spi.loader.StAXLoaderRegistry;
import org.jmock.Mock;
import org.jmock.MockObjectTestCase;

/**
 * @version $Rev$ $Date$
 */
public class ServiceLoaderTestCase extends MockObjectTestCase {
    private ServiceLoader loader;
    private LoaderContext loaderContext;
    private Mock mockReader;
    private Mock mockRegistry;

    public void testWithNoInterface() throws LoaderException, XMLStreamException {
        String name = "service";
        mockReader.expects(once()).method("getName").will(returnValue(AssemblyConstants.SERVICE));
        mockReader.expects(once()).method("getAttributeValue").with(NULL, eq("name")).will(returnValue(name));
        mockReader.expects(once()).method("next").will(returnValue(END_ELEMENT));
        Service service = loader.load((XMLStreamReader) mockReader.proxy(), null);
        assertNotNull(service);
        assertEquals(name, service.getName());
    }

    public void testWithInterface() throws LoaderException, XMLStreamException {
        String name = "service";
        ServiceContract sc = new ServiceContract(){};
        mockReader.expects(once()).method("getName").will(returnValue(AssemblyConstants.SERVICE));
        mockReader.expects(once()).method("getAttributeValue").with(NULL, eq("name")).will(returnValue(name));
        mockReader.expects(atLeastOnce()).method("next").will(onConsecutiveCalls(returnValue(START_ELEMENT), returnValue(END_ELEMENT)));
        mockRegistry.expects(once()).method("load").with(eq(mockReader.proxy()), eq(loaderContext)).will(returnValue(sc));
        Service service = loader.load((XMLStreamReader) mockReader.proxy(), loaderContext);
        assertNotNull(service);
        assertEquals(name, service.getName());
        assertSame(sc, service.getServiceContract());
    }

    protected void setUp() throws Exception {
        super.setUp();
        loader = new ServiceLoader();
        mockReader = mock(XMLStreamReader.class);
        mockRegistry = mock(StAXLoaderRegistry.class);
        loader.setRegistry((StAXLoaderRegistry) mockRegistry.proxy());
        loaderContext = new LoaderContext(null, null);
    }
}
