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
import org.apache.tuscany.spi.event.RuntimeEventListener;
import org.apache.tuscany.spi.model.Scope;

import junit.framework.TestCase;
import org.apache.tuscany.core.component.WorkContextImpl;
import org.apache.tuscany.core.component.event.HttpSessionEnd;
import org.apache.tuscany.core.component.event.HttpSessionStart;
import org.apache.tuscany.core.mock.component.OrderedInitPojo;
import org.apache.tuscany.core.mock.component.OrderedInitPojoImpl;
import org.easymock.EasyMock;
import org.easymock.IAnswer;

/**
 * Lifecycle unit tests for the module scope container
 *
 * @version $Rev$ $Date$
 */
public class HttpSessionScopeInstanceLifecycleTestCase extends TestCase {

    public void testInitDestroy() throws Exception {
        WorkContext ctx = new WorkContextImpl();
        HttpSessionScopeContainer scope = new HttpSessionScopeContainer(ctx, null);
        scope.start();
        Foo comp = new Foo();
        SystemAtomicComponent component = EasyMock.createMock(SystemAtomicComponent.class);
        EasyMock.expect(component.createInstance()).andReturn(comp);
        EasyMock.expect(component.isEagerInit()).andReturn(true).atLeastOnce();
        component.addListener(EasyMock.isA(RuntimeEventListener.class));
        component.init(EasyMock.eq(comp));
        component.destroy(EasyMock.eq(comp));
        EasyMock.replay(component);
        scope.register(component);
        Object session = new Object();
        ctx.setIdentifier(Scope.SESSION, session);
        scope.onEvent(new HttpSessionStart(this, session));
        assertNotNull(scope.getInstance(component));
        // expire
        scope.onEvent(new HttpSessionEnd(this, session));
        scope.stop();
        EasyMock.verify(component);
        scope.stop();
    }

    public void testDestroyOrder() throws Exception {
        WorkContext ctx = new WorkContextImpl();
        HttpSessionScopeContainer scope = new HttpSessionScopeContainer(ctx, null);
        scope.start();

        SystemAtomicComponent oneComponent = createComponent(false);
        scope.register(oneComponent);
        SystemAtomicComponent twoComponent = createComponent(false);
        scope.register(twoComponent);
        SystemAtomicComponent threeComponent = createComponent(false);
        scope.register(threeComponent);

        Object session = new Object();
        ctx.setIdentifier(Scope.SESSION, session);
        scope.onEvent(new HttpSessionStart(this, session));
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

        scope.onEvent(new HttpSessionEnd(this, session));
        assertEquals(0, one.getNumberInstantiated());
        scope.stop();
        EasyMock.verify(oneComponent);
        EasyMock.verify(twoComponent);
        EasyMock.verify(threeComponent);
    }

    public void testEagerInitDestroyOrder() throws Exception {
        WorkContext ctx = new WorkContextImpl();
        HttpSessionScopeContainer scope = new HttpSessionScopeContainer(ctx, null);
        scope.start();

        SystemAtomicComponent oneComponent = createComponent(true);
        scope.register(oneComponent);
        SystemAtomicComponent twoComponent = createComponent(true);
        scope.register(twoComponent);
        SystemAtomicComponent threeComponent = createComponent(true);
        scope.register(threeComponent);

        Object session = new Object();
        ctx.setIdentifier(Scope.SESSION, session);
        scope.onEvent(new HttpSessionStart(this, session));
        scope.onEvent(new HttpSessionEnd(this, session));
        scope.stop();
        EasyMock.verify(oneComponent);
        EasyMock.verify(twoComponent);
        EasyMock.verify(threeComponent);
    }

    @SuppressWarnings("unchecked")
    private SystemAtomicComponent createComponent(boolean init) throws TargetException {
        SystemAtomicComponent component = EasyMock.createMock(SystemAtomicComponent.class);
        EasyMock.expect(component.createInstance()).andStubAnswer(new IAnswer() {
            public Object answer() throws Throwable {
                return new OrderedInitPojoImpl();
            }
        });
        EasyMock.expect(component.isEagerInit()).andReturn(init).atLeastOnce();
        component.addListener(EasyMock.isA(RuntimeEventListener.class));
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
