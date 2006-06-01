package org.apache.tuscany.core.wire;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Map;

import junit.framework.TestCase;
import org.apache.tuscany.core.mock.wire.MockHandler;
import org.apache.tuscany.core.mock.wire.MockStaticInvoker;
import org.apache.tuscany.core.mock.wire.MockSyncInterceptor;
import org.apache.tuscany.core.util.MethodHashMap;
import org.apache.tuscany.core.wire.jdk.JDKInboundInvocationHandler;
import org.apache.tuscany.spi.wire.InboundInvocationChain;

/**
 * Tests handling of exceptions thrown during an inbound wire invocation
 *
 * @version $Rev: 377006 $ $Date: 2006-02-11 09:41:59 -0800 (Sat, 11 Feb 2006) $
 */
public class InboundInvocationErrorTestCase extends TestCase {

    private Method checkedMethod;
    private Method runtimeMethod;

    public InboundInvocationErrorTestCase() {
        super();
    }

    public InboundInvocationErrorTestCase(String arg0) {
        super(arg0);
    }

    public void setUp() throws Exception {
        checkedMethod = InboundInvocationErrorTestCase.TestBean.class.getDeclaredMethod("checkedException", (Class[]) null);
        runtimeMethod = InboundInvocationErrorTestCase.TestBean.class.getDeclaredMethod("runtimeException", (Class[]) null);
        assertNotNull(checkedMethod);
        assertNotNull(runtimeMethod);
    }

    public void testCheckedException() throws Exception {
        Map<Method, InboundInvocationChain> chains = new MethodHashMap<InboundInvocationChain>();
        chains.put(checkedMethod, createChain(checkedMethod));
        JDKInboundInvocationHandler handler = new JDKInboundInvocationHandler(chains);
        try {
            InboundInvocationErrorTestCase.TestBean proxy = (InboundInvocationErrorTestCase.TestBean) Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(),
                    new Class[]{InboundInvocationErrorTestCase.TestBean.class}, handler);
            proxy.checkedException();
        } catch (InboundInvocationErrorTestCase.TestException e) {
            return;
        }
        fail(InboundInvocationErrorTestCase.TestException.class.getName() + " should have been thrown");
    }

    public void testRuntimeException() throws Exception {
        Map<Method, InboundInvocationChain> chains = new MethodHashMap<InboundInvocationChain>();
        chains.put(runtimeMethod, createChain(runtimeMethod));
        JDKInboundInvocationHandler handler = new JDKInboundInvocationHandler(chains);
        try {
            InboundInvocationErrorTestCase.TestBean proxy = (InboundInvocationErrorTestCase.TestBean) Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(),
                    new Class[]{InboundInvocationErrorTestCase.TestBean.class}, handler);
            proxy.runtimeException();
        } catch (InboundInvocationErrorTestCase.TestRuntimeException e) {
            return;
        }
        fail(InboundInvocationErrorTestCase.TestException.class.getName() + " should have been thrown");
    }

    private InboundInvocationChain createChain(Method m) {
        MockStaticInvoker invoker = new MockStaticInvoker(m, new InboundInvocationErrorTestCase.TestBeanImpl());
        InboundInvocationChain chain = new InboundInvocationChainImpl(m);
        chain.addInterceptor(new MockSyncInterceptor());
        chain.addRequestHandler(new MockHandler());
        chain.setTargetInvoker(invoker);
        chain.addInterceptor(new InvokerInterceptor());
        chain.build();
        return chain;
    }

    public interface TestBean {

        public void checkedException() throws InboundInvocationErrorTestCase.TestException;

        public void runtimeException() throws InboundInvocationErrorTestCase.TestRuntimeException;

    }

    public class TestBeanImpl implements InboundInvocationErrorTestCase.TestBean {

        public void checkedException() throws InboundInvocationErrorTestCase.TestException {
            throw new InboundInvocationErrorTestCase.TestException();
        }

        public void runtimeException() throws InboundInvocationErrorTestCase.TestRuntimeException {
            throw new InboundInvocationErrorTestCase.TestRuntimeException();
        }
    }

    public class TestException extends Exception {
    }

    public class TestRuntimeException extends RuntimeException {
    }

}
