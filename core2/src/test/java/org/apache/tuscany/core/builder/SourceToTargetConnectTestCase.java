package org.apache.tuscany.core.builder;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.apache.tuscany.core.mock.component.SimpleTarget;
import org.apache.tuscany.core.mock.wire.MockHandler;
import org.apache.tuscany.core.mock.wire.MockSyncInterceptor;
import org.apache.tuscany.core.wire.InvokerInterceptor;
import org.apache.tuscany.core.wire.ReferenceInvocationChainImpl;
import org.apache.tuscany.core.wire.ServiceInvocationChainImpl;
import org.apache.tuscany.spi.wire.Interceptor;
import org.apache.tuscany.spi.wire.Message;
import org.apache.tuscany.spi.wire.MessageHandler;
import org.apache.tuscany.spi.wire.MessageImpl;
import org.apache.tuscany.spi.wire.ReferenceInvocationChain;
import org.apache.tuscany.spi.wire.ServiceInvocationChain;
import org.apache.tuscany.spi.wire.TargetInvoker;
import org.jmock.Mock;
import org.jmock.MockObjectTestCase;

/**
 * Verifies connection strategies between {@link org.apache.tuscany.spi.wire.ReferenceInvocationChain}s and
 * {@link org.apache.tuscany.spi.wire.ServiceInvocationChain}s
 *
 * @version $$Rev$$ $$Date$$
 */
public class SourceToTargetConnectTestCase extends MockObjectTestCase {

