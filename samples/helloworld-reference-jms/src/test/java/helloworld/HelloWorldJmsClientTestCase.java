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

import org.apache.tuscany.sca.host.embedded.SCADomain;
import org.apache.tuscany.sca.host.embedded.SCATestCaseRunner;
import org.apache.tuscany.sca.host.jms.activemq.ActiveMQModuleActivator;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


/**
 * Test case for helloworld web service client 
 */
public class HelloWorldJmsClientTestCase {

    private HelloWorldService helloWorldService;
    private HelloWorldService helloTuscanyService;
    private SCADomain scaClientDomain;
    private SCADomain scaServiceDomain;
   

    @Before
    public void startClient() throws Exception {
        try {
            ActiveMQModuleActivator.startBroker();
            scaServiceDomain = SCADomain.newInstance("helloworldjmsservice.composite");
            scaClientDomain = SCADomain.newInstance("helloworldjmsreference.composite");
            helloWorldService = scaClientDomain.getService(HelloWorldService.class, "HelloWorldServiceComponent");
                
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }
    
    @Test
    public void testWSClient() throws Exception {
        String msg = helloWorldService.getGreetings("Smith");
        Assert.assertEquals("Hello Smith", msg);
        Thread.sleep(2000);
   }
    
    
    @After
    public void stopClient() throws Exception {
        scaServiceDomain.close();
        // TODO - causes problems on shudown
        //scaClientDomain.close();
    }

}
