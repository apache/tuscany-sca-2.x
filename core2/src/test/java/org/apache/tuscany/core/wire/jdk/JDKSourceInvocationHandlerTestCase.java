package org.apache.tuscany.core.wire.jdk;

import java.lang.reflect.Method;
import java.util.Map;

import junit.framework.TestCase;
import org.apache.tuscany.core.mock.component.SimpleTarget;
import org.apache.tuscany.core.mock.component.SimpleTargetImpl;
import org.apache.tuscany.core.mock.wire.MockHandler;
import org.apache.tuscany.core.mock.wire.MockStaticInvoker;
import org.apache.tuscany.core.mock.wire.MockSyncInterceptor;
import org.apache.tuscany.core.util.MethodHashMap;
import org.apache.tuscany.core.wire.InvokerInterceptor;
import org.apache.tuscany.core.wire.MessageChannelImpl;
import org.apache.tuscany.core.wire.ReferenceInvocationChainImpl;
import org.apache.tuscany.core.wire.ServiceInvocationChainImpl;
import org.apache.tuscany.spi.wire.ReferenceInvocationChain;
import org.apache.tuscany.spi.wire.ReferenceInvocationHandler;
import org.apache.tuscany.spi.wire.ServiceInvocationChain;

public class JDKSourceInvocationHandlerTestCase extends TestCase {

    private Method hello;

    public JDKSourceInvocationHandlerTestCase() {
        super();
    }

    public JDKSourceInvocationHandlerTestCase(String arg0) {
        super(arg0);
    }

    public void setUp() throws Exception {
        hello = SimpleTarget.class.getMethod("hello", String.class);
    }

    public void testBasicInvoke() throws Throwable {
        Map<Method, ReferenceInvocationChain> configs = new MethodHashMap<ReferenceInvocationChain>();
        configs.put(hello, createChain(hello));
        ReferenceInvocationHandler handler = new ReferenceInvocationHandler(configs);
        assertEquals("foo", handler.invoke(null, hello, new Object[]{"foo"}));
    }

    public void testErrorInvoke() throws Throwable {
        Map<Method, ReferenceInvocationChain> configs = new MethodHashMap<ReferenceInvocationChain>();
        configs.put(hello, createChain(hello));
        ReferenceInvocationHandler handler = new ReferenceInvocationHandler(configs);
        try {
            assertEquals("foo", handler.invoke(null, hello, new Object[]{}));
            fail("Expected " + IllegalArgumentException.class.getName());
        } catch (IllegalArgumentException e) {
            // should throw
        }
    }

    public void testDirectErrorInvoke() throws Throwable {
        ReferenceInvocationChainImpl source = new ReferenceInvocationChainImpl(hello);
        MockStaticInvoker invoker = new MockStaticInvoker(hello, new SimpleTargetImpl());
        source.setTargetInvoker(invoker);

        Map<Method, ReferenceInvocationChain> configs = new MethodHashMap<ReferenceInvocationChain>();
        configs.put(hello, source);
        ReferenceInvocationHandler handler = new ReferenceInvocationHandler(configs);
        try {
            assertEquals("foo", handler.invoke(null, hello, new Object[]{}));
            fail("Expected " + IllegalArgumentException.class.getName());
        } catch (IllegalArgumentException e) {
            // should throw
        }
    }

    public void testDirectInvoke() throws Throwable {
        ReferenceInvocationChainImpl source = new ReferenceInvocationChainImpl(hello);
        MockStaticInvoker invoker = new MockStaticInvoker(hello, new SimpleTargetImpl());
        source.setTargetInvoker(invoker);

        Map<Method, ReferenceInvocationChain> configs = new MethodHashMap<ReferenceInvocationChain>();
        configs.put(hello, source);
        ReferenceInvocationHandler handler = new ReferenceInvocationHandler(configs);
        assertEquals("foo", handler.invoke(null, hello, new Object[]{"foo"}));
    }

    private ReferenceInvocationChain createChain(Method m) {
        ReferenceInvocationChain source = new ReferenceInvocationChainImpl(m);
        MockHandler sourceRequestHandler = new MockHandler();
        MockHandler sourceResponseHandler = new MockHandler();
        MockSyncInterceptor sourceInterceptor = new MockSyncInterceptor();
        source.addRequestHandler(sourceRequestHandler);
        source.addResponseHandler(sourceResponseHandler);
        source.addInterceptor(sourceInterceptor);

        ServiceInvocationChain target = new ServiceInvocationChainImpl(m);
        MockHandler targetRequestHandler = new MockHandler();
        MockHandler targetResponseHandler = new MockHandler();
        MockSyncInterceptor targetInterceptor = new MockSyncInterceptor();
        target.addRequestHandler(targetRequestHandler);
        target.addResponseHandler(targetResponseHandler);
        target.addInterceptor(targetInterceptor);
        target.addInterceptor(new InvokerInterceptor());

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
