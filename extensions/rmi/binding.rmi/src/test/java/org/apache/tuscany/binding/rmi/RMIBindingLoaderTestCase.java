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
package org.apache.tuscany.binding.rmi;

import static org.easymock.EasyMock.expect;
import static org.easymock.classextension.EasyMock.createMock;
import static org.easymock.classextension.EasyMock.replay;
import static org.easymock.classextension.EasyMock.verify;

import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import junit.framework.TestCase;

import org.apache.tuscany.spi.component.CompositeComponent;
import org.apache.tuscany.spi.deployer.DeploymentContext;
import org.apache.tuscany.spi.loader.LoaderException;
import org.apache.tuscany.spi.loader.LoaderRegistry;

public class RMIBindingLoaderTestCase extends TestCase {
    private CompositeComponent parent;

    private XMLStreamReader reader;

    private DeploymentContext deploymentContext;

    private LoaderRegistry registry;

    private RMIBindingLoader loader;

    public void testLoad() throws LoaderException, XMLStreamException {
        expect(reader.getAttributeValue(null, "host")).andReturn("host");
        expect(reader.getAttributeValue(null, "port")).andReturn("0");
        expect(reader.getAttributeValue(null, "serviceName")).andReturn("servicename");
        expect(reader.next()).andReturn(XMLStreamConstants.END_ELEMENT);

        replay(reader);
        replay(deploymentContext);

        RMIBindingLoader mockLoader = new RMIBindingLoader(registry);
        mockLoader.load(parent, null, reader, deploymentContext);
        verify(reader);
        verify(deploymentContext);
    }

    public void testGetXMLType() throws LoaderException {
        assertEquals(RMIBindingLoader.BINDING_RMI, loader.getXMLType());
    }

    protected void setUp() throws Exception {
        super.setUp();
        registry = createMock(LoaderRegistry.class);
        loader = new RMIBindingLoader(registry);

        parent = createMock(CompositeComponent.class);
        reader = createMock(XMLStreamReader.class);
        deploymentContext = createMock(DeploymentContext.class);
    }
}
