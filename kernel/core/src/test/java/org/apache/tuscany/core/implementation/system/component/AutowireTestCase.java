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

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;
import org.easymock.EasyMock;

/**
 * Performs basic autowiring tests to composite artifacts
 *
 * @version $$Rev$$ $$Date$$
 */
public class AutowireTestCase extends TestCase {

    /**
     * Tests autowiring to an atomic context
     *
     * @throws Exception
     */
    public void testAtomicAutowire() throws Exception {
        SystemCompositeComponent parent = new SystemCompositeComponentImpl("parent", null, null, null, null);
        parent.start();

        List<Class<?>> interfaces = new ArrayList<Class<?>>();
        interfaces.add(Source.class);
        interfaces.add(Source2.class);
        Source originalSource = new SourceImpl();
        SystemAtomicComponent component = EasyMock.createMock(SystemAtomicComponent.class);
        EasyMock.expect(component.getName()).andReturn("source").atLeastOnce();
        EasyMock.expect(component.getServiceInstance()).andReturn(originalSource).atLeastOnce();
        EasyMock.expect(component.getServiceInterfaces()).andReturn(interfaces);
        EasyMock.replay(component);
        parent.register(component);

        Source source = parent.resolveInstance(Source.class);
        assertNotNull(source);
        Source2 source2 = parent.resolveInstance(Source2.class);
        assertSame(source, source2);
        assertNull(parent.resolveExternalInstance(Source.class));
        EasyMock.verify(component);
    }

    /**
     * Tests autowiring to a service context which is wired to an atomic context.
     */
    public void testServiceAutowire() throws Exception {
        SystemCompositeComponent parent = new SystemCompositeComponentImpl("parent", null, null, null, null);
        parent.start();

        List<Class<?>> interfaces = new ArrayList<Class<?>>();
        interfaces.add(Source.class);
        interfaces.add(Source2.class);

        Source serviceSource = new SourceImpl();
        SystemService component = EasyMock.createMock(SystemService.class);
        EasyMock.expect(component.getName()).andReturn("service").atLeastOnce();
        component.getInterface();
        EasyMock.expectLastCall().andReturn(Source.class).atLeastOnce();
        EasyMock.expect(component.getServiceInstance()).andReturn(serviceSource);
        EasyMock.replay(component);
        parent.register(component);


        SystemAtomicComponent component2 = EasyMock.createMock(SystemAtomicComponent.class);
        EasyMock.expect(component2.getName()).andReturn("source").atLeastOnce();
        EasyMock.expect(component2.getServiceInterfaces()).andReturn(interfaces).atLeastOnce();
        EasyMock.replay(component2);
        parent.register(component2);

        Source source = parent.resolveExternalInstance(Source.class);
        assertSame(serviceSource, source);
        Source2 source2 = parent.resolveExternalInstance(Source2.class);
        assertNull(source2);
        EasyMock.verify(component);
        EasyMock.verify(component2);
    }

    /**
     * Tests autowiring to a reference
     */
    public void testReferenceAutowire() throws Exception {
        SystemCompositeComponent parent = new SystemCompositeComponentImpl("parent", null, null, null, null);
        parent.start();

        Source refSource = new SourceImpl();
        SystemReference reference = EasyMock.createMock(SystemReference.class);
        EasyMock.expect(reference.getName()).andReturn("service").atLeastOnce();
        EasyMock.expect(reference.getServiceInstance()).andReturn(refSource);
        reference.getInterface();
        EasyMock.expectLastCall().andReturn(Source.class);
        EasyMock.replay(reference);
        parent.register(reference);

        Source source = parent.resolveInstance(Source.class);
        assertNotNull(source);
        assertNull(parent.resolveExternalInstance(Source.class));
        EasyMock.verify(reference);
    }

    public static class SourceImpl implements Source, Source2 {
        public SourceImpl() {
        }
    }

    public static interface Source {

    }

    public static interface Source2 {
    }

}
