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

import static org.osoa.sca.Constants.SCA_NS;

import org.apache.tuscany.spi.deployer.CompositeClassLoader;
import org.apache.tuscany.spi.deployer.DeploymentContext;
import org.apache.tuscany.spi.loader.LoaderException;
import org.apache.tuscany.spi.model.CompositeImplementation;
import org.apache.tuscany.spi.services.artifact.Artifact;
import org.apache.tuscany.spi.services.artifact.ArtifactRepository;

import junit.framework.TestCase;
import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.reportMatcher;
import static org.easymock.EasyMock.verify;
import org.easymock.IArgumentMatcher;

/**
 * @version $Rev$ $Date$
 */
public class ImplementationCompositeLoaderTestCase extends TestCase {
    private static final QName IMPLEMENTATION_COMPOSITE = new QName(SCA_NS, "implementation.composite");

    private ClassLoader cl;
    private ImplementationCompositeLoader loader;
    private XMLStreamReader reader;
    private DeploymentContext context;
    private ArtifactRepository artifactRepository;

    public void testName() throws LoaderException, XMLStreamException, MalformedURLException {
        String name = "foo";
        expect(reader.getName()).andReturn(IMPLEMENTATION_COMPOSITE);
        expect(reader.getAttributeValue(null, "name")).andReturn(name);
        expect(reader.getAttributeValue(null, "group")).andReturn(null);
        expect(reader.getAttributeValue(null, "version")).andReturn(null);
        expect(reader.getAttributeValue(null, "scdlLocation")).andReturn(null);
        expect(reader.getAttributeValue(null, "jarLocation")).andReturn(null);
        expect(reader.next()).andReturn(END_ELEMENT);
        replay(reader);

        replay(context);
        replay(artifactRepository);

        CompositeImplementation impl = loader.load(null, reader, context);
        verify(reader);
        verify(context);
        verify(artifactRepository);
        assertEquals(name, impl.getName());
        assertNull(impl.getScdlLocation());
        assertNull(impl.getClassLoader());
    }

    public void testWithArtifact() throws LoaderException, XMLStreamException, MalformedURLException {
        String name = "foo";
        expect(reader.getName()).andReturn(IMPLEMENTATION_COMPOSITE);
        expect(reader.getAttributeValue(null, "name")).andReturn(name);
        expect(reader.getAttributeValue(null, "group")).andReturn("com.example");
        expect(reader.getAttributeValue(null, "version")).andReturn("1.0");
        expect(reader.getAttributeValue(null, "scdlLocation")).andReturn(null);
        expect(reader.getAttributeValue(null, "jarLocation")).andReturn(null);
        expect(reader.next()).andReturn(END_ELEMENT);
        replay(reader);

        expect(context.getClassLoader()).andReturn(cl);
        replay(context);
        URL url = new URL("http://www.example.com/sca/base.jar");
        artifactRepository.resolve(artifactMatcher(url, "com.example", name, "1.0"));
        replay(artifactRepository);

        CompositeImplementation impl = loader.load(null, reader, context);
        verify(reader);
        verify(context);
        verify(artifactRepository);
        assertEquals(name, impl.getName());
        assertEquals(new URL("jar:http://www.example.com/sca/base.jar!/META-INF/sca/default.scdl"),
            impl.getScdlLocation());
        assertTrue(impl.getClassLoader() instanceof CompositeClassLoader);
    }

    public void testWithScdlLocation() throws LoaderException, XMLStreamException, MalformedURLException {
        String name = "foo";
        expect(reader.getName()).andReturn(IMPLEMENTATION_COMPOSITE);
        expect(reader.getAttributeValue(null, "name")).andReturn(name);
        expect(reader.getAttributeValue(null, "group")).andReturn(null);
        expect(reader.getAttributeValue(null, "version")).andReturn(null);
        expect(reader.getAttributeValue(null, "scdlLocation")).andReturn("bar.scdl");
        expect(reader.getAttributeValue(null, "jarLocation")).andReturn(null);
        expect(reader.next()).andReturn(END_ELEMENT);
        replay(reader);

        expect(context.getScdlLocation()).andReturn(new URL("http://www.example.com/sca/base.scdl"));
        expect(context.getClassLoader()).andReturn(cl);
        replay(context);
        replay(artifactRepository);

        CompositeImplementation impl = loader.load(null, reader, context);
        verify(reader);
        verify(context);
        verify(artifactRepository);
        assertEquals(name, impl.getName());
        assertEquals(new URL("http://www.example.com/sca/bar.scdl"), impl.getScdlLocation());
        assertSame(cl, impl.getClassLoader());
    }

    public void testWithJarLocation() throws LoaderException, XMLStreamException, MalformedURLException {
        String name = "foo";
        expect(reader.getName()).andReturn(IMPLEMENTATION_COMPOSITE);
        expect(reader.getAttributeValue(null, "name")).andReturn(name);
        expect(reader.getAttributeValue(null, "group")).andReturn(null);
        expect(reader.getAttributeValue(null, "version")).andReturn(null);
        expect(reader.getAttributeValue(null, "scdlLocation")).andReturn(null);
        expect(reader.getAttributeValue(null, "jarLocation")).andReturn("bar.jar");
        expect(reader.next()).andReturn(END_ELEMENT);
        replay(reader);

        expect(context.getScdlLocation()).andReturn(new URL("http://www.example.com/sca/base.scdl"));
        expect(context.getClassLoader()).andReturn(cl);
        replay(context);
        replay(artifactRepository);

        CompositeImplementation impl = loader.load(null, reader, context);
        verify(reader);
        verify(context);
        verify(artifactRepository);
        assertEquals(name, impl.getName());
        assertEquals(new URL("jar:http://www.example.com/sca/bar.jar!/META-INF/sca/default.scdl"),
            impl.getScdlLocation());
    }

    protected void setUp() throws Exception {
        super.setUp();
        artifactRepository = createMock(ArtifactRepository.class);
        reader = createMock(XMLStreamReader.class);
        context = createMock(DeploymentContext.class);
        cl = getClass().getClassLoader();
        loader = new ImplementationCompositeLoader(null, artifactRepository);
    }

    protected static Artifact artifactMatcher(final URL url,
                                              final String group,
                                              final String name,
                                              final String version) {
        reportMatcher(new IArgumentMatcher() {

            public boolean matches(Object object) {
                if (!(object instanceof Artifact)) {
                    return false;
                }

                Artifact artifact = (Artifact) object;
                boolean match = group.equals(artifact.getGroup())
                    && name.equals(artifact.getName())
                    && version.equals(artifact.getVersion())
                    && "jar".equals(artifact.getType());
                if (match) {
                    artifact.setUrl(url);
                }
                return match;
            }

            public void appendTo(StringBuffer stringBuffer) {
                stringBuffer.append(group).append(':').append(name).append(':').append(version);
            }
        });
        return null;
    }
}
