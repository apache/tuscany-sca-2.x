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
package org.apache.tuscany.core.component.scope;

import java.net.URI;

import junit.framework.TestCase;
import org.easymock.EasyMock;
import org.easymock.IMocksControl;

import org.apache.tuscany.core.component.event.RequestEnd;
import org.apache.tuscany.core.component.event.RequestStart;
import org.apache.tuscany.spi.component.AtomicComponent;
import org.apache.tuscany.spi.component.InstanceWrapper;
import org.apache.tuscany.spi.component.ScopeContainer;
import org.apache.tuscany.spi.component.ScopeContainerMonitor;
import org.apache.tuscany.spi.component.TargetDestructionException;
import org.apache.tuscany.spi.component.TargetNotFoundException;

/**
 * @version $$Rev$$ $$Date$$
 */
public abstract class BasicRequestScopeTestCase extends TestCase {
    private ScopeContainerMonitor monitor;
    private ScopeContainer scopeContainer;
    private AtomicComponent component;
    private InstanceWrapper wrapper;

    public void testLifecycleManagement() throws Exception {
        EasyMock.expect(component.createInstanceWrapper()).andReturn(wrapper);
        wrapper.start();
        EasyMock.replay(component, wrapper);
        assertSame(wrapper, scopeContainer.getWrapper(component));
        assertSame(wrapper, scopeContainer.getWrapper(component));
        EasyMock.verify(component, wrapper);
    }

    public void testGetAssociatedInstance() throws Exception {
        EasyMock.expect(component.createInstanceWrapper()).andReturn(wrapper);
        wrapper.start();
        EasyMock.replay(component, wrapper);
        assertSame(wrapper, scopeContainer.getWrapper(component));
        assertSame(wrapper, scopeContainer.getAssociatedWrapper(component));
        EasyMock.verify(component, wrapper);
    }

    public void testGetAssociatedInstanceNonExistent() throws Exception {
        URI id = URI.create("oops");
        EasyMock.expect(component.getUri()).andReturn(id);
        EasyMock.replay(component);

        try {
            scopeContainer.getAssociatedWrapper(component);
            fail();
        } catch (TargetNotFoundException e) {
            assertEquals(id.toString(), e.getMessage());
            EasyMock.verify(component);
        }
    }

    public void testRequestIsolation() throws Exception {
        IMocksControl control = EasyMock.createStrictControl();
        InstanceWrapper wrapper1 = control.createMock(InstanceWrapper.class);
        InstanceWrapper wrapper2 = control.createMock(InstanceWrapper.class);
        AtomicComponent component1 = control.createMock(AtomicComponent.class);
        AtomicComponent component2 = control.createMock(AtomicComponent.class);

        EasyMock.expect(component1.createInstanceWrapper()).andReturn(wrapper1);
        wrapper1.start();
        wrapper1.stop();
        EasyMock.expect(component2.createInstanceWrapper()).andReturn(wrapper2);
        wrapper2.start();
        wrapper2.stop();
        control.replay();

        scopeContainer.register(component1, null);
        scopeContainer.register(component2, null);
        scopeContainer.onEvent(new RequestStart(this));
        assertSame(wrapper1, scopeContainer.getWrapper(component1));
        scopeContainer.onEvent(new RequestEnd(this));
        scopeContainer.onEvent(new RequestStart(this));
        assertSame(wrapper2, scopeContainer.getWrapper(component2));
        scopeContainer.onEvent(new RequestEnd(this));
        control.verify();
    }

    public void testDestroyErrorMonitor() throws Exception {
        TargetDestructionException ex = new TargetDestructionException("oops", "again");
        monitor.destructionError(ex);
        EasyMock.expect(component.createInstanceWrapper()).andReturn(wrapper);
        wrapper.start();
        wrapper.stop();
        EasyMock.expectLastCall().andThrow(ex);
        EasyMock.replay(component, wrapper, monitor);

        scopeContainer.onEvent(new RequestStart(this));
        assertSame(wrapper, scopeContainer.getWrapper(component));
        scopeContainer.onEvent(new RequestEnd(this));
        EasyMock.verify(component, wrapper, monitor);
    }

    public void testDestroyOrder() throws Exception {
        IMocksControl control = EasyMock.createStrictControl();
        InstanceWrapper wrapper1 = control.createMock(InstanceWrapper.class);
        InstanceWrapper wrapper2 = control.createMock(InstanceWrapper.class);
        InstanceWrapper wrapper3 = control.createMock(InstanceWrapper.class);
        AtomicComponent component1 = control.createMock(AtomicComponent.class);
        AtomicComponent component2 = control.createMock(AtomicComponent.class);
        AtomicComponent component3 = control.createMock(AtomicComponent.class);

        EasyMock.expect(component1.createInstanceWrapper()).andReturn(wrapper1);
        wrapper1.start();
        EasyMock.expect(component2.createInstanceWrapper()).andReturn(wrapper2);
        wrapper2.start();
        EasyMock.expect(component3.createInstanceWrapper()).andReturn(wrapper3);
        wrapper3.start();
        wrapper3.stop();
        wrapper2.stop();
        wrapper1.stop();
        control.replay();

        scopeContainer.register(component1, null);
        scopeContainer.register(component2, null);
        scopeContainer.register(component3, null);
        scopeContainer.onEvent(new RequestStart(this));
        assertSame(wrapper1, scopeContainer.getWrapper(component1));
        assertSame(wrapper2, scopeContainer.getWrapper(component2));
        assertSame(wrapper3, scopeContainer.getWrapper(component3));
        scopeContainer.onEvent(new RequestEnd(this));
        control.verify();
    }

    protected void setUp() throws Exception {
        super.setUp();
        component = EasyMock.createStrictMock(AtomicComponent.class);
        wrapper = EasyMock.createStrictMock(InstanceWrapper.class);
        monitor = EasyMock.createStrictMock(ScopeContainerMonitor.class);
        scopeContainer = new RequestScopeContainer(monitor);
        scopeContainer.start();

        component.addListener(scopeContainer);
        EasyMock.replay(component);
        scopeContainer.register(component, null);
        EasyMock.reset(component);
    }
}
