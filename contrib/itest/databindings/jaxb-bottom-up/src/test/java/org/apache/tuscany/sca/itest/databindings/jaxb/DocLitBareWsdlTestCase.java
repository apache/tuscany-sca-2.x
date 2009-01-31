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

package org.apache.tuscany.sca.itest.databindings.jaxb;

import junit.framework.Assert;

import org.apache.tuscany.sca.host.embedded.SCADomain;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @version $Rev$ $Date$
 */
public class DocLitBareWsdlTestCase {

    private static SCADomain domain;

    /**
     * Runs once before running the tests
     */
    @BeforeClass
    public static void setUp() throws Exception {
        try {
            domain = SCADomain.newInstance("doclitbarewsdl.composite");
        } catch (Throwable e) {
            // @Ignore("TUSCANY-2398")
            e.printStackTrace();
        }
    }

    /**
     * Runs once after running the tests
     */
    @AfterClass
    public static void tearDown() {
        if (domain != null) {
            domain.close();
        }
    }

    // @Ignore("TUSCANY-2398")
    @Test
    public void testDocLitBareWsdl() throws Exception {
        AClientService client = domain.getService(AClientService.class, "AClientComponent");
        String name = "Pandu";
        String resp = client.getGreetingsForward(name);
        Assert.assertEquals("Hello " + name, resp);
    }
}
