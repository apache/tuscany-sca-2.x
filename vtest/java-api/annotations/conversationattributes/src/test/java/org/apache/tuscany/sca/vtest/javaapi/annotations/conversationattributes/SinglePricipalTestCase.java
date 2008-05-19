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

package org.apache.tuscany.sca.vtest.javaapi.annotations.conversationattributes;

import javax.security.auth.login.Configuration;

import org.apache.tuscany.sca.vtest.utilities.ServiceFinder;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * 
 */
public class SinglePricipalTestCase {

    protected static String compositeName = "singleprincipal.composite";
    protected static AService aService = null;

    @Before
    public void init() throws Exception {
        try {
            Configuration.getConfiguration();
        } catch (java.lang.SecurityException e) {
            System.out.println("Caught SecurityException");
            System.setProperty("java.security.auth.login.config", this.getClass().getClassLoader()
                .getResource("AJass.config").toString());
        }
        System.out.println("Setting up");
        ServiceFinder.init(compositeName);
        aService = ServiceFinder.getService(AService.class, "AComponent");
    }

    @After
    public void destroy() throws Exception {

        System.out.println("Cleaning up");
        ServiceFinder.cleanup();

    }

    /**
     * Line 1669, 1670
     * <p>
     * singlePrincipal (optional) – If true, only the principal (the user) that
     * started the conversation has authority to continue the conversation. The
     * default value is false.
     */
    @Test(expected = Exception.class)
    public void singlePrincipal() throws Exception {
        aService.testSinglePrincipal();
    }
}
