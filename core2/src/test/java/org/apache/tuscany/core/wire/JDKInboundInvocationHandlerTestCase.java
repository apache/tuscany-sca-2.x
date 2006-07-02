package org.apache.tuscany.core.wire;

import java.lang.reflect.Method;
import java.util.Map;

import org.apache.tuscany.spi.wire.InboundInvocationChain;

import org.apache.tuscany.core.mock.component.SimpleTarget;
import org.apache.tuscany.core.mock.component.SimpleTargetImpl;
import org.apache.tuscany.core.mock.wire.MockHandler;
import org.apache.tuscany.core.mock.wire.MockStaticInvoker;
import org.apache.tuscany.core.mock.wire.MockSyncInterceptor;
import org.apache.tuscany.core.util.MethodHashMap;
import org.apache.tuscany.core.wire.jdk.JDKInboundInvocationHandler;
import org.jmock.MockObjectTestCase;

/**
 * Verifies invocations on inbound wires
 *
 * @version $$Rev$$ $$Date$$
 */
public class JDKInboundInvocationHandlerTestCase extends MockObjectTestCase {

    private Method echo;

    public void testHandlersInterceptorInvoke() throws Throwable {
        Map<Method, InboundInvocationChain> chains = new MethodHashMap<InboundInvocationChain>();
        MockStaticInvoker invoker = new MockStaticInvoker(echo, new SimpleTargetImpl());
        InboundInvocationChain chain = new InboundInvocationChainImpl(echo);
        MockSyncInterceptor interceptor = new MockSyncInterceptor();
        chain.addInterceptor(interceptor);
        chain.addInterceptor(new InvokerInterceptor());
        MockHandler requestHandler = new MockHandler();
        chain.addRequestHandler(requestHandler);
        MockHandler responseHandler = new MockHandler();
        chain.addResponseHandler(responseHandler);
        chain.setTargetInvoker(invoker);
        chain.prepare();
        chains.put(echo, chain);
        JDKInboundInvocationHandler handler = new JDKInboundInvocationHandler(chains);
        assertEquals("foo", handler.invoke(echo, new String[]{"foo"}));
        assertEquals(1, interceptor.getCount());
        assertEquals(1, requestHandler.getCount());
        assertEquals(1, responseHandler.getCount());
    }

    public void testInterceptorInvoke() throws Throwable {
        Map<Method, InboundInvocationChain> chains = new MethodHashMap<InboundInvocationChain>();
        MockStaticInvoker invoker = new MockStaticInvoker(echo, new SimpleTargetImpl());
        InboundInvocationChain chain = new InboundInvocationChainImpl(echo);
        MockSyncInterceptor interceptor = new MockSyncInterceptor();
        chain.addInterceptor(interceptor);
        chain.addInterceptor(new InvokerInterceptor());
        chain.setTargetInvoker(invoker);
        chain.prepare();
        chains.put(echo, chain);
        JDKInboundInvocationHandler handler = new JDKInboundInvocationHandler(chains);
        assertEquals("foo", handler.invoke(echo, new String[]{"foo"}));
        assertEquals(1, interceptor.getCount());
    }


    public void testDirectErrorInvoke() throws Throwable {
        InboundInvocationChain source = new InboundInvocationChainImpl(echo);
        MockStaticInvoker invoker = new MockStaticInvoker(echo, new SimpleTargetImpl());
        source.setTargetInvoker(invoker);

        Map<Method, InboundInvocationChain> chains = new MethodHashMap<InboundInvocationChain>();
        chains.put(echo, source);
        JDKInboundInvocationHandler handler = new JDKInboundInvocationHandler(chains);
        try {
            assertEquals("foo", handler.invoke(echo, new Object[]{}));
            fail("Expected " + IllegalArgumentException.class.getName());
        } catch (IllegalArgumentException e) {
            // should throw
        }
    }

    public void testDirectInvoke() throws Throwable {
        InboundInvocationChain source = new InboundInvocationChainImpl(echo);
        MockStaticInvoker invoker = new MockStaticInvoker(echo, new SimpleTargetImpl());
        source.setTargetInvoker(invoker);

        Map<Method, InboundInvocationChain> chains = new MethodHashMap<InboundInvocationChain>();
        chains.put(echo, source);
        JDKInboundInvocationHandler handler = new JDKInboundInvocationHandler(chains);
        assertEquals("foo", handler.invoke(echo, new Object[]{"foo"}));
    }


    public void setUp() throws Exception {
        super.setUp();
        echo = SimpleTarget.class.getMethod("echo", String.class);
    }

}
