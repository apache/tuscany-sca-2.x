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

package helloworld;

import junit.framework.Assert;

import org.apache.tuscany.host.embedded.SCARuntime;
import org.apache.tuscany.host.embedded.SCATestCaseRunner;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.osoa.sca.ComponentContext;
import org.osoa.sca.ServiceReference;

/**
 * Test case for helloworld web service client 
 */
public class HelloWorldClientTestCase {

    private HelloWorldService helloWorldService;
    
    private SCATestCaseRunner server;

    @Before
    public void startClient() throws Exception {
        try {
            SCARuntime.start("helloworldwsclient.composite");
            
            ComponentContext context = SCARuntime.getComponentContext("HelloWorldServiceComponent");
            ServiceReference<HelloWorldService> service = context.createSelfReference(HelloWorldService.class);
            helloWorldService = service.getService();
    
            server =  new SCATestCaseRunner(HelloWorldServerTest.class);
            server.before();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @Test
    public void testWSClient() throws Exception {
        String msg = helloWorldService.getGreetings("Smith");
        Assert.assertEquals("Hello Smith", msg);
    }
    
    @After
    public void stopClient() throws Exception {
    	server.after();
    	SCARuntime.stop();
    }

}
