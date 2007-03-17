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
package org.apache.tuscany.core.implementation;

import java.net.URI;
import java.util.Collections;
import java.util.List;

import junit.framework.TestCase;
import static org.easymock.EasyMock.createNiceMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.reset;
import static org.easymock.EasyMock.verify;

import org.apache.tuscany.core.component.InstanceFactory;
import org.apache.tuscany.core.component.InstanceFactoryProvider;
import org.apache.tuscany.spi.component.InstanceWrapper;
import org.apache.tuscany.spi.component.ScopeContainer;
import org.apache.tuscany.spi.wire.Wire;

/**
 * @version $Rev$ $Date$
 */
public class PojoComponentTestCase<T> extends TestCase {
    private URI componentId;
    private PojoComponent component;
    private InstanceFactoryProvider<T> provider;
    private InstanceFactory<T> instanceFactory;
    private InstanceWrapper<T> wrapper;
    private ScopeContainer scopeContainer;
    private Wire wire;
    private List<Wire> wires;

    public void testConversationAttributes() {
        TestComponent<T> component = new TestComponent<T>(componentId, null, null, 0, 12, 34);
        assertEquals(0, component.getInitLevel());
        assertEquals(false, component.isEagerInit());
        assertEquals(12, component.getMaxIdleTime());
        assertEquals(34, component.getMaxAge());
    }

    public void testEagerInit() {
        TestComponent<T> component = new TestComponent<T>(componentId, null, null, 50, 12, 34);
        assertEquals(50, component.getInitLevel());
        assertEquals(true, component.isEagerInit());
        assertEquals(12, component.getMaxIdleTime());
        assertEquals(34, component.getMaxAge());
    }

    public void testLifecycleAndWrapperCreation() {
        // test start method creates the factory
        expect(provider.createFactory()).andReturn(instanceFactory);
        scopeContainer.register(component, null);
        replay(provider, instanceFactory, wrapper, scopeContainer);
        component.start();
        verify(provider, instanceFactory, wrapper, scopeContainer);

        // test creating an wrapper calls the factory
        // we piggyback this here has the component needs to be started for the factory to be active
        reset(provider, instanceFactory, wrapper, scopeContainer);
        expect(instanceFactory.newInstance()).andReturn(wrapper);
        replay(provider, instanceFactory, wrapper, scopeContainer);
        component.createInstanceWrapper();
        verify(provider, instanceFactory, wrapper, scopeContainer);

        // test stop method
        reset(provider, instanceFactory, wrapper, scopeContainer);
        scopeContainer.unregister(component);
        replay(provider, instanceFactory, wrapper, scopeContainer);
        component.stop();
        verify(provider, instanceFactory, wrapper, scopeContainer);
    }

/*
    public void testAttachSingleReferenceWire() {
        provider.attachWire(wire);
        replay(provider);
        component.attachWire(wire);
        verify(provider);
    }

    public void testAttachMultipleReferenceWire() {
        provider.attachWires(wires);
        replay(provider);
        component.attachWires(wires);
        verify(provider);
    }

    public void testAttachCallbackWire() {
        provider.attachCallbackWire(wire);
        replay(provider);
        component.attachCallbackWire(wire);
        verify(provider);
    }
*/

    @SuppressWarnings("unchecked")
    protected void setUp() throws Exception {
        super.setUp();
        componentId = URI.create("sca://./component");
        provider = createNiceMock(InstanceFactoryProvider.class);
        instanceFactory = createNiceMock(InstanceFactory.class);
        wrapper = createNiceMock(InstanceWrapper.class);
        scopeContainer = createNiceMock(ScopeContainer.class);
        wire = createNiceMock(Wire.class);
        wires = Collections.singletonList(wire);
        component = new TestComponent<T>(componentId, provider, scopeContainer, 0, -1, -1);
    }

    public static class TestComponent<T> extends PojoComponent<T> {

        public TestComponent(URI componentId,
                             InstanceFactoryProvider<T> instanceFactoryProvider,
                             ScopeContainer scopeContainer,
                             int initLevel,
                             long maxIdleTime,
                             long maxAge) {
            super(componentId, instanceFactoryProvider, scopeContainer, initLevel, maxIdleTime, maxAge);
        }
    }
}
