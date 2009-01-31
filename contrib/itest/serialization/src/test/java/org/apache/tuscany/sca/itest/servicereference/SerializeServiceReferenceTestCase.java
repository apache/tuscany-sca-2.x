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
package org.apache.tuscany.sca.itest.servicereference;

import junit.framework.Assert;

import org.apache.tuscany.sca.host.embedded.SCADomain;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * A test case that will attempt to Serialize and Deserialize Service References
 * 
 * @version $Date$ $Revision$
 */
public class SerializeServiceReferenceTestCase {
    /**
     * Reference to the SCA Domain
     */
    private static SCADomain domain;

    /**
     * Initialise the SCA Domain
     * 
     * @throws Exception Failed to initialise the SCA Domain
     */
    @BeforeClass
    public static void init() throws Exception {
        domain = SCADomain.newInstance("ServiceReferenceSerializationTest.composite");
        Assert.assertNotNull(domain);
    }

    /**
     * Shutdown the SCA Domain
     * 
     * @throws Exception Failed to shutdown the SCA Domain
     */
    @AfterClass
    public static void destroy() throws Exception {
        if (domain != null) {
            domain.close();
        }
    }

    /**
     * Tests Serializing a Stateless ServiceReference as managed
     * SCA code
     * 
     * @throws Exception Test failed
     */
    @Test
    public void testSerializeStatelessServiceReferenceInsideSCA() throws Exception {
        SCAManagedClient client = domain.getService(
                SCAManagedClient.class, "SCAManagedClientComponent");

        client.testSerializeStatelessServiceReference();
    }

    /**
     * Tests Serializing a Nested Stateless ServiceReference as managed
     * SCA code.
     * 
     * @throws Exception Test failed
     */
    @Test
    public void testSerializeNestedStatelessServiceReferenceInsideSCA() throws Exception {
        SCAManagedClient client = domain.getService(
                SCAManagedClient.class, "SCAManagedClientComponent");

        client.testSerializeNestedStatelessServiceReference();
    }

    /**
     * Tests Serializing a Conversational ServiceReference as managed
     * SCA code
     * 
     * @throws Exception Test failed
     */
    @Test
    public void testSerializeConversationalServiceReferenceInsideSCA() throws Exception {
        SCAManagedConversationalClient client = domain.getService(
                SCAManagedConversationalClient.class, "SCAManagedConversationalClientComponent");

        client.testSerializeConversationalServiceReference();
    }

    /**
     * Tests Serializing a Nested Conversational ServiceReference as managed
     * SCA code.
     * 
     * @throws Exception Test failed
     */
    @Test
    public void testSerializeNestedConversationalServiceReferenceInsideSCA() throws Exception {
        SCAManagedConversationalClient client = domain.getService(
                SCAManagedConversationalClient.class, "SCAManagedConversationalClientComponent");

        client.testSerializeNestedConversationalServiceReference();
    }

    /**
     * Tests Serializing a Callback to a Stateless Service as managed
     * SCA code
     * 
     * @throws Exception Test failed
     */
    @Test
    public void testSerializeCallbackToStatelessServiceInsideSCA() throws Exception {
        SCAManagedClient client = domain.getService(
                SCAManagedClient.class, "SCAManagedClientComponent");

        client.testSerializeCallbackToStatelessServiceInsideSCA();
    }

    /**
     * Tests Serializing a Callback to a Conversational Service as managed
     * SCA code.
     * 
     * @throws Exception Test failed
     */
    @Test
    public void testSerializeCallbackToConversationalServiceInsideSCA() throws Exception {
        SCAManagedConversationalClient client = domain.getService(
                SCAManagedConversationalClient.class, "SCAManagedConversationalClientComponent");

        client.testSerializeCallbackToConversationalServiceInsideSCA();
    }
}
