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

package org.apache.tuscany.sca.vtest.javaapi.apis.componentcontext;

import org.apache.tuscany.sca.host.embedded.SCADomain;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * This test class tests the ComponentContext interface described in 1.7.1 of the SCA Java Annotations & APIs Specification 1.0.
 */
public class ComponentContextTestCase {

    protected static SCADomain domain;
    protected static String compositeName = "ab.composite";
    protected static AComponent a;

    @BeforeClass
    public static void init() throws Exception {
        try {
            System.out.println("Setting up");
            domain = SCADomain.newInstance(compositeName);
            a = domain.getService(AComponent.class, "AComponent");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @AfterClass
    public static void destroy() throws Exception {
        System.out.println("Cleaning up");
        if (domain != null) {
            domain.close();
        }
    }

    /**
     * L776: Tests getURI() of the ComponentContext interface.
     * @throws Exception
     */
    @Test
    public void testGetURI() throws Exception {
        Assert.assertEquals(a.getContextURI(), "AComponent");
    }

    /**
     * L778: Tests getService() of the ComponentContext interface.
     * @throws Exception
     */
    @Test
    public void testGetService() throws Exception {
        Assert.assertEquals(a.getServiceBName(), "ComponentB");
    }

    /**
     * L780: Tests getServiceReference() of the ComponentContext interface.
     * @throws Exception
     */
    @Test
    public void testGetServiceReference() throws Exception {
        Assert.assertEquals(a.getServiceReferenceBName(), "ComponentB");
    }

}
