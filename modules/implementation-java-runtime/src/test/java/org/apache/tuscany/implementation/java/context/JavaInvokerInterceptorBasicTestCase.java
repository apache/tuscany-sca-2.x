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
package org.apache.tuscany.implementation.java.context;

import java.lang.reflect.Method;

import junit.framework.TestCase;

import org.apache.tuscany.implementation.java.context.JavaInvokerInterceptor;
import org.apache.tuscany.spi.Scope;
import org.apache.tuscany.spi.component.AtomicComponent;
import org.apache.tuscany.spi.component.InstanceWrapper;
import org.apache.tuscany.spi.component.ScopeContainer;
import org.apache.tuscany.spi.component.WorkContext;
import org.apache.tuscany.spi.wire.Message;
import org.easymock.EasyMock;
import org.easymock.IMocksControl;

public class JavaInvokerInterceptorBasicTestCase<CONTEXT> extends TestCase {
    private TestBean bean;
    private CONTEXT contextId;
    private Method echoMethod;
    private Method arrayMethod;
    private Method nullParamMethod;
    private Method primitiveMethod;
    private Method checkedMethod;
    private Method runtimeMethod;

    private IMocksControl control;
    private WorkContext workContext;
    private ScopeContainer<CONTEXT> scopeContainer;
    private InstanceWrapper<TestBean> wrapper;
    private AtomicComponent<TestBean> component;
    private Message message;

    public void testObjectInvoke() throws Throwable {
        JavaInvokerInterceptor<TestBean, CONTEXT> invoker =
            new JavaInvokerInterceptor<TestBean, CONTEXT>(echoMethod, component, scopeContainer);
        String value = "foo";
        mockCall(new Object[]{value});
        message.setBody(value);
        control.replay();
        Message ret = invoker.invoke(message);
        assertSame(ret, message);
        control.verify();
    }

    public void testPrimitiveInvoke() throws Throwable {
        JavaInvokerInterceptor<TestBean, CONTEXT> invoker =
            new JavaInvokerInterceptor<TestBean, CONTEXT>(primitiveMethod, component, scopeContainer);
        Integer value = 1;
        mockCall(new Object[]{value});
        message.setBody(value);
        control.replay();
        Message ret = invoker.invoke(message);
        assertSame(ret, message);
        control.verify();
    }

    public void testArrayInvoke() throws Throwable {
        JavaInvokerInterceptor<TestBean, CONTEXT> invoker =
            new JavaInvokerInterceptor<TestBean, CONTEXT>(arrayMethod, component, scopeContainer);
        String[] value = new String[]{"foo", "bar"};
        mockCall(new Object[]{value});
        message.setBody(value);
        control.replay();
        Message ret = invoker.invoke(message);
        assertSame(ret, message);
        control.verify();
    }

    public void testEmptyInvoke() throws Throwable {
        JavaInvokerInterceptor<TestBean, CONTEXT> invoker =
            new JavaInvokerInterceptor<TestBean, CONTEXT>(nullParamMethod, component, scopeContainer);
        mockCall(new Object[]{});
        message.setBody("foo");
        control.replay();
        Message ret = invoker.invoke(message);
        assertSame(ret, message);
        control.verify();
    }

    public void testNullInvoke() throws Throwable {
        JavaInvokerInterceptor<TestBean, CONTEXT> invoker =
            new JavaInvokerInterceptor<TestBean, CONTEXT>(nullParamMethod, component, scopeContainer);
        mockCall(null);
        message.setBody("foo");
        control.replay();
        Message ret = invoker.invoke(message);
        assertSame(ret, message);
        control.verify();
    }

    public void testInvokeCheckedException() throws Throwable {
        JavaInvokerInterceptor<TestBean, CONTEXT> invoker =
            new JavaInvokerInterceptor<TestBean, CONTEXT>(checkedMethod, component, scopeContainer);
        mockCall(null);
        message.setBodyWithFault(EasyMock.isA(TestException.class));
        control.replay();
        Message ret = invoker.invoke(message);
        assertSame(ret, message);
        control.verify();
    }

    public void testInvokeRuntimeException() throws Throwable {
        JavaInvokerInterceptor<TestBean, CONTEXT> invoker =
            new JavaInvokerInterceptor<TestBean, CONTEXT>(runtimeMethod, component, scopeContainer);
        mockCall(null);
        message.setBodyWithFault(EasyMock.isA(TestRuntimeException.class));
        control.replay();
        Message ret = invoker.invoke(message);
        assertSame(ret, message);
        control.verify();
    }

