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
import org.osoa.sca.ConversationEndedException;

public class ConversationalAgeTestCase {

    private SCADomain domain;

    @Before
    public void setUp() throws Exception {
        System.setProperty("org.apache.tuscany.sca.core.scope.ConversationalScopeContainer.ReaperInterval", "2");        
        domain = SCADomain.newInstance("ConversationAge.composite");

    }

    @After
    public void tearDown() throws Exception {
        System.clearProperty("org.apache.tuscany.sca.core.scope.ConversationalScopeContainer.ReaperInterval");        
        if (domain != null) {
            domain.close();
        }
    }

    @Test
    public void testMaxAge() throws InterruptedException {

        ConversationalService conversationalService =
            domain.getService(ConversationalService.class, "ConversationAgeComponent");

        Assert.assertEquals(0, conversationalService.retrieveCount());
        conversationalService.initializeCount(42);
        Assert.assertEquals(42, conversationalService.retrieveCount());
        Assert.assertEquals(42, conversationalService.retrieveCount());
        Thread.sleep(3100);
        Assert.assertEquals(0, conversationalService.retrieveCount());
    }

    @Test
    public void testAgeExpired() throws InterruptedException {

        ConversationalService conversationalService =
            domain.getService(ConversationalService.class, "ConversationAgeComponent");

        Assert.assertEquals(0, conversationalService.retrieveCount());
        conversationalService.initializeCount(42);
        Assert.assertEquals(42, conversationalService.retrieveCount());
        Assert.assertEquals(42, conversationalService.retrieveCount());
        Thread.sleep(1100);
        try {
            Assert.assertEquals(0, conversationalService.retrieveCount());
            Assert.fail();
        } catch (ConversationEndedException e) {
            // expected
        }
    }

    @Test
    public void testMaxIdle() throws InterruptedException {

        ConversationalService conversationalService =
            domain.getService(ConversationalService.class, "ConversationIdleComponent");

        Assert.assertEquals(0, conversationalService.retrieveCount());
        conversationalService.initializeCount(42);
        Assert.assertEquals(42, conversationalService.retrieveCount());
        Assert.assertEquals(42, conversationalService.retrieveCount());
        Thread.sleep(3100);
        Assert.assertEquals(0, conversationalService.retrieveCount());
    }

}
