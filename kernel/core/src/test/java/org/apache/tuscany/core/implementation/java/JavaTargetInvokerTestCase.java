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
package org.apache.tuscany.core.implementation.java;

import java.lang.reflect.Method;

import org.apache.tuscany.spi.component.ScopeContainer;

import junit.framework.Assert;
import junit.framework.TestCase;
import org.apache.tuscany.core.component.scope.ModuleScopeContainer;
import org.apache.tuscany.core.implementation.java.mock.MockFactory;

public class JavaTargetInvokerTestCase extends TestCase {

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
        ScopeContainer scope = new ModuleScopeContainer(null);
        scope.start();
        JavaAtomicComponent component =
            MockFactory.createJavaComponent("foo", scope, Echo.class);
        scope.register(component);
        JavaTargetInvoker invoker = new JavaTargetInvoker(echoMethod, component, null, null, null);
        invoker.setCacheable(false);
        assertEquals("foo", invoker.invokeTarget("foo", JavaTargetInvoker.NONE));
        scope.stop();
    }

    public static class Echo {
        public String echo(String message) throws Exception {
            return message;
        }

    }

}
