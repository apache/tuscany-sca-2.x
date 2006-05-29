package org.apache.tuscany.core.builder;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.apache.tuscany.core.mock.component.SimpleTarget;
import org.apache.tuscany.core.mock.wire.MockHandler;
import org.apache.tuscany.core.mock.wire.MockSyncInterceptor;
import org.apache.tuscany.core.wire.OutboundInvocationChainImpl;
import org.apache.tuscany.core.wire.InvokerInterceptor;
import org.apache.tuscany.spi.wire.Interceptor;
import org.apache.tuscany.spi.wire.Message;
import org.apache.tuscany.spi.wire.MessageHandler;
import org.apache.tuscany.spi.wire.MessageImpl;
import org.apache.tuscany.spi.wire.OutboundInvocationChain;
import org.apache.tuscany.spi.wire.TargetInvoker;
import org.jmock.Mock;
import org.jmock.MockObjectTestCase;
import org.jmock.core.Stub;
import org.jmock.core.Invocation;

/**
 * Verifies connection strategies between {@link org.apache.tuscany.spi.wire.OutboundInvocationChain}s and
 * {@link org.apache.tuscany.spi.wire.InboundInvocationChain}s
 *
 * @version $$Rev$$ $$Date$$
 */
public class ReferenceToReferenceConnectTestCase extends MockObjectTestCase {

    @SuppressWarnings("unchecked")
    public void testNoInterceptorsNoHandlers() throws Exception {
        ConnectorImpl connector = new ConnectorImpl();
        OutboundInvocationChain sourceChain = setupChain(null, null, null);
        OutboundInvocationChain targetChain = setupTerminatingChain(null, null, null);
        String[] val = new String[]{"foo"};
        Mock mock = mock(TargetInvoker.class);
        mock.expects(once()).method("invokeTarget").with(eq(val)).will(returnValue(val));
        TargetInvoker invoker = (TargetInvoker) mock.proxy();
        connector.connect(sourceChain, targetChain, invoker);
        sourceChain.build();
        assertEquals(val, sourceChain.getTargetInvoker().invokeTarget(val));
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
        OutboundInvocationChain sourceChain = setupChain(interceptors, null, null);
        OutboundInvocationChain targetChain = setupTerminatingChain(null, null, null);
        Message msg = new MessageImpl();
        Mock mock = mock(TargetInvoker.class);
        mock.expects(once()).method("invoke").with(eq(msg)).will(returnValue(msg));
        TargetInvoker invoker = (TargetInvoker) mock.proxy();
        assertEquals(0, interceptor.getCount());
        connector.connect(sourceChain, targetChain, invoker);
        sourceChain.build();
        targetChain.build();
        msg.setTargetInvoker(sourceChain.getTargetInvoker());
        assertEquals(msg, sourceChain.getHeadInterceptor().invoke(msg));
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

        OutboundInvocationChain sourceChain = setupChain(null, null, null);
        OutboundInvocationChain targetChain = setupTerminatingChain(interceptors, null, null);
        Message msg = new MessageImpl();
        Mock mock = mock(TargetInvoker.class);
        mock.expects(once()).method("invoke").with(eq(msg)).will(returnValue(msg));
        TargetInvoker invoker = (TargetInvoker) mock.proxy();
        assertEquals(0, interceptor.getCount());
        connector.connect(sourceChain, targetChain, invoker);
        sourceChain.build();
        targetChain.build();
        msg.setTargetInvoker(sourceChain.getTargetInvoker());
        assertEquals(msg, sourceChain.getHeadInterceptor().invoke(msg));
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

        OutboundInvocationChain sourceChain = setupChain(sourceInterceptors, null, null);
        OutboundInvocationChain targetChain = setupTerminatingChain(targetInterceptors, null, null);
        Message msg = new MessageImpl();
        Mock mock = mock(TargetInvoker.class);
        mock.expects(once()).method("invoke").with(eq(msg)).will(returnValue(msg));
        TargetInvoker invoker = (TargetInvoker) mock.proxy();
        assertEquals(0, sourceInterceptor.getCount());
        assertEquals(0, targetInterceptor.getCount());
        connector.connect(sourceChain, targetChain, invoker);
        sourceChain.build();
        targetChain.build();
        msg.setTargetInvoker(sourceChain.getTargetInvoker());
        assertEquals(msg, sourceChain.getHeadInterceptor().invoke(msg));
        assertEquals(1, sourceInterceptor.getCount());
        assertEquals(1, targetInterceptor.getCount());
    }

