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
package org.apache.tuscany.core.integration.wire;

import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.tuscany.spi.component.AtomicComponent;
import org.apache.tuscany.spi.component.ScopeContainer;
import org.apache.tuscany.spi.model.Scope;

import junit.framework.TestCase;
import org.apache.tuscany.core.integration.mock.MockFactory;
import org.apache.tuscany.core.mock.component.OtherTarget;
import org.apache.tuscany.core.mock.component.OtherTargetImpl;
import org.apache.tuscany.core.mock.component.Target;
import org.apache.tuscany.core.mock.component.SourceImpl;
import org.apache.tuscany.core.mock.component.Source;
import org.easymock.EasyMock;
import org.easymock.IAnswer;


/**
 * Tests wires that have different interfaces on the source and target side
 *
 * @version $Rev$ $Date$
 */
public class DifferentInterfaceWireTestCase extends TestCase {

    public void testDifferentInterfaceInjection() throws Exception {
        Map<String, Member> members = new HashMap<String, Member>();
        Method m = SourceImpl.class.getMethod("setTarget", Target.class);
        members.put("target", m);
        ScopeContainer scope = createMock();
        scope.start();
        Map<String, AtomicComponent> contexts =
            MockFactory.createWiredComponents("source",
                SourceImpl.class,
                Target.class,
                scope,
                members,
                "target",
                OtherTarget.class,
                OtherTargetImpl.class,
                scope);
        AtomicComponent sourceComponent = contexts.get("source");
        Source source = (Source) sourceComponent.getServiceInstance();
        Target target = source.getTarget();
        assertTrue(Proxy.isProxyClass(target.getClass()));
        assertNotNull(target);
        scope.stop();
        EasyMock.verify(scope);
    }

    public void testDifferentInterfaceMultiplicityInjection() throws Exception {
        Map<String, Member> members = new HashMap<String, Member>();
        Method m = SourceImpl.class.getMethod("setTargets", List.class);
        members.put("target", m);
        ScopeContainer scope = createMock();
        scope.start();
        Map<String, AtomicComponent> contexts =
            MockFactory.createWiredMultiplicity("source", SourceImpl.class, Target.class, scope,
                "target", OtherTarget.class, OtherTargetImpl.class, members, scope);
        AtomicComponent sourceComponent = contexts.get("source");
        Source source = (Source) sourceComponent.getServiceInstance();
        List<Target> targets = source.getTargets();
        assertEquals(1, targets.size());
        Target target = targets.get(0);
        target.setString("foo");
        assertEquals("foo", target.getString());
        assertTrue(Proxy.isProxyClass(target.getClass()));
        scope.stop();
        EasyMock.verify(scope);
    }

    protected void setUp() throws Exception {
        super.setUp();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    private ScopeContainer createMock() {
        ScopeContainer scope = EasyMock.createMock(ScopeContainer.class);
        scope.start();
        scope.stop();
        scope.register(EasyMock.isA(AtomicComponent.class));
        EasyMock.expectLastCall().atLeastOnce();
        EasyMock.expect(scope.getScope()).andReturn(Scope.MODULE).atLeastOnce();
        scope.getInstance(EasyMock.isA(AtomicComponent.class));
        EasyMock.expectLastCall().andAnswer(new IAnswer<Object>() {
            private Map<AtomicComponent, Object> cache = new HashMap<AtomicComponent, Object>();

            public Object answer() throws Throwable {
                AtomicComponent component = (AtomicComponent) EasyMock.getCurrentArguments()[0];
                Object instance = cache.get(component);
                if (instance == null) {
                    instance = component.createInstance();
                    cache.put(component, instance);
                }
                return instance;
            }
        }).anyTimes();
        EasyMock.replay(scope);
        return scope;
    }
}
