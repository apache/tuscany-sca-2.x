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


import org.apache.tuscany.spi.component.AtomicComponent;
import org.apache.tuscany.spi.component.TargetException;
import org.apache.tuscany.spi.component.InstanceWrapper;
import org.apache.tuscany.spi.component.TargetDestructionException;
import org.apache.tuscany.spi.component.TargetInitializationException;

import junit.framework.TestCase;
import org.apache.tuscany.core.component.event.ComponentStart;
import org.apache.tuscany.core.component.event.ComponentStop;
import org.apache.tuscany.core.mock.component.OrderedInitPojo;
import org.apache.tuscany.core.mock.component.OrderedInitPojoImpl;
import org.apache.tuscany.core.injection.MethodEventInvoker;
import org.apache.tuscany.core.injection.EventInvoker;

import org.easymock.EasyMock;
import org.easymock.IAnswer;

/**
 * Lifecycle unit tests for the composite scope container
 *
 * @version $Rev$ $Date$
 */
public class CompositeScopeInstanceLifecycleTestCase extends TestCase {
    private EventInvoker<OrderedInitPojoImpl> initInvoker;
    private EventInvoker<OrderedInitPojoImpl> destroyInvoker;

    /**
     * Verify init and stop by scope container on an atomic component
     *
     * @throws Exception
     */
    public void testInitDestroy() throws Exception {
        Foo comp = new Foo();
        InstanceWrapper wrapper = createWrapper(comp);

        CompositeScopeContainer scope = new CompositeScopeContainer(null);
        scope.start();
        AtomicComponent component = EasyMock.createMock(AtomicComponent.class);
        EasyMock.expect(component.createInstanceWrapper()).andReturn(wrapper);
        EasyMock.expect(component.getInitLevel()).andReturn(1).atLeastOnce();
        EasyMock.replay(component);

        scope.register(component);
        scope.onEvent(new ComponentStart(this, null));
        assertNotNull(scope.getInstance(component));
        // expire composite
        scope.onEvent(new ComponentStop(this, null));
        scope.stop();
        EasyMock.verify(component);
    }

    /**
     * Verify init and stop by scope container on an atomic component when set to eager initialize
     *
     * @throws Exception
     */
    public void testEagerInitDestroy() throws Exception {
        Foo comp = new Foo();
        InstanceWrapper wrapper = createWrapper(comp);

        CompositeScopeContainer scope = new CompositeScopeContainer(null);
        scope.start();
        AtomicComponent component = EasyMock.createMock(AtomicComponent.class);
        EasyMock.expect(component.createInstanceWrapper()).andReturn(wrapper);
        EasyMock.expect(component.getInitLevel()).andReturn(1).atLeastOnce();
        EasyMock.replay(component);

        scope.register(component);
        scope.onEvent(new ComponentStart(this, null));
        // expire composite
        scope.onEvent(new ComponentStop(this, null));
        scope.stop();
        EasyMock.verify(component);
    }


    public void testDestroyOrder() throws Exception {
        CompositeScopeContainer scope = new CompositeScopeContainer(null);
        scope.start();

        AtomicComponent oneComponent = createComponent(0);
        scope.register(oneComponent);
        AtomicComponent twoComponent = createComponent(0);
        scope.register(twoComponent);
        AtomicComponent threeComponent = createComponent(0);
        scope.register(threeComponent);

        scope.onEvent(new ComponentStart(this, null));
        OrderedInitPojo one = (OrderedInitPojo) scope.getInstance(oneComponent);
        assertNotNull(one);
        assertEquals(1, one.getNumberInstantiated());
        assertEquals(1, one.getInitOrder());

        OrderedInitPojo two = (OrderedInitPojo) scope.getInstance(twoComponent);
        assertNotNull(two);
        assertEquals(2, two.getNumberInstantiated());
        assertEquals(2, two.getInitOrder());

        OrderedInitPojo three = (OrderedInitPojo) scope.getInstance(threeComponent);
        assertNotNull(three);
        assertEquals(3, three.getNumberInstantiated());
        assertEquals(3, three.getInitOrder());

        // expire composite
        scope.onEvent(new ComponentStop(this, null));
        assertEquals(0, one.getNumberInstantiated());
        scope.stop();
        EasyMock.verify(oneComponent);
        EasyMock.verify(twoComponent);
        EasyMock.verify(threeComponent);
    }

    public void testEagerInitDestroyOrder() throws Exception {
        CompositeScopeContainer scope = new CompositeScopeContainer(null);
        scope.start();

        AtomicComponent oneComponent = createComponent(1);
        scope.register(oneComponent);
        AtomicComponent twoComponent = createComponent(1);
        scope.register(twoComponent);
        AtomicComponent threeComponent = createComponent(1);
        scope.register(threeComponent);

        scope.onEvent(new ComponentStart(this, null));
        OrderedInitPojo one = (OrderedInitPojo) scope.getInstance(oneComponent);
        assertNotNull(one);

        OrderedInitPojo two = (OrderedInitPojo) scope.getInstance(twoComponent);
        assertNotNull(two);

        OrderedInitPojo three = (OrderedInitPojo) scope.getInstance(threeComponent);
        assertNotNull(three);

        // expire composite
        scope.onEvent(new ComponentStop(this, null));
        assertEquals(0, one.getNumberInstantiated());
        scope.stop();
        EasyMock.verify(oneComponent);
        EasyMock.verify(twoComponent);
        EasyMock.verify(threeComponent);
    }

    protected void setUp() throws Exception {
        super.setUp();
        initInvoker = new MethodEventInvoker<OrderedInitPojoImpl>(OrderedInitPojoImpl.class.getMethod("init"));
        destroyInvoker = new MethodEventInvoker<OrderedInitPojoImpl>(OrderedInitPojoImpl.class.getMethod("destroy"));

    }

    @SuppressWarnings("unchecked")
    private AtomicComponent createComponent(int init) throws TargetException {
        AtomicComponent component = EasyMock.createMock(AtomicComponent.class);
        EasyMock.expect(component.createInstanceWrapper()).andStubAnswer(new IAnswer() {
            public Object answer() throws Throwable {
                return new ReflectiveInstanceWrapper<OrderedInitPojoImpl>(new OrderedInitPojoImpl(),
                                                                          initInvoker,
                                                                          destroyInvoker);
            }
        });
        EasyMock.expect(component.getInitLevel()).andReturn(init).atLeastOnce();
        EasyMock.replay(component);
        return component;
    }


    private InstanceWrapper createWrapper(Foo comp) throws TargetInitializationException, TargetDestructionException {
        InstanceWrapper wrapper = EasyMock.createMock(InstanceWrapper.class);
        wrapper.start();
        EasyMock.expect(wrapper.isStarted()).andReturn(true);
        EasyMock.expect(wrapper.getInstance()).andReturn(comp);
        wrapper.stop();
        EasyMock.replay(wrapper);
        return wrapper;
    }

    private class Foo {

    }
}
