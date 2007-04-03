package org.apache.tuscany.sca.test;

import junit.framework.TestCase;

import org.apache.tuscany.api.SCARuntime;
import org.osoa.sca.CurrentCompositeContext;

public class ConversationsTest extends TestCase  {

    private ConversationsClient aConversationsClient;
	
    public void testConversations() {
    	aConversationsClient.run(); 
    }

    protected void setUp() throws Exception {
    	SCARuntime.start("ConversationsTest.composite");
    	
       aConversationsClient = CurrentCompositeContext.getContext().locateService(ConversationsClient.class, "ConversationsClient/org.apache.tuscany.sca.test.ConversationsClient");
 
    }
    
    protected void tearDown() throws Exception {
    	SCARuntime.stop();
    }
   
}
