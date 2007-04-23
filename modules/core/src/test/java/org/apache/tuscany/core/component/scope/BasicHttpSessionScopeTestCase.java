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

import junit.framework.TestCase;

import org.apache.tuscany.core.component.SimpleWorkContext;
import org.apache.tuscany.spi.component.AtomicComponent;
import org.apache.tuscany.spi.component.InstanceWrapper;
import org.apache.tuscany.spi.component.ScopeContainer;
import org.apache.tuscany.spi.component.WorkContext;
import org.easymock.EasyMock;

/**
 * @version $$Rev$$ $$Date$$
 */
public abstract class BasicHttpSessionScopeTestCase extends TestCase {
    private ScopeContainer scopeContainer;
    private AtomicComponent component;
    private InstanceWrapper wrapper;
    private WorkContext workContext;

    public void testLifecycleManagement() throws Exception {
/*
        // start the request
        Object session = new Object();
        workContext.setIdentifier(Scope.SESSION, session);

        EasyMock.expect(component.createInstanceWrapper()).andReturn(wrapper);
        wrapper.start();
        wrapper.stop();
        EasyMock.replay(component, wrapper);
        assertSame(wrapper, scopeContainer.getWrapper(component));
        assertSame(wrapper, scopeContainer.getWrapper(component));
        scopeContainer.onEvent(new HttpSessionEnd(this, session));
        EasyMock.verify(component, wrapper);
*/
    }

    public void testGetAssociatedInstance() throws Exception {
/*
        // start the request
        Object session = new Object();
        workContext.setIdentifier(Scope.SESSION, session);

        EasyMock.expect(component.createInstanceWrapper()).andReturn(wrapper);
        wrapper.start();
        EasyMock.replay(component, wrapper);
        assertSame(wrapper, scopeContainer.getWrapper(component));
        assertSame(wrapper, scopeContainer.getAssociatedWrapper(component));
        EasyMock.verify(component, wrapper);
*/
    }

    public void testGetAssociatedInstanceNonExistent() throws Exception {
/*
        URI id = URI.create("oops");
        EasyMock.expect(component.getUri()).andReturn(id);
        EasyMock.replay(component);

        // start the request
        Object session = new Object();
        workContext.setIdentifier(Scope.SESSION, session);
        try {
            scopeContainer.getAssociatedWrapper(component);
            fail();
        } catch (TargetNotFoundException e) {
            assertEquals(id.toString(), e.getMessage());
            EasyMock.verify(component);
        }
*/
    }

    public void testSessionIsolation() throws Exception {
/*
        // start the request
        Object session1 = new Object();
        Object session2 = new Object();

        InstanceWrapper wrapper2 = EasyMock.createNiceMock(InstanceWrapper.class);
        EasyMock.expect(component.createInstanceWrapper()).andReturn(wrapper);
        wrapper.start();
        EasyMock.expect(component.createInstanceWrapper()).andReturn(wrapper2);
        wrapper2.start();
        EasyMock.replay(component, wrapper);
        workContext.setIdentifier(Scope.SESSION, session1);
        assertSame(wrapper, scopeContainer.getWrapper(component));
        assertSame(wrapper, scopeContainer.getAssociatedWrapper(component));
        workContext.setIdentifier(Scope.SESSION, session2);
        assertSame(wrapper2, scopeContainer.getWrapper(component));
        assertSame(wrapper2, scopeContainer.getAssociatedWrapper(component));
        workContext.setIdentifier(Scope.SESSION, session1);
        assertSame(wrapper, scopeContainer.getWrapper(component));
        EasyMock.verify(component, wrapper);
*/
    }

    public void testDestroyErrorMonitor() throws Exception {
/*
        TargetDestructionException ex = new TargetDestructionException("oops", "again");
        monitor.destructionError(ex);
        EasyMock.expect(component.createInstanceWrapper()).andReturn(wrapper);
        wrapper.start();
        wrapper.stop();
        EasyMock.expectLastCall().andThrow(ex);
        EasyMock.replay(component, wrapper, monitor);

        Object id = new Object();
        scopeContainer.onEvent(new HttpSessionStart(this, id));
        workContext.setIdentifier(Scope.SESSION, id);
        assertSame(wrapper, scopeContainer.getWrapper(component));
        scopeContainer.onEvent(new HttpSessionEnd(this, id));
        EasyMock.verify(component, wrapper, monitor);
*/
    }

    public void testDestroyOrder() throws Exception {
/*
        Object session = new Object();
        workContext.setIdentifier(Scope.SESSION, session);

        IMocksControl control = EasyMock.createStrictControl();
        InstanceWrapper wrapper1 = control.createMock(InstanceWrapper.class);
        InstanceWrapper wrapper2 = control.createMock(InstanceWrapper.class);
        InstanceWrapper wrapper3 = control.createMock(InstanceWrapper.class);
        AtomicComponent component1 = control.createMock(AtomicComponent.class);
        AtomicComponent component2 = control.createMock(AtomicComponent.class);
        AtomicComponent component3 = control.createMock(AtomicComponent.class);

        component1.addListener(scopeContainer);
        component2.addListener(scopeContainer);
        component3.addListener(scopeContainer);
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
        scopeContainer.onEvent(new HttpSessionStart(this, session));
        assertSame(wrapper1, scopeContainer.getWrapper(component1));
        assertSame(wrapper2, scopeContainer.getWrapper(component2));
        assertSame(wrapper3, scopeContainer.getWrapper(component3));
        scopeContainer.onEvent(new HttpSessionEnd(this, session));
        control.verify();
*/
    }

    public void testReuseSession() throws Exception {
/*
        Object session = new Object();
        workContext.setIdentifier(Scope.SESSION, session);

        IMocksControl control = EasyMock.createStrictControl();
        InstanceWrapper wrapper1 = control.createMock(InstanceWrapper.class);
        InstanceWrapper wrapper2 = control.createMock(InstanceWrapper.class);
        AtomicComponent component1 = control.createMock(AtomicComponent.class);

        component1.addListener(scopeContainer);
        EasyMock.expect(component1.createInstanceWrapper()).andReturn(wrapper1);
        wrapper1.start();
        wrapper1.stop();
        EasyMock.expect(component1.createInstanceWrapper()).andReturn(wrapper2);
        wrapper2.start();
        wrapper2.stop();
        control.replay();

        scopeContainer.register(component1, null);
        scopeContainer.onEvent(new HttpSessionStart(this, session));
        assertSame(wrapper1, scopeContainer.getWrapper(component1));
        scopeContainer.onEvent(new HttpSessionEnd(this, session));

        scopeContainer.onEvent(new HttpSessionStart(this, session));
        assertSame(wrapper2, scopeContainer.getWrapper(component1));
        scopeContainer.onEvent(new HttpSessionEnd(this, session));
        control.verify();
*/
    }

    protected void setUp() throws Exception {
        super.setUp();
        component = EasyMock.createStrictMock(AtomicComponent.class);
        wrapper = EasyMock.createStrictMock(InstanceWrapper.class);

        workContext = new SimpleWorkContext();
        scopeContainer = new HttpSessionScopeContainer(workContext);
        scopeContainer.start();

        component.addListener(scopeContainer);
        EasyMock.replay(component);
        scopeContainer.register(component, null);
        EasyMock.reset(component);
    }
}
