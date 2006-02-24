/**
 * 
 */
package org.apache.tuscany.container.js.invocation.mock;

import org.apache.tuscany.core.invocation.MessageHandler;
import org.apache.tuscany.core.message.Message;

/**
 * A test handler
 * 
 * @version $Rev$ $Date$
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
