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

import org.apache.tuscany.spi.component.CompositeComponent;
import org.apache.tuscany.spi.component.SCAObject;
import org.apache.tuscany.spi.component.Service;
import org.apache.tuscany.spi.host.ResourceHost;
import org.apache.tuscany.spi.host.ResourceResolutionException;

import junit.framework.TestCase;
import org.easymock.EasyMock;

/**
 * @version $Rev$ $Date$
 */
public class DelegatingResourceHostRegistryTestCase extends TestCase {

    public void testResolveByUri() throws Exception {
        CompositeComponent parent = EasyMock.createNiceMock(CompositeComponent.class);
        ResourceHost host = EasyMock.createMock(ResourceHost.class);
        EasyMock.expect(host.resolveResource(String.class, "Foo://foo")).andReturn("result");
        EasyMock.replay(host);
        DelegatingResourceHostRegistry registry = new DelegatingResourceHostRegistry(parent);
        registry.register("Foo://", host);
        assertEquals("result", registry.resolveResource(String.class, "Foo://foo"));
        EasyMock.verify(host);
    }

    public void testResolveBySCAUri() throws Exception {
        Service child = EasyMock.createMock(Service.class);
        EasyMock.expect(child.getServiceInstance()).andReturn("result");
        EasyMock.replay(child);
        CompositeComponent parent = EasyMock.createMock(CompositeComponent.class);
        EasyMock.expect(parent.getSystemChild("foo")).andReturn(child);
        EasyMock.replay(parent);
        ResourceHost host = EasyMock.createMock(ResourceHost.class);
        EasyMock.replay(host);
        DelegatingResourceHostRegistry registry = new DelegatingResourceHostRegistry(parent);
        registry.register("Foo://", host);
        assertEquals("result", registry.resolveResource(String.class, "SCA://foo"));
        EasyMock.verify(host);
    }


    /**
     * Tests system components not exposed as services are not visible
     */
    public void testResolveNonService() throws Exception {
        SCAObject child = EasyMock.createMock(SCAObject.class);
        EasyMock.expect(child.getServiceInstance()).andReturn("result");
        EasyMock.replay(child);
        CompositeComponent parent = EasyMock.createMock(CompositeComponent.class);
        EasyMock.expect(parent.getSystemChild("foo")).andReturn(child);
        EasyMock.replay(parent);
        ResourceHost host = EasyMock.createMock(ResourceHost.class);
        EasyMock.replay(host);
        DelegatingResourceHostRegistry registry = new DelegatingResourceHostRegistry(parent);
        registry.register("Foo://", host);
        assertNull(registry.resolveResource(String.class, "SCA://foo"));
        EasyMock.verify(host);
    }

    public void testResolveBySCALocalHostUri() throws Exception {
        Service child = EasyMock.createMock(Service.class);
        EasyMock.expect(child.getServiceInstance()).andReturn("result");
        EasyMock.replay(child);
        CompositeComponent parent = EasyMock.createMock(CompositeComponent.class);
        EasyMock.expect(parent.getSystemChild("foo")).andReturn(child);
        EasyMock.replay(parent);
        ResourceHost host = EasyMock.createMock(ResourceHost.class);
        EasyMock.replay(host);
        DelegatingResourceHostRegistry registry = new DelegatingResourceHostRegistry(parent);
        registry.register("Foo://", host);
        assertEquals("result", registry.resolveResource(String.class, "SCA://localhost/foo"));
        EasyMock.verify(host);
    }


    public void testResolveByUriNotFound() throws Exception {
        CompositeComponent parent = EasyMock.createNiceMock(CompositeComponent.class);
        ResourceHost host = EasyMock.createMock(ResourceHost.class);
        EasyMock.replay(host);
        DelegatingResourceHostRegistry registry = new DelegatingResourceHostRegistry(parent);
        registry.register("Foo://", host);
        try {
            assertEquals("result", registry.resolveResource(String.class, "Bar://bar"));
            fail();
        } catch (ResourceResolutionException e) {
            //expected
        }
        EasyMock.verify(host);
    }

    public void testUnregister() throws Exception {
        CompositeComponent parent = EasyMock.createNiceMock(CompositeComponent.class);
        ResourceHost host = EasyMock.createMock(ResourceHost.class);
        EasyMock.replay(host);
        DelegatingResourceHostRegistry registry = new DelegatingResourceHostRegistry(parent);
        registry.register("Foo://", host);
        registry.unregister("Foo://");
        try {
            registry.resolveResource(String.class, "Foo://foo");
            fail();
        } catch (ResourceResolutionException e) {
            //expected
        }
        EasyMock.verify(host);
    }

    public void testDelegatingResolveResource() throws Exception {
        CompositeComponent parent = EasyMock.createMock(CompositeComponent.class);
        EasyMock.expect(parent.resolveSystemExternalInstance(String.class)).andReturn(null);
        EasyMock.replay(parent);
        ResourceHost host = EasyMock.createMock(ResourceHost.class);
        EasyMock.expect(host.resolveResource(String.class)).andReturn("result");
        EasyMock.replay(host);
        DelegatingResourceHostRegistry registry = new DelegatingResourceHostRegistry(parent);
        registry.register("Foo://", host);
        assertEquals("result", registry.resolveResource(String.class));
        EasyMock.verify(host);
    }


}
