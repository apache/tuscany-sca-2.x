package org.apache.tuscany.core.wire;

import org.apache.tuscany.spi.wire.MessageHandler;
import org.apache.tuscany.spi.wire.Message;

/**
 * Bridges between handlers in two {@link org.apache.tuscany.spi.wire.InboundInvocationChain}s
 * 
  * @version $$Rev$$ $$Date$$
 */
public class BridgingHandler implements MessageHandler {
    private MessageHandler next;

    public BridgingHandler(MessageHandler next) {
        this.next = next;
    }

    public BridgingHandler() {
    }

    public boolean processMessage(Message message) {
        return next.processMessage(message);
    }

    public boolean isOptimizable() {
        return true;
    }

    public void setNext(MessageHandler next) {
        this.next = next;
    }

    public MessageHandler getNext() {
        return next;
    }
}
