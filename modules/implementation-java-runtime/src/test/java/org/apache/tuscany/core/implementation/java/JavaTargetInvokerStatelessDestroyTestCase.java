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

import junit.framework.TestCase;

import org.apache.tuscany.spi.Scope;
import org.apache.tuscany.spi.component.InstanceWrapper;
import org.apache.tuscany.spi.component.ScopeContainer;
import org.apache.tuscany.spi.component.WorkContext;
import org.easymock.EasyMock;

public class JavaTargetInvokerStatelessDestroyTestCase extends TestCase {
    private JavaAtomicComponent component;
    private ScopeContainer scopeContainer;
    private InstanceWrapper wrapper;
    private Method echoMethod;
    private WorkContext workContext;

    public void testDestroy() throws Exception {
        EasyMock.expect(scopeContainer.getWrapper(component, null)).andReturn(wrapper);
        EasyMock.expect(wrapper.getInstance()).andReturn(new Echo());
        scopeContainer.returnWrapper(component, wrapper, null);
        EasyMock.replay(component);
        EasyMock.replay(scopeContainer);
        EasyMock.replay(wrapper);
        JavaTargetInvoker invoker = new JavaTargetInvoker(echoMethod, component, scopeContainer);
        invoker.setCacheable(false);
        assertEquals("foo", invoker.invokeTarget("foo", JavaTargetInvoker.NONE, workContext));
        EasyMock.verify(component);
        EasyMock.verify(scopeContainer);
        EasyMock.verify(wrapper);
    }

    protected void setUp() throws Exception {
        super.setUp();
        echoMethod = Echo.class.getDeclaredMethod("echo", String.class);
        component = EasyMock.createMock(JavaAtomicComponent.class);
        scopeContainer = EasyMock.createNiceMock(ScopeContainer.class);
        EasyMock.expect(scopeContainer.getScope()).andStubReturn(Scope.STATELESS);
        wrapper = EasyMock.createNiceMock(InstanceWrapper.class);
        workContext = EasyMock.createMock(WorkContext.class);
        EasyMock.expect(workContext.getIdentifier(Scope.STATELESS)).andStubReturn(null);
        EasyMock.replay(workContext);
    }

    public static class Echo {
        public String echo(String message) throws Exception {
            return message;
        }

    }

}
