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

import javax.xml.namespace.QName;

import junit.framework.Assert;

import org.apache.tuscany.sca.domain.SCADomain;
import org.apache.tuscany.sca.itest.conversational.impl.ConversationalClientStatefulImpl;
import org.apache.tuscany.sca.itest.conversational.impl.ConversationalClientStatefulNonConversationalCallbackImpl;
import org.apache.tuscany.sca.itest.conversational.impl.ConversationalClientStatelessImpl;
import org.apache.tuscany.sca.itest.conversational.impl.ConversationalServiceRequestImpl;
import org.apache.tuscany.sca.itest.conversational.impl.ConversationalServiceStatefulImpl;
import org.apache.tuscany.sca.itest.conversational.impl.ConversationalServiceStatefulNonConversationalCallbackImpl;
import org.apache.tuscany.sca.itest.conversational.impl.ConversationalServiceStatelessImpl;
import org.apache.tuscany.sca.node.SCANode;
import org.apache.tuscany.sca.node.SCANodeFactory;
import org.apache.tuscany.sca.node.util.SCAContributionUtil;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class StatefulStatefulTestCase {

    private static SCADomain domain;
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
                node = SCANodeFactory.newInstance().createSCANode(null, null);
                node.addContribution("mycontribution",
                                     StatefulStatefulTestCase.class.getResource("/Conversational/."));                                                                     
                node.addToDomainLevelComposite(new QName("http://conversations", "ConversationalITest"));
                node.start();
                domain = node.getDomain();
	
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

    @AfterClass
    public static void tearDown() throws Exception {
        node.destroy();
        domain = null;
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
        
}
