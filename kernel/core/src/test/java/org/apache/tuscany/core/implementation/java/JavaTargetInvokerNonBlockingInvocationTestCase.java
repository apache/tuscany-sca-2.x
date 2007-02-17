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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.apache.tuscany.spi.component.WorkContext;
import org.apache.tuscany.spi.extension.ExecutionMonitor;
import org.apache.tuscany.spi.model.Scope;
import org.apache.tuscany.spi.wire.Message;
import org.apache.tuscany.spi.wire.MessageImpl;
import static org.apache.tuscany.spi.wire.TargetInvoker.NONE;

import junit.framework.TestCase;
import org.easymock.classextension.EasyMock;

/**
 * Verifies dispatching invocations to a Java implementation instance
 *
 * @version $Rev$ $Date$
 */
public class JavaTargetInvokerNonBlockingInvocationTestCase extends TestCase {

    private Method echoMethod;
    private Method arrayMethod;
    private Method nullParamMethod;
    private Method primitiveMethod;
    private Method checkedMethod;
    private Method runtimeMethod;
    private WorkContext context;
    private ExecutionMonitor monitor;

    public JavaTargetInvokerNonBlockingInvocationTestCase(String arg0) {
        super(arg0);
    }

    public void testInvoke() throws Exception {
        AsyncTarget target = EasyMock.createMock(AsyncTarget.class);
        target.invoke();
        EasyMock.expectLastCall().once();
        EasyMock.replay(target);
        JavaAtomicComponent component = EasyMock.createMock(JavaAtomicComponent.class);
        EasyMock.expect(component.getTargetInstance()).andReturn(target);
        EasyMock.expect(component.getScope()).andReturn(Scope.COMPOSITE);
        EasyMock.replay(component);
        ExecutionMonitor monitor = EasyMock.createMock(ExecutionMonitor.class);
        EasyMock.replay(monitor);

        Message msg = new MessageImpl();
        Object id = new Object();
        msg.setMessageId(id);

        WorkContext context = EasyMock.createMock(WorkContext.class);
        context.setCurrentCorrelationId(id);
        EasyMock.replay(context);
        Method method = AsyncTarget.class.getMethod("invoke");
        method.setAccessible(true);
        JavaTargetInvoker invoker = new JavaTargetInvoker(method, component, context, monitor);
        invoker.invoke(msg);
        EasyMock.verify(target);
        EasyMock.verify(component);
    }

    public void setUp() throws Exception {
        echoMethod = TestBean.class.getDeclaredMethod("echo", String.class);
        arrayMethod = TestBean.class.getDeclaredMethod("arrayEcho", String[].class);
        nullParamMethod = TestBean.class.getDeclaredMethod("nullParam", (Class[]) null);
        primitiveMethod = TestBean.class.getDeclaredMethod("primitiveEcho", Integer.TYPE);
        checkedMethod = TestBean.class.getDeclaredMethod("checkedException", (Class[]) null);
        runtimeMethod = TestBean.class.getDeclaredMethod("runtimeException", (Class[]) null);
        context = EasyMock.createNiceMock(WorkContext.class);
        monitor = EasyMock.createNiceMock(ExecutionMonitor.class);
        assertNotNull(echoMethod);
        assertNotNull(checkedMethod);
        assertNotNull(runtimeMethod);
    }

    public void testObjectInvoke() throws Throwable {
        TestBean bean = new TestBean();
        JavaAtomicComponent component = EasyMock.createMock(JavaAtomicComponent.class);
        EasyMock.expect(component.getTargetInstance()).andReturn(bean);
        EasyMock.expect(component.getScope()).andReturn(Scope.COMPOSITE);
        EasyMock.replay(component);
        JavaTargetInvoker invoker = new JavaTargetInvoker(echoMethod, component, context, monitor);
        Object ret = invoker.invokeTarget("foo", NONE);
        assertEquals("foo", ret);
    }

