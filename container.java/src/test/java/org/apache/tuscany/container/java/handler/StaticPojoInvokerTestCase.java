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
package org.apache.tuscany.container.java.handler;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.apache.tuscany.container.java.handler.StaticJavaComponentTargetInvoker;

import junit.framework.Assert;
import junit.framework.TestCase;

public class StaticPojoInvokerTestCase extends TestCase {

    private Method echoMethod;
    private Method arrayMethod;
    private Method nullParamMethod;
    private Method primitiveMethod;
    private Method checkedMethod;
    private Method runtimeMethod;

    public StaticPojoInvokerTestCase() {

    }

    public StaticPojoInvokerTestCase(String arg0) {
        super(arg0);
    }

    public void setUp() throws Exception {
        echoMethod = TestBean.class.getDeclaredMethod("echo", new Class[]{String.class});
        arrayMethod = TestBean.class.getDeclaredMethod("arrayEcho", new Class[]{String[].class});
        nullParamMethod = TestBean.class.getDeclaredMethod("nullParam", (Class[]) null);
        primitiveMethod = TestBean.class.getDeclaredMethod("primitiveEcho", new Class[]{Integer.TYPE});
        checkedMethod = TestBean.class.getDeclaredMethod("checkedException", (Class[]) null);
        runtimeMethod = TestBean.class.getDeclaredMethod("runtimeException", (Class[]) null);
        Assert.assertNotNull(echoMethod);
        Assert.assertNotNull(checkedMethod);
        Assert.assertNotNull(runtimeMethod);
    }

    public void testObjectInvoke() throws Throwable {
        TestBean bean = new TestBean();
        StaticJavaComponentTargetInvoker invoker = new StaticJavaComponentTargetInvoker(echoMethod, bean);
        Object ret = invoker.invokeTarget("foo");
        Assert.assertEquals("foo", ret);
    }

    public void testArrayInvoke() throws Throwable {
        TestBean bean = new TestBean();
        StaticJavaComponentTargetInvoker invoker = new StaticJavaComponentTargetInvoker(arrayMethod, bean);
        String[] args = new String[]{"foo", "bar"};
        Object ret = invoker.invokeTarget(new Object[]{args});
        String[] retA = (String[]) ret;
        Assert.assertNotNull(retA);
        Assert.assertEquals(2, retA.length);
        Assert.assertEquals("foo", retA[0]);
        Assert.assertEquals("bar", retA[1]);
    }

    public void testNullInvoke() throws Throwable {
        TestBean bean = new TestBean();
        StaticJavaComponentTargetInvoker invoker = new StaticJavaComponentTargetInvoker(nullParamMethod, bean);
        Object ret = invoker.invokeTarget(null);
        String retS = (String) ret;
        Assert.assertEquals("foo", retS);
    }

    public void testPrimitiveInvoke() throws Throwable {
        TestBean bean = new TestBean();
        StaticJavaComponentTargetInvoker invoker = new StaticJavaComponentTargetInvoker(primitiveMethod, bean);
        Object ret = invoker.invokeTarget(new Integer[]{new Integer(1)});
        Integer retI = (Integer) ret;
        Assert.assertEquals(1, retI.intValue());
    }

    public void testInvokeCheckedException() throws Throwable {
        TestBean bean = new TestBean();
        StaticJavaComponentTargetInvoker invoker = new StaticJavaComponentTargetInvoker(checkedMethod, bean);
        try {
            invoker.invokeTarget(null);
        } catch (InvocationTargetException e) {
            if (e.getCause() != null && TestException.class.equals(e.getCause().getClass())) {
                return;
            }
        } catch (Throwable e) {
        }
        fail(TestException.class.getName() + " should have been thrown");
    }

    public void testInvokeRuntimeException() throws Throwable {
        TestBean bean = new TestBean();
        StaticJavaComponentTargetInvoker invoker = new StaticJavaComponentTargetInvoker(runtimeMethod, bean);
        try {
            invoker.invokeTarget(null);
        } catch (InvocationTargetException e) {
            if (e.getCause() != null && e.getCause() instanceof TestRuntimeException) {
                return;
            }
        }
        fail(TestException.class.getName() + " should have been thrown");
    }

    private class TestBean {

        public String echo(String msg) throws Exception {
            Assert.assertEquals("foo", msg);
            return msg;
        }

        public String[] arrayEcho(String[] msg) throws Exception {
            Assert.assertNotNull(msg);
            Assert.assertEquals(2, msg.length);
            Assert.assertEquals("foo", msg[0]);
            Assert.assertEquals("bar", msg[1]);
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
