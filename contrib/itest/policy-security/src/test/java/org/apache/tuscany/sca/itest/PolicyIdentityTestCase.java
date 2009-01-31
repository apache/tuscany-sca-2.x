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

package org.apache.tuscany.sca.itest;

import helloworld.HelloWorldService;

import org.apache.tuscany.sca.host.embedded.SCADomain;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class PolicyIdentityTestCase {
    private static SCADomain domain;
    private static HelloWorldService service;

    @Test
    public void testPolicies() {
        //TODO
        String greetings = service.getGreetings("Luciano");
        System.out.println(">>>" + greetings);
    }

    @BeforeClass
    public static void init() throws Exception {
        try {
            domain = SCADomain.newInstance("helloworld.composite");
        } catch (Exception e) {
            e.printStackTrace();
        }
        service = domain.getService(HelloWorldService.class, "HelloWorldServiceComponent");
    }

    @AfterClass
    public static void destroy() throws Exception {
        domain.close();
    }
}
