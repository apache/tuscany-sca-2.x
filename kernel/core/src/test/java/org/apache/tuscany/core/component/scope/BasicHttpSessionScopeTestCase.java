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

import org.apache.tuscany.core.component.WorkContextImpl;
import org.apache.tuscany.core.component.event.HttpSessionStart;
import org.apache.tuscany.core.component.event.HttpSessionEnd;
import org.apache.tuscany.spi.component.AtomicComponent;
import org.apache.tuscany.spi.component.InstanceWrapper;
import org.apache.tuscany.spi.component.TargetNotFoundException;
import org.apache.tuscany.spi.component.WorkContext;
import org.apache.tuscany.spi.component.ScopeContainer;
import org.apache.tuscany.spi.component.ScopeContainerMonitor;
import org.apache.tuscany.spi.component.TargetDestructionException;
import org.apache.tuscany.spi.model.Scope;

/**
 * @version $$Rev$$ $$Date$$
 */
public class BasicHttpSessionScopeTestCase extends TestCase {
    private ScopeContainerMonitor monitor;
    private ScopeContainer scopeContainer;
    private AtomicComponent component;
    private InstanceWrapper wrapper;
    private WorkContext workContext;

    public void testLifecycleManagement() throws Exception {
        // start the request
        Object session = new Object();
        workContext.setIdentifier(Scope.SESSION, session);

        EasyMock.expect(component.createInstanceWrapper()).andReturn(wrapper);
        EasyMock.replay(component, wrapper);
        assertSame(wrapper, scopeContainer.getWrapper(component));
        assertSame(wrapper, scopeContainer.getWrapper(component));
        EasyMock.verify(component, wrapper);
    }

    public void testGetAssociatedInstance() throws Exception {
        // start the request
        Object session = new Object();
        workContext.setIdentifier(Scope.SESSION, session);

        EasyMock.expect(component.createInstanceWrapper()).andReturn(wrapper);
        EasyMock.replay(component, wrapper);
        assertSame(wrapper, scopeContainer.getWrapper(component));
        assertSame(wrapper, scopeContainer.getAssociatedWrapper(component));
        EasyMock.verify(component, wrapper);
    }

    public void testGetAssociatedInstanceNonExistent() throws Exception {
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
    }

    public void testSessionIsolation() throws Exception {
        // start the request
        Object session1 = new Object();
        Object session2 = new Object();

        InstanceWrapper wrapper2 = EasyMock.createNiceMock(InstanceWrapper.class);
        EasyMock.expect(component.createInstanceWrapper()).andReturn(wrapper).andReturn(wrapper2);
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
    }

    public void testDestroyErrorMonitor() throws Exception {
        TargetDestructionException ex = new TargetDestructionException("oops", "again");
        monitor.destructionError(ex);
        EasyMock.expect(component.createInstanceWrapper()).andReturn(wrapper);
        wrapper.stop();
        EasyMock.expectLastCall().andThrow(ex);
        EasyMock.replay(component, wrapper, monitor);

        Object id = new Object();
        scopeContainer.onEvent(new HttpSessionStart(this, id));
        workContext.setIdentifier(Scope.SESSION, id);
        assertSame(wrapper, scopeContainer.getWrapper(component));
        scopeContainer.onEvent(new HttpSessionEnd(this, id));
        EasyMock.verify(component, wrapper, monitor);
    }

    protected void setUp() throws Exception {
        super.setUp();
        component = EasyMock.createNiceMock(AtomicComponent.class);
        wrapper = EasyMock.createNiceMock(InstanceWrapper.class);

        workContext = new WorkContextImpl();
        monitor = EasyMock.createMock(ScopeContainerMonitor.class);
        scopeContainer = new HttpSessionScopeContainer(workContext, monitor);
        scopeContainer.start();

        component.addListener(scopeContainer);
        EasyMock.replay(component);
        scopeContainer.register(component);
        EasyMock.reset(component);
    }
}
