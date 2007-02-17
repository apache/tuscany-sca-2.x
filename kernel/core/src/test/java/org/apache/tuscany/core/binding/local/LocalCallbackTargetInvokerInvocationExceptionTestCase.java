package org.apache.tuscany.core.binding.local;

import java.lang.reflect.Type;

import org.apache.tuscany.spi.model.Operation;
import org.apache.tuscany.spi.wire.Interceptor;
import org.apache.tuscany.spi.wire.InvocationChain;
import org.apache.tuscany.spi.wire.Message;
import org.apache.tuscany.spi.wire.MessageImpl;
import org.apache.tuscany.spi.wire.Wire;

import junit.framework.TestCase;
import org.apache.tuscany.core.wire.InvocationChainImpl;
import org.apache.tuscany.core.wire.WireImpl;

/**
 * @version $Rev$ $Date$
 */
public class LocalCallbackTargetInvokerInvocationExceptionTestCase extends TestCase {

    /**
     * Verfies an InvocationTargetException thrown when invoking the target is propagated to the client correctly and
     * the originating error is unwrapped
     */
    public void testThrowableTargetInvocation() throws Exception {
        Operation<Type> operation = new Operation<Type>("echo", null, null, null);
        Interceptor head = new ErrorInterceptor();
        InvocationChain chain = new InvocationChainImpl(operation);
        chain.addInterceptor(head);
        Wire wire = new WireImpl();
        wire.addCallbackInvocationChain(operation, chain);
        LocalCallbackTargetInvoker invoker = new LocalCallbackTargetInvoker(operation, wire);
        Message msg = new MessageImpl();
        msg.setBody("foo");
        Message response = invoker.invoke(msg);
        assertTrue(response.isFault());
        Object body = response.getBody();
        assertTrue(SomeException.class.equals(body.getClass()));
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
