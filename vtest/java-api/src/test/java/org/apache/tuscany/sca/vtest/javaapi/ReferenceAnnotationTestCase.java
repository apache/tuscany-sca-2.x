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

package org.apache.tuscany.sca.vtest.javaapi;

import static org.junit.Assert.fail;
import junit.framework.Assert;

import org.apache.tuscany.sca.host.embedded.SCADomain;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

/**
 * 
 */
public class ReferenceAnnotationTestCase {

    protected static SCADomain domain;
    protected static AService a;
    protected static String compositeName = "ab.composite";

    @BeforeClass
    public static void init() throws Exception {
        try {
            System.out.println("Setting up");
            domain = SCADomain.newInstance(compositeName);
            a = domain.getService(AService.class, "AComponent");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @AfterClass
    public static void destroy() throws Exception {

        System.out.println("Cleaning up");
        if (domain != null)
            domain.close();

    }


    @Test
    @Ignore
    //Temporary test unrelated to spec test effort.  Remove after resolution of 
    //JIRA T-2145
    public void bogusComponentName() throws Exception {
        SCADomain tempDomain = SCADomain.newInstance(compositeName);
        try {
            AService a = tempDomain.getService(AService.class, "AReallyBogusComponentName");
            if (a == null)
                fail("Should have thrown an exception rather than return null");
            else
                fail("Should have thrown an exception rather than return a proxy");
        } finally {
            if (tempDomain != null)
                tempDomain.close();
        }

    }


    /**
     * This tests the use of the three usages of the @Reference annotation
     * B1 is injected via field injection
     * B2 is injected via constructor parameter
     * B3 is injected via setter method
     */
    @Test
    public void atReference() throws Exception {
        
        Assert.assertEquals("BService", a.getB1Name());
        Assert.assertEquals("BService", a.getB2Name());
        Assert.assertEquals("BService", a.getB3Name());

    }

}
