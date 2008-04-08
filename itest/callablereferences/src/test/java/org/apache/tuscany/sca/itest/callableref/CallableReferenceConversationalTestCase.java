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
package org.apache.tuscany.sca.itest.callableref;

import junit.framework.Assert;

import org.apache.tuscany.sca.host.embedded.SCADomain;
import org.apache.tuscany.sca.itest.callablerefconversational.ConversationalService;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.osoa.sca.ServiceReference;

/**
 * Simple test case that creates a ServiceReference to a Conversational Component
 * using ComponentContext.createSelfReference()
 * <p>
 * This test case is for TUSCANY-2208
 * 
 * @version $Date$ $Revision$
 */
public class CallableReferenceConversationalTestCase {
    private static SCADomain domain;
    private static ConversationalService acomponent;

    @BeforeClass
    public static void init() throws Exception {
        domain = SCADomain.newInstance("CallableReferenceConversationalTest.composite");
        Assert.assertNotNull(domain);
        acomponent = domain.getService(ConversationalService.class, "ConversationalComponent");
    }

    @AfterClass
    public static void destroy() throws Exception {
        if (domain != null) {
            domain.close();
        }
    }

    /**
     * This is a dummy test so that this Unit Test has a test so it will build.
     * Once TUSCANY-2208 is fixed, this dummy test method can be removed 
     */
    @Test
    public void dummyTestRemoveWhenTuscany2208IsFixed() {
    }

    /**
     * Tests creating Self References and validate them with Conversation IDs
     */
    // Disabled until TUSCANY-2208 is fixed
    // @Test
    public void testCreateSelfRefUsingConvID() {
        Assert.assertNotNull(acomponent);

        final Object origConvID = acomponent.getConversationID();
        Assert.assertNotNull(origConvID);
        final ServiceReference<ConversationalService> ref = acomponent.createSelfRef();
        Assert.assertNotNull(ref);

        final ConversationalService resolvedRef = ref.getService();
        Assert.assertNotNull(resolvedRef);
        final Object newConvID = resolvedRef.getConversationID();
        Assert.assertNotNull(newConvID);

        Assert.assertEquals(origConvID, newConvID);
    }

    /**
     * Tests creating Self References and validate them with user specified data 
     */
    // Disabled until TUSCANY-2208 is fixed
    // @Test
    public void testCreateSelfRefUsingUserData() {
        Assert.assertNotNull(acomponent);

        final String origUserData = acomponent.getUserData();
        Assert.assertEquals(ConversationalService.DEFAULT_USER_DATA, origUserData);

        final String userData = "Some new user data set at " + System.currentTimeMillis();
        acomponent.setUserData(userData);

        final ServiceReference<ConversationalService> ref = acomponent.createSelfRef();
        Assert.assertNotNull(ref);

        final ConversationalService resolvedRef = ref.getService();
        Assert.assertNotNull(resolvedRef);
        final String newUserData = resolvedRef.getUserData();
        Assert.assertNotNull(newUserData);

        Assert.assertEquals(userData, newUserData);
    }
}
