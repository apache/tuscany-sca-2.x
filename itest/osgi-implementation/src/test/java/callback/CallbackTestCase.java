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

package callback;

import junit.framework.TestCase;

import org.apache.tuscany.sca.host.embedded.SCADomain;

import callback.client.CallbackClient;

import util.OSGiTestUtil;

public abstract class CallbackTestCase extends TestCase {

    private SCADomain scaDomain;
    private CallbackClient aCallbackClient;
    
    protected String compositeName;
    
   
    public CallbackTestCase(String compositeName) {
        super();
        this.compositeName = compositeName;
    }

   

    protected void setUp() throws Exception {
        OSGiTestUtil.setUpOSGiTestRuntime();
        
        scaDomain = SCADomain.newInstance(compositeName);
        aCallbackClient = scaDomain.getService(CallbackClient.class, "CallbackClient");
    }

    
    protected void tearDown() throws Exception {
        scaDomain.close();
        
        OSGiTestUtil.shutdownOSGiRuntime();
    }

    public void test() throws Exception {
        aCallbackClient.run(); 
    }

}
