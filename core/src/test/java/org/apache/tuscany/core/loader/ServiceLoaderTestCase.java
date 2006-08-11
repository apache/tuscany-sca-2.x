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

import javax.xml.namespace.QName;
import static javax.xml.stream.XMLStreamConstants.END_ELEMENT;
import static javax.xml.stream.XMLStreamConstants.START_ELEMENT;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.jmock.Mock;
import org.jmock.MockObjectTestCase;
import static org.osoa.sca.Version.XML_NAMESPACE_1_0;

import org.apache.tuscany.core.deployer.RootDeploymentContext;
import org.apache.tuscany.spi.deployer.DeploymentContext;
import org.apache.tuscany.spi.loader.LoaderException;
import org.apache.tuscany.spi.loader.LoaderRegistry;
import org.apache.tuscany.spi.model.ServiceContract;
import org.apache.tuscany.spi.model.ServiceDefinition;

/**
 * Verifies loading of a service definition from an XML-based assembly
 *
 * @version $Rev$ $Date$
 */
public class ServiceLoaderTestCase extends MockObjectTestCase {
    private static final QName SERVICE = new QName(XML_NAMESPACE_1_0, "service");
    private static final QName REFERENCE = new QName(XML_NAMESPACE_1_0, "reference");

    private ServiceLoader loader;
    private DeploymentContext deploymentContext;
    private Mock mockReader;
    private Mock mockRegistry;

    public void testWithNoInterface() throws LoaderException, XMLStreamException {
        String name = "serviceDefinition";
//        String target = "target";
        mockReader.expects(once()).method("getName").will(returnValue(SERVICE));
        // todo figure out how to check ordering
        mockReader.expects(once()).method("getAttributeValue")
                .with(ANYTHING, ANYTHING)
                .will(returnValue(name));
        mockReader.expects(once()).method("next").will(returnValue(END_ELEMENT));
        ServiceDefinition serviceDefinition = loader.load(null, (XMLStreamReader) mockReader.proxy(), null);
        assertNotNull(serviceDefinition);
        assertEquals(name, serviceDefinition.getName());
    }

    public void testWithInterface() throws LoaderException, XMLStreamException {
        String name = "serviceDefinition";
        String target = "target";
        ServiceContract sc = new ServiceContract() {
        };
        mockReader.expects(atLeastOnce()).method("getName")
          .will(onConsecutiveCalls(returnValue(SERVICE),returnValue(SERVICE)
                  ,returnValue(REFERENCE)));
        // todo figure out how to check ordering
        mockReader.expects(once()).method("getAttributeValue")
                .with(ANYTHING, ANYTHING)
                .will((returnValue(name)));
        mockReader.expects(atLeastOnce()).method("next")
            .will(onConsecutiveCalls(returnValue(START_ELEMENT), returnValue(START_ELEMENT), returnValue(END_ELEMENT)));
        mockRegistry.expects(once()).method("load").with(eq(null), eq(mockReader.proxy()), eq(deploymentContext))
            .will(returnValue(sc));
        mockReader.expects(once()).method("getElementText").withNoArguments()
        
        .will((returnValue(target)));
        
        ServiceDefinition serviceDefinition =
            loader.load(null, (XMLStreamReader) mockReader.proxy(), deploymentContext);
        assertNotNull(serviceDefinition);
        assertEquals(name, serviceDefinition.getName());
        assertSame(sc, serviceDefinition.getServiceContract());
    }

    protected void setUp() throws Exception {
        super.setUp();
        mockReader = mock(XMLStreamReader.class);
        mockRegistry = mock(LoaderRegistry.class);
        loader = new ServiceLoader((LoaderRegistry) mockRegistry.proxy());
        deploymentContext = new RootDeploymentContext(null, null, null, null);
    }
}
