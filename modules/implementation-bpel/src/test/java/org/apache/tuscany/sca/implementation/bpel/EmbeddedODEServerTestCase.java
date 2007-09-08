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

package org.apache.tuscany.sca.implementation.bpel;

import java.io.File;
import java.net.URL;

import junit.framework.TestCase;

import org.apache.tuscany.sca.implementation.bpel.ode.EmbeddedODEServer;
import org.apache.tuscany.sca.implementation.bpel.ode.ODEDeployment;

/**
 * @version $Rev$ $Date$
 */
public class EmbeddedODEServerTestCase extends TestCase {

    private EmbeddedODEServer odeServer;
    
    @Override
    protected void setUp() throws Exception {
        this.odeServer = new EmbeddedODEServer();
        odeServer.init();
    }
    
    @Override
    protected void tearDown() throws Exception {
        odeServer.stop();
    }
    
    public void testProcessInvocation() throws Exception{
        if(! odeServer.isInitialized()) {
            fail("Server did not start !");
        }
        
        URL deployURL = getClass().getClassLoader().getResource("deploy.xml");
        File deploymentDir = new File(deployURL.toURI().getPath()).getParentFile();
        System.out.println(deploymentDir);
        if(odeServer.isInitialized()) {
            odeServer.deploy(new ODEDeployment(deploymentDir));
        }
    }

}
