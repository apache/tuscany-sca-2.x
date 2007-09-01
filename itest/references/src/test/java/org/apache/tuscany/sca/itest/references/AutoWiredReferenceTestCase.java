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
package org.apache.tuscany.sca.itest.references;

import static junit.framework.Assert.assertEquals;
import junit.framework.Assert;

import org.apache.tuscany.sca.host.embedded.SCADomain;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.osoa.sca.ServiceUnavailableException;

public class AutoWiredReferenceTestCase {
    private static SCADomain domain;
    private static AComponent acomponent;

    @BeforeClass
    public static void init() throws Exception {
        domain = SCADomain.newInstance("AutoWiredReferencesTest.composite");
        acomponent = domain.getService(AComponent.class, "AComponent");
    }

    @AfterClass
    public static void destroy() throws Exception {
        domain.close();
    }

    @Test
    public void testBReference() {
        assertEquals("BComponent", acomponent.fooB());
    }

    @Test
    public void testCReference() {
        assertEquals("CComponent", acomponent.fooC());
    }

    @Test
    public void testBCReference() {
        assertEquals("BCComponent", acomponent.fooBC());
    }

    @Test(expected = ServiceUnavailableException.class)
    public void testD1Reference() {
        acomponent.fooD1();
    }

    @Test
    public void testD2Reference() {
        assertEquals("DComponent", acomponent.fooD2());
    }

    @Test
    public void testRequiredFalseReference() {
        try {
            acomponent.getDReference().dFoo();
        } catch (Exception e) {
            Assert.assertTrue(true);
        }
    }

}
