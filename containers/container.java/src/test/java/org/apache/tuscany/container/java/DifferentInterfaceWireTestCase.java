/**
 * 
 * Copyright 2005 The Apache Software Foundation or its licensors, as applicable.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package org.apache.tuscany.container.java;

import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.tuscany.container.java.mock.MockFactory;
import org.apache.tuscany.container.java.mock.components.OtherTarget;
import org.apache.tuscany.container.java.mock.components.OtherTargetImpl;
import org.apache.tuscany.container.java.mock.components.Source;
import org.apache.tuscany.container.java.mock.components.SourceImpl;
import org.apache.tuscany.container.java.mock.components.Target;
import org.apache.tuscany.spi.component.AtomicComponent;
import org.apache.tuscany.spi.component.ScopeContainer;
import org.apache.tuscany.spi.model.Scope;
import org.jmock.Mock;
import org.jmock.MockObjectTestCase;
import org.jmock.core.Invocation;
import org.jmock.core.Stub;


/**
 * Tests wires that have different interfaces on the source and target side
 *
 * @version $Rev$ $Date$
 */
public class DifferentInterfaceWireTestCase extends MockObjectTestCase {

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
    }

    protected void setUp() throws Exception {
        super.setUp();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    private ScopeContainer createMock() {
        Mock mock = mock(ScopeContainer.class);
        mock.expects(once()).method("start");
        mock.expects(once()).method("stop");
        mock.expects(atLeastOnce()).method("register");
        mock.expects(atLeastOnce()).method("getScope").will(returnValue(Scope.MODULE));
        mock.expects(atLeastOnce()).method("getInstance").will(new Stub() {
            private Map<AtomicComponent, Object> cache = new HashMap<AtomicComponent, Object>();

            public Object invoke(Invocation invocation) throws Throwable {
                AtomicComponent component = (AtomicComponent) invocation.parameterValues.get(0);
                Object instance = cache.get(component);
                if (instance == null) {
                    instance = component.createInstance();
                    cache.put(component, instance);
                }
                return instance;
            }

            public StringBuffer describeTo(StringBuffer stringBuffer) {
                return null;
            }
        });
        return (ScopeContainer) mock.proxy();
    }
}
