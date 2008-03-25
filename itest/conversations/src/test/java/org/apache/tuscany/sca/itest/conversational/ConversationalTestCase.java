/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.    
 */

package org.apache.tuscany.sca.itest.conversational;

import junit.framework.Assert;

import org.apache.tuscany.sca.host.embedded.SCADomain;
import org.apache.tuscany.sca.itest.conversational.impl.ConversationalClientStatefulImpl;
import org.apache.tuscany.sca.itest.conversational.impl.ConversationalClientStatefulNonConversationalCallbackImpl;
import org.apache.tuscany.sca.itest.conversational.impl.ConversationalClientStatelessImpl;
import org.apache.tuscany.sca.itest.conversational.impl.ConversationalServiceRequestImpl;
import org.apache.tuscany.sca.itest.conversational.impl.ConversationalServiceStatefulImpl;
import org.apache.tuscany.sca.itest.conversational.impl.ConversationalServiceStatefulNonConversationalCallbackImpl;
import org.apache.tuscany.sca.itest.conversational.impl.ConversationalServiceStatelessImpl;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class ConversationalTestCase {

    private static SCADomain domain;
    private static ConversationalClient conversationalStatelessClientStatelessService;
    private static ConversationalClient conversationalStatelessClientStatefulService;
    private static ConversationalClient conversationalStatefulClientStatelessService;
    private static ConversationalClient conversationalStatefulClientStatefulService; 
    private static ConversationalClient conversationalStatelessClientRequestService;
    private static ConversationalClient conversationalStatefulClientNonConversationalCallbackStatelessService;    

    @BeforeClass
    public static void setUp() throws Exception {
    	try {
	        domain = SCADomain.newInstance("conversational.composite");
	
	        conversationalStatelessClientStatelessService = domain.getService(ConversationalClient.class,
	                                                                          "ConversationalStatelessClientStatelessService");
	
	        conversationalStatelessClientStatefulService  = domain.getService(ConversationalClient.class,
	                                                                          "ConversationalStatelessClientStatefulService");
	
	        conversationalStatefulClientStatelessService  = domain.getService(ConversationalClient.class,
	                                                                          "ConversationalStatefulClientStatelessService");
	
	        conversationalStatefulClientStatefulService   = domain.getService(ConversationalClient.class,
	                                                                          "ConversationalStatefulClientStatefulService");
	        conversationalStatelessClientRequestService    = domain.getService(ConversationalClient.class,
	                                                                          "ConversationalStatelessClientRequestService");
	        conversationalStatefulClientNonConversationalCallbackStatelessService    = domain.getService(ConversationalClient.class,
	                                                                          "ConversationalStatefulClientNonConversationalCallbackStatefulService");
        
    	} catch(Exception ex) {
    		System.err.println(ex.toString());
    	}
               
    }

    @AfterClass
    public static void tearDown() throws Exception {
        domain.close();
    }
    
    private void resetCallStack() {
        
        // reset the place where we record the sequence of calls passing
        // through each component instance
        ConversationalServiceStatelessImpl.calls = new StringBuffer();
        ConversationalServiceStatefulImpl.calls  = new StringBuffer();
        ConversationalClientStatelessImpl.calls  = new StringBuffer();         
        ConversationalClientStatefulImpl.calls   = new StringBuffer();    
        ConversationalClientStatefulNonConversationalCallbackImpl.calls = new StringBuffer();
        ConversationalServiceStatefulNonConversationalCallbackImpl.calls = new StringBuffer();
        
    }

    // stateless client stateful service tests
    // =======================================
    @Test
    public void testStatelessStatefulConversationFromInjectedReference() {
        int count = conversationalStatelessClientStatefulService.runConversationFromInjectedReference();
        Assert.assertEquals(2, count);
    } 
    
    @Test
    public void testStatelessStatefulConversationFromInjectedReference2() {
        int count = conversationalStatelessClientStatefulService.runConversationFromInjectedReference2();
        Assert.assertEquals(2, count);
    }     
    
    @Test
    public void testStatelessStatefulConversationFromServiceReference() {
        int count = conversationalStatelessClientStatefulService.runConversationFromServiceReference();
        Assert.assertEquals(2, count);
    }          
   
    @Test
    public void testStatelessStatefulConversationWithUserDefinedConversationId() {
        int count = conversationalStatelessClientStatefulService.runConversationWithUserDefinedConversationId();
        Assert.assertEquals(2, count);
    }   
    
    @Test
    public void testStatelessStatefulConversationCheckUserDefinedConversationId() {
        String conversationId = conversationalStatelessClientStatefulService.runConversationCheckUserDefinedConversationId();
        Assert.assertEquals("MyConversation2", conversationId);
    } 
    
    @Test
    public void testStatelessStatefulConversationCheckingScope() {
        resetCallStack();
        conversationalStatelessClientStatefulService.runConversationCheckingScope();
        Assert.assertEquals("init,initializeCount,incrementCount,retrieveCount,endConversation,destroy,", 
                            ConversationalServiceStatefulImpl.calls.toString());
    }     

    @Test
    public void testStatelessStatefulConversationWithCallback() {
        resetCallStack();
        int count = conversationalStatelessClientStatefulService.runConversationWithCallback();
        Assert.assertEquals(0, count);
               
        Assert.assertEquals("init,runConversationWithCallback,init,initializeCount,destroy,init,incrementCount,destroy,init,retrieveCount,destroy,init,endConversation,destroy,destroy,", 
                            ConversationalClientStatelessImpl.calls.toString());        
    }  
    
    //@Test
    public void testStatelessStatefulConversationHavingPassedReference() {
        int count = conversationalStatelessClientStatefulService.runConversationHavingPassedReference();
        Assert.assertEquals(3, count);
    }    
  
    @Test
    public void testStatelessStatefulConversationBusinessException() {
        String message = conversationalStatelessClientStatefulService.runConversationBusinessException();
        Assert.assertEquals("Business Exception", message);
    }     
    
    @Test
    public void testStatelessStatefulConversationBusinessExceptionCallback() {
        String message = conversationalStatelessClientStatefulService.runConversationBusinessExceptionCallback();
        Assert.assertEquals("Business Exception", message);
    }  
    
    @Test
    public void testStatelessStatefulConversationCallingEndedConversation() {
        int count = conversationalStatelessClientStatefulService.runConversationCallingEndedConversation();
        Assert.assertEquals(0, count);
    }     
    
    @Test
    public void testStatelessStatefulConversationCallingEndedConversationCallback() {
        int count = conversationalStatelessClientStatefulService.runConversationCallingEndedConversationCallback();
        Assert.assertEquals(0, count);
    }   
    
    @Test
    public void testStatelessStatefulConversationCallingEndedConversationCheckConversationId() {
        String id = conversationalStatelessClientStatefulService.runConversationCallingEndedConversationCheckConversationId();
        Assert.assertEquals(null, id);
    }     
    
    //@Test
    public void testStatelessStatefulConversationCallingEndedConversationCallbackCheckConversationId() {
        String id = conversationalStatelessClientStatefulService.runConversationCallingEndedConversationCallbackCheckConversationId();
        Assert.assertEquals(null, id);
    }    

    // stateless client stateless service tests
    // ========================================
    @Test
    public void testStatelessStatelessConversationFromInjectedReference() {
        int count = conversationalStatelessClientStatelessService.runConversationFromInjectedReference();
        Assert.assertEquals(2, count);
    } 

    @Test
    public void testStatelessStatelessConversationFromInjectedReference2() {
        int count = conversationalStatelessClientStatelessService.runConversationFromInjectedReference2();
        Assert.assertEquals(2, count);
    }     
    
    @Test
    public void testStatelessStatelessConversationFromServiceReference() {
        int count = conversationalStatelessClientStatelessService.runConversationFromServiceReference();
        Assert.assertEquals(2, count);
    }    
    
    @Test
    public void testStatelessStatelessConversationWithUserDefinedConversationId() {
        int count = conversationalStatelessClientStatelessService.runConversationWithUserDefinedConversationId();
        Assert.assertEquals(2, count);
    }  
    @Test
    public void testStatelessStatelessConversationCheckUserDefinedConversationId() {
        String conversationId = conversationalStatelessClientStatelessService.runConversationCheckUserDefinedConversationId();
        Assert.assertEquals("MyConversation2", conversationId);
    }     
    
    @Test
    public void testStatelessStatelessConversationCheckingScope() {
        resetCallStack();
        conversationalStatelessClientStatelessService.runConversationCheckingScope();
        Assert.assertEquals("init,initializeCount,destroy,init,incrementCount,destroy,init,retrieveCount,destroy,init,endConversation,destroy,", 
                            ConversationalServiceStatelessImpl.calls.toString());
    }       
    
    @Test
    public void testStatelessStatelessConversationWithCallback() {
        resetCallStack();
        int count = conversationalStatelessClientStatelessService.runConversationWithCallback();
        Assert.assertEquals(0, count);
               
        Assert.assertEquals("init,runConversationWithCallback,init,initializeCount,destroy,init,incrementCount,destroy,init,retrieveCount,destroy,init,endConversation,destroy,destroy,", 
                            ConversationalClientStatelessImpl.calls.toString());        
    }
    //@Test
    public void testStatelessStatelessConversationHavingPassedReference() {
        int count = conversationalStatelessClientStatelessService.runConversationHavingPassedReference();
        Assert.assertEquals(3, count);
    }     

    @Test
    public void testStatelessStatelessConversationCallingEndedConversation() {
        int count = conversationalStatelessClientStatelessService.runConversationCallingEndedConversation();
        Assert.assertEquals(-999, count);
    }     
    
    @Test
    public void testStatelessStatelessConversationCallingEndedConversationCallback() {
        int count = conversationalStatelessClientStatelessService.runConversationCallingEndedConversationCallback();
        Assert.assertEquals(0, count);
    } 
    
    @Test
    public void testStatelessStatelessConversationCallingEndedConversationCheckConversationId() {
        String id = conversationalStatelessClientStatelessService.runConversationCallingEndedConversationCheckConversationId();
        Assert.assertEquals(null, id);
    }     
    
    //@Test
    public void testStatelessStatelessConversationCallingEndedConversationCallbackCheckConversationId() {
        String id = conversationalStatelessClientStatelessService.runConversationCallingEndedConversationCallbackCheckConversationId();
        Assert.assertEquals(null, id);
    }     

    // stateful client stateful service tests  
    // ======================================
    @Test
    public void testStatefulStatefulConversationFromInjectedReference() {
        int count = conversationalStatefulClientStatefulService.runConversationFromInjectedReference();
        Assert.assertEquals(2, count);
    } 
    
    @Test
    public void testStatefulStatefulConversationFromInjectedReference2() {
        int count = conversationalStatefulClientStatefulService.runConversationFromInjectedReference2();
        Assert.assertEquals(2, count);
    }     
    
    @Test
    public void testStatefulStatefulConversationFromServiceReference() {
        int count = conversationalStatefulClientStatefulService.runConversationFromServiceReference();
        Assert.assertEquals(2, count);
    }          
    
    @Test
    public void testStatefulStatefulConversationWithUserDefinedConversationId() {
        int count = conversationalStatefulClientStatefulService.runConversationWithUserDefinedConversationId();
        Assert.assertEquals(2, count);
    }    
    
    @Test
    public void testStatefulStatefulConversationCheckUserDefinedConversationId() {
        String conversationId = conversationalStatefulClientStatefulService.runConversationCheckUserDefinedConversationId();
        Assert.assertEquals("MyConversation2", conversationId);
    } 
    
    @Test
    public void testStatefulStatefulConversationCheckingScope() {
        resetCallStack();
        conversationalStatefulClientStatefulService.runConversationCheckingScope();
        Assert.assertEquals("init,initializeCount,incrementCount,retrieveCount,endConversation,destroy,", 
                            ConversationalServiceStatefulImpl.calls.toString());
    }  

    @Test
    public void testStatefulStatefulConversationWithCallback() {
        resetCallStack();
        int count = conversationalStatefulClientStatefulService.runConversationWithCallback();
        Assert.assertEquals(4, count);
               
        Assert.assertEquals("init,runConversationWithCallback,initializeCount,incrementCount,retrieveCount,endConversation,destroy,", 
                            ConversationalClientStatefulImpl.calls.toString());        
    }   
    
    //@Test
    public void testStatefulStatefulConversationHavingPassedReference() {
        int count = conversationalStatefulClientStatefulService.runConversationHavingPassedReference();
        Assert.assertEquals(3, count);
    } 
    
    @Test
    public void testStatefulStatefulConversationCallingEndedConversation() {
        int count = conversationalStatefulClientStatefulService.runConversationCallingEndedConversation();
        Assert.assertEquals(0, count);
    }     

    @Test
    public void testStatefulStatefulConversationCallingEndedConversationCallback() {
        int count = conversationalStatefulClientStatefulService.runConversationCallingEndedConversationCallback();
        Assert.assertEquals(0, count);
    }    
   
    @Test
    public void testStatefulStatefulConversationCallingEndedConversationCheckConversationId() {
        String id = conversationalStatefulClientStatefulService.runConversationCallingEndedConversationCheckConversationId();
        Assert.assertEquals(null, id);
    }     
 
    @Test
    public void testStatefulStatefulConversationCallingEndedConversationCallbackCheckConversationId() {
        String id = conversationalStatefulClientStatefulService.runConversationCallingEndedConversationCallbackCheckConversationId();
        Assert.assertEquals(null, id);
    }    
   
    // stateful client stateless service tests  
    // =======================================
    @Test
    public void testStatefulStatelessConversationFromInjectedReference() {
        int count = conversationalStatefulClientStatelessService.runConversationFromInjectedReference();
        Assert.assertEquals(2, count);
    } 
    
    @Test
    public void testStatefulStatelessConversationFromInjectedReference2() {
        int count = conversationalStatefulClientStatelessService.runConversationFromInjectedReference2();
        Assert.assertEquals(2, count);
    }    
    
    @Test
    public void testStatefulStatelessConversationFromServiceReference() {
        int count = conversationalStatefulClientStatelessService.runConversationFromServiceReference();
        Assert.assertEquals(2, count);
    }    
    
    @Test
    public void testStatefulStatelessConversationWithUserDefinedConversationId() {
        int count = conversationalStatefulClientStatelessService.runConversationWithUserDefinedConversationId();
        Assert.assertEquals(2, count);
    }    

    @Test
    public void testStatefulStatelessConversationCheckUserDefinedConversationId() {
        String conversationId = conversationalStatefulClientStatelessService.runConversationCheckUserDefinedConversationId();
        Assert.assertEquals("MyConversation2", conversationId);
    } 
    
    @Test
    public void testStatefulStatelessConversationCheckingScope() {
        resetCallStack();
        conversationalStatefulClientStatelessService.runConversationCheckingScope();
        Assert.assertEquals("init,initializeCount,destroy,init,incrementCount,destroy,init,retrieveCount,destroy,init,endConversation,destroy,", 
                            ConversationalServiceStatelessImpl.calls.toString());
    } 

    @Test
    public void testStatefulStatelessConversationWithCallback() {
        resetCallStack();
        int count = conversationalStatefulClientStatelessService.runConversationWithCallback();
        Assert.assertEquals(4, count);
               
        Assert.assertEquals("init,runConversationWithCallback,initializeCount,incrementCount,retrieveCount,endConversation,destroy,", 
                            ConversationalClientStatefulImpl.calls.toString());        
    }     
    
    //@Test
    public void testStatefulStatelessConversationHavingPassedReference() {
        int count = conversationalStatefulClientStatelessService.runConversationHavingPassedReference();
        Assert.assertEquals(3, count);
    }     
   
    @Test
    public void testStatefulStatelessConversationCallingEndedConversation() {
        int count = conversationalStatefulClientStatelessService.runConversationCallingEndedConversation();
        Assert.assertEquals(-999, count);
    }     
  
    @Test
    public void testStatefulStatelessConversationCallingEndedConversationCallback() {
        int count = conversationalStatefulClientStatelessService.runConversationCallingEndedConversationCallback();
        Assert.assertEquals(0, count);
    }  
    
    @Test
    public void testStatefulStatelessConversationCallingEndedConversationCheckConversationId() {
        String id = conversationalStatefulClientStatelessService.runConversationCallingEndedConversationCheckConversationId();
        Assert.assertEquals(null, id);
    }     
    
    @Test
    public void testStatefulStatelessConversationCallingEndedConversationCallbackCheckConversationId() {
        String id = conversationalStatefulClientStatelessService.runConversationCallingEndedConversationCallbackCheckConversationId();
        Assert.assertEquals(null, id);
    }      
    
    // stateless client request scope service tests
    // ============================================
    @Test
    public void testStatelessRequestConversationFromInjectedReference() {
        int count = conversationalStatelessClientRequestService.runConversationFromInjectedReference();
        Assert.assertEquals(2, count);
    } 
   
    @Test
    public void testStatelessRequestConversationFromInjectedReference2() {
        int count = conversationalStatelessClientRequestService.runConversationFromInjectedReference2();
        Assert.assertEquals(1, count);
    }     
    
    @Test
    public void testStatelessRequestConversationFromServiceReference() {
        int count = conversationalStatelessClientRequestService.runConversationFromServiceReference();
        Assert.assertEquals(2, count);
    }          
   
    @Test
    public void testStatelessRequestConversationWithUserDefinedConversationId() {
        int count = conversationalStatelessClientRequestService.runConversationWithUserDefinedConversationId();
        Assert.assertEquals(2, count);
    }   
    
    //@Test
    public void testStatelessRequestConversationCheckUserDefinedConversationId() {
        String conversationId = conversationalStatelessClientRequestService.runConversationCheckUserDefinedConversationId();
        Assert.assertEquals("MyConversation2", conversationId);
    } 
    
    @Test
    public void testStatelessRequestConversationCheckingScope() {
        resetCallStack();
    	ConversationalServiceRequestImpl.calls = new StringBuffer();
    	conversationalStatelessClientRequestService.runConversationCheckingScope();
        Assert.assertEquals("initializeCount,incrementCount,retrieveCount,endConversation,", 
        		            ConversationalServiceRequestImpl.calls.toString());
    }     

    @Test
    public void testStatelessRequestConversationWithCallback() {
        resetCallStack();
    	ConversationalClientStatelessImpl.calls = new StringBuffer();    	
        int count = conversationalStatelessClientRequestService.runConversationWithCallback();
        Assert.assertEquals(0, count);
               
        Assert.assertEquals("init,runConversationWithCallback,init,initializeCount,destroy,init,incrementCount,destroy,init,retrieveCount,destroy,init,endConversation,destroy,destroy,", 
                            ConversationalClientStatelessImpl.calls.toString());        
    }  
    
    //@Test
    public void testStatelessRequestConversationHavingPassedReference() {
        int count = conversationalStatelessClientRequestService.runConversationHavingPassedReference();
        Assert.assertEquals(3, count);
    }    
  
    @Test
    public void testStatelessRequestConversationBusinessException() {
        String message = conversationalStatelessClientRequestService.runConversationBusinessException();
        Assert.assertEquals("Business Exception", message);
    }     
    
    @Test
    public void testStatelessRequestConversationBusinessExceptionCallback() {
        String message = conversationalStatelessClientRequestService.runConversationBusinessExceptionCallback();
        Assert.assertEquals("Business Exception", message);
    }  
    
    @Test
    public void testStatelessRequestConversationCallingEndedConversation() {
        int count = conversationalStatelessClientRequestService.runConversationCallingEndedConversation();
        Assert.assertEquals(-999, count);
    }     
    
    @Test
    public void testStatelessRequestConversationCallingEndedConversationCallback() {
        int count = conversationalStatelessClientRequestService.runConversationCallingEndedConversationCallback();
        Assert.assertEquals(0, count);
    }  
    
    @Test
    public void testStatelessRequestConversationCallingEndedConversationCheckConversationId() {
        String id = conversationalStatelessClientRequestService.runConversationCallingEndedConversationCheckConversationId();
        Assert.assertEquals(null, id);
    }     
    
    //@Test
    public void testStatelessRequestConversationCallingEndedConversationCallbackCheckConversationId() {
        String id = conversationalStatelessClientRequestService.runConversationCallingEndedConversationCallbackCheckConversationId();
        Assert.assertEquals(null, id);
    }    
    
    // stateful client non conversational callback stateful service tests  
    // ==================================================================
   
    @Test
    public void testStatefulNonConversationalCallbackStatefulConversationFromInjectedReference() {
        int count = conversationalStatefulClientNonConversationalCallbackStatelessService.runConversationFromInjectedReference();
        Assert.assertEquals(2, count);
    } 
    
    @Test
    public void testStatefulNonConversationalCallbackStatefulConversationFromInjectedReference2() {
        int count = conversationalStatefulClientNonConversationalCallbackStatelessService.runConversationFromInjectedReference2();
        Assert.assertEquals(2, count);
    }     
    
    @Test
    public void testStatefulNonConversationalCallbackStatefulConversationFromServiceReference() {
        int count = conversationalStatefulClientNonConversationalCallbackStatelessService.runConversationFromServiceReference();
        Assert.assertEquals(2, count);
    }          
    
    @Test
    public void testStatefulNonConversationalCallbackStatefulConversationWithUserDefinedConversationId() {
        int count = conversationalStatefulClientNonConversationalCallbackStatelessService.runConversationWithUserDefinedConversationId();
        Assert.assertEquals(2, count);
    }    
      
    @Test
    public void testStatefulNonConversationalCallbackStatefulConversationCheckUserDefinedConversationId() {
        String conversationId = conversationalStatefulClientNonConversationalCallbackStatelessService.runConversationCheckUserDefinedConversationId();
        Assert.assertEquals("MyConversation2", conversationId);
    } 
  
    @Test
    public void testStatefulNonConversationalCallbackStatefulConversationCheckingScope() {
        resetCallStack();
    	conversationalStatefulClientNonConversationalCallbackStatelessService.runConversationCheckingScope();
        Assert.assertEquals("init,initializeCount,incrementCount,retrieveCount,endConversation,destroy,", 
        		            ConversationalServiceStatefulNonConversationalCallbackImpl.calls.toString());
    }  

    @Test
    public void testStatefulNonConversationalCallbackStatefulConversationWithCallback() {
        resetCallStack();
        int count = conversationalStatefulClientNonConversationalCallbackStatelessService.runConversationWithCallback();
        Assert.assertEquals(0, count);
               
        Assert.assertEquals("init,runConversationWithCallback,init,initializeCount,destroy,init,incrementCount,destroy,init,retrieveCount,destroy,init,endConversation,destroy,", 
        		           ConversationalClientStatefulNonConversationalCallbackImpl.calls.toString());        
    }   
    
    //@Test
    public void testStatefulNonConversationalCallbackStatefulConversationHavingPassedReference() {
        int count = conversationalStatefulClientNonConversationalCallbackStatelessService.runConversationHavingPassedReference();
        Assert.assertEquals(0, count);
    } 
    
    @Test
    public void testStatefulNonConversationalCallbackStatefulConversationCallingEndedConversation() {
        int count = conversationalStatefulClientNonConversationalCallbackStatelessService.runConversationCallingEndedConversation();
        Assert.assertEquals(0, count);
    }     
    
    @Test
    public void testStatefulNonConversationalCallbackStatefulConversationCallingEndedConversationCallback() {
        int count = conversationalStatefulClientNonConversationalCallbackStatelessService.runConversationCallingEndedConversationCallback();
        Assert.assertEquals(0, count);
    }    
    
    @Test
    public void testStatefulNonConversationalCallbackStatefulConversationCallingEndedConversationCheckConversationId() {
        String id = conversationalStatefulClientNonConversationalCallbackStatelessService.runConversationCallingEndedConversationCheckConversationId();
        Assert.assertEquals(null, id);
    }     
    
    @Test
    public void testStatefulNonConversationalCallbackStatefulConversationCallingEndedConversationCallbackCheckConversationId() {
        String id = conversationalStatefulClientNonConversationalCallbackStatelessService.runConversationCallingEndedConversationCallbackCheckConversationId();
        Assert.assertEquals("MyConversation3", id);
    }    

    private static final String NEW_A_VALUE = "First Instance - TestCode Set state on A";
    private static final String NEW_B_VALUE = "First Instance - TestCode Set state on B";
    private static final String SECOND_NEW_A_VALUE = "Second Instance - TestCode Set state on A";
    private static final String SECOND_NEW_B_VALUE = "Second Instance - TestCode Set state on B";
    
    @Test
    public void testMultipleConversations() {
        //////////
        // Tests on first instance
        //////////
        System.out.println("========= First instance tests =========");
        AService aService = domain.getService(AService.class, "ConversationalAComponent");
        
        // Make sure initial values are correct
        Assert.assertEquals(Constants.A_INITIAL_VALUE, aService.getState());
        Assert.assertEquals(Constants.B_INITIAL_VALUE, aService.getStateOnB());

        // Set some new values
        aService.setState(NEW_A_VALUE);
        aService.setStateOnB(NEW_B_VALUE);
        
        // Verify the set worked
        Assert.assertEquals(NEW_A_VALUE, aService.getState());
        Assert.assertEquals(NEW_B_VALUE, aService.getStateOnB());

        
        //////////
        // Tests on second instance
        //////////
        System.out.println("========= Second instance tests =========");

        // Do another look up
        AService aService2 = domain.getService(AService.class, "ConversationalAComponent");

        // Make sure initial values are correct on the second instance
        Assert.assertEquals(Constants.A_INITIAL_VALUE, aService2.getState());
        Assert.assertEquals(Constants.B_INITIAL_VALUE, aService2.getStateOnB());
        
        // Set some new values on the second instance
        aService2.setState(SECOND_NEW_A_VALUE);
        aService2.setStateOnB(SECOND_NEW_B_VALUE);
    
        // Verify the set worked on the second instance
        Assert.assertEquals(SECOND_NEW_A_VALUE, aService2.getState());
        Assert.assertEquals(SECOND_NEW_B_VALUE, aService2.getStateOnB());

        // Verify the values have not been changed on the first instance
        Assert.assertEquals(NEW_A_VALUE, aService.getState());
        Assert.assertEquals(NEW_B_VALUE, aService.getStateOnB());
        
        System.out.println("========= Done instance tests =========");
    }
        
}
