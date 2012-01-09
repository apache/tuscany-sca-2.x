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

package org.apache.tuscany.sca.binding.ws.launcher.axis2;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.apache.tuscany.sca.binding.ws.jaxws.external.client.HelloWorldClientLauncher;
import org.apache.tuscany.sca.binding.ws.jaxws.external.service.HelloWorldServiceLauncher;
import org.apache.tuscany.sca.binding.ws.jaxws.sca.Bar;
import org.apache.tuscany.sca.binding.ws.jaxws.sca.Foo;
import org.apache.tuscany.sca.node.Contribution;
import org.apache.tuscany.sca.node.Node;
import org.apache.tuscany.sca.node.NodeFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class DocLitWrappedDOMTestCase {

    private Node node;
    private HelloWorldServiceLauncher externalService;
    private HelloWorldClientLauncher externalClient;

    @Before
    public void setUp() throws Exception {
        // Start the external service
        externalService = new HelloWorldServiceLauncher();
        externalService.createService();
       
        // Start the SCA contribution
        node = NodeFactory.newInstance().createNode(new Contribution("doc-lit-wrapped", "../contribution-doc-lit-wrapped-dom/target/itest-ws-contribution-doc-lit-wrapped-dom.jar"));
        node.start();
        
        // start the external client
        try {
            externalClient = new HelloWorldClientLauncher();
            externalClient.createClient();
        } catch (Exception ex) {
            ex.printStackTrace();
            throw ex;
        }
    }

/*
    @Test
    public void testWait() throws Exception {
        System.out.println("Press a key");
        System.in.read();
    }
*/
  
    @Test
    public void testGetGreetingsDOM() throws Exception { 
        assertEquals("Hello Fred", externalClient.getGreetings("Fred"));
    }
    
    
    @After
    public void tearDown() throws Exception {
        node.stop();
        externalClient.destroyClient();
        externalService.destoryService();
    }

}
