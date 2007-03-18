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

import org.apache.tuscany.spi.component.InstanceWrapper;
import org.apache.tuscany.spi.component.ScopeContainer;
import org.apache.tuscany.spi.component.WorkContext;
import org.apache.tuscany.spi.model.Scope;
import static org.apache.tuscany.spi.wire.TargetInvoker.NONE;
import org.apache.tuscany.spi.wire.Wire;

import junit.framework.TestCase;
import org.easymock.classextension.EasyMock;

public class JavaTargetInvokerBasicInvocationTestCase extends TestCase {
    private Method echoMethod;
    private Method arrayMethod;
    private Method nullParamMethod;
    private Method primitiveMethod;
    private Method checkedMethod;
    private Method runtimeMethod;
    private Wire wire;
    private WorkContext context;
    private ScopeContainer scopeContainer;
    private InstanceWrapper wrapper;
    private TestBean bean;
    private JavaAtomicComponent component;

    public JavaTargetInvokerBasicInvocationTestCase(String arg0) {
        super(arg0);
    }

    public void setUp() throws Exception {
        echoMethod = TestBean.class.getDeclaredMethod("echo", String.class);
        arrayMethod = TestBean.class.getDeclaredMethod("arrayEcho", String[].class);
        nullParamMethod = TestBean.class.getDeclaredMethod("nullParam", (Class[]) null);
        primitiveMethod = TestBean.class.getDeclaredMethod("primitiveEcho", Integer.TYPE);
        checkedMethod = TestBean.class.getDeclaredMethod("checkedException", (Class[]) null);
        runtimeMethod = TestBean.class.getDeclaredMethod("runtimeException", (Class[]) null);
        assertNotNull(echoMethod);
        assertNotNull(checkedMethod);
        assertNotNull(runtimeMethod);

        wire = EasyMock.createNiceMock(Wire.class);
        context = EasyMock.createNiceMock(WorkContext.class);
        component = EasyMock.createMock(JavaAtomicComponent.class);
        scopeContainer = EasyMock.createNiceMock(ScopeContainer.class);
        wrapper = EasyMock.createNiceMock(InstanceWrapper.class);
        bean = new TestBean();
        EasyMock.replay(component);
        EasyMock.expect(scopeContainer.getScope()).andReturn(Scope.COMPOSITE);
        EasyMock.expect(scopeContainer.getWrapper(component, null)).andReturn(wrapper);
        EasyMock.replay(scopeContainer);
        EasyMock.expect(wrapper.getInstance()).andReturn(bean);
        EasyMock.replay(wrapper);
    }


    protected void tearDown() throws Exception {
        EasyMock.verify(component);
        EasyMock.verify(scopeContainer);
        EasyMock.verify(wrapper);
        super.tearDown();
    }

    public void testObjectInvoke() throws Throwable {
        JavaTargetInvoker invoker = new JavaTargetInvoker(echoMethod, component, scopeContainer);
        Object ret = invoker.invokeTarget("foo", NONE, context);
        assertEquals("foo", ret);
    }

    public void testArrayInvoke() throws Throwable {
        JavaTargetInvoker invoker = new JavaTargetInvoker(arrayMethod, component, scopeContainer);
        String[] args = new String[]{"foo", "bar"};
        Object ret = invoker.invokeTarget(new Object[]{args}, NONE, context);
        String[] retA = (String[]) ret;
        assertNotNull(retA);
        assertEquals(2, retA.length);
        assertEquals("foo", retA[0]);
        assertEquals("bar", retA[1]);
    }

    public void testNullInvoke() throws Throwable {
        JavaTargetInvoker invoker = new JavaTargetInvoker(nullParamMethod, component, scopeContainer);
        Object ret = invoker.invokeTarget(null, NONE, context);
        String retS = (String) ret;
        assertEquals("foo", retS);
    }

    public void testPrimitiveInvoke() throws Throwable {
        JavaTargetInvoker invoker = new JavaTargetInvoker(primitiveMethod, component, scopeContainer);
        Object ret = invoker.invokeTarget(new Integer[]{1}, NONE, context);
        Integer retI = (Integer) ret;
        assertEquals(1, retI.intValue());
    }

    public void testInvokeCheckedException() throws Throwable {
        JavaTargetInvoker invoker = new JavaTargetInvoker(checkedMethod, component, scopeContainer);
        try {
            invoker.invokeTarget(null, NONE, context);
            fail(TestException.class.getName() + " should have been thrown");
        } catch (InvocationTargetException e) {
            assertTrue(e.getCause() instanceof TestException);
        }
    }

    public void testInvokeRuntimeException() throws Throwable {
        JavaTargetInvoker invoker = new JavaTargetInvoker(runtimeMethod, component, scopeContainer);
        try {
            invoker.invokeTarget(null, NONE, context);
            fail(TestException.class.getName() + " should have been thrown");
        } catch (InvocationTargetException e) {
            assertTrue(e.getCause() instanceof TestRuntimeException);
        }
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
}
