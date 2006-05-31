package org.apache.tuscany.core.builder;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.apache.tuscany.core.mock.component.SimpleTarget;
import org.apache.tuscany.core.mock.wire.MockSyncInterceptor;
import org.apache.tuscany.core.wire.InboundInvocationChainImpl;
import org.apache.tuscany.core.wire.InvokerInterceptor;
import org.apache.tuscany.core.wire.OutboundInvocationChainImpl;
import org.apache.tuscany.spi.wire.InboundInvocationChain;
import org.apache.tuscany.spi.wire.Interceptor;
import org.apache.tuscany.spi.wire.Message;
import org.apache.tuscany.spi.wire.MessageHandler;
import org.apache.tuscany.spi.wire.MessageImpl;
import org.apache.tuscany.spi.wire.OutboundInvocationChain;
import org.apache.tuscany.spi.wire.TargetInvoker;
import org.jmock.Mock;
import org.jmock.MockObjectTestCase;

/**
 * Verifies connection strategies between {@link org.apache.tuscany.spi.wire.OutboundInvocationChain}s and
 * {@link org.apache.tuscany.spi.wire.InboundInvocationChain}s
 *
 * @version $$Rev$$ $$Date$$
 */
public class InboundtoOutboundConnectTestCase extends MockObjectTestCase {

    @SuppressWarnings("unchecked")
    public void testNoInterceptorsNoHandlers() throws Exception {
        ConnectorImpl connector = new ConnectorImpl();
        InboundInvocationChain inboundChain = setupInbound(null, null, null);
        OutboundInvocationChain outboundChain = setupOutbound(null, null, null);
        String[] val = new String[]{"foo"};
        Mock mock = mock(TargetInvoker.class);
        mock.expects(once()).method("invokeTarget").with(eq(val)).will(returnValue(val));
        TargetInvoker invoker = (TargetInvoker) mock.proxy();
        connector.connect(inboundChain, outboundChain);
        inboundChain.setTargetInvoker(invoker);
        inboundChain.build();
        assertEquals(val, inboundChain.getTargetInvoker().invokeTarget(val));
    }


    /**
     * Verifies an invocation with a single source interceptor
     */
    @SuppressWarnings("unchecked")
    public void testSourceInterceptor() throws Exception {
        ConnectorImpl connector = new ConnectorImpl();
        MockSyncInterceptor interceptor = new MockSyncInterceptor();
        List<Interceptor> interceptors = new ArrayList<Interceptor>();
        interceptors.add(interceptor);

        InboundInvocationChain inboundChain = setupInbound(interceptors, null, null);
        OutboundInvocationChain outboundChain = setupOutbound(null, null, null);
        Message msg = new MessageImpl();
        Mock mock = mock(TargetInvoker.class);
        mock.expects(once()).method("invoke").with(eq(msg)).will(returnValue(msg));
        TargetInvoker invoker = (TargetInvoker) mock.proxy();
        assertEquals(0, interceptor.getCount());
        connector.connect(inboundChain, outboundChain);
        inboundChain.setTargetInvoker(invoker);
        inboundChain.build();
        msg.setTargetInvoker(inboundChain.getTargetInvoker());
        assertEquals(msg, inboundChain.getHeadInterceptor().invoke(msg));
        assertEquals(1, interceptor.getCount());
    }

    /**
     * Verifies an invocation with a single target interceptor
     */
    @SuppressWarnings("unchecked")
    public void testTargetInterceptor() throws Exception {
        ConnectorImpl connector = new ConnectorImpl();
        MockSyncInterceptor interceptor = new MockSyncInterceptor();
        List<Interceptor> interceptors = new ArrayList<Interceptor>();
        interceptors.add(interceptor);

        InboundInvocationChain inboundChain = setupInbound(interceptors, null, null);
        OutboundInvocationChain outboundChain = setupOutbound(null, null, null);
        Message msg = new MessageImpl();
        Mock mock = mock(TargetInvoker.class);
        mock.expects(once()).method("invoke").with(eq(msg)).will(returnValue(msg));
        TargetInvoker invoker = (TargetInvoker) mock.proxy();
        assertEquals(0, interceptor.getCount());
        connector.connect(inboundChain, outboundChain);
        inboundChain.setTargetInvoker(invoker);
        inboundChain.build();
        msg.setTargetInvoker(inboundChain.getTargetInvoker());
        assertEquals(msg, inboundChain.getHeadInterceptor().invoke(msg));
        assertEquals(1, interceptor.getCount());
    }

