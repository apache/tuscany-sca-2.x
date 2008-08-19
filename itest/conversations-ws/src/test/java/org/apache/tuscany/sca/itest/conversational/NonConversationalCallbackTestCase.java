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

import java.io.File;

import junit.framework.Assert;

import org.apache.tuscany.sca.itest.conversational.impl.ConversationalClientStatefulImpl;
import org.apache.tuscany.sca.itest.conversational.impl.ConversationalClientStatefulNonConversationalCallbackImpl;
import org.apache.tuscany.sca.itest.conversational.impl.ConversationalClientStatelessImpl;
import org.apache.tuscany.sca.itest.conversational.impl.ConversationalServiceStatefulImpl;
import org.apache.tuscany.sca.itest.conversational.impl.ConversationalServiceStatefulNonConversationalCallbackImpl;
import org.apache.tuscany.sca.itest.conversational.impl.ConversationalServiceStatelessImpl;
import org.apache.tuscany.sca.node.SCAClient;
import org.apache.tuscany.sca.node.SCAContribution;
import org.apache.tuscany.sca.node.SCANode;
import org.apache.tuscany.sca.node.SCANodeFactory;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class NonConversationalCallbackTestCase {

    private static SCANode node;
    private static ConversationalClient conversationalStatelessClientStatelessService;
    private static ConversationalClient conversationalStatelessClientStatefulService;
    private static ConversationalClient conversationalStatefulClientStatelessService;
    private static ConversationalClient conversationalStatefulClientStatefulService; 
    private static ConversationalClient conversationalStatelessClientRequestService;
    private static ConversationalClient conversationalStatefulClientNonConversationalCallbackStatelessService;    

    @BeforeClass
    public static void setUp() throws Exception {
    	try {
            SCANodeFactory nodeFactory = SCANodeFactory.newInstance();
            node = nodeFactory.createSCANode(new File("src/main/resources/Conversational/conversational.composite").toURL().toString(),
                                             new SCAContribution("TestContribution", 
                                                                 new File("src/main/resources/Conversational").toURL().toString()));
                    
             
            node.start();
            
            conversationalStatelessClientStatelessService = ((SCAClient)node).getService(ConversationalClient.class, 
	                                                                          "ConversationalStatelessClientStatelessService");
	
	    conversationalStatelessClientStatefulService  = ((SCAClient)node).getService(ConversationalClient.class, 
	                                                                          "ConversationalStatelessClientStatefulService");
	
	    conversationalStatefulClientStatelessService  = ((SCAClient)node).getService(ConversationalClient.class, 
	                                                                          "ConversationalStatefulClientStatelessService");
	
	    conversationalStatefulClientStatefulService   = ((SCAClient)node).getService(ConversationalClient.class, 
	                                                                          "ConversationalStatefulClientStatefulService");
	    conversationalStatelessClientRequestService    = ((SCAClient)node).getService(ConversationalClient.class, 
	                                                                          "ConversationalStatelessClientRequestService");
	    conversationalStatefulClientNonConversationalCallbackStatelessService    = ((SCAClient)node).getService(ConversationalClient.class, 
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

    @AfterClass
    public static void tearDown() throws Exception {
        node.stop();
        conversationalStatelessClientStatelessService = null;
        conversationalStatelessClientStatefulService = null;
        conversationalStatefulClientStatelessService = null;
        conversationalStatefulClientStatefulService = null; 
        conversationalStatelessClientRequestService = null;
        conversationalStatefulClientNonConversationalCallbackStatelessService = null;    
    }
    
    private static void resetCallStack() {
        
        // reset the place where we record the sequence of calls passing
        // through each component instance
        ConversationalServiceStatelessImpl.calls = new StringBuffer();
        ConversationalServiceStatefulImpl.calls  = new StringBuffer();
        ConversationalClientStatelessImpl.calls  = new StringBuffer();         
        ConversationalClientStatefulImpl.calls   = new StringBuffer();    
        ConversationalClientStatefulNonConversationalCallbackImpl.calls = new StringBuffer();
        
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
    	ConversationalServiceStatefulNonConversationalCallbackImpl.calls = new StringBuffer();
    	conversationalStatefulClientNonConversationalCallbackStatelessService.runConversationCheckingScope();
        Assert.assertEquals("init,initializeCount,incrementCount,retrieveCount,endConversation,destroy,", 
        		            ConversationalServiceStatefulNonConversationalCallbackImpl.calls.toString());
    }  

    //@Test
    public void testStatefulNonConversationalCallbackStatefulConversationWithCallback() {
        resetCallStack();
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
    
    //@Test
    public void testStatefulNonConversationalCallbackStatefulConversationCallingEndedConversationCallback() {
        int count = conversationalStatefulClientNonConversationalCallbackStatelessService.runConversationCallingEndedConversationCallback();
        Assert.assertEquals(0, count);
    }    
    
    @Test
    public void testStatefulNonConversationalCallbackStatefulConversationCallingEndedConversationCheckConversationId() {
        String id = conversationalStatefulClientNonConversationalCallbackStatelessService.runConversationCallingEndedConversationCheckConversationId();
        Assert.assertEquals(null, id);
    }     
    
    //@Test
    public void testStatefulNonConversationalCallbackStatefulConversationCallingEndedConversationCallbackCheckConversationId() {
        String id = conversationalStatefulClientNonConversationalCallbackStatelessService.runConversationCallingEndedConversationCallbackCheckConversationId();
        Assert.assertEquals("MyConversation3", id);
    } 
    
       
        
}
