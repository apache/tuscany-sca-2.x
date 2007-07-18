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
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ConversationalTest {

    private SCADomain domain;
    private ConversationalClient conversationalClient;

    @Before
    public void setUp() throws Exception {
        domain = SCADomain.newInstance("conversational.composite");

        conversationalClient = domain.getService(ConversationalClient.class,
                                                 "ConversationalClientStateful");
    }

    @After
    public void tearDown() throws Exception {
        domain.close();
    }
    
    @Test
    public void testStatefulConversation() {
        int count = conversationalClient.runConversation();
        Assert.assertEquals(count, 2);
    }    

}
