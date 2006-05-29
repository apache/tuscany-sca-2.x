package org.apache.tuscany.core.wire;

import org.apache.tuscany.spi.wire.Interceptor;
import org.apache.tuscany.spi.wire.Message;
import org.apache.tuscany.spi.wire.MessageChannel;

/**
 * Bridges between interceptors in two {@link org.apache.tuscany.spi.wire.InboundInvocationChain}s
 *  
 * @version $$Rev$$ $$Date$$
 */
public class BridgingInterceptor implements Interceptor {
    private Interceptor next;
    private MessageChannel responseChannel;

    public BridgingInterceptor() {
    }

    public BridgingInterceptor(Interceptor next, MessageChannel responseChannel) {
        this.next = next;
        this.responseChannel = responseChannel;
    }

    public BridgingInterceptor(Interceptor next) {
        this.next = next;
    }

    public Message invoke(Message msg) {
        Message response = next.invoke(msg);
        if (responseChannel != null){
            responseChannel.send(response);
        }
        return response;
    }

    public Interceptor getNext() {
        return next;
    }

    public void setNext(Interceptor next) {
        this.next = next;
    }

    public boolean isOptimizable() {
        return true;
    }
}
