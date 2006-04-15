/**
 * 
 */
package org.apache.tuscany.core.wire.mock;

import org.apache.tuscany.core.wire.MessageHandler;
import org.apache.tuscany.core.message.Message;

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
