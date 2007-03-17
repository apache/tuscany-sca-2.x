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
package org.apache.tuscany.core.services.deployment;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;

import org.apache.tuscany.spi.generator.GeneratorRegistry;
import org.apache.tuscany.spi.loader.LoaderRegistry;

import junit.framework.TestCase;
import org.apache.tuscany.core.resolver.AutowireResolver;
import org.apache.tuscany.host.deployment.UnsupportedContentTypeException;
import org.easymock.classextension.EasyMock;

/**
 * @version $Rev$ $Date$
 */
public class AssemblyServiceImplTestCase extends TestCase {
    private AssemblyServiceImpl service;

    public void testApplyChangesWithNullURL() {
        try {
            service.applyChanges(null);
            fail();
        } catch (IllegalArgumentException e) {
            //ok
        } catch (Throwable t) {
            fail();
        }
    }

    public void testApplyChangesWhenURLContentTypeIsNull() throws Exception {
        final URLConnection urlConnection = EasyMock.createMock(URLConnection.class);
        EasyMock.expect(urlConnection.getContentType()).andReturn(null);
        EasyMock.replay(urlConnection);
        URLStreamHandler handler = new MockURLStreamHandler(urlConnection);

        URL url = new URL(null, "file:/tmp/foo.xml", handler);
        try {
            service.applyChanges(url);
        } catch (UnsupportedContentTypeException e) {
            assertNull(e.getMessage());
            assertEquals(url.toString(), e.getIdentifier());
            EasyMock.verify(urlConnection);
        } catch (Throwable t) {
            fail();
        }
    }

    public void testApplyChangesWithNullStream() {
        try {
            service.applyChanges(null, "xxx/xxx");
            fail();
        } catch (IllegalArgumentException e) {
            //ok
        } catch (Throwable t) {
            fail();
        }
    }

    public void testApplyChangesWithNullContentType() {
        InputStream is = EasyMock.createMock(InputStream.class);
        EasyMock.replay(is);
        try {
            service.applyChanges(is, null);
            fail();
        } catch (IllegalArgumentException e) {
            EasyMock.verify(is);
        } catch (Throwable t) {
            fail();
        }
    }

    protected void setUp() throws Exception {
        super.setUp();
        LoaderRegistry loaderRegistry = EasyMock.createMock(LoaderRegistry.class);
        EasyMock.replay(loaderRegistry);
        GeneratorRegistry generatorRegistry = EasyMock.createMock(GeneratorRegistry.class);
        EasyMock.replay(generatorRegistry);
        AutowireResolver resolver = EasyMock.createMock(AutowireResolver.class);
        EasyMock.replay(resolver);
        service = new AssemblyServiceImpl(loaderRegistry, generatorRegistry, resolver);
    }

    private static class MockURLStreamHandler extends URLStreamHandler {
        private final URLConnection urlConnection;

        public MockURLStreamHandler(URLConnection urlConnection) {
            this.urlConnection = urlConnection;
        }

        protected URLConnection openConnection(URL url) throws IOException {
            return urlConnection;
        }
    }
}
