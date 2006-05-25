package org.apache.tuscany.core.wire.jdk;

import java.lang.reflect.Method;

import junit.framework.TestCase;
import org.apache.tuscany.core.wire.SourceInvocationChainImpl;
import org.apache.tuscany.core.wire.TargetInvocationChainImpl;
import org.apache.tuscany.spi.wire.Interceptor;
import org.apache.tuscany.spi.wire.Message;
import org.apache.tuscany.spi.wire.MessageHandler;
import org.apache.tuscany.spi.wire.SourceInvocationChain;
import org.apache.tuscany.spi.wire.SourceWire;
import org.apache.tuscany.spi.wire.TargetWire;
import org.apache.tuscany.spi.wire.TargetInvocationChain;

/**
 * @version $$Rev$$ $$Date$$
 */
public class JDKWireOptimizationTestCase extends TestCase {


    private Method m;

    public void foo() {
    }

    public void testSourceWireInterceptorOptimization() throws Exception {
        SourceWire<?> wire = new JDKSourceWire();
        SourceInvocationChain chain = new SourceInvocationChainImpl(m);
        chain.addInterceptor(new OptimizableInterceptor());
        wire.addInvocationChain(m, chain);
        assertTrue(wire.isOptimizable());
    }

    public void testSourceWireHandlerOptimization() throws Exception {
        SourceWire<?> wire = new JDKSourceWire();
        SourceInvocationChain chain = new SourceInvocationChainImpl(m);
        chain.addRequestHandler(new OptimizableHandler());
        chain.addResponseHandler(new OptimizableHandler());
        wire.addInvocationChain(m, chain);
        assertTrue(wire.isOptimizable());
    }

    public void testSourceWireNonInterceptorOptimization() throws Exception {
        SourceWire<?> wire = new JDKSourceWire();
        SourceInvocationChain chain = new SourceInvocationChainImpl(m);
        chain.addInterceptor(new NonOptimizableInterceptor());
        wire.addInvocationChain(m, chain);
        assertFalse(wire.isOptimizable());
    }

    public void testSourceWireNonRequestHandlerOptimization() throws Exception {
        SourceWire<?> wire = new JDKSourceWire();
        SourceInvocationChain chain = new SourceInvocationChainImpl(m);
        chain.addInterceptor(new OptimizableInterceptor());
        chain.addRequestHandler(new NonOptimizableHandler());
        wire.addInvocationChain(m, chain);
        assertFalse(wire.isOptimizable());
    }

    public void testSourceWireNonResponseHandlerOptimization() throws Exception {
        SourceWire<?> wire = new JDKSourceWire();
        SourceInvocationChain chain = new SourceInvocationChainImpl(m);
        chain.addInterceptor(new OptimizableInterceptor());
        chain.addResponseHandler(new NonOptimizableHandler());
        wire.addInvocationChain(m, chain);
        assertFalse(wire.isOptimizable());
    }


    public void testTargetWireInterceptorOptimization() throws Exception {
        TargetWire<?> wire = new JDKTargetWire();
        TargetInvocationChain chain = new TargetInvocationChainImpl(m);
        chain.addInterceptor(new OptimizableInterceptor());
        wire.addInvocationChain(m, chain);
        assertTrue(wire.isOptimizable());
    }

    public void testTargetWireHandlerOptimization() throws Exception {
        TargetWire<?> wire = new JDKTargetWire();
        TargetInvocationChain chain = new TargetInvocationChainImpl(m);
        chain.addRequestHandler(new OptimizableHandler());
        chain.addResponseHandler(new OptimizableHandler());
        wire.addInvocationChain(m, chain);
        assertTrue(wire.isOptimizable());
    }

    public void testTargetWireNonInterceptorOptimization() throws Exception {
        TargetWire<?> wire = new JDKTargetWire();
        TargetInvocationChain chain = new TargetInvocationChainImpl(m);
        chain.addInterceptor(new NonOptimizableInterceptor());
        wire.addInvocationChain(m, chain);
        assertFalse(wire.isOptimizable());
    }

    public void testTargetWireNonRequestHandlerOptimization() throws Exception {
        TargetWire<?> wire = new JDKTargetWire();
        TargetInvocationChain chain = new TargetInvocationChainImpl(m);
        chain.addInterceptor(new OptimizableInterceptor());
        chain.addRequestHandler(new NonOptimizableHandler());
        wire.addInvocationChain(m, chain);
        assertFalse(wire.isOptimizable());
    }

    public void testTargetWireNonResponseHandlerOptimization() throws Exception {
        TargetWire<?> wire = new JDKTargetWire();
        TargetInvocationChain chain = new TargetInvocationChainImpl(m);
        chain.addInterceptor(new OptimizableInterceptor());
        chain.addResponseHandler(new NonOptimizableHandler());
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
