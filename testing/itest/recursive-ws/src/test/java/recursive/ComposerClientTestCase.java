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

package recursive;

import java.io.File;

import junit.framework.Assert;

import org.apache.tuscany.sca.node.Client;
import org.apache.tuscany.sca.node.Node;
import org.apache.tuscany.sca.node.NodeFactory;
import org.apache.tuscany.sca.node.Contribution;
//import org.apache.tuscany.sca.host.embedded.SCATestCaseRunner;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import recursive.Composer;

/**
 * Test case for helloworld web service client 
 */
public class ComposerClientTestCase {

    private Composer composer;
    private Node node;
    
    //private SCATestCaseRunner server;

    @Before
    public void startClient() throws Exception {
        try {
        	NodeFactory factory = NodeFactory.newInstance();
            node = factory.createNode(new File("src/main/resources/Client.composite").toURI().toURL().toString(),
                    new Contribution("TestContribution", new File("src/main/resources/").toURI().toURL().toString()));
            node.start();
            composer = ((Client)node).getService(Composer.class, "ClientComponent/Composer");
    
            //server =  new SCATestCaseRunner(ComposerTestServer.class);
            //server.before();

        } catch (Throwable e) {
            e.printStackTrace();
        }
    }
    
    @Test
    public void testWSClient() throws Exception {
        String msg = composer.Compose("ABC");
        Assert.assertEquals("Composed: ABC", msg);
    }
    
    @After
    public void stopClient() throws Exception {
    	//server.after();
        node.stop();
    }

}
