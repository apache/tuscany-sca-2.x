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

import org.apache.tuscany.spi.component.AtomicComponent;
import org.apache.tuscany.spi.component.CompositeComponent;
import org.apache.tuscany.spi.component.SystemAtomicComponent;

import junit.framework.TestCase;
import org.apache.tuscany.core.mock.component.Source;
import org.apache.tuscany.core.mock.component.SourceImpl;
import org.easymock.EasyMock;

/**
 * @version $$Rev$$ $$Date$$
 */
public class CompositeLifecycleTestCase extends TestCase {

    public void testLifecycle() throws Exception {
        CompositeComponent composite = new CompositeComponentImpl("foo", null, null, null);
        composite.start();
        assertNull(composite.getChild("nothtere"));
        composite.stop();
        composite.start();
        assertNull(composite.getChild("nothtere"));
        composite.stop();
    }

    public void testSystemRestart() throws NoSuchMethodException {
        List<Class<?>> interfaces = new ArrayList<Class<?>>();
        interfaces.add(Source.class);
        Source originalSource = new SourceImpl();
        SystemAtomicComponent component = EasyMock.createMock(SystemAtomicComponent.class);
        component.start();
        component.stop();
        EasyMock.expectLastCall().times(2);
        EasyMock.expect(component.getName()).andReturn("source").atLeastOnce();
        EasyMock.expect(component.isSystem()).andReturn(true).atLeastOnce();
        EasyMock.expect(component.getServiceInstance()).andReturn(originalSource).atLeastOnce();
        EasyMock.expect(component.getServiceInterfaces()).andReturn(interfaces);
        EasyMock.replay(component);

        CompositeComponent composite = new CompositeComponentImpl("foo", null, null, null);
        composite.start();
        composite.register(component);

        AtomicComponent atomicComponent = (AtomicComponent) composite.getSystemChild("source");
        Source source = (Source) atomicComponent.getServiceInstance();
        assertNotNull(source);
        composite.stop();
        composite.start();
        atomicComponent = (AtomicComponent) composite.getSystemChild("source");
        Source source2 = (Source) atomicComponent.getServiceInstance();
        assertNotNull(source2);
        composite.stop();
        EasyMock.verify(component);
    }

    public void testRestart() throws NoSuchMethodException {
        List<Class<?>> interfaces = new ArrayList<Class<?>>();
        interfaces.add(Source.class);
        Source originalSource = new SourceImpl();
        AtomicComponent component = EasyMock.createMock(AtomicComponent.class);
        component.start();
        component.stop();
        EasyMock.expectLastCall().times(2);
        EasyMock.expect(component.getName()).andReturn("source").atLeastOnce();
        EasyMock.expect(component.isSystem()).andReturn(false).atLeastOnce();
        EasyMock.expect(component.getServiceInstance()).andReturn(originalSource).atLeastOnce();
        EasyMock.expect(component.getServiceInterfaces()).andReturn(interfaces);
        EasyMock.replay(component);

        CompositeComponent composite = new CompositeComponentImpl("foo", null, null, null);
        composite.start();
        composite.register(component);

        AtomicComponent atomicComponent = (AtomicComponent) composite.getChild("source");
        Source source = (Source) atomicComponent.getServiceInstance();
        assertNotNull(source);
        composite.stop();
        composite.start();
        atomicComponent = (AtomicComponent) composite.getChild("source");
        Source source2 = (Source) atomicComponent.getServiceInstance();
        assertNotNull(source2);
        composite.stop();
        EasyMock.verify(component);
    }

    protected void setUp() throws Exception {
        super.setUp();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }
}
