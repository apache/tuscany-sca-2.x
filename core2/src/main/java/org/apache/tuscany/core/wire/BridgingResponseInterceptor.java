package org.apache.tuscany.core.wire;

import org.apache.tuscany.spi.wire.Interceptor;
import org.apache.tuscany.spi.wire.Message;
import org.apache.tuscany.spi.wire.MessageChannel;

/**
 * Bridges between two {@link org.apache.tuscany.spi.wire.ServiceInvocationChain}s where the destination chain
 * has an interceptor and response handler chain but no request handlers
 * 
 * @version $$Rev$$ $$Date$$
 */
public class BridgingResponseInterceptor implements Interceptor {
    private Interceptor next;
    private MessageChannel responseChannel;

    public BridgingResponseInterceptor(Interceptor next) {
        this.next = next;
    }

    public BridgingResponseInterceptor(Interceptor next, MessageChannel responseChannel) {
        this.next = next;
        this.responseChannel = responseChannel;
    }

    public Message invoke(Message msg) {
        responseChannel.send (next.invoke(msg));
        return msg;
    }

    public Interceptor getNext() {
        return next;
    }

    public void setNext(Interceptor next) {
        this.next = next;
    }

    public MessageChannel getResponseChannel() {
        return responseChannel;
    }

    public void setResponseChannel(MessageChannel responseChannel) {
        this.responseChannel = responseChannel;
    }

    public boolean isOptimizable() {
        return true;
    }
}
