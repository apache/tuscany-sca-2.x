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

import org.apache.tuscany.spi.component.InstanceWrapper;
import org.apache.tuscany.spi.component.ScopeContainer;
import org.apache.tuscany.spi.wire.Message;
import org.apache.tuscany.spi.wire.MessageImpl;

import junit.framework.TestCase;
import org.easymock.classextension.EasyMock;

public class JavaInvokerInterceptorStatelessDestroyTestCase extends TestCase {
    private JavaAtomicComponent component;
    private ScopeContainer scopeContainer;
    private InstanceWrapper wrapper;
    private Method echoMethod;

    @SuppressWarnings({"unchecked"})
    public void testDestroy() throws Exception {
        EasyMock.expect(scopeContainer.getWrapper(component)).andReturn(wrapper);
        EasyMock.expect(wrapper.getInstance()).andReturn(new Echo());
        scopeContainer.returnWrapper(component, wrapper);
        EasyMock.replay(component);
        EasyMock.replay(scopeContainer);
        EasyMock.replay(wrapper);
        JavaInvokerInterceptor invoker = new JavaInvokerInterceptor(echoMethod, component, scopeContainer, null);
        Message message = new MessageImpl();
        message.setBody("foo");
        Message ret = invoker.invoke(message);
        assertEquals("foo", ret.getBody());
        EasyMock.verify(component);
        EasyMock.verify(scopeContainer);
        EasyMock.verify(wrapper);
    }

    protected void setUp() throws Exception {
        super.setUp();
        echoMethod = Echo.class.getDeclaredMethod("echo", String.class);
        component = EasyMock.createMock(JavaAtomicComponent.class);
        scopeContainer = EasyMock.createNiceMock(ScopeContainer.class);
        wrapper = EasyMock.createNiceMock(InstanceWrapper.class);
    }

    public static class Echo {
        public String echo(String message) throws Exception {
            return message;
        }

    }

}