    /**
     * Verifies an invocation with a source interceptor and a request handler
     */
    @SuppressWarnings("unchecked")
    public void testSourceInterceptorSourceRequestHandler() throws Exception {
        ConnectorImpl connector = new ConnectorImpl();
        MockSyncInterceptor interceptor = new MockSyncInterceptor();
        List<Interceptor> interceptors = new ArrayList<Interceptor>();
        interceptors.add(interceptor);
        MockHandler handler = new MockHandler();
        List<MessageHandler> handlers = new ArrayList<MessageHandler>();
        handlers.add(handler);

        OutboundInvocationChain sourceChain = setupChain(interceptors, handlers, null);
        OutboundInvocationChain targetChain = setupTerminatingChain(null, null, null);
        Message msg = new MessageImpl();
        Mock mock = mock(TargetInvoker.class);
        mock.expects(once()).method("invoke").with(eq(msg)).will(returnValue(msg));
        TargetInvoker invoker = (TargetInvoker) mock.proxy();
        assertEquals(0, interceptor.getCount());
        assertEquals(0, handler.getCount());
        connector.connect(sourceChain, targetChain, invoker);
        sourceChain.build();
        targetChain.build();
        msg.setTargetInvoker(sourceChain.getTargetInvoker());
        assertEquals(msg, sourceChain.getHeadInterceptor().invoke(msg));
        assertEquals(1, interceptor.getCount());
        assertEquals(1, handler.getCount());
    }

    /**
     * Verifies an invocation with a target interceptor and a request handler
     */
    @SuppressWarnings("unchecked")
    public void testTargetInterceptorTargetRequestHandler() throws Exception {
        ConnectorImpl connector = new ConnectorImpl();
        MockSyncInterceptor interceptor = new MockSyncInterceptor();
        List<Interceptor> interceptors = new ArrayList<Interceptor>();
        interceptors.add(interceptor);
        MockHandler handler = new MockHandler();
        final Message msg = new MessageImpl();
        Mock terminatingHandler = mock(MessageHandler.class);
        terminatingHandler.expects(once()).method("processMessage").with(eq(msg)).will(new Stub(){
            public Object invoke(Invocation invocation) throws Throwable {
                return true;
            }

            public StringBuffer describeTo(StringBuffer stringBuffer) {
                return null;
            }
        });
        List<MessageHandler> handlers = new ArrayList<MessageHandler>();
        handlers.add(handler);
        handlers.add((MessageHandler)terminatingHandler.proxy());
        OutboundInvocationChain sourceChain = setupChain(null, null, null);
        OutboundInvocationChain targetChain = setupChain(interceptors, handlers, null);
        assertEquals(0, interceptor.getCount());
        assertEquals(0, handler.getCount());
        connector.connect(sourceChain, targetChain, null);
        sourceChain.build();
        targetChain.build();
        msg.setTargetInvoker(sourceChain.getTargetInvoker());
        assertEquals(msg, sourceChain.getHeadInterceptor().invoke(msg));
        assertEquals(1, interceptor.getCount());
        assertEquals(1, handler.getCount());
    }


