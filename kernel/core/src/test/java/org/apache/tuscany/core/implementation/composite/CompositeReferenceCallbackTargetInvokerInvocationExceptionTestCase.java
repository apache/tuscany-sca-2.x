package org.apache.tuscany.core.implementation.composite;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import org.apache.tuscany.spi.model.Operation;
import org.apache.tuscany.spi.wire.InboundWire;
import org.apache.tuscany.spi.wire.Interceptor;
import org.apache.tuscany.spi.wire.Message;
import org.apache.tuscany.spi.wire.MessageImpl;
import org.apache.tuscany.spi.wire.OutboundInvocationChain;

import junit.framework.TestCase;
import org.easymock.EasyMock;

/**
 * @version $Rev$ $Date$
 */
public class CompositeReferenceCallbackTargetInvokerInvocationExceptionTestCase extends TestCase {
    private InboundWire wire;
    private Message message;
    private OutboundInvocationChain chain;
    private CompositeReferenceCallbackTargetInvoker invoker;

    /**
     * Verfies an InvocationTargetException thrown when invoking the target is propagated to the client correctly and
     * the originating error is unwrapped
     */
    public void testThrowableTargetInvocation() throws Exception {
        Message response = invoker.invoke(message);
        assertTrue(response.isFault());
        Object body = response.getBody();
        assertTrue(SomeException.class.equals(body.getClass()));
        EasyMock.verify(wire);
        EasyMock.verify(chain);
    }

    protected void setUp() throws Exception {
        super.setUp();
        Object id = new Object();
        Object corrId = new Object();
        Object targetAddress = new Object();
        message = new MessageImpl();
        message.setMessageId(id);
        message.setCorrelationId(corrId);
        message.setBody("foo");
        Message response = new MessageImpl();
        response.setBody("response");
        Operation<Type> operation = new Operation<Type>("echo", null, null, null);
        Interceptor head = new ErrorInterceptor();
        chain = EasyMock.createMock(OutboundInvocationChain.class);
        EasyMock.expect(chain.getTargetInvoker()).andReturn(null);
        EasyMock.expect(chain.getHeadInterceptor()).andReturn(head);
        EasyMock.replay(chain);
        Map<Operation<?>, OutboundInvocationChain> chains = new HashMap<Operation<?>, OutboundInvocationChain>();
        chains.put(operation, chain);
        wire = EasyMock.createMock(InboundWire.class);
        EasyMock.expect(wire.retrieveMapping(corrId)).andReturn(targetAddress);
        EasyMock.expect(wire.getSourceCallbackInvocationChains(targetAddress)).andReturn(chains);
        EasyMock.expect(wire.getContainer()).andReturn(null);
        EasyMock.replay(wire);
        invoker = new CompositeReferenceCallbackTargetInvoker(operation, wire);
    }

    private class SomeException extends Exception {

    }

    private class ErrorInterceptor implements Interceptor {

        public Message invoke(Message msg) {
            msg.setBodyWithFault(new SomeException());
            return msg;
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


}
