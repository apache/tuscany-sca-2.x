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

import java.util.List;

import org.apache.tuscany.spi.component.CompositeComponent;
import org.apache.tuscany.spi.component.Reference;
import org.apache.tuscany.spi.component.Service;
import org.apache.tuscany.spi.event.Event;
import org.apache.tuscany.spi.event.RuntimeEventListener;
import org.apache.tuscany.spi.extension.ServiceExtension;
import org.apache.tuscany.spi.idl.InvalidServiceContractException;
import org.apache.tuscany.spi.model.Scope;
import org.apache.tuscany.spi.wire.InboundWire;

import junit.framework.Assert;
import junit.framework.TestCase;
import org.apache.tuscany.core.component.event.CompositeStart;
import org.apache.tuscany.core.implementation.TestUtils;
import org.easymock.EasyMock;
import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.eq;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.isA;
import static org.easymock.EasyMock.replay;

/**
 * @version $Rev$ $Date$
 */
public class CompositeComponentImplBasicTestCase extends TestCase {

    public void testGetScope() {
        CompositeComponent composite = new CompositeComponentImpl("parent", null, null, null);
        Assert.assertEquals(Scope.SYSTEM, composite.getScope());
    }

    public void testGetChildren() throws Exception {
        CompositeComponent composite = new CompositeComponentImpl("parent", null, null, null);
        composite.register(new ServiceExtension("foo", null));
        Assert.assertEquals(1, composite.getChildren().size());
    }

    public void testGetServices() throws Exception {
        CompositeComponent composite = new CompositeComponentImpl("parent", null, null, null);
        ServiceExtension extension = new ServiceExtension("foo", null);
        InboundWire wire = TestUtils.createInboundWire(Foo.class);
        wire.setContainer(extension);
        extension.setInboundWire(wire);
        composite.register(extension);
        composite.register(getReference("bar"));
        Assert.assertEquals(1, composite.getServices().size());
    }

    public void testGetService() throws Exception {
        CompositeComponent composite = new CompositeComponentImpl("parent", null, null, null);
        composite.register(new ServiceExtension("foo", null));
        composite.start();
        assertNotNull(composite.getService("foo"));
    }

    public void testServiceNotFound() throws Exception {
        CompositeComponent composite = new CompositeComponentImpl("parent", null, null, null);
        composite.register(new ServiceExtension("foo", null));
        composite.start();
        assertNull(composite.getService("bar"));
    }

    public void testNotService() throws Exception {
        CompositeComponent composite = new CompositeComponentImpl("parent", null, null, null);
        composite.register(getReference("foo"));
        composite.start();
        assertNull(composite.getService("foo"));
    }

    public void testReferencesServices() throws Exception {
        CompositeComponent composite = new CompositeComponentImpl("parent", null, null, null);
        composite.register(new ServiceExtension("foo", null));
        composite.register(getReference("bar"));
        Assert.assertEquals(1, composite.getReferences().size());
    }

    public void testServiceInterfaces() throws Exception {
        CompositeComponent composite = new CompositeComponentImpl("parent", null, null, null);
        Service service1 = getService("foo", Foo.class);
        composite.register(service1);
        Service service2 = getService("bar", Bar.class);
        composite.register(service2);

        List<Class<?>> interfaces = composite.getServiceInterfaces();
        assertEquals(2, interfaces.size());
        for (Class o : interfaces) {
            if (!(Foo.class.isAssignableFrom(o)) && !(Bar.class.isAssignableFrom(o))) {
                fail();
            }
        }
    }

    public void testOnEvent() {
        CompositeComponent composite = new CompositeComponentImpl("parent", null, null, null);
        Event event = new Event() {
            public Object getSource() {
                return null;
            }
        };
        RuntimeEventListener listener = createMock(RuntimeEventListener.class);
        listener.onEvent(isA(CompositeStart.class));
        listener.onEvent(eq(event));
        expectLastCall();
        replay(listener);
        composite.addListener(listener);
        composite.start();
        composite.onEvent(event);
    }

    public void testPrepare() throws Exception {
        CompositeComponent composite = new CompositeComponentImpl("parent", null, null, null);
        composite.prepare();
    }

    private Reference getReference(String name) throws InvalidServiceContractException {
        Reference reference = EasyMock.createNiceMock(Reference.class);
        EasyMock.expect(reference.isSystem()).andReturn(false).atLeastOnce();
        InboundWire wire = TestUtils.createInboundWire(Bar.class);
        wire.setContainer(reference);
        EasyMock.expect(reference.getInboundWire()).andReturn(wire).atLeastOnce();

        reference.getName();
        expectLastCall().andReturn(name).anyTimes();
        replay(reference);
        return reference;
    }

    private Service getService(String name, Class<?> interfaze) throws InvalidServiceContractException {
        Service service = createMock(Service.class);
        EasyMock.expect(service.isSystem()).andReturn(false).atLeastOnce();
        service.getName();
        expectLastCall().andReturn(name).anyTimes();
        InboundWire wire = TestUtils.createInboundWire(interfaze);
        wire.setContainer(service);
        EasyMock.expect(service.getInboundWire()).andReturn(wire).atLeastOnce();
        replay(service);
        return service;
    }

    private interface Foo {
    }

    private interface Bar {
    }

}
