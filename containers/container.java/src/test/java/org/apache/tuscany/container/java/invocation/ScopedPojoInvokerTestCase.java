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

import org.apache.tuscany.container.java.invocation.mock.SimpleTarget;
import org.apache.tuscany.container.java.mock.MockScopeContext;
import org.apache.tuscany.core.context.QualifiedName;
import org.apache.tuscany.core.context.ScopeContext;

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
        ScopeContext container = new MockScopeContext();
        ScopedJavaComponentInvoker invoker = new ScopedJavaComponentInvoker(new QualifiedName("foo"), echoMethod, container);
        Object ret = invoker.invokeTarget("foo");
        Assert.assertEquals("foo", ret);
    }

}
