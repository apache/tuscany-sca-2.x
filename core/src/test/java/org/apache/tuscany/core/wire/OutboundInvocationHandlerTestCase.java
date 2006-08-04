package org.apache.tuscany.core.wire;

import java.lang.reflect.Method;

import org.apache.tuscany.spi.wire.InboundInvocationChain;
import org.apache.tuscany.spi.wire.OutboundInvocationChain;
import org.apache.tuscany.spi.wire.OutboundWire;

import junit.framework.TestCase;
import org.apache.tuscany.core.mock.component.SimpleTarget;
import org.apache.tuscany.core.mock.component.SimpleTargetImpl;
import org.apache.tuscany.core.mock.wire.MockHandler;
import org.apache.tuscany.core.mock.wire.MockStaticInvoker;
import org.apache.tuscany.core.mock.wire.MockSyncInterceptor;
import org.apache.tuscany.core.wire.jdk.JDKOutboundInvocationHandler;

public class OutboundInvocationHandlerTestCase extends TestCase {

    private Method hello;

    public OutboundInvocationHandlerTestCase() {
        super();
    }

    public OutboundInvocationHandlerTestCase(String arg0) {
        super(arg0);
    }

    public void setUp() throws Exception {
        hello = SimpleTarget.class.getMethod("hello", String.class);
    }

    public void testBasicInvoke() throws Throwable {
        OutboundWire wire = new OutboundWireImpl();
        wire.addInvocationChain(hello, createChain(hello));
        JDKOutboundInvocationHandler handler = new JDKOutboundInvocationHandler(wire);
        assertEquals("foo", handler.invoke(hello, new Object[]{"foo"}));
    }

    public void testErrorInvoke() throws Throwable {
        OutboundWire wire = new OutboundWireImpl();
        wire.addInvocationChain(hello, createChain(hello));
        JDKOutboundInvocationHandler handler = new JDKOutboundInvocationHandler(wire);
        try {
            handler.invoke(hello, new Object[]{});
            fail("Expected " + IllegalArgumentException.class.getName());
        } catch (IllegalArgumentException e) {
            // should throw
        }
    }

    public void testDirectErrorInvoke() throws Throwable {
        OutboundInvocationChain source = new OutboundInvocationChainImpl(hello);
        MockStaticInvoker invoker = new MockStaticInvoker(hello, new SimpleTargetImpl());
        source.setTargetInvoker(invoker);

        OutboundWire wire = new OutboundWireImpl();
        wire.addInvocationChain(hello, source);
        JDKOutboundInvocationHandler handler = new JDKOutboundInvocationHandler(wire);
        try {
            assertEquals("foo", handler.invoke(hello, new Object[]{}));
            fail("Expected " + IllegalArgumentException.class.getName());
        } catch (IllegalArgumentException e) {
            // should throw
        }
    }

    public void testDirectInvoke() throws Throwable {
        OutboundInvocationChain source = new OutboundInvocationChainImpl(hello);
        MockStaticInvoker invoker = new MockStaticInvoker(hello, new SimpleTargetImpl());
        source.setTargetInvoker(invoker);

        OutboundWire wire = new OutboundWireImpl();
        wire.addInvocationChain(hello, source);
        JDKOutboundInvocationHandler handler = new JDKOutboundInvocationHandler(wire);
        assertEquals("foo", handler.invoke(hello, new Object[]{"foo"}));
    }

    private OutboundInvocationChain createChain(Method m) {
        OutboundInvocationChain source = new OutboundInvocationChainImpl(m);
        MockHandler sourceRequestHandler = new MockHandler();
        MockHandler sourceResponseHandler = new MockHandler();
        MockSyncInterceptor sourceInterceptor = new MockSyncInterceptor();
        source.addRequestHandler(sourceRequestHandler);
        source.addResponseHandler(sourceResponseHandler);
        source.addInterceptor(sourceInterceptor);

        InboundInvocationChain target = new InboundInvocationChainImpl(m);
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
        source.prepare();
        target.prepare();
        MockStaticInvoker invoker = new MockStaticInvoker(m, new SimpleTargetImpl());
        source.setTargetInvoker(invoker);
        return source;
    }
}
