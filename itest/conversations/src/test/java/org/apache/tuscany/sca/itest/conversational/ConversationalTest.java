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
import org.apache.tuscany.sca.itest.conversational.impl.ConversationalServiceStatefulImpl;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ConversationalTest {

    private SCADomain domain;
    private ConversationalClient conversationalStatelessClientStatelessService;
    private ConversationalClient conversationalStatelessClientStatefulService;

    @Before
    public void setUp() throws Exception {
        domain = SCADomain.newInstance("conversational.composite");

        conversationalStatelessClientStatelessService = domain.getService(ConversationalClient.class,
                                                                          "ConversationalStatelessClientStatelessService");

        conversationalStatelessClientStatefulService  = domain.getService(ConversationalClient.class,
                                                                          "ConversationalStatelessClientStatefulService");

    }

    @After
    public void tearDown() throws Exception {
        domain.close();
    }
   
    // Stateful service tests
    @Test
    public void testStatefulConversationFromInjectedReference() {
        int count = conversationalStatelessClientStatefulService.runConversationFromInjectedReference();
        Assert.assertEquals(2, count);
    } 
    
    @Test
    public void testStatefulConversationFromServiceReference() {
        int count = conversationalStatelessClientStatefulService.runConversationFromServiceReference();
        Assert.assertEquals(2, count);
    }          
    
    @Test
    public void testStatefulConversationWithUserDefinedConversationId() {
        int count = conversationalStatelessClientStatefulService.runConversationWithUserDefinedConversationId();
        Assert.assertEquals(2, count);
    }    
    
    @Test
    public void testStatefulConversationCheckingScope() {
        int count = conversationalStatelessClientStatefulService.runConversationCheckingScope();
        int initCount = ConversationalServiceStatefulImpl.getInitValue();
        Assert.assertEquals(5, initCount);
    }    
    
    // Stateless service tests
    @Test
    public void testStatelessConversationFromInjectedReference() {
        int count = conversationalStatelessClientStatelessService.runConversationFromInjectedReference();
        Assert.assertEquals(2, count);
    } 
    
    @Test
    public void testStatelessConversationFromServiceReference() {
        int count = conversationalStatelessClientStatelessService.runConversationFromServiceReference();
        Assert.assertEquals(2, count);
    }    
    
    @Test
    public void testStatelessConversationWithUserDefinedConversationId() {
        int count = conversationalStatelessClientStatelessService.runConversationWithUserDefinedConversationId();
        Assert.assertEquals(2, count);
    }    
    
    @Test
    public void testStatelessConversationCheckingScope() {
        int count = conversationalStatelessClientStatelessService.runConversationCheckingScope();
        int initCount = ConversationalServiceStatefulImpl.getInitValue();
        Assert.assertEquals(15, initCount);
    }       
}
