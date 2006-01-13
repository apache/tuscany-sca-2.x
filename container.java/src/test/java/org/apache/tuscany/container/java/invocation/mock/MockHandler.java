/**
 * 
 */
package org.apache.tuscany.container.java.invocation.mock;

import org.apache.tuscany.core.message.Message;
import org.apache.tuscany.core.message.handler.MessageHandler;

/**
 *
 */
public class MockHandler implements MessageHandler {

    public boolean processMessage(Message message) {
        System.out.println("Invoking handler");
        return true;
    }
}
