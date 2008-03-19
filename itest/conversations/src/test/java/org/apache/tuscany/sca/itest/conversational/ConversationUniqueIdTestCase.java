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
import org.apache.tuscany.sca.itest.TestResult;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * @version $Rev$ $Date$
 */
public class ConversationUniqueIdTestCase {

    private SCADomain domain;

    @Before
    public void setUp() throws Exception {
        domain = SCADomain.newInstance("ConversationUniqueId.composite");
    }

    @After
    public void tearDown() throws Exception {
        if (domain != null) {
            domain.close();
        }
    }

    @Test
    public void testConversationUniqueId() {
        Alpha alpha = domain.getService(Alpha.class, "Alpha");
        int numConversations = 3;
        
        for(int i = 0; i < numConversations; ++i) {
            alpha.run(5);
        }
        
        // Wait for the conversations to complete
        while (TestResult.getCompleted() < numConversations) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
            }
        }
        
        Assert.assertEquals(TestResult.results.size(), numConversations);
        for(Boolean value : TestResult.results.values()) {
            Assert.assertTrue(value);
        }
    }

}
