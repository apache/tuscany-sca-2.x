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

import java.util.ArrayList;
import java.util.List;
import java.net.URI;

import org.apache.tuscany.spi.component.AtomicComponent;
import org.apache.tuscany.spi.component.CompositeComponent;
import org.apache.tuscany.spi.component.DuplicateNameException;
import org.apache.tuscany.spi.wire.InboundWire;

import junit.framework.TestCase;
import org.apache.tuscany.core.implementation.TestUtils;
import org.apache.tuscany.core.mock.component.Source;
import org.easymock.EasyMock;

/**
 * Verfies children with the same name cannot be registered in the same composite
 *
 * @version $Rev$ $Date$
 */
public class DuplicateRegistrationTestCase extends TestCase {

    public void testDuplicateRegistration() throws Exception {
        CompositeComponent parent = new CompositeComponentImpl(URI.create("parent"), null, null, null);
        parent.start();

        List<Class<?>> interfaces = new ArrayList<Class<?>>();
        interfaces.add(Source.class);
        AtomicComponent component1 = EasyMock.createMock(AtomicComponent.class);
        EasyMock.expect(component1.getUri()).andReturn(URI.create("source")).atLeastOnce();
        component1.stop();
        List<InboundWire> wires = TestUtils.createInboundWires(interfaces);
        TestUtils.populateInboundWires(component1, wires);
        EasyMock.expect(component1.getInboundWires()).andReturn(wires).atLeastOnce();
        EasyMock.replay(component1);

        AtomicComponent component2 = EasyMock.createMock(AtomicComponent.class);
        EasyMock.expect(component2.getUri()).andReturn(URI.create("source")).atLeastOnce();
        component2.stop();
        EasyMock.replay(component2);

        parent.register(component1);
        try {
            parent.register(component2);
            fail();
        } catch (DuplicateNameException e) {
            // ok
        }
        parent.stop();
    }

    public void testDuplicateNameSystemService() throws Exception {
        List<Class<?>> services = new ArrayList<Class<?>>();
        services.add(Source.class);
        CompositeComponent parent = new CompositeComponentImpl(URI.create("foo"), null, null, null);
        AtomicComponent component = EasyMock.createMock(AtomicComponent.class);
        EasyMock.expect(component.getUri()).andReturn(URI.create("bar")).atLeastOnce();
        List<InboundWire> wires = TestUtils.createInboundWires(services);
        TestUtils.populateInboundWires(component, wires);
        EasyMock.expect(component.getInboundWires()).andReturn(wires).atLeastOnce();
        EasyMock.replay(component);
        parent.register(component);
        AtomicComponent component2 = EasyMock.createMock(AtomicComponent.class);
        EasyMock.expect(component2.getUri()).andReturn(URI.create("bar")).atLeastOnce();
        EasyMock.replay(component2);
        try {
            parent.register(component2);
            fail();
        } catch (DuplicateNameException e) {
            // expected
        }
    }


    protected void setUp() throws Exception {
        super.setUp();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

}
