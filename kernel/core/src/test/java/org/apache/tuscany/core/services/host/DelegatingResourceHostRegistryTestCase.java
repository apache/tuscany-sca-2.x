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
package org.apache.tuscany.core.services.host;

import org.apache.tuscany.spi.host.ResourceHost;
import org.apache.tuscany.spi.host.ResourceResolutionException;

import junit.framework.TestCase;
import org.easymock.EasyMock;

/**
 * @version $Rev$ $Date$
 */
public class DelegatingResourceHostRegistryTestCase extends TestCase {

    public void testResolveByType() throws Exception {
        Object ret = new Object();
        DelegatingResourceHostRegistry registry = new DelegatingResourceHostRegistry();
        registry.registerResource(Object.class, ret);
        assertEquals(ret, registry.resolveResource(Object.class));
    }

    public void testResolveByUri() throws Exception {
        ResourceHost host = EasyMock.createMock(ResourceHost.class);
        EasyMock.expect(host.resolveResource(String.class, "Foo://foo")).andReturn("result");
        EasyMock.replay(host);
        DelegatingResourceHostRegistry registry = new DelegatingResourceHostRegistry();
        registry.registerResourceHost("Foo://", host);
        assertEquals("result", registry.resolveResource(String.class, "Foo://foo"));
        EasyMock.verify(host);
    }

    public void testResolveBySCAUri() throws Exception {
        ResourceHost host = EasyMock.createMock(ResourceHost.class);
        EasyMock.replay(host);
        DelegatingResourceHostRegistry registry = new DelegatingResourceHostRegistry();
        registry.registerResourceHost("Foo://", host);
        Object ret = new Object();
        registry.registerResource(Object.class, "foo", ret);
        assertEquals(ret, registry.resolveResource(Object.class, "SCA://foo"));
        EasyMock.verify(host);
    }

    public void testResolveByUriNotFound() throws Exception {
        ResourceHost host = EasyMock.createMock(ResourceHost.class);
        EasyMock.replay(host);
        DelegatingResourceHostRegistry registry = new DelegatingResourceHostRegistry();
        registry.registerResourceHost("Foo://", host);
        try {
            assertEquals("result", registry.resolveResource(String.class, "Bar://bar"));
            fail();
        } catch (ResourceResolutionException e) {
            //expected
        }
        EasyMock.verify(host);
    }

    public void testUnregisterHost() throws Exception {
        ResourceHost host = EasyMock.createMock(ResourceHost.class);
        EasyMock.replay(host);
        DelegatingResourceHostRegistry registry = new DelegatingResourceHostRegistry();
        registry.registerResourceHost("Foo://", host);
        registry.unregisterResourceHost("Foo://");
        try {
            registry.resolveResource(String.class, "Foo://foo");
            fail();
        } catch (ResourceResolutionException e) {
            //expected
        }
        EasyMock.verify(host);
    }

    public void testUnregisterResource() throws Exception {
        DelegatingResourceHostRegistry registry = new DelegatingResourceHostRegistry();
        registry.registerResource(Object.class, new Object());
        registry.unregisterResource(Object.class);
        assertNull(registry.resolveResource(Object.class));
    }

    public void testUnregisterMappedResource() throws Exception {
        DelegatingResourceHostRegistry registry = new DelegatingResourceHostRegistry();
        registry.registerResource(Object.class, "foo", new Object());
        registry.registerResource(Object.class, new Object());
        registry.unregisterResource(Object.class);
        assertNull(registry.resolveResource(Object.class));
        assertNotNull(registry.resolveResource(Object.class, "foo"));
        registry.unregisterResource(Object.class, "foo");
        assertNull(registry.resolveResource(Object.class));
    }

    public void testReolvedByTypeToMappedResource() throws Exception {
        DelegatingResourceHostRegistry registry = new DelegatingResourceHostRegistry();
        registry.registerResource(Object.class, "foo", new Object());
        assertNull(registry.resolveResource(Object.class));
    }

    public void testDelegatingResolveResource() throws Exception {
        Object ret = new Object();
        ResourceHost host = EasyMock.createMock(ResourceHost.class);
        EasyMock.expect(host.resolveResource(Object.class)).andReturn(ret);
        EasyMock.replay(host);
        DelegatingResourceHostRegistry registry = new DelegatingResourceHostRegistry();
        registry.registerResourceHost("Foo://", host);
        assertEquals(ret, registry.resolveResource(Object.class));
        EasyMock.verify(host);
    }

    public void testDelegatingResolveResourceByTypeandName() throws Exception {
        Object ret = new Object();
        ResourceHost host = EasyMock.createMock(ResourceHost.class);
        EasyMock.expect(host.resolveResource(EasyMock.eq(Object.class), EasyMock.eq("Foo://bar"))).andReturn(ret);
        EasyMock.replay(host);
        DelegatingResourceHostRegistry registry = new DelegatingResourceHostRegistry();
        registry.registerResourceHost("Foo://", host);
        assertEquals(ret, registry.resolveResource(Object.class, "Foo://bar"));
        EasyMock.verify(host);
    }

    public void testResolveLocalResourceFirst() throws Exception {
        Object local = new Object();
        ResourceHost host = EasyMock.createMock(ResourceHost.class);
        EasyMock.replay(host);
        DelegatingResourceHostRegistry registry = new DelegatingResourceHostRegistry();
        registry.registerResourceHost("Foo://", host);
        registry.registerResource(Object.class, local);
        assertEquals(local, registry.resolveResource(Object.class));
        EasyMock.verify(host);
    }


}