    @SuppressWarnings("unchecked")
    public void testNoInterceptorsNoHandlers() throws Exception {
        ConnectorImpl connector = new ConnectorImpl();
        ServiceInvocationChain serviceChain = setupTarget(null, null, null);
        ReferenceInvocationChain referenceChain = setupSource(null, null, null);
        String[] val = new String[]{"foo"};
        Mock mock = mock(TargetInvoker.class);
        mock.expects(once()).method("invokeTarget").with(eq(val)).will(returnValue(val));
        TargetInvoker invoker = (TargetInvoker) mock.proxy();
        connector.connect(referenceChain, serviceChain, invoker);
        serviceChain.build();
        assertEquals(val, referenceChain.getTargetInvoker().invokeTarget(val));
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

        ReferenceInvocationChain referenceChain = setupSource(interceptors, null, null);
        ServiceInvocationChain serviceChain = setupTarget(null, null, null);
        Message msg = new MessageImpl();
        Mock mock = mock(TargetInvoker.class);
        mock.expects(once()).method("invoke").with(eq(msg)).will(returnValue(msg));
        TargetInvoker invoker = (TargetInvoker) mock.proxy();
        assertEquals(0, interceptor.getCount());
        connector.connect(referenceChain, serviceChain, invoker);
        serviceChain.build();
        msg.setTargetInvoker(referenceChain.getTargetInvoker());
        assertEquals(msg, referenceChain.getHeadInterceptor().invoke(msg));
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

        ReferenceInvocationChain referenceChain = setupSource(null, null, null);
        ServiceInvocationChain serviceChain = setupTarget(interceptors, null, null);
        Message msg = new MessageImpl();
        Mock mock = mock(TargetInvoker.class);
        mock.expects(once()).method("invoke").with(eq(msg)).will(returnValue(msg));
        TargetInvoker invoker = (TargetInvoker) mock.proxy();
        assertEquals(0, interceptor.getCount());
        connector.connect(referenceChain, serviceChain, invoker);
        serviceChain.build();
        msg.setTargetInvoker(referenceChain.getTargetInvoker());
        assertEquals(msg, referenceChain.getHeadInterceptor().invoke(msg));
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

        ReferenceInvocationChain referenceChain = setupSource(sourceInterceptors, null, null);
        ServiceInvocationChain serviceChain = setupTarget(targetInterceptors, null, null);
        Message msg = new MessageImpl();
        Mock mock = mock(TargetInvoker.class);
        mock.expects(once()).method("invoke").with(eq(msg)).will(returnValue(msg));
        TargetInvoker invoker = (TargetInvoker) mock.proxy();
        assertEquals(0, sourceInterceptor.getCount());
        assertEquals(0, targetInterceptor.getCount());
        connector.connect(referenceChain, serviceChain, invoker);
        serviceChain.build();
        msg.setTargetInvoker(referenceChain.getTargetInvoker());
        assertEquals(msg, referenceChain.getHeadInterceptor().invoke(msg));
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

        ReferenceInvocationChain referenceChain = setupSource(interceptors, handlers, null);
        ServiceInvocationChain serviceChain = setupTarget(null, null, null);
        Message msg = new MessageImpl();
        Mock mock = mock(TargetInvoker.class);
        mock.expects(once()).method("invoke").with(eq(msg)).will(returnValue(msg));
        TargetInvoker invoker = (TargetInvoker) mock.proxy();
        assertEquals(0, interceptor.getCount());
        assertEquals(0, handler.getCount());
        connector.connect(referenceChain, serviceChain, invoker);
        serviceChain.build();
        msg.setTargetInvoker(referenceChain.getTargetInvoker());
        assertEquals(msg, referenceChain.getHeadInterceptor().invoke(msg));
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
        List<MessageHandler> handlers = new ArrayList<MessageHandler>();
        handlers.add(handler);

        ReferenceInvocationChain referenceChain = setupSource(null, null, null);
        ServiceInvocationChain serviceChain = setupTarget(interceptors, handlers, null);
        Message msg = new MessageImpl();
        Mock mock = mock(TargetInvoker.class);
        mock.expects(once()).method("invoke").with(eq(msg)).will(returnValue(msg));
        TargetInvoker invoker = (TargetInvoker) mock.proxy();
        assertEquals(0, interceptor.getCount());
        assertEquals(0, handler.getCount());
        connector.connect(referenceChain, serviceChain, invoker);
        serviceChain.build();
        msg.setTargetInvoker(referenceChain.getTargetInvoker());
        assertEquals(msg, referenceChain.getHeadInterceptor().invoke(msg));
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

        ReferenceInvocationChain referenceChain = setupSource(interceptors, null, handlers);
        ServiceInvocationChain serviceChain = setupTarget(null, null, null);
        Message msg = new MessageImpl();
        Mock mock = mock(TargetInvoker.class);
        mock.expects(once()).method("invoke").with(eq(msg)).will(returnValue(msg));
        TargetInvoker invoker = (TargetInvoker) mock.proxy();
        assertEquals(0, interceptor.getCount());
        assertEquals(0, handler.getCount());
        connector.connect(referenceChain, serviceChain, invoker);
        serviceChain.build();
        msg.setTargetInvoker(referenceChain.getTargetInvoker());
        assertEquals(msg, referenceChain.getHeadInterceptor().invoke(msg));
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
        List<MessageHandler> handlers = new ArrayList<MessageHandler>();
        handlers.add(handler);

        ReferenceInvocationChain referenceChain = setupSource(null, null, null);
        ServiceInvocationChain serviceChain = setupTarget(interceptors, null, handlers);
        Message msg = new MessageImpl();
        Mock mock = mock(TargetInvoker.class);
        mock.expects(once()).method("invoke").with(eq(msg)).will(returnValue(msg));
        TargetInvoker invoker = (TargetInvoker) mock.proxy();
        assertEquals(0, interceptor.getCount());
        assertEquals(0, handler.getCount());
        connector.connect(referenceChain, serviceChain, invoker);
        serviceChain.build();
        msg.setTargetInvoker(referenceChain.getTargetInvoker());
        assertEquals(msg, referenceChain.getHeadInterceptor().invoke(msg));
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

        ReferenceInvocationChain referenceChain = setupSource(interceptors, handlers, handlers);
        ServiceInvocationChain serviceChain = setupTarget(null, null, null);
        Message msg = new MessageImpl();
        Mock mock = mock(TargetInvoker.class);
        mock.expects(once()).method("invoke").with(eq(msg)).will(returnValue(msg));
        TargetInvoker invoker = (TargetInvoker) mock.proxy();
        assertEquals(0, interceptor.getCount());
        assertEquals(0, handler.getCount());
        connector.connect(referenceChain, serviceChain, invoker);
        serviceChain.build();
        msg.setTargetInvoker(referenceChain.getTargetInvoker());
        assertEquals(msg, referenceChain.getHeadInterceptor().invoke(msg));
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
        List<MessageHandler> handlers = new ArrayList<MessageHandler>();
        handlers.add(handler);

        ReferenceInvocationChain referenceChain = setupSource(null, null, handlers);
        ServiceInvocationChain serviceChain = setupTarget(interceptors, handlers, null);
        Message msg = new MessageImpl();
        Mock mock = mock(TargetInvoker.class);
        mock.expects(once()).method("invoke").with(eq(msg)).will(returnValue(msg));
        TargetInvoker invoker = (TargetInvoker) mock.proxy();
        assertEquals(0, interceptor.getCount());
        assertEquals(0, handler.getCount());
        connector.connect(referenceChain, serviceChain, invoker);
        serviceChain.build();
        msg.setTargetInvoker(referenceChain.getTargetInvoker());
        assertEquals(msg, referenceChain.getHeadInterceptor().invoke(msg));
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

        ReferenceInvocationChain referenceChain = setupSource(null, handlers, handlers);
        ServiceInvocationChain serviceChain = setupTarget(null, null, null);
        Message msg = new MessageImpl();
        Mock mock = mock(TargetInvoker.class);
        mock.expects(once()).method("invoke").with(eq(msg)).will(returnValue(msg));
        TargetInvoker invoker = (TargetInvoker) mock.proxy();
        assertEquals(0, handler.getCount());
        connector.connect(referenceChain, serviceChain, invoker);
        serviceChain.build();
        msg.setTargetInvoker(referenceChain.getTargetInvoker());
        assertEquals(msg, referenceChain.getHeadInterceptor().invoke(msg));
        assertEquals(2, handler.getCount());
    }

