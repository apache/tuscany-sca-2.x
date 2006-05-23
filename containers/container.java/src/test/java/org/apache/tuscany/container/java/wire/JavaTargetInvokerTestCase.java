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
package org.apache.tuscany.container.java.wire;

import java.lang.reflect.Method;

import junit.framework.Assert;
import org.apache.tuscany.container.java.context.JavaAtomicContext;
import org.apache.tuscany.container.java.mock.MockContextFactory;
import org.apache.tuscany.core.context.scope.ModuleScopeContext;
import org.apache.tuscany.spi.context.ScopeContext;
import org.apache.tuscany.spi.model.Scope;
import org.jmock.MockObjectTestCase;

public class JavaTargetInvokerTestCase extends MockObjectTestCase {

    private Method echoMethod;

    public JavaTargetInvokerTestCase() {
        super();
    }

    public JavaTargetInvokerTestCase(String arg0) {
        super(arg0);
    }

    public void setUp() throws Exception {
        echoMethod = Echo.class.getDeclaredMethod("echo", String.class);
        Assert.assertNotNull(echoMethod);
    }

    public void testScopedInvoke() throws Exception {
        ScopeContext scope = new ModuleScopeContext(null);
        scope.start();
        JavaAtomicContext context = MockContextFactory.createJavaAtomicContext("foo", Echo.class, Scope.MODULE);
        scope.register(context);
        context.setScopeContext(scope);
        JavaTargetInvoker invoker = new JavaTargetInvoker(echoMethod, context);
        invoker.setCacheable(false);
        assertEquals("foo", invoker.invokeTarget("foo"));
        scope.stop();
    }

    public static class Echo {
        public String echo(String message) throws Exception {
            return message;
        }

    }

}
