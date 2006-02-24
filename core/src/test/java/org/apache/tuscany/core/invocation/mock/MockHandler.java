/**
 * 
 */
package org.apache.tuscany.core.invocation.mock;

import org.apache.tuscany.core.invocation.MessageHandler;
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
