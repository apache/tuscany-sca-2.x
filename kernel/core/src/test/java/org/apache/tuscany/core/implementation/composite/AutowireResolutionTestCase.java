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

import org.apache.tuscany.spi.component.CompositeComponent;
import org.apache.tuscany.spi.wire.InboundWire;

import junit.framework.TestCase;
import org.apache.tuscany.core.implementation.TestUtils;
import org.easymock.EasyMock;
import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.eq;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

/**
 * Verfies specific autowire resolution scenarios
 *
 * @version $Rev$ $Date$
 */
public class AutowireResolutionTestCase extends TestCase {

    public void testConstruction() {
        CompositeComponent parent = createMock(CompositeComponent.class);
        replay(parent);
        CompositeComponent component = new CompositeComponentImpl("test", parent, null, null);
        assertEquals("test", component.getName());
        assertSame(parent, component.getParent());
        verify(parent);
    }

    public void testResolvedByAutowire() throws Exception {
        CompositeComponent parent = createMock(CompositeComponent.class);
        InboundWire wire = TestUtils.createInboundWire(Foo.class);
        wire.setContainer(parent);
        EasyMock.expect(parent.resolveAutowire(eq(Foo.class))).andReturn(wire);
        replay(parent);
        CompositeComponent component = new CompositeComponentImpl("test", parent, null, null);
        assertSame(wire, component.resolveAutowire(Foo.class));
        verify(parent);
    }

    public void testSystemResolvedByAutowire() throws Exception {
        CompositeComponent parent = createMock(CompositeComponent.class);
        InboundWire wire = TestUtils.createInboundWire(Foo.class);
        wire.setContainer(parent);
        EasyMock.expect(parent.resolveSystemAutowire(eq(Foo.class))).andReturn(wire);
        replay(parent);
        CompositeComponent component = new CompositeComponentImpl("test", parent, null, null);
        assertSame(wire, component.resolveSystemAutowire(Foo.class));
        verify(parent);
    }

    /**
     * Verify parent resolution strategy for application serviceBindings
     */
    public void testNamespaceIsolationAutowire() throws Exception {
        Foo foo = new Foo() {
        };
        CompositeComponent parent = new CompositeComponentImpl("parent", null, null, null);
        parent.registerJavaObject("foo", Foo.class, foo);
        CompositeComponent component = new CompositeComponentImpl("test", parent, null, null);
        assertNull(component.resolveAutowire(Foo.class));
    }

    protected void setUp() throws Exception {
        super.setUp();
    }

    public static interface Foo {
    }
}
