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
package org.apache.tuscany.sca.test;

import junit.framework.TestCase;

import org.apache.tuscany.sca.host.embedded.SCADomain;

public class CallBackApiTestCase extends TestCase {

    private static SCADomain domain;
    private CallBackApiClient aCallBackClient;

    /**
     * This function uses the Instance of CallBackApiClient.class <br>
     * This function calls the run function defined in the CallBackApiClinet Interface <br>
     * which in turn executes the following test cases. <br>
     * 1. Basic callback patterns <br>
     * 2. Test in which the target does not call back to the client <br>
     * 3. Test in which the target calls back multiple times to the client.
     */
    public void testCallBackBasic() {
        aCallBackClient.run();
    }

    /**
     * This function creates the SCADomain instance and gets an Instance of CallBackApiClient.class
     */
    @Override
    protected void setUp() throws Exception {
        if (domain == null) {
            domain = SCADomain.newInstance("CallBackApiTest.composite");
        }

        aCallBackClient = domain.getService(CallBackApiClient.class, "CallBackApiClient");
    }

    /**
     * This function destroys the SCADomain instance that was created in setUp()
     */
    @Override
    protected void tearDown() throws Exception {
        domain.close();
    }

}
