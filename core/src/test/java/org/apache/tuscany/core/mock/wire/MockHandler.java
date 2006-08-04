/**
 * 
 */
package org.apache.tuscany.core.mock.wire;

import org.apache.tuscany.spi.wire.Message;
import org.apache.tuscany.spi.wire.MessageHandler;

/**
 *
 */
public class MockHandler implements MessageHandler {

    private int count;

    public boolean processMessage(Message message) {
        count++;
        return true;
    }

    public int getCount() {
        return count;
    }

    public boolean isOptimizable() {
        return true;
    }

}