    /**
     * Verifies an invocation with a source and target interceptor
     */
    @SuppressWarnings("unchecked")
    public void testSourceTargetInterceptor() throws Exception {
        ConnectorImpl connector = new ConnectorImpl();
        MockSyncInterceptor sourceInterceptor = new MockSyncInterceptor();
        List<Interceptor> sourceInterceptors = new ArrayList<Interceptor>();
        sourceInterceptors.add(sourceInterceptor);
        MockSyncInterceptor targetInterceptor = new MockSyncInterceptor();
        List<Interceptor> targetInterceptors = new ArrayList<Interceptor>();
        targetInterceptors.add(targetInterceptor);

        OutboundInvocationChain outboundChain = setupOutbound(sourceInterceptors, null, null);
        InboundInvocationChain inboundChain = setupInbound(targetInterceptors, null, null);
        Message msg = new MessageImpl();
        Mock mock = mock(TargetInvoker.class);
        mock.expects(once()).method("invoke").with(eq(msg)).will(returnValue(msg));
        TargetInvoker invoker = (TargetInvoker) mock.proxy();
        assertEquals(0, sourceInterceptor.getCount());
        assertEquals(0, targetInterceptor.getCount());
        connector.connect(inboundChain, outboundChain);
        inboundChain.setTargetInvoker(invoker);
        inboundChain.build();
        msg.setTargetInvoker(inboundChain.getTargetInvoker());
        assertEquals(msg, inboundChain.getHeadInterceptor().invoke(msg));
        assertEquals(1, sourceInterceptor.getCount());
        assertEquals(1, targetInterceptor.getCount());
    }

    public InboundInvocationChain setupInbound(List<Interceptor> interceptors,
                                               List<MessageHandler> requestHandlers,
                                               List<MessageHandler> responseHandlers) {

        Method echo;
        try {
            echo = SimpleTarget.class.getMethod("echo", String.class);
        } catch (NoSuchMethodException e) {
            throw new AssertionError();
        }
        InboundInvocationChainImpl chain = new InboundInvocationChainImpl(echo);
        if (interceptors != null) {
            for (Interceptor interceptor : interceptors) {
                chain.addInterceptor(interceptor);
            }
        }
        if (requestHandlers != null) {
            for (MessageHandler handler : requestHandlers) {
                chain.addRequestHandler(handler);
            }
        }
        if (responseHandlers != null) {
            for (MessageHandler handler : responseHandlers) {
                chain.addResponseHandler(handler);
            }
        }
        return chain;
    }

    public OutboundInvocationChain setupOutbound(List<Interceptor> interceptors,
                                                 List<MessageHandler> requestHandlers,
                                                 List<MessageHandler> responseHandlers) {

        Method echo;
        try {
            echo = SimpleTarget.class.getMethod("echo", String.class);
        } catch (NoSuchMethodException e) {
            throw new AssertionError();
        }
        OutboundInvocationChainImpl chain = new OutboundInvocationChainImpl(echo);
        if (interceptors != null) {
            for (Interceptor interceptor : interceptors) {
                chain.addInterceptor(interceptor);
            }
        }
        if (requestHandlers != null) {
            for (MessageHandler handler : requestHandlers) {
                chain.addRequestHandler(handler);
            }
        }
        if (responseHandlers != null) {
            for (MessageHandler handler : responseHandlers) {
                chain.addResponseHandler(handler);
            }
        }
        chain.addInterceptor(new InvokerInterceptor()); // add tail interceptor
        return chain;
    }

}
