/**
 * 
 */
package org.apache.tuscany.core.async.wire.mock;

import org.apache.tuscany.spi.wire.MessageHandler;
import org.apache.tuscany.spi.wire.Message;

/**
 *
 */
public class MockHandler implements MessageHandler {

    private int count =0;

    public boolean processMessage(Message message) {
        //System.out.println("Invoking handler");
        count++;
        return true;
    }

    public int getCount(){
        return count;
    }
}
