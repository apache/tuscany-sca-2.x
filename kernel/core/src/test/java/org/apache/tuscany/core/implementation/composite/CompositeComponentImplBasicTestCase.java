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

import java.net.URI;
import java.util.Collections;

import org.apache.tuscany.spi.component.AtomicComponent;
import org.apache.tuscany.spi.component.CompositeComponent;
import org.apache.tuscany.spi.component.Reference;
import org.apache.tuscany.spi.component.ReferenceBinding;
import org.apache.tuscany.spi.component.Service;
import org.apache.tuscany.spi.event.Event;
import org.apache.tuscany.spi.event.RuntimeEventListener;
import org.apache.tuscany.spi.idl.InvalidServiceContractException;
import org.apache.tuscany.spi.model.Scope;
import org.apache.tuscany.spi.wire.InboundWire;

import junit.framework.Assert;
import junit.framework.TestCase;
import org.apache.tuscany.core.component.event.ComponentStart;
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
    private AtomicComponent component;

    public void testGetScope() {
        CompositeComponent composite = new CompositeComponentImpl(URI.create("parent"), null);
        Assert.assertEquals(Scope.SYSTEM, composite.getScope());
    }

    public void testReferencesServices() throws Exception {
        CompositeComponent composite = new CompositeComponentImpl(URI.create("parent"), null);
        Service service = EasyMock.createMock(Service.class);
        EasyMock.expect(service.getUri()).andReturn(URI.create("#service")).atLeastOnce();
        service.getServiceBindings();
        EasyMock.expectLastCall().andReturn(Collections.emptyList()).atLeastOnce();
        EasyMock.replay(service);
        composite.register(service);
        composite.register(getReference("#bar"));
    }

    public void testOnEvent() {
        CompositeComponent composite = new CompositeComponentImpl(URI.create("parent"), null);
        Event event = new Event() {
            public Object getSource() {
                return null;
            }
        };
        RuntimeEventListener listener = createMock(RuntimeEventListener.class);
        listener.onEvent(isA(ComponentStart.class));
        listener.onEvent(eq(event));
        expectLastCall();
        replay(listener);
        composite.addListener(listener);
        composite.start();
        composite.onEvent(event);
    }

    public void testPrepare() throws Exception {
        CompositeComponent composite = new CompositeComponentImpl(URI.create("parent"), null);
        composite.prepare();
    }

    private Reference getReference(String name) throws InvalidServiceContractException {
        ReferenceBinding binding = EasyMock.createNiceMock(ReferenceBinding.class);
        InboundWire wire = TestUtils.createInboundWire(Bar.class);
        wire.setContainer(binding);
        EasyMock.expect(binding.getInboundWire()).andReturn(wire).atLeastOnce();
        EasyMock.expect(binding.getUri()).andReturn(URI.create("#reference")).atLeastOnce();
        replay(binding);

        Reference reference = new ReferenceImpl(URI.create(name), wire.getServiceContract());
        reference.addReferenceBinding(binding);
        return reference;
    }

    protected void setUp() throws Exception {
        super.setUp();
        component = EasyMock.createMock(AtomicComponent.class);
        component.getInboundWires();
        EasyMock.expectLastCall().andReturn(Collections.emptyList()).atLeastOnce();
        EasyMock.replay(component);
    }

    private interface Bar {
    }

}
