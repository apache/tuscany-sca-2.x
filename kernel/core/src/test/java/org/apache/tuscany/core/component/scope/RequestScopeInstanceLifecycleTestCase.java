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
import org.apache.tuscany.spi.component.WorkContext;
import org.apache.tuscany.spi.component.InstanceWrapper;

import junit.framework.TestCase;
import org.apache.tuscany.core.component.WorkContextImpl;
import org.apache.tuscany.core.component.event.RequestEnd;
import org.apache.tuscany.core.component.event.RequestStart;
import org.apache.tuscany.core.mock.component.OrderedInitPojo;
import org.apache.tuscany.core.mock.component.OrderedInitPojoImpl;
import org.apache.tuscany.core.injection.MethodEventInvoker;
import org.apache.tuscany.core.injection.EventInvoker;

import org.easymock.EasyMock;
import org.easymock.IAnswer;

/**
 * Lifecycle unit tests for the request scope container
 *
 * @version $Rev$ $Date$
 */
public class RequestScopeInstanceLifecycleTestCase extends TestCase {
    private EventInvoker<OrderedInitPojoImpl> initInvoker;
    private EventInvoker<OrderedInitPojoImpl> destroyInvoker;
    private RequestScopeContainer scope;

    public void testInitDestroy() throws Exception {
        scope.start();

        Foo comp = new Foo();
        InstanceWrapper wrapper = EasyMock.createMock(InstanceWrapper.class);
        wrapper.start();
        EasyMock.expect(wrapper.isStarted()).andReturn(true);
        EasyMock.expect(wrapper.getInstance()).andReturn(comp);
        wrapper.stop();
        EasyMock.replay(wrapper);

        AtomicComponent component = EasyMock.createMock(AtomicComponent.class);
        EasyMock.expect(component.createInstanceWrapper()).andReturn(wrapper);
        EasyMock.replay(component);
        scope.register(component);
        scope.onEvent(new RequestStart(this));
        assertNotNull(scope.getInstance(component));
        // expire
        scope.onEvent(new RequestEnd(this));
        scope.stop();
        scope.stop();
        EasyMock.verify(component);
        EasyMock.verify(wrapper);
    }

    public void testDestroyOrder() throws Exception {
        scope.start();

        AtomicComponent oneComponent = createComponent();
        scope.register(oneComponent);
        AtomicComponent twoComponent = createComponent();
        scope.register(twoComponent);
        AtomicComponent threeComponent = createComponent();
        scope.register(threeComponent);

        scope.onEvent(new RequestStart(this));
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

        scope.onEvent(new RequestEnd(this));
        assertEquals(0, one.getNumberInstantiated());
        scope.stop();
        EasyMock.verify(oneComponent);
        EasyMock.verify(twoComponent);
        EasyMock.verify(threeComponent);
    }

    protected void setUp() throws Exception {
        super.setUp();

        WorkContext ctx = new WorkContextImpl();
        scope = new RequestScopeContainer(ctx, null);
        initInvoker = new MethodEventInvoker<OrderedInitPojoImpl>(OrderedInitPojoImpl.class.getMethod("init"));
        destroyInvoker = new MethodEventInvoker<OrderedInitPojoImpl>(OrderedInitPojoImpl.class.getMethod("destroy"));
    }

    @SuppressWarnings("unchecked")
    private AtomicComponent createComponent() throws TargetException {
        AtomicComponent component = EasyMock.createMock(AtomicComponent.class);
        EasyMock.expect(component.createInstanceWrapper()).andStubAnswer(new IAnswer() {
            public Object answer() throws Throwable {
                return new ReflectiveInstanceWrapper<OrderedInitPojoImpl>(new OrderedInitPojoImpl(),
                                                                          initInvoker,
                                                                          destroyInvoker);
            }
        });
        EasyMock.replay(component);
        return component;
    }

    private class Foo {

    }

}
