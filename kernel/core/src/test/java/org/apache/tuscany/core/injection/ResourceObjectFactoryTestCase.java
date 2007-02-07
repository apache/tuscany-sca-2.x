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
package org.apache.tuscany.core.injection;

import org.apache.tuscany.spi.component.AtomicComponent;
import org.apache.tuscany.spi.component.CompositeComponent;
import org.apache.tuscany.spi.host.ResourceHost;
import org.apache.tuscany.spi.wire.InboundWire;

import junit.framework.TestCase;
import org.easymock.EasyMock;

/**
 * @version $Rev$ $Date$
 */
public class ResourceObjectFactoryTestCase extends TestCase {

    public void testResolveFromHostByType() throws Exception {
        ResourceHost host = EasyMock.createMock(ResourceHost.class);
        EasyMock.expect(host.resolveResource(EasyMock.eq(String.class))).andReturn("foo");
        EasyMock.replay(host);
        CompositeComponent parent = EasyMock.createMock(CompositeComponent.class);
        EasyMock.expect(parent.resolveAutowire(EasyMock.eq(String.class))).andReturn(null);
        EasyMock.replay(parent);
        ResourceObjectFactory<String> factory = new ResourceObjectFactory<String>(String.class, false, parent, host);
        assertEquals("foo", factory.getInstance());
        EasyMock.verify(host);
        EasyMock.verify(parent);
    }

    public void testResolveFromHostByName() throws Exception {
        ResourceHost host = EasyMock.createMock(ResourceHost.class);
        EasyMock.expect(host.resolveResource(EasyMock.eq(String.class),
            EasyMock.eq("sca://localhost/bar"))).andReturn("foo");
        EasyMock.replay(host);
        ResourceObjectFactory<String> factory =
            new ResourceObjectFactory<String>(String.class, "sca://localhost/bar", false, null, host);
        assertEquals("foo", factory.getInstance());
        EasyMock.verify(host);
    }

    public void testResolveFromParentByType() throws Exception {
        CompositeComponent parent = EasyMock.createMock(CompositeComponent.class);
        InboundWire wire = EasyMock.createMock(InboundWire.class);
        EasyMock.expect(wire.getTargetService()).andReturn("foo");
        EasyMock.replay(wire);

        EasyMock.expect(parent.resolveAutowire(EasyMock.eq(String.class))).andReturn(wire);
        EasyMock.replay(parent);
        ResourceObjectFactory<String> factory = new ResourceObjectFactory<String>(String.class, false, parent, null);
        assertEquals("foo", factory.getInstance());
        EasyMock.verify(parent);
    }

    public void testResolveFromParentByName() throws Exception {
        AtomicComponent component = EasyMock.createMock(AtomicComponent.class);
        EasyMock.expect(component.getTargetInstance()).andReturn("foo");
        EasyMock.replay(component);
        CompositeComponent parent = EasyMock.createMock(CompositeComponent.class);
        EasyMock.expect(parent.getChild(EasyMock.eq("bar"))).andReturn(component);
        EasyMock.replay(parent);
        ResourceObjectFactory<String> factory =
            new ResourceObjectFactory<String>(String.class, "bar", false, parent, null);
        assertEquals("foo", factory.getInstance());
        EasyMock.verify(parent);
        EasyMock.verify(component);
    }

    /**
     * Verifies if a resource is not found as a child of the parent, the host namespace will be searched
     */
    public void testResolveFromParentThenResolveFromHost() throws Exception {
        ResourceHost host = EasyMock.createMock(ResourceHost.class);
        EasyMock.expect(host.resolveResource(EasyMock.eq(String.class))).andReturn("foo");
        EasyMock.replay(host);

        CompositeComponent parent = EasyMock.createMock(CompositeComponent.class);
        EasyMock.expect(parent.resolveAutowire(EasyMock.eq(String.class))).andReturn(null);
        EasyMock.replay(parent);
        ResourceObjectFactory<String> factory = new ResourceObjectFactory<String>(String.class, false, parent, host);
        assertEquals("foo", factory.getInstance());
        EasyMock.verify(parent);
        EasyMock.verify(host);
    }

    public void testResolveFromParentThenResolveFromHostNotFound() throws Exception {
        ResourceHost host = EasyMock.createMock(ResourceHost.class);
        EasyMock.expect(host.resolveResource(EasyMock.eq(String.class))).andReturn(null);
        EasyMock.replay(host);
        CompositeComponent parent = EasyMock.createMock(CompositeComponent.class);
        EasyMock.expect(parent.resolveAutowire(EasyMock.eq(String.class))).andReturn(null);
        EasyMock.replay(parent);
        ResourceObjectFactory<String> factory = new ResourceObjectFactory<String>(String.class, true, parent, host);
        assertNull(factory.getInstance());
        EasyMock.verify(parent);
        EasyMock.verify(host);
    }

    public void testResolveByTypeNotFound() throws Exception {
        ResourceHost host = EasyMock.createMock(ResourceHost.class);
        EasyMock.expect(host.resolveResource(EasyMock.eq(String.class))).andReturn(null);
        EasyMock.replay(host);

        InboundWire wire = EasyMock.createMock(InboundWire.class);
        EasyMock.expect(wire.getTargetService()).andReturn(null);
        EasyMock.replay(wire);

        CompositeComponent parent = EasyMock.createMock(CompositeComponent.class);
        EasyMock.expect(parent.resolveAutowire(EasyMock.eq(String.class))).andReturn(null);
        EasyMock.replay(parent);
        ResourceObjectFactory<String> factory = new ResourceObjectFactory<String>(String.class, false, parent, host);
        try {
            factory.getInstance();
            fail();
        } catch (ResourceNotFoundException e) {
            //expected
        }
        EasyMock.verify(parent);
        EasyMock.verify(host);
    }

    public void testResolveByTypeNotFoundOptional() throws Exception {
        ResourceHost host = EasyMock.createMock(ResourceHost.class);
        EasyMock.expect(host.resolveResource(EasyMock.eq(String.class))).andReturn(null);
        EasyMock.replay(host);
        CompositeComponent parent = EasyMock.createMock(CompositeComponent.class);
        EasyMock.expect(parent.resolveAutowire(EasyMock.eq(String.class))).andReturn(null);
        EasyMock.replay(parent);
        ResourceObjectFactory<String> factory = new ResourceObjectFactory<String>(String.class, true, parent, host);
        assertNull(factory.getInstance());
        EasyMock.verify(parent);
        EasyMock.verify(host);
    }


}
