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
import org.apache.tuscany.core.wire.SourceInvocationChainImpl;
import org.apache.tuscany.core.wire.TargetInvocationChainImpl;
import org.apache.tuscany.spi.wire.SourceInvocationChain;
import org.apache.tuscany.spi.wire.TargetInvocationChain;
import org.apache.tuscany.spi.wire.WireInvocationHandler;
import org.apache.tuscany.spi.wire.SourceInvocationHandler;

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
        Map<Method, SourceInvocationChain> configs = new MethodHashMap<SourceInvocationChain>();
        configs.put(hello, createChain(hello));
        WireInvocationHandler handler = new SourceInvocationHandler(configs);
        assertEquals("foo", handler.invoke(null, hello, new Object[]{"foo"}));
    }

    public void testErrorInvoke() throws Throwable {
        Map<Method, SourceInvocationChain> configs = new MethodHashMap<SourceInvocationChain>();
        configs.put(hello, createChain(hello));
        WireInvocationHandler handler = new SourceInvocationHandler(configs);
        try {
            assertEquals("foo", handler.invoke(null, hello, new Object[]{}));
            fail("Expected " + IllegalArgumentException.class.getName());
        } catch (IllegalArgumentException e) {
            // should throw
        }
    }

    public void testDirectErrorInvoke() throws Throwable {
        SourceInvocationChainImpl source = new SourceInvocationChainImpl(hello);
        MockStaticInvoker invoker = new MockStaticInvoker(hello, new SimpleTargetImpl());
        source.setTargetInvoker(invoker);

        Map<Method, SourceInvocationChain> configs = new MethodHashMap<SourceInvocationChain>();
        configs.put(hello, source);
        WireInvocationHandler handler = new SourceInvocationHandler(configs);
        try {
            assertEquals("foo", handler.invoke(null, hello, new Object[]{}));
            fail("Expected " + IllegalArgumentException.class.getName());
        } catch (IllegalArgumentException e) {
            // should throw
        }
    }

    public void testDirectInvoke() throws Throwable {
        SourceInvocationChainImpl source = new SourceInvocationChainImpl(hello);
        MockStaticInvoker invoker = new MockStaticInvoker(hello, new SimpleTargetImpl());
        source.setTargetInvoker(invoker);

        Map<Method, SourceInvocationChain> configs = new MethodHashMap<SourceInvocationChain>();
        configs.put(hello, source);
        WireInvocationHandler handler = new SourceInvocationHandler(configs);
        assertEquals("foo", handler.invoke(null, hello, new Object[]{"foo"}));
    }

    private SourceInvocationChain createChain(Method m) {
        SourceInvocationChain source = new SourceInvocationChainImpl(m);
        MockHandler sourceRequestHandler = new MockHandler();
        MockHandler sourceResponseHandler = new MockHandler();
        MockSyncInterceptor sourceInterceptor = new MockSyncInterceptor();
        source.addRequestHandler(sourceRequestHandler);
        source.addResponseHandler(sourceResponseHandler);
        source.addInterceptor(sourceInterceptor);

        TargetInvocationChain target = new TargetInvocationChainImpl(m);
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