    /**
     * Verifies an invocation with a target request handler and response handler
     */
    @SuppressWarnings("unchecked")
    public void testTargetRequestResponseHandler() throws Exception {
        ConnectorImpl connector = new ConnectorImpl();
        MockHandler handler = new MockHandler();
        List<MessageHandler> handlers = new ArrayList<MessageHandler>();
        handlers.add(handler);

        ReferenceInvocationChain referenceChain = setupSource(null, null, null);
        ServiceInvocationChain serviceChain = setupTarget(null, handlers, handlers);
        Message msg = new MessageImpl();
        Mock mock = mock(TargetInvoker.class);
        mock.expects(once()).method("invoke").with(eq(msg)).will(returnValue(msg));
        TargetInvoker invoker = (TargetInvoker) mock.proxy();
        assertEquals(0, handler.getCount());
        connector.connect(referenceChain, serviceChain, invoker);
        serviceChain.build();
        msg.setTargetInvoker(referenceChain.getTargetInvoker());
        assertEquals(msg, referenceChain.getHeadInterceptor().invoke(msg));
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

        ReferenceInvocationChain referenceChain = setupSource(null, handlers, null);
        ServiceInvocationChain serviceChain = setupTarget(null, null, null);
        Message msg = new MessageImpl();
        Mock mock = mock(TargetInvoker.class);
        mock.expects(once()).method("invoke").with(eq(msg)).will(returnValue(msg));
        TargetInvoker invoker = (TargetInvoker) mock.proxy();
        assertEquals(0, handler.getCount());
        connector.connect(referenceChain, serviceChain, invoker);
        serviceChain.build();
        msg.setTargetInvoker(referenceChain.getTargetInvoker());
        assertEquals(msg, referenceChain.getHeadInterceptor().invoke(msg));
        assertEquals(1, handler.getCount());
    }

