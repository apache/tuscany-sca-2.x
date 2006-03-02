package org.apache.tuscany.core.invocation.jdk;

import java.lang.reflect.Method;
import java.util.Map;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.apache.tuscany.core.invocation.InvocationConfiguration;
import org.apache.tuscany.core.invocation.MethodHashMap;
import org.apache.tuscany.core.invocation.impl.InvokerInterceptor;
import org.apache.tuscany.core.invocation.impl.MessageChannelImpl;
import org.apache.tuscany.core.invocation.mock.MockHandler;
import org.apache.tuscany.core.invocation.mock.MockStaticInvoker;
import org.apache.tuscany.core.invocation.mock.MockSyncInterceptor;
import org.apache.tuscany.core.invocation.mock.SimpleTarget;
import org.apache.tuscany.core.invocation.mock.SimpleTargetImpl;
import org.apache.tuscany.core.message.impl.MessageFactoryImpl;

public class JDKInvocationHandlerTestCase extends TestCase {

    private Method hello;

    private Method goodbye;

    public JDKInvocationHandlerTestCase() {
        super();
    }

    public JDKInvocationHandlerTestCase(String arg0) {
        super(arg0);
    }

    public void setUp() throws Exception {
        hello = SimpleTarget.class.getMethod("hello", new Class[] { String.class });
        goodbye = SimpleTarget.class.getMethod("goodbye", new Class[] { String.class });
    }

    public void testBasicInvoke() throws Throwable {
        Map<Method, InvocationConfiguration> configs = new MethodHashMap();
        configs.put(hello, getInvocationHandler(hello));
        JDKInvocationHandler handler = new JDKInvocationHandler(new MessageFactoryImpl(), configs);
        Assert.assertEquals("foo", handler.invoke(null, hello, new Object[] { "foo" }));
    }

    public void testErrorInvoke() throws Throwable {
        Map<Method, InvocationConfiguration> configs = new MethodHashMap();
        configs.put(hello, getInvocationHandler(hello));
        JDKInvocationHandler handler = new JDKInvocationHandler(new MessageFactoryImpl(), configs);
        try {
            Assert.assertEquals("foo", handler.invoke(null, hello, new Object[] {}));
            fail("Expected " + IllegalArgumentException.class.getName());
        } catch (IllegalArgumentException e) {
            // should throw
        }
    }

    public void testDirectErrorInvoke() throws Throwable {
        InvocationConfiguration source = new InvocationConfiguration(hello);
        MockStaticInvoker invoker = new MockStaticInvoker(hello, new SimpleTargetImpl());
        source.setTargetInvoker(invoker);

        Map<Method, InvocationConfiguration> configs = new MethodHashMap();
        configs.put(hello, source);
        JDKInvocationHandler handler = new JDKInvocationHandler(new MessageFactoryImpl(), configs);
        try {
            Assert.assertEquals("foo", handler.invoke(null, hello, new Object[] {}));
            fail("Expected " + IllegalArgumentException.class.getName());
        } catch (IllegalArgumentException e) {
            // should throw
        }
    }

    public void testDirectInvoke() throws Throwable {
        InvocationConfiguration source = new InvocationConfiguration(hello);
        MockStaticInvoker invoker = new MockStaticInvoker(hello, new SimpleTargetImpl());
        source.setTargetInvoker(invoker);

        Map<Method, InvocationConfiguration> configs = new MethodHashMap();
        configs.put(hello, source);
        JDKInvocationHandler handler = new JDKInvocationHandler(new MessageFactoryImpl(), configs);
        Assert.assertEquals("foo", handler.invoke(null, hello, new Object[] { "foo" }));
    }

    private InvocationConfiguration getInvocationHandler(Method m) {
        InvocationConfiguration source = new InvocationConfiguration(m);
        MockHandler sourceRequestHandler = new MockHandler();
        MockHandler sourceResponseHandler = new MockHandler();
        MockSyncInterceptor sourceInterceptor = new MockSyncInterceptor();
        source.addRequestHandler(sourceRequestHandler);
        source.addResponseHandler(sourceResponseHandler);
        source.addSourceInterceptor(sourceInterceptor);

        InvocationConfiguration target = new InvocationConfiguration(m);
        MockHandler targetRequestHandler = new MockHandler();
        MockHandler targetResponseHandler = new MockHandler();
        MockSyncInterceptor targetInterceptor = new MockSyncInterceptor();
        target.addRequestHandler(targetRequestHandler);
        target.addResponseHandler(targetResponseHandler);
        target.addTargetInterceptor(targetInterceptor);
        target.addTargetInterceptor(new InvokerInterceptor());

        // connect the source to the target
        source.setTargetRequestChannel(new MessageChannelImpl(target.getRequestHandlers()));
        source.setTargetResponseChannel(new MessageChannelImpl(target.getResponseHandlers()));
        source.build();
        target.build();
        MockStaticInvoker invoker = new MockStaticInvoker(m, new SimpleTargetImpl());
        source.setTargetInvoker(invoker);
        return source;
    }
}
