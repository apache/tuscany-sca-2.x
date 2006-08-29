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

import org.jmock.Mock;
import org.jmock.MockObjectTestCase;
import org.osoa.sca.Version;

import org.apache.tuscany.core.deployer.RootDeploymentContext;
import org.apache.tuscany.spi.loader.LoaderException;
import org.apache.tuscany.spi.model.CompositeImplementation;

/**
 * @version $Rev$ $Date$
 */
public class ImplementationCompositeLoaderTestCase extends MockObjectTestCase {
    private static final QName IMPLEMENTATION_COMPOSITE =
            new QName(Version.XML_NAMESPACE_1_0, "implementation.composite");

    private ImplementationCompositeLoader loader;
    private Mock mockReader;

    public void testName() throws LoaderException, XMLStreamException, MalformedURLException {
        String name = "foo";
        mockReader.expects(once()).method("getName").will(returnValue(IMPLEMENTATION_COMPOSITE));
        mockReader.expects(atLeastOnce()).method("getAttributeValue")
                .with(ANYTHING, ANYTHING)
                .will(returnValue(name));
        mockReader.expects(once()).method("next").will(returnValue(END_ELEMENT));
        URL scdlLocation = new URL("http://META-INF/sca/");
        CompositeImplementation impl = loader.load(null, (XMLStreamReader) mockReader.proxy(),
                new RootDeploymentContext(getClass().getClassLoader(), null, null, scdlLocation));
        assertEquals(name, impl.getName());
    }

    protected void setUp() throws Exception {
        super.setUp();
        this.loader = new ImplementationCompositeLoader(null);
        mockReader = mock(XMLStreamReader.class);

    }
}
