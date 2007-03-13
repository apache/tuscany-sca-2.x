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

import org.apache.tuscany.spi.component.AtomicComponent;
import org.apache.tuscany.spi.component.InstanceWrapper;
import org.apache.tuscany.spi.component.TargetResolutionException;
import org.apache.tuscany.core.component.event.ComponentStart;
import org.apache.tuscany.core.component.event.ComponentStop;
import org.apache.tuscany.core.component.event.HttpSessionStart;
import org.apache.tuscany.core.component.event.HttpSessionEnd;

/**
 * @version $$Rev$$ $$Date$$
 */
public class BasicCompositeScopeTestCase extends TestCase {

    private CompositeScopeContainer scopeContainer;
    private AtomicComponent component;
    private InstanceWrapper wrapper;

    public void testWrapperCreation() throws Exception {
        EasyMock.expect(component.createInstanceWrapper()).andReturn(wrapper);
        wrapper.start();
        EasyMock.replay(component, wrapper);
        assertSame(wrapper, scopeContainer.getWrapper(component));
        EasyMock.verify(component, wrapper);
    }

    public void testWrapperRetrieve() throws Exception {
        // first create a wrapper in the context's cache
        EasyMock.expect(component.createInstanceWrapper()).andReturn(wrapper);
        wrapper.start();
        EasyMock.replay(component, wrapper);
        assertSame(wrapper, scopeContainer.getWrapper(component));
        EasyMock.verify(component, wrapper);
        EasyMock.reset(component, wrapper);

        // fetch again and check that the component and wrapper are not called
        EasyMock.replay(component, wrapper);
        assertSame(wrapper, scopeContainer.getWrapper(component));
        EasyMock.verify(component, wrapper);
    }

    public void testAssociatedWrapperRetrieve() throws Exception {
        // first create a wrapper in the context's cache
        EasyMock.expect(component.createInstanceWrapper()).andReturn(wrapper);
        wrapper.start();
        EasyMock.replay(component, wrapper);
        assertSame(wrapper, scopeContainer.getWrapper(component));
        EasyMock.verify(component, wrapper);
        EasyMock.reset(component, wrapper);

        // fetch again and check that the component and wrapper are not called
        EasyMock.replay(component, wrapper);
        assertSame(wrapper, scopeContainer.getAssociatedWrapper(component));
        EasyMock.verify(component, wrapper);
    }

    public void testGetAssociatedInstanceNonExistent() throws Exception {
        URI uri = URI.create("oops");
        EasyMock.expect(component.getUri()).andReturn(uri);
        EasyMock.replay(component, wrapper);
        try {
            scopeContainer.getAssociatedWrapper(component);
            fail();
        } catch (TargetResolutionException e) {
            assertEquals(uri.toString(), e.getMessage());
        }
        EasyMock.verify(component, wrapper);
    }

    @SuppressWarnings("unchecked")
    public void testWrapperReturn() throws Exception{
        EasyMock.expect(component.createInstanceWrapper()).andReturn(wrapper);
        wrapper.start();
        EasyMock.replay(component, wrapper);
        assertSame(wrapper, scopeContainer.getWrapper(component));
        scopeContainer.returnWrapper(component, wrapper);
        EasyMock.verify(component, wrapper);
    }

    public void testLifecycleWithNoEagerInit() throws Exception {
        EasyMock.expect(component.getInitLevel()).andReturn(0);
        EasyMock.replay(component, wrapper);
        scopeContainer.onEvent(new ComponentStart(this, null));
        scopeContainer.onEvent(new ComponentStop(this, null));
        EasyMock.verify(component, wrapper);
    }

    public void testLifecycleWithEagerInit() throws Exception {
        EasyMock.expect(component.getInitLevel()).andReturn(1);
        EasyMock.expect(component.createInstanceWrapper()).andReturn(wrapper);
        wrapper.start();
        wrapper.stop();
        EasyMock.replay(component, wrapper);
        scopeContainer.onEvent(new ComponentStart(this, null));
        scopeContainer.onEvent(new ComponentStop(this, null));
        EasyMock.verify(component, wrapper);
    }

    public void testDestroyOrder() throws Exception {
        scopeContainer = new CompositeScopeContainer(null);
        scopeContainer.start();
        IMocksControl control = EasyMock.createStrictControl();
        InstanceWrapper wrapper1 = control.createMock(InstanceWrapper.class);
        InstanceWrapper wrapper2 = control.createMock(InstanceWrapper.class);
        InstanceWrapper wrapper3 = control.createMock(InstanceWrapper.class);
        AtomicComponent component1 = control.createMock(AtomicComponent.class);
        AtomicComponent component2 = control.createMock(AtomicComponent.class);
        AtomicComponent component3 = control.createMock(AtomicComponent.class);

        EasyMock.expect(component1.getInitLevel()).andStubReturn(-1);
        EasyMock.expect(component2.getInitLevel()).andStubReturn(1);
        EasyMock.expect(component3.getInitLevel()).andStubReturn(-1);

        EasyMock.expect(component2.createInstanceWrapper()).andReturn(wrapper2);
        wrapper2.start();
        EasyMock.expect(component1.createInstanceWrapper()).andReturn(wrapper1);
        wrapper1.start();
        EasyMock.expect(component3.createInstanceWrapper()).andReturn(wrapper3);
        wrapper3.start();
        wrapper3.stop();
        wrapper1.stop();
        wrapper2.stop();
        control.replay();

        scopeContainer.register(component1);
        scopeContainer.register(component2);
        scopeContainer.register(component3);
        scopeContainer.onEvent(new ComponentStart(this, null));
        assertSame(wrapper1, scopeContainer.getWrapper(component1));
        assertSame(wrapper2, scopeContainer.getWrapper(component2));
        assertSame(wrapper3, scopeContainer.getWrapper(component3));
        scopeContainer.onEvent(new ComponentStop(this, null));
        control.verify();
    }
    @SuppressWarnings("unchecked")
    protected void setUp() throws Exception {
        super.setUp();
        component = EasyMock.createStrictMock(AtomicComponent.class);
        wrapper = EasyMock.createStrictMock(InstanceWrapper.class);

        scopeContainer = new CompositeScopeContainer(null);
        scopeContainer.start();
        scopeContainer.register(component);
    }
}