    public void testArrayInvoke() throws Throwable {
        TestBean bean = new TestBean();
        JavaAtomicComponent component = EasyMock.createMock(JavaAtomicComponent.class);
        EasyMock.expect(component.getTargetInstance()).andReturn(bean);
        EasyMock.expect(component.getScope()).andReturn(Scope.COMPOSITE);
        EasyMock.replay(component);
        JavaTargetInvoker invoker = new JavaTargetInvoker(arrayMethod, component, context, monitor);

        String[] args = new String[]{"foo", "bar"};
        Object ret = invoker.invokeTarget(new Object[]{args}, NONE);
        String[] retA = (String[]) ret;
        assertNotNull(retA);
        assertEquals(2, retA.length);
        assertEquals("foo", retA[0]);
        assertEquals("bar", retA[1]);
    }

    public void testNullInvoke() throws Throwable {
        TestBean bean = new TestBean();
        JavaAtomicComponent component = EasyMock.createMock(JavaAtomicComponent.class);
        EasyMock.expect(component.getTargetInstance()).andReturn(bean);
        EasyMock.expect(component.getScope()).andReturn(Scope.COMPOSITE);
        EasyMock.replay(component);
        JavaTargetInvoker invoker = new JavaTargetInvoker(nullParamMethod, component, context, monitor);
        Object ret = invoker.invokeTarget(null, NONE);
        String retS = (String) ret;
        assertEquals("foo", retS);
    }

    public void testPrimitiveInvoke() throws Throwable {
        TestBean bean = new TestBean();
        JavaAtomicComponent component = EasyMock.createMock(JavaAtomicComponent.class);
        EasyMock.expect(component.getTargetInstance()).andReturn(bean);
        EasyMock.expect(component.getScope()).andReturn(Scope.COMPOSITE);
        EasyMock.replay(component);
        JavaTargetInvoker invoker = new JavaTargetInvoker(primitiveMethod, component, context, monitor);
        Object ret = invoker.invokeTarget(new Integer[]{1}, NONE);
        Integer retI = (Integer) ret;
        assertEquals(1, retI.intValue());
    }

    public void testInvokeCheckedException() throws Throwable {
        TestBean bean = new TestBean();
        JavaAtomicComponent component = EasyMock.createMock(JavaAtomicComponent.class);
        EasyMock.expect(component.getTargetInstance()).andReturn(bean);
        EasyMock.expect(component.getScope()).andReturn(Scope.COMPOSITE);
        EasyMock.replay(component);
        JavaTargetInvoker invoker = new JavaTargetInvoker(checkedMethod, component, context, monitor);
        try {
            invoker.invokeTarget(null, NONE);
        } catch (InvocationTargetException e) {
            if (e.getCause() != null && TestException.class.equals(e.getCause().getClass())) {
                return;
            }
        } catch (Throwable e) {
            //ok
        }
        fail(TestException.class.getName() + " should have been thrown");
    }

    public void testInvokeRuntimeException() throws Throwable {
        TestBean bean = new TestBean();
        JavaAtomicComponent component = EasyMock.createMock(JavaAtomicComponent.class);
        EasyMock.expect(component.getTargetInstance()).andReturn(bean);
        EasyMock.expect(component.getScope()).andReturn(Scope.COMPOSITE);
        EasyMock.replay(component);
        JavaTargetInvoker invoker = new JavaTargetInvoker(runtimeMethod, component, context, monitor);
        try {
            invoker.invokeTarget(null, NONE);
        } catch (InvocationTargetException e) {
            if (e.getCause() != null && e.getCause() instanceof TestRuntimeException) {
                return;
            }
        }
        fail(TestException.class.getName() + " should have been thrown");
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

    public class TestException extends Exception {
    }

    public class TestRuntimeException extends RuntimeException {
    }

    public interface AsyncTarget {
        void invoke();
    }

}
