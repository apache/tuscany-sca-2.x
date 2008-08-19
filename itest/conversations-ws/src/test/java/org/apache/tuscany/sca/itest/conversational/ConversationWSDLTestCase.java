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


import org.apache.tuscany.sca.itest.conversational.impl.ConversationalClientStatelessImpl;
import org.apache.tuscany.sca.node.SCAClient;
import org.apache.tuscany.sca.node.SCAContribution;
import org.apache.tuscany.sca.node.SCANode;
import org.apache.tuscany.sca.node.SCANodeFactory;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class ConversationWSDLTestCase {

    private SCANode node; 
    private ConversationalClient conversationalStatelessClientStatefulService;    

    @Before
    public void setUp() throws Exception {
        SCANodeFactory nodeFactory = SCANodeFactory.newInstance();
        node = nodeFactory.createSCANode(new File("src/main/resources/ConversationalWSDL/conversationalWSDL.composite").toURL().toString(),
                                         new SCAContribution("TestContribution", 
                                                                     new File("src/main/resources/ConversationalWSDL").toURL().toString()));
                
         
        node.start();
        
        conversationalStatelessClientStatefulService = ((SCAClient)node).getService(ConversationalClient.class, "ConversationalStatelessClientStatefulService");
        
        
        ConversationalClientStatelessImpl.calls  = new StringBuffer(); 
    }

    @After
    public void tearDown() throws Exception {
        node.stop();
        conversationalStatelessClientStatefulService = null;
    }

    @Test
    public void testStatelessStatefulConversationFromInjectedReference() {
        int count = conversationalStatelessClientStatefulService.runConversationFromInjectedReference();
        Assert.assertEquals(2, count);
    } 
    
    //@Test
    public void testKeepServerRunning() throws Exception {
        System.out.println("press enter to continue");
        System.in.read();
    }    

}
