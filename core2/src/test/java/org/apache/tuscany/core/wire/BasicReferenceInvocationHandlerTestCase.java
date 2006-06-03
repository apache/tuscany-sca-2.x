package org.apache.tuscany.core.wire;

import java.lang.reflect.Method;
import java.util.Map;

import org.apache.tuscany.core.mock.component.SimpleTarget;
import org.apache.tuscany.core.mock.component.SimpleTargetImpl;
import org.apache.tuscany.core.mock.wire.MockHandler;
import org.apache.tuscany.core.mock.wire.MockStaticInvoker;
import org.apache.tuscany.core.mock.wire.MockSyncInterceptor;
import org.apache.tuscany.core.util.MethodHashMap;
import org.apache.tuscany.core.wire.jdk.JDKOutboundInvocationHandler;
import org.apache.tuscany.spi.wire.OutboundInvocationChain;
import org.jmock.MockObjectTestCase;

/**
 * @version $$Rev$$ $$Date$$
 */
public class BasicReferenceInvocationHandlerTestCase extends MockObjectTestCase {

    private Method echo;

    public void testHandlersInterceptorInvoke() throws Throwable {
        Map<Method, OutboundInvocationChain> chains = new MethodHashMap<OutboundInvocationChain>();
        MockStaticInvoker invoker = new MockStaticInvoker(echo, new SimpleTargetImpl());
        OutboundInvocationChain chain = new OutboundInvocationChainImpl(echo);
        MockSyncInterceptor interceptor = new MockSyncInterceptor();
        chain.addInterceptor(interceptor);
        chain.setTargetInterceptor(new InvokerInterceptor());
        MockHandler requestHandler = new MockHandler();
        chain.addRequestHandler(requestHandler);
        MockHandler responseHandler = new MockHandler();
        chain.addResponseHandler(responseHandler);
        chain.setTargetInvoker(invoker);
        chain.build();
        chains.put(echo, chain);
        JDKOutboundInvocationHandler handler = new JDKOutboundInvocationHandler(chains);
        assertEquals("foo", handler.invoke(null, echo, new String[]{"foo"}));
        assertEquals(1, interceptor.getCount());
        assertEquals(1, requestHandler.getCount());
        assertEquals(1, responseHandler.getCount());
    }

    public void setUp() throws Exception {
        super.setUp();
        echo = SimpleTarget.class.getMethod("echo", String.class);
    }

}
