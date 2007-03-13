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

import org.apache.tuscany.core.component.event.RequestEnd;
import org.apache.tuscany.spi.component.AtomicComponent;
import org.apache.tuscany.spi.component.InstanceWrapper;
import org.apache.tuscany.spi.component.ScopeContainer;
import org.apache.tuscany.spi.component.TargetNotFoundException;

/**
 * @version $$Rev$$ $$Date$$
 */
public class BasicRequestScopeTestCase extends TestCase {
    private AtomicComponent component;
    private InstanceWrapper wrapper;
    private ScopeContainer scopeContainer;

    public void testLifecycleManagement() throws Exception {
        EasyMock.expect(component.createInstanceWrapper()).andReturn(wrapper);
        EasyMock.replay(component, wrapper);
        assertSame(wrapper, scopeContainer.getWrapper(component));
        assertSame(wrapper, scopeContainer.getWrapper(component));
        EasyMock.verify(component, wrapper);
    }

    public void testGetAssociatedInstance() throws Exception {
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

        try {
            scopeContainer.getAssociatedWrapper(component);
            fail();
        } catch (TargetNotFoundException e) {
            assertEquals(id.toString(), e.getMessage());
            EasyMock.verify(component);
        }
    }

    public void testRequestIsolation() throws Exception {
        InstanceWrapper wrapper2 = EasyMock.createNiceMock(InstanceWrapper.class);
        EasyMock.expect(component.createInstanceWrapper()).andReturn(wrapper).andReturn(wrapper2);
        EasyMock.replay(component, wrapper);
        assertSame(wrapper, scopeContainer.getWrapper(component));
        scopeContainer.onEvent(new RequestEnd(this));
        assertSame(wrapper2, scopeContainer.getWrapper(component));
        EasyMock.verify(component, wrapper);
    }

    protected void setUp() throws Exception {
        super.setUp();
        component = EasyMock.createNiceMock(AtomicComponent.class);
        wrapper = EasyMock.createNiceMock(InstanceWrapper.class);
        scopeContainer = new RequestScopeContainer(null, null);
        scopeContainer.start();

        component.addListener(scopeContainer);
        EasyMock.replay(component);
        scopeContainer.register(component);
        EasyMock.reset(component);
    }
}