    public void testSequenceStart() throws Throwable {
        JavaInvokerInterceptor<TestBean, CONTEXT> invoker =
            new JavaInvokerInterceptor<TestBean, CONTEXT>(nullParamMethod, component, scopeContainer);
        mockCall(null, JavaInvokerInterceptor.START);
        message.setBody("foo");
        control.replay();
        Message ret = invoker.invoke(message);
        assertSame(ret, message);
        control.verify();
    }

    public void testSequenceContinue() throws Throwable {
        JavaInvokerInterceptor<TestBean, CONTEXT> invoker =
            new JavaInvokerInterceptor<TestBean, CONTEXT>(nullParamMethod, component, scopeContainer);
        mockCall(null, JavaInvokerInterceptor.CONTINUE);
        message.setBody("foo");
        control.replay();
        Message ret = invoker.invoke(message);
        assertSame(ret, message);
        control.verify();
    }

    public void testSequenceEnd() throws Throwable {
        JavaInvokerInterceptor<TestBean, CONTEXT> invoker =
            new JavaInvokerInterceptor<TestBean, CONTEXT>(nullParamMethod, component, scopeContainer);
        mockCall(null, JavaInvokerInterceptor.END);
        message.setBody("foo");
        control.replay();
        Message ret = invoker.invoke(message);
        assertSame(ret, message);
        control.verify();
    }

    private void mockCall(Object value) throws Exception {
        mockCall(value, JavaInvokerInterceptor.NONE);
    }

    private void mockCall(Object value, short sequence) throws Exception {
        EasyMock.expect(message.getBody()).andReturn(value);
        EasyMock.expect(message.getConversationSequence()).andReturn(sequence);
        EasyMock.expect(message.getWorkContext()).andReturn(workContext);
        EasyMock.expect(scopeContainer.getScope()).andReturn(Scope.COMPOSITE);
        EasyMock.expect(workContext.getIdentifier(Scope.COMPOSITE)).andReturn(contextId);
        if (sequence == JavaInvokerInterceptor.START || sequence == JavaInvokerInterceptor.NONE) {
            EasyMock.expect(scopeContainer.getWrapper(component, contextId)).andReturn(wrapper);
        } else if (sequence == JavaInvokerInterceptor.CONTINUE || sequence == JavaInvokerInterceptor.END) {
            EasyMock.expect(scopeContainer.getAssociatedWrapper(component, contextId)).andReturn(wrapper);
        } else {
            fail();
        }
        EasyMock.expect(wrapper.getInstance()).andReturn(bean);
        scopeContainer.returnWrapper(component, wrapper, contextId);
        if (sequence == JavaInvokerInterceptor.END) {
            scopeContainer.remove(component);
        }
    }

    @SuppressWarnings("unchecked")
    public void setUp() throws Exception {
        bean = new TestBean();
        contextId = (CONTEXT) new Object();
        echoMethod = TestBean.class.getDeclaredMethod("echo", String.class);
        arrayMethod = TestBean.class.getDeclaredMethod("arrayEcho", String[].class);
        nullParamMethod = TestBean.class.getDeclaredMethod("nullParam");
        primitiveMethod = TestBean.class.getDeclaredMethod("primitiveEcho", Integer.TYPE);
        checkedMethod = TestBean.class.getDeclaredMethod("checkedException");
        runtimeMethod = TestBean.class.getDeclaredMethod("runtimeException");
        assertNotNull(echoMethod);
        assertNotNull(checkedMethod);
        assertNotNull(runtimeMethod);

        control = EasyMock.createStrictControl();
        workContext = control.createMock(WorkContext.class);
        component = control.createMock(AtomicComponent.class);
        scopeContainer = control.createMock(ScopeContainer.class);
        wrapper = control.createMock(InstanceWrapper.class);
        message = control.createMock(Message.class);
    }

    private class TestBean {

        public String echo(String msg) throws Exception {
            assertEquals("foo", msg);
            return msg;
        }

        public String[] arrayEcho(String[] msg) throws Exception {
            assertNotNull(msg);
            assertEquals(2, msg.length);
            assertEquals("foo", msg[0]);
            assertEquals("bar", msg[1]);
            return msg;
        }

        public String nullParam() throws Exception {
            return "foo";
        }

        public int primitiveEcho(int i) throws Exception {
            return i;
        }

        public void checkedException() throws TestException {
            throw new TestException();
        }

        public void runtimeException() throws TestRuntimeException {
            throw new TestRuntimeException();
        }
    }

    public static class TestException extends Exception {
    }

    public static class TestRuntimeException extends RuntimeException {
    }
}