    /**
     * Verifies an invocation with a single target request handler
     */
    @SuppressWarnings("unchecked")
    public void testTargetRequestHandler() throws Exception {
        ConnectorImpl connector = new ConnectorImpl();
        MockHandler handler = new MockHandler();
        List<MessageHandler> handlers = new ArrayList<MessageHandler>();
        handlers.add(handler);

        ReferenceInvocationChain referenceChain = setupSource(null, null, null);
        ServiceInvocationChain serviceChain = setupTarget(null, handlers, null);
        Message msg = new MessageImpl();
        Mock mock = mock(TargetInvoker.class);
        mock.expects(once()).method("invoke").with(eq(msg)).will(returnValue(msg));
        TargetInvoker invoker = (TargetInvoker) mock.proxy();
        assertEquals(0, handler.getCount());
        connector.connect(referenceChain, serviceChain, invoker);
        serviceChain.build();
        msg.setTargetInvoker(referenceChain.getTargetInvoker());
        assertEquals(msg, referenceChain.getHeadInterceptor().invoke(msg));
        assertEquals(1, handler.getCount());
    }

    /**
     * Verifies an invocation with a single source response handler
     */
    @SuppressWarnings("unchecked")
    public void testSourceResponseHandler() throws Exception {
        ConnectorImpl connector = new ConnectorImpl();
        MockHandler handler = new MockHandler();
        List<MessageHandler> handlers = new ArrayList<MessageHandler>();
        handlers.add(handler);

        ReferenceInvocationChain referenceChain = setupSource(null, null, handlers);
        ServiceInvocationChain serviceChain = setupTarget(null, null, null);
        Message msg = new MessageImpl();
        Mock mock = mock(TargetInvoker.class);
        mock.expects(once()).method("invoke").with(eq(msg)).will(returnValue(msg));
        TargetInvoker invoker = (TargetInvoker) mock.proxy();
        assertEquals(0, handler.getCount());
        connector.connect(referenceChain, serviceChain, invoker);
        serviceChain.build();
        msg.setTargetInvoker(referenceChain.getTargetInvoker());
        assertEquals(msg, referenceChain.getHeadInterceptor().invoke(msg));
        assertEquals(1, handler.getCount());
    }

    /**
     * Verifies an invocation with a single target response handler
     */
    @SuppressWarnings("unchecked")
    public void testTargetResponseHandler() throws Exception {
        ConnectorImpl connector = new ConnectorImpl();
        MockHandler handler = new MockHandler();
        List<MessageHandler> handlers = new ArrayList<MessageHandler>();
        handlers.add(handler);

        ReferenceInvocationChain referenceChain = setupSource(null, null, null);
        ServiceInvocationChain serviceChain = setupTarget(null, null, handlers);
        Message msg = new MessageImpl();
        Mock mock = mock(TargetInvoker.class);
        mock.expects(once()).method("invoke").with(eq(msg)).will(returnValue(msg));
        TargetInvoker invoker = (TargetInvoker) mock.proxy();
        assertEquals(0, handler.getCount());
        connector.connect(referenceChain, serviceChain, invoker);
        serviceChain.build();
        msg.setTargetInvoker(referenceChain.getTargetInvoker());
        assertEquals(msg, referenceChain.getHeadInterceptor().invoke(msg));
        assertEquals(1, handler.getCount());
    }


    public ServiceInvocationChain setupTarget(List<Interceptor> interceptors,
                                             List<MessageHandler> requestHandlers,
                                             List<MessageHandler> responseHandlers) {

        Method echo;
        try {
            echo = SimpleTarget.class.getMethod("echo", String.class);
        } catch (NoSuchMethodException e) {
            throw new AssertionError();
        }
        ServiceInvocationChainImpl chain = new ServiceInvocationChainImpl(echo);
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

    public ReferenceInvocationChain setupSource(List<Interceptor> interceptors,
                                             List<MessageHandler> requestHandlers,
                                             List<MessageHandler> responseHandlers) {

        Method echo;
        try {
            echo = SimpleTarget.class.getMethod("echo", String.class);
        } catch (NoSuchMethodException e) {
            throw new AssertionError();
        }
        ReferenceInvocationChainImpl chain = new ReferenceInvocationChainImpl(echo);
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

}
