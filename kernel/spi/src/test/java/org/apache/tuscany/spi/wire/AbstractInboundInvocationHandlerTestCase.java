package org.apache.tuscany.spi.wire;

import java.lang.reflect.Array;

import junit.framework.TestCase;
import org.easymock.EasyMock;

/**
 * @version $Rev$ $Date$
 */
public class AbstractInboundInvocationHandlerTestCase extends TestCase {

    public void testInvocation() throws Throwable {
        InvocationHandler handler = new InvocationHandler();
        Interceptor interceptor = new MockInterceptor();
        TargetInvoker invoker = EasyMock.createMock(TargetInvoker.class);
        EasyMock.replay(invoker);
        InboundInvocationChain chain = EasyMock.createMock(InboundInvocationChain.class);
        EasyMock.expect(chain.getHeadInterceptor()).andReturn(interceptor);
        EasyMock.replay(chain);
        Object resp = handler.invoke(chain, invoker, new String[]{"foo"});
        assertEquals("response", resp);
    }


    private class InvocationHandler extends AbstractInboundInvocationHandler {

    }

    private class MockInterceptor implements Interceptor {

        public Message invoke(Message msg) {
            assertNotNull(msg.getCorrelationId());
            assertNotNull(msg.getTargetInvoker());
            assertNotNull(msg.getMessageId());
            assertEquals("foo", Array.get(msg.getBody(), 0));
            msg.setBody("response");
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