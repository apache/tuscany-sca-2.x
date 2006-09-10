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
package org.apache.tuscany.core.implementation.system.component;

import org.apache.tuscany.spi.component.CompositeComponent;

import junit.framework.TestCase;
import org.apache.tuscany.core.component.AutowireComponent;
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
        AutowireComponent autowire = createMock(AutowireComponent.class);
        replay(parent);
        replay(autowire);
        SystemCompositeComponent component = new SystemCompositeComponentImpl("test", parent, autowire, null, null);
        assertEquals("test", component.getName());
        assertSame(parent, component.getParent());
        verify(parent);
        verify(autowire);
    }

    public void testResolveToSelf() {
        CompositeComponent parent = createMock(CompositeComponent.class);
        AutowireComponent autowire = createMock(AutowireComponent.class);
        replay(parent);
        replay(autowire);
        SystemCompositeComponent component = new SystemCompositeComponentImpl("test", parent, autowire, null, null);
        assertSame(component, component.resolveInstance(CompositeComponent.class));
        assertSame(component, component.resolveInstance(SystemCompositeComponent.class));
        verify(parent);
        verify(autowire);
    }

    public void testResolvedByAutowire() {
        Foo foo = new Foo() {
        };
        CompositeComponent parent = createMock(CompositeComponent.class);
        AutowireComponent autowire = createMock(AutowireComponent.class);
        EasyMock.expect(autowire.resolveInstance(eq(Foo.class))).andReturn(foo);
        replay(parent);
        replay(autowire);
        SystemCompositeComponent component = new SystemCompositeComponentImpl("test", parent, autowire, null, null);
        assertSame(foo, component.resolveInstance(Foo.class));
        verify(parent);
        verify(autowire);
    }

    protected void setUp() throws Exception {
        super.setUp();
    }

    public static interface Foo {
    }
}
