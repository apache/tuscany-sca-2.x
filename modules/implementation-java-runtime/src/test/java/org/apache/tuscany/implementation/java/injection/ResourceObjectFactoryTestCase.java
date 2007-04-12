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
package org.apache.tuscany.implementation.java.injection;

import org.apache.tuscany.implementation.java.injection.ResourceNotFoundException;
import org.apache.tuscany.implementation.java.injection.ResourceObjectFactory;
import org.apache.tuscany.spi.host.ResourceHost;
import org.apache.tuscany.spi.wire.Wire;

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
        ResourceObjectFactory<String> factory = new ResourceObjectFactory<String>(String.class, false, host);
        assertEquals("foo", factory.getInstance());
        EasyMock.verify(host);
    }

    public void testResolveFromHostByName() throws Exception {
        ResourceHost host = EasyMock.createMock(ResourceHost.class);
        EasyMock.expect(host.resolveResource(EasyMock.eq(String.class),
            EasyMock.eq("sca://localhost/bar"))).andReturn("foo");
        EasyMock.replay(host);
        ResourceObjectFactory<String> factory =
            new ResourceObjectFactory<String>(String.class, "sca://localhost/bar", false, host);
        assertEquals("foo", factory.getInstance());
        EasyMock.verify(host);
    }


    public void testResolveFromParentThenResolveFromHostNotFound() throws Exception {
        ResourceHost host = EasyMock.createMock(ResourceHost.class);
        EasyMock.expect(host.resolveResource(EasyMock.eq(String.class))).andReturn(null);
        EasyMock.replay(host);
        ResourceObjectFactory<String> factory = new ResourceObjectFactory<String>(String.class, true, host);
        assertNull(factory.getInstance());
        EasyMock.verify(host);
    }

    public void testResolveByTypeNotFound() throws Exception {
        ResourceHost host = EasyMock.createMock(ResourceHost.class);
        EasyMock.expect(host.resolveResource(EasyMock.eq(String.class))).andReturn(null);
        EasyMock.replay(host);

        Wire wire = EasyMock.createMock(Wire.class);
        EasyMock.expect(wire.getTargetInstance()).andReturn(null);
        EasyMock.replay(wire);

        ResourceObjectFactory<String> factory = new ResourceObjectFactory<String>(String.class, false, host);
        try {
            factory.getInstance();
            fail();
        } catch (ResourceNotFoundException e) {
            //expected
        }
        EasyMock.verify(host);
    }

    public void testResolveByTypeNotFoundOptional() throws Exception {
        ResourceHost host = EasyMock.createMock(ResourceHost.class);
        EasyMock.expect(host.resolveResource(EasyMock.eq(String.class))).andReturn(null);
        EasyMock.replay(host);
        ResourceObjectFactory<String> factory = new ResourceObjectFactory<String>(String.class, true, host);
        assertNull(factory.getInstance());
        EasyMock.verify(host);
    }


}