    /**
     * Verifies an invocation with a source interceptor and response handler
     */
    @SuppressWarnings("unchecked")
    public void testSourceInterceptorSourceResponseHandler() throws Exception {
        ConnectorImpl connector = new ConnectorImpl();
        MockSyncInterceptor interceptor = new MockSyncInterceptor();
        List<Interceptor> interceptors = new ArrayList<Interceptor>();
        interceptors.add(interceptor);
        MockHandler handler = new MockHandler();
        List<MessageHandler> handlers = new ArrayList<MessageHandler>();
        handlers.add(handler);

        OutboundInvocationChain sourceChain = setupChain(interceptors, null, handlers);
        OutboundInvocationChain targetChain = setupTerminatingChain(null, null, null);
        Message msg = new MessageImpl();
        Mock mock = mock(TargetInvoker.class);
        mock.expects(once()).method("invoke").with(eq(msg)).will(returnValue(msg));
        TargetInvoker invoker = (TargetInvoker) mock.proxy();
        assertEquals(0, interceptor.getCount());
        assertEquals(0, handler.getCount());
        connector.connect(sourceChain, targetChain, invoker);
        sourceChain.build();
        targetChain.build();
        msg.setTargetInvoker(sourceChain.getTargetInvoker());
        assertEquals(msg, sourceChain.getHeadInterceptor().invoke(msg));
        assertEquals(1, interceptor.getCount());
        assertEquals(1, handler.getCount());
    }

    /**
     * Verifies an invocation with a source interceptor and response handler
     */
    @SuppressWarnings("unchecked")
    public void testTargetInterceptorTargetResponseHandler() throws Exception {
        ConnectorImpl connector = new ConnectorImpl();
        MockSyncInterceptor interceptor = new MockSyncInterceptor();
        List<Interceptor> interceptors = new ArrayList<Interceptor>();
        interceptors.add(interceptor);
        MockHandler handler = new MockHandler();
        final Message msg = new MessageImpl();
        Mock terminatingHandler = mock(MessageHandler.class);
        terminatingHandler.expects(once()).method("processMessage").with(eq(msg)).will(new Stub(){
            public Object invoke(Invocation invocation) throws Throwable {
                return true;
            }

            public StringBuffer describeTo(StringBuffer stringBuffer) {
                return null;
            }
        });
        List<MessageHandler> handlers = new ArrayList<MessageHandler>();
        handlers.add(handler);
        handlers.add((MessageHandler)terminatingHandler.proxy());

        OutboundInvocationChain sourceChain = setupChain(null, null, null);
        OutboundInvocationChain targetChain = setupChain(interceptors, null, handlers);
        assertEquals(0, interceptor.getCount());
        assertEquals(0, handler.getCount());
        connector.connect(sourceChain, targetChain, null);
        sourceChain.build();
        targetChain.build();
        msg.setTargetInvoker(sourceChain.getTargetInvoker());
        assertEquals(msg, sourceChain.getHeadInterceptor().invoke(msg));
        assertEquals(1, interceptor.getCount());
        assertEquals(1, handler.getCount());
    }

    /**
     * Verifies an invocation with a source interceptor, request handler, and response handler
     */
    @SuppressWarnings("unchecked")
    public void testSourceInterceptorSourceRequestResponseHandler() throws Exception {
        ConnectorImpl connector = new ConnectorImpl();
        MockSyncInterceptor interceptor = new MockSyncInterceptor();
        List<Interceptor> interceptors = new ArrayList<Interceptor>();
        interceptors.add(interceptor);
        MockHandler handler = new MockHandler();
        List<MessageHandler> handlers = new ArrayList<MessageHandler>();
        handlers.add(handler);

        OutboundInvocationChain sourceChain = setupChain(interceptors, handlers, handlers);
        OutboundInvocationChain targetChain = setupTerminatingChain(null, null, null);
        Message msg = new MessageImpl();
        Mock mock = mock(TargetInvoker.class);
        mock.expects(once()).method("invoke").with(eq(msg)).will(returnValue(msg));
        TargetInvoker invoker = (TargetInvoker) mock.proxy();
        assertEquals(0, interceptor.getCount());
        assertEquals(0, handler.getCount());
        connector.connect(sourceChain, targetChain, invoker);
        sourceChain.build();
        targetChain.build();
        msg.setTargetInvoker(sourceChain.getTargetInvoker());
        assertEquals(msg, sourceChain.getHeadInterceptor().invoke(msg));
        assertEquals(1, interceptor.getCount());
        assertEquals(2, handler.getCount());
    }

