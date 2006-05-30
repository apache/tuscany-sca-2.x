package org.apache.tuscany.core.wire;

import java.lang.reflect.Method;

import junit.framework.TestCase;
import org.apache.tuscany.core.wire.OutboundInvocationChainImpl;
import org.apache.tuscany.core.wire.InboundInvocationChainImpl;
import org.apache.tuscany.core.wire.OutboundWireImpl;
import org.apache.tuscany.core.wire.InboundWireImpl;
import org.apache.tuscany.core.wire.StaticPojoTargetInvoker;
import org.apache.tuscany.spi.wire.Interceptor;
import org.apache.tuscany.spi.wire.Message;
import org.apache.tuscany.spi.wire.MessageHandler;
import org.apache.tuscany.spi.wire.OutboundInvocationChain;
import org.apache.tuscany.spi.wire.OutboundWire;
import org.apache.tuscany.spi.wire.InboundWire;
import org.apache.tuscany.spi.wire.InboundInvocationChain;
import org.apache.tuscany.spi.wire.TargetInvoker;

/**
 * @version $$Rev$$ $$Date$$
 */
public class WireOptimizationTestCase extends TestCase {


    private Method m;

    public void foo() {
    }

    public void testSourceWireInterceptorOptimization() throws Exception {
        OutboundWire<?> wire = new OutboundWireImpl();
        OutboundInvocationChain chain = new OutboundInvocationChainImpl(m);
        chain.addInterceptor(new OptimizableInterceptor());
        wire.addInvocationChain(m, chain);
        assertTrue(wire.isOptimizable());
    }

    public void testSourceWireHandlerOptimization() throws Exception {
        OutboundWire<?> wire = new OutboundWireImpl();
        OutboundInvocationChain chain = new OutboundInvocationChainImpl(m);
        chain.addRequestHandler(new OptimizableHandler());
        chain.addResponseHandler(new OptimizableHandler());
        wire.addInvocationChain(m, chain);
        assertTrue(wire.isOptimizable());
    }

    public void testSourceWireNonInterceptorOptimization() throws Exception {
        OutboundWire<?> wire = new OutboundWireImpl();
        OutboundInvocationChain chain = new OutboundInvocationChainImpl(m);
        chain.addInterceptor(new NonOptimizableInterceptor());
        wire.addInvocationChain(m, chain);
        assertFalse(wire.isOptimizable());
    }

    public void testSourceWireNonRequestHandlerOptimization() throws Exception {
        OutboundWire<?> wire = new OutboundWireImpl();
        OutboundInvocationChain chain = new OutboundInvocationChainImpl(m);
        chain.addInterceptor(new OptimizableInterceptor());
        chain.addRequestHandler(new NonOptimizableHandler());
        wire.addInvocationChain(m, chain);
        assertFalse(wire.isOptimizable());
    }

    public void testSourceWireNonResponseHandlerOptimization() throws Exception {
        OutboundWire<?> wire = new OutboundWireImpl();
        OutboundInvocationChain chain = new OutboundInvocationChainImpl(m);
        chain.addInterceptor(new OptimizableInterceptor());
        chain.addResponseHandler(new NonOptimizableHandler());
        wire.addInvocationChain(m, chain);
        assertFalse(wire.isOptimizable());
    }


    public void testTargetWireInterceptorOptimization() throws Exception {
        InboundWire<?> wire = new InboundWireImpl();
        InboundInvocationChain chain = new InboundInvocationChainImpl(m);
        chain.addInterceptor(new OptimizableInterceptor());
        wire.addInvocationChain(m, chain);
        assertTrue(wire.isOptimizable());
    }

    public void testTargetWireHandlerOptimization() throws Exception {
        InboundWire<?> wire = new InboundWireImpl();
        InboundInvocationChain chain = new InboundInvocationChainImpl(m);
        chain.addRequestHandler(new OptimizableHandler());
        chain.addResponseHandler(new OptimizableHandler());
        wire.addInvocationChain(m, chain);
        assertTrue(wire.isOptimizable());
    }

    public void testTargetWireNonInterceptorOptimization() throws Exception {
        InboundWire<?> wire = new InboundWireImpl();
        InboundInvocationChain chain = new InboundInvocationChainImpl(m);
        chain.addInterceptor(new NonOptimizableInterceptor());
        wire.addInvocationChain(m, chain);
        assertFalse(wire.isOptimizable());
    }

    public void testTargetWireNonRequestHandlerOptimization() throws Exception {
        InboundWire<?> wire = new InboundWireImpl();
        InboundInvocationChain chain = new InboundInvocationChainImpl(m);
        chain.addInterceptor(new OptimizableInterceptor());
        chain.addRequestHandler(new NonOptimizableHandler());
        wire.addInvocationChain(m, chain);
        assertFalse(wire.isOptimizable());
    }

    public void testTargetWireNonResponseHandlerOptimization() throws Exception {
        InboundWire<?> wire = new InboundWireImpl();
        InboundInvocationChain chain = new InboundInvocationChainImpl(m);
        chain.addInterceptor(new OptimizableInterceptor());
        chain.addResponseHandler(new NonOptimizableHandler());
        wire.addInvocationChain(m, chain);
        assertFalse(wire.isOptimizable());
    }

    public void testTargetWireNonTargetInvokerOptimization() throws Exception {
        InboundWire<?> wire = new InboundWireImpl();
        InboundInvocationChain chain = new InboundInvocationChainImpl(m);
        TargetInvoker invoker = new StaticPojoTargetInvoker(m,new Object());
        invoker.setCacheable(false);
        chain.setTargetInvoker(invoker);
        wire.addInvocationChain(m, chain);
        assertFalse(wire.isOptimizable());
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    protected void setUp() throws Exception {
        super.setUp();
        m = getClass().getMethod("foo", (Class[]) null);
    }

    private class OptimizableInterceptor implements Interceptor {

        public Message invoke(Message msg) {
            return null;
        }

        public void setNext(Interceptor next) {

        }

        public Interceptor getNext() {
            return null;
        }

        public boolean isOptimizable() {
            return true;
        }
    }

    private class NonOptimizableInterceptor implements Interceptor {

        public Message invoke(Message msg) {
            return null;
        }

        public void setNext(Interceptor next) {

        }

        public Interceptor getNext() {
            return null;
        }

        public boolean isOptimizable() {
            return false;
        }
    }

    private class OptimizableHandler implements MessageHandler {

        public boolean processMessage(Message message) {
            return false;
        }

        public boolean isOptimizable() {
            return true;
        }
    }

    private class NonOptimizableHandler implements MessageHandler {

        public boolean processMessage(Message message) {
            return false;
        }

        public boolean isOptimizable() {
            return false;
        }
    }
}
