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
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ConversationalTestCase {

    private SCADomain domain;
    private ConversationalClient conversationalStatelessClientStatelessService;
    private ConversationalClient conversationalStatelessClientStatefulService;
    private ConversationalClient conversationalStatefulClientStatelessService;
    private ConversationalClient conversationalStatefulClientStatefulService; 
    private ConversationalClient conversationalStatelessClientRequestService;
    private ConversationalClient conversationalStatefulClientNonConversationalCallbackStatelessService;    

    @Before
    public void setUp() throws Exception {
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
	        
	        // reset the place where we record the sequence of calls passing
	        // through each component instance
	        ConversationalServiceStatelessImpl.calls = new StringBuffer();
	        ConversationalServiceStatefulImpl.calls  = new StringBuffer();
	        ConversationalClientStatelessImpl.calls  = new StringBuffer();         
	        ConversationalClientStatefulImpl.calls   = new StringBuffer();
        
    	} catch(Exception ex) {
    		System.err.println(ex.toString());
    	}
               
    }

    @After
    public void tearDown() throws Exception {
        domain.close();
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
        conversationalStatelessClientStatefulService.runConversationCheckingScope();
        Assert.assertEquals("init,initializeCount,incrementCount,retrieveCount,endConversation,destroy,", 
                            ConversationalServiceStatefulImpl.calls.toString());
    }     

    @Test
    public void testStatelessStatefulConversationWithCallback() {
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
        conversationalStatelessClientStatelessService.runConversationCheckingScope();
        Assert.assertEquals("init,initializeCount,destroy,init,incrementCount,destroy,init,retrieveCount,destroy,init,endConversation,destroy,", 
                            ConversationalServiceStatelessImpl.calls.toString());
    }       
    
    @Test
    public void testStatelessStatelessConversationWithCallback() {
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
        conversationalStatefulClientStatefulService.runConversationCheckingScope();
        Assert.assertEquals("init,initializeCount,incrementCount,retrieveCount,endConversation,destroy,", 
                            ConversationalServiceStatefulImpl.calls.toString());
    }  

    @Test
    public void testStatefulStatefulConversationWithCallback() {
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
        conversationalStatefulClientStatelessService.runConversationCheckingScope();
        Assert.assertEquals("init,initializeCount,destroy,init,incrementCount,destroy,init,retrieveCount,destroy,init,endConversation,destroy,", 
                            ConversationalServiceStatelessImpl.calls.toString());
    } 

    @Test
    public void testStatefulStatelessConversationWithCallback() {
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
    
    @Test
    public void testStatelessRequestConversationCheckUserDefinedConversationId() {
        String conversationId = conversationalStatelessClientRequestService.runConversationCheckUserDefinedConversationId();
        Assert.assertEquals("MyConversation2", conversationId);
    } 
    
    @Test
    public void testStatelessRequestConversationCheckingScope() {
    	ConversationalServiceRequestImpl.calls = new StringBuffer();
    	conversationalStatelessClientRequestService.runConversationCheckingScope();
        Assert.assertEquals("init,initializeCount,incrementCount,retrieveCount,endConversation,", 
        		            ConversationalServiceRequestImpl.calls.toString());
    }     

    @Test
    public void testStatelessRequestConversationWithCallback() {
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
    	ConversationalServiceStatefulNonConversationalCallbackImpl.calls = new StringBuffer();
    	conversationalStatefulClientNonConversationalCallbackStatelessService.runConversationCheckingScope();
        Assert.assertEquals("init,initializeCount,incrementCount,retrieveCount,endConversation,destroy,", 
        		            ConversationalServiceStatefulNonConversationalCallbackImpl.calls.toString());
    }  

    @Test
    public void testStatefulNonConversationalCallbackStatefulConversationWithCallback() {
    	ConversationalClientStatefulNonConversationalCallbackImpl.calls = new StringBuffer();
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
        
}