    /**
     * Verifies an invocation with a target interceptor, request handler, and response handler
     */
    @SuppressWarnings("unchecked")
    public void testTargetInterceptorTargetRequestResponseHandler() throws Exception {
        ConnectorImpl connector = new ConnectorImpl();
        MockSyncInterceptor interceptor = new MockSyncInterceptor();
        List<Interceptor> interceptors = new ArrayList<Interceptor>();
        interceptors.add(interceptor);
        MockHandler handler = new MockHandler();
        final Message msg = new MessageImpl();
        Mock terminatingHandler = mock(MessageHandler.class);
        terminatingHandler.expects(atLeastOnce()).method("processMessage").with(eq(msg)).will(new Stub(){
            public Object invoke(Invocation invocation) throws Throwable {
                return true;
            }

            public StringBuffer describeTo(StringBuffer stringBuffer) {
                return null;
            }
        });
        List<MessageHandler> handlers = new ArrayList<MessageHandler>();
        handlers.add(handler);
        handlers.add((MessageHandler)terminatingHandler.proxy());

        OutboundInvocationChain sourceChain = setupChain(null, null, handlers);
        OutboundInvocationChain targetChain = setupChain(interceptors, handlers, null);

        assertEquals(0, interceptor.getCount());
        assertEquals(0, handler.getCount());
        connector.connect(sourceChain, targetChain, null);
        sourceChain.build();
        targetChain.build();
        msg.setTargetInvoker(sourceChain.getTargetInvoker());
        assertEquals(msg, sourceChain.getHeadInterceptor().invoke(msg));
        assertEquals(1, interceptor.getCount());
        assertEquals(2, handler.getCount());
    }

    /**
     * Verifies an invocation with a source request handler and response handler
     */
    @SuppressWarnings("unchecked")
    public void testSourceRequestResponseHandler() throws Exception {
        ConnectorImpl connector = new ConnectorImpl();
        MockHandler handler = new MockHandler();
        List<MessageHandler> handlers = new ArrayList<MessageHandler>();
        handlers.add(handler);

        OutboundInvocationChain sourceChain = setupChain(null, handlers, handlers);
        OutboundInvocationChain targetChain = setupTerminatingChain(null, null, null);
        Message msg = new MessageImpl();
        Mock mock = mock(TargetInvoker.class);
        mock.expects(once()).method("invoke").with(eq(msg)).will(returnValue(msg));
        TargetInvoker invoker = (TargetInvoker) mock.proxy();
        assertEquals(0, handler.getCount());
        connector.connect(sourceChain, targetChain, invoker);
        sourceChain.build();
        targetChain.build();
        msg.setTargetInvoker(sourceChain.getTargetInvoker());
        assertEquals(msg, sourceChain.getHeadInterceptor().invoke(msg));
        assertEquals(2, handler.getCount());
    }

    /**
     * Verifies an invocation with a single source request handler
     */
    @SuppressWarnings("unchecked")
    public void testSourceRequestHandler() throws Exception {
        ConnectorImpl connector = new ConnectorImpl();
        MockHandler handler = new MockHandler();
        List<MessageHandler> handlers = new ArrayList<MessageHandler>();
        handlers.add(handler);

        OutboundInvocationChain sourceChain = setupChain(null, handlers, null);
        OutboundInvocationChain targetChain = setupTerminatingChain(null, null, null);
        Message msg = new MessageImpl();
        Mock mock = mock(TargetInvoker.class);
        mock.expects(once()).method("invoke").with(eq(msg)).will(returnValue(msg));
        TargetInvoker invoker = (TargetInvoker) mock.proxy();
        assertEquals(0, handler.getCount());
        connector.connect(sourceChain, targetChain, invoker);
        sourceChain.build();
        targetChain.build();
        msg.setTargetInvoker(sourceChain.getTargetInvoker());
        assertEquals(msg, sourceChain.getHeadInterceptor().invoke(msg));
        assertEquals(1, handler.getCount());
    }



    public OutboundInvocationChain setupChain(List<Interceptor> interceptors,
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
        return chain;
    }

    public OutboundInvocationChain setupTerminatingChain(List<Interceptor> interceptors,
                                                          List<MessageHandler> requestHandlers,
                                                          List<MessageHandler> responseHandlers) {
        OutboundInvocationChain chain = setupChain(interceptors, requestHandlers, responseHandlers);
        chain.addInterceptor(new InvokerInterceptor()); // add tail interceptor
        return chain;

    }

}
