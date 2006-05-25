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

import junit.framework.TestCase;
import org.apache.tuscany.container.java.mock.MockContextFactory;
import org.apache.tuscany.container.java.mock.components.OtherTarget;
import org.apache.tuscany.container.java.mock.components.OtherTargetImpl;
import org.apache.tuscany.container.java.mock.components.Source;
import org.apache.tuscany.container.java.mock.components.SourceImpl;
import org.apache.tuscany.container.java.mock.components.Target;
import org.apache.tuscany.core.context.scope.ModuleScopeContext;
import org.apache.tuscany.spi.context.AtomicContext;
import org.apache.tuscany.spi.context.ScopeContext;


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
        ScopeContext scope = new ModuleScopeContext(null);
        scope.start();
        Map<String, AtomicContext> contexts = MockContextFactory.createWiredContexts("source", SourceImpl.class, Target.class, scope,
                members, "target", OtherTarget.class, OtherTargetImpl.class, scope);
        AtomicContext sourceContext = contexts.get("source");
        Source source = (Source) sourceContext.getService();
        Target target = source.getTarget();
        assertTrue(Proxy.isProxyClass(target.getClass()));
        assertNotNull(target);
        scope.stop();
    }

    public void testDifferentInterfaceMultiplicityInjection() throws Exception {
        Map<String, Member> members = new HashMap<String, Member>();
        Method m = SourceImpl.class.getMethod("setTargets", List.class);
        members.put("target", m);
        ScopeContext scope = new ModuleScopeContext(null);
        scope.start();
        Map<String, AtomicContext> contexts = MockContextFactory.createWiredMultiplicity("source", SourceImpl.class, Target.class, scope,
                "target", OtherTarget.class, OtherTargetImpl.class, members, scope);
        AtomicContext sourceContext = contexts.get("source");
        Source source = (Source) sourceContext.getService();
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

}
