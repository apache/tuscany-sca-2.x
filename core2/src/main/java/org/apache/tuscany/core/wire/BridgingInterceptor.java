package org.apache.tuscany.core.wire;

import org.apache.tuscany.spi.wire.Interceptor;
import org.apache.tuscany.spi.wire.Message;

/**
 * @version $$Rev$$ $$Date$$
 */
public class BridgingInterceptor implements Interceptor {
    private Interceptor next;

    public BridgingInterceptor(Interceptor next) {
        this.next = next;
    }


    public Message invoke(Message msg) {
        return next.invoke(msg);
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
