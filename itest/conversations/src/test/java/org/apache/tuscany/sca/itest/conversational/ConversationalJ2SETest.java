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

import org.apache.tuscany.sca.host.embedded.SCADomain;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class ConversationalJ2SETest {

    private SCADomain domain;

    @Before
    public void setUp() throws Exception {
        domain = SCADomain.newInstance("conversational.composite");

    }

    @After
    public void tearDown() throws Exception {
        if (domain != null) {
            domain.close();
        }
    }

    @Test
    public void testStatefulConversation() {
        ConversationalService conversationalService =
            domain.getService(ConversationalService.class, "ConversationalServiceStateful");

        conversationalService.initializeCount(1);
        Assert.assertEquals(1, conversationalService.retrieveCount());
        conversationalService.incrementCount();
        Assert.assertEquals(2, conversationalService.retrieveCount());
        conversationalService.endConversation();

        Assert.assertEquals(0, conversationalService.retrieveCount());

        conversationalService.initializeCount(4);
        Assert.assertEquals(4, conversationalService.retrieveCount());
        conversationalService.incrementCount();
        Assert.assertEquals(5, conversationalService.retrieveCount());
        conversationalService.endConversation();

    }

    @Test
    public void testStatelessConversation() {
        ConversationalService conversationalService =
            domain.getService(ConversationalService.class, "ConversationalServiceStateless");

        conversationalService.initializeCount(1);
        Assert.assertEquals(1, conversationalService.retrieveCount());
        conversationalService.incrementCount();
        Assert.assertEquals(2, conversationalService.retrieveCount());
        conversationalService.endConversation();

        conversationalService.initializeCount(4);
        Assert.assertEquals(4, conversationalService.retrieveCount());
        conversationalService.incrementCount();
        Assert.assertEquals(5, conversationalService.retrieveCount());
        conversationalService.endConversation();

    }
}
