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
package org.apache.tuscany.sca.test.spec;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.apache.tuscany.host.embedded.SCADomain;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class ComponentContextTestCase {
    
    static SCADomain domain;

    @Test
    public void getServiceReference() {
        
        //FIXME this does not test ComponentContext, we'll need a component impl
        // to test it
        MyService myService = domain.getService(MyService.class, "MyService");

        assertNotNull(myService);
        assertEquals("RTP", myService.getLocation());
        assertEquals("2006", myService.getYear());

    }

    @BeforeClass
    public static void init() throws Exception {
        domain = SCADomain.newInstance("CompositeTest.composite");
    }

    @AfterClass
    public static void destroy() throws Exception {
        domain.close();
    }
}
