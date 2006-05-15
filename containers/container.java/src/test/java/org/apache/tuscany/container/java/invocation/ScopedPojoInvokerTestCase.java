/**
 *
 *  Copyright 2005 The Apache Software Foundation or its licensors, as applicable.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.apache.tuscany.container.java.invocation;

import java.lang.reflect.Method;

import junit.framework.Assert;
import junit.framework.TestCase;
import org.apache.tuscany.container.java.context.JavaAtomicContext;
import org.apache.tuscany.container.java.invocation.mock.SimpleTarget;
import org.apache.tuscany.container.java.invocation.mock.SimpleTargetImpl;
import org.apache.tuscany.container.java.mock.MockContextFactory;
import org.apache.tuscany.core.context.WorkContextImpl;
import org.apache.tuscany.core.context.scope.ModuleScopeContext;
import org.apache.tuscany.spi.context.AtomicContext;
import org.apache.tuscany.spi.context.ScopeContext;
import org.apache.tuscany.spi.context.WorkContext;
import org.apache.tuscany.model.Scope;

public class ScopedPojoInvokerTestCase extends TestCase {

    private Method echoMethod;

    public ScopedPojoInvokerTestCase() {
        super();
    }

    public ScopedPojoInvokerTestCase(String arg0) {
        super(arg0);
    }

    public void setUp() throws Exception {
        echoMethod = SimpleTarget.class.getDeclaredMethod("echo", String.class);
        Assert.assertNotNull(echoMethod);
    }

    public void testScopedInvoke() throws Exception {
        WorkContext ctx = new WorkContextImpl();
        ScopeContext<AtomicContext> scope = new ModuleScopeContext(ctx);
        scope.start();
        JavaAtomicContext context = MockContextFactory.createJavaAtomicContext("foo", SimpleTargetImpl.class, Scope.MODULE);
        scope.register(context);
        context.setScopeContext(scope);
        ScopedJavaComponentInvoker invoker = new ScopedJavaComponentInvoker(echoMethod, context);
        invoker.setCacheable(false);
        Object ret = invoker.invokeTarget("foo");
        assertEquals("foo", ret);
        scope.stop();
    }

}
