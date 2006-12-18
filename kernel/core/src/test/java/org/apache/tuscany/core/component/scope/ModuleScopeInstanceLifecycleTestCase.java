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


import org.apache.tuscany.spi.component.SystemAtomicComponent;
import org.apache.tuscany.spi.component.WorkContext;
import org.apache.tuscany.spi.component.TargetException;

import junit.framework.TestCase;
import org.apache.tuscany.core.component.WorkContextImpl;
import org.apache.tuscany.core.component.event.CompositeStart;
import org.apache.tuscany.core.component.event.CompositeStop;
import org.apache.tuscany.core.mock.component.OrderedInitPojo;
import org.apache.tuscany.core.mock.component.OrderedInitPojoImpl;
import org.easymock.EasyMock;
import org.easymock.IAnswer;

/**
 * Lifecycle unit tests for the module scope container
 *
 * @version $Rev$ $Date$
 */
public class ModuleScopeInstanceLifecycleTestCase extends TestCase {

    /**
     * Verify init and stop by scope container on an atomic component
     *
     * @throws Exception
     */
    public void testInitDestroy() throws Exception {
        WorkContext ctx = new WorkContextImpl();
        ModuleScopeContainer scope = new ModuleScopeContainer(null);
        scope.start();
        Foo comp = new Foo();
        SystemAtomicComponent component = EasyMock.createMock(SystemAtomicComponent.class);
        EasyMock.expect(component.createInstance()).andReturn(comp);
        EasyMock.expect(component.getInitLevel()).andReturn(1).atLeastOnce();
        component.init(EasyMock.eq(comp));
        component.destroy(EasyMock.eq(comp));
        EasyMock.replay(component);
        scope.register(component);
        scope.onEvent(new CompositeStart(this, null));
        assertNotNull(scope.getInstance(component));
        // expire module
        scope.onEvent(new CompositeStop(this, null));
        scope.stop();
        EasyMock.verify(component);
    }

    /**
     * Verify init and stop by scope container on an atomic component when set to eager initialize
     *
     * @throws Exception
     */
    public void testEagerInitDestroy() throws Exception {
        WorkContext ctx = new WorkContextImpl();
        ModuleScopeContainer scope = new ModuleScopeContainer(null);
        scope.start();
        Foo comp = new Foo();
        SystemAtomicComponent initDestroyComponent = EasyMock.createMock(SystemAtomicComponent.class);
        EasyMock.expect(initDestroyComponent.createInstance()).andReturn(comp);
        EasyMock.expect(initDestroyComponent.getInitLevel()).andReturn(1).atLeastOnce();
        initDestroyComponent.init(EasyMock.eq(comp));
        initDestroyComponent.destroy(EasyMock.eq(comp));
        EasyMock.replay(initDestroyComponent);
        scope.register(initDestroyComponent);
        scope.onEvent(new CompositeStart(this, null));
        // expire module
        scope.onEvent(new CompositeStop(this, null));
        scope.stop();
        EasyMock.verify(initDestroyComponent);
    }


    public void testDestroyOrder() throws Exception {
        WorkContext ctx = new WorkContextImpl();
        ModuleScopeContainer scope = new ModuleScopeContainer(null);
        scope.start();

        SystemAtomicComponent oneComponent = createComponent(0);
        scope.register(oneComponent);
        SystemAtomicComponent twoComponent = createComponent(0);
        scope.register(twoComponent);
        SystemAtomicComponent threeComponent = createComponent(0);
        scope.register(threeComponent);

        scope.onEvent(new CompositeStart(this, null));
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

        // expire module
        scope.onEvent(new CompositeStop(this, null));
        assertEquals(0, one.getNumberInstantiated());
        scope.stop();
        EasyMock.verify(oneComponent);
        EasyMock.verify(twoComponent);
        EasyMock.verify(threeComponent);
    }

    public void testEagerInitDestroyOrder() throws Exception {
        WorkContext ctx = new WorkContextImpl();
        ModuleScopeContainer scope = new ModuleScopeContainer(null);
        scope.start();

        SystemAtomicComponent oneComponent = createComponent(1);
        scope.register(oneComponent);
        SystemAtomicComponent twoComponent = createComponent(1);
        scope.register(twoComponent);
        SystemAtomicComponent threeComponent = createComponent(1);
        scope.register(threeComponent);

        scope.onEvent(new CompositeStart(this, null));
        OrderedInitPojo one = (OrderedInitPojo) scope.getInstance(oneComponent);
        assertNotNull(one);

        OrderedInitPojo two = (OrderedInitPojo) scope.getInstance(twoComponent);
        assertNotNull(two);

        OrderedInitPojo three = (OrderedInitPojo) scope.getInstance(threeComponent);
        assertNotNull(three);

        // expire module
        scope.onEvent(new CompositeStop(this, null));
        assertEquals(0, one.getNumberInstantiated());
        scope.stop();
        EasyMock.verify(oneComponent);
        EasyMock.verify(twoComponent);
        EasyMock.verify(threeComponent);
    }

    @SuppressWarnings("unchecked")
    private SystemAtomicComponent createComponent(int init) throws TargetException {
        SystemAtomicComponent component = EasyMock.createMock(SystemAtomicComponent.class);
        EasyMock.expect(component.createInstance()).andStubAnswer(new IAnswer() {
            public Object answer() throws Throwable {
                return new OrderedInitPojoImpl();
            }
        });
        EasyMock.expect(component.getInitLevel()).andReturn(init).atLeastOnce();
        component.init(EasyMock.isA(OrderedInitPojoImpl.class));
        EasyMock.expectLastCall().andAnswer(new IAnswer() {
            public Object answer() throws Throwable {
                OrderedInitPojoImpl pojo = (OrderedInitPojoImpl) EasyMock.getCurrentArguments()[0];
                pojo.init();
                return null;
            }
        });
        component.destroy(EasyMock.isA(OrderedInitPojoImpl.class));
        EasyMock.expectLastCall().andAnswer(new IAnswer() {
            public Object answer() throws Throwable {
                OrderedInitPojoImpl pojo = (OrderedInitPojoImpl) EasyMock.getCurrentArguments()[0];
                pojo.destroy();
                return null;
            }
        });
        EasyMock.replay(component);
        return component;
    }

    private class Foo {

    }
}
