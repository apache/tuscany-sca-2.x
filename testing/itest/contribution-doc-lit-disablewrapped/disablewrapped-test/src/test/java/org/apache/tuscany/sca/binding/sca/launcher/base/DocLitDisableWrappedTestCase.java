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

package org.apache.tuscany.sca.binding.sca.launcher.base;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.net.URI;

import org.apache.tuscany.sca.binding.sca.jaxb.iface.GuessAndGreetDisableWrapped;
import org.apache.tuscany.sca.binding.sca.jaxb.iface.GuessAndGreetService;
import org.apache.tuscany.sca.binding.sca.jaxb.iface.SendGuessAndName;
import org.apache.tuscany.sca.binding.sca.jaxb.iface.SendGuessAndNameResponse;
import org.apache.tuscany.sca.node.Contribution;
import org.apache.tuscany.sca.node.Node;
import org.apache.tuscany.sca.node.NodeFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.oasisopen.sca.client.SCAClientFactory;

public class DocLitDisableWrappedTestCase {

    private Node node;

    @Before
    public void setUp() throws Exception {       
        // Start the SCA contribution
        node = NodeFactory.newInstance().createNode(new Contribution("doc-lit-disablewrapped", "../disablewrapped-app/target/disablewrapped-app.jar"));
        node.start();
    }

/*
    @Test
    public void testWait() throws Exception {
        System.out.println("Press a key");
        System.in.read();
    }
*/
  
    @Ignore
    @Test
    public void testGetGreetings() throws Exception {
        GuessAndGreetDisableWrapped service = SCAClientFactory.newInstance(URI.create("default")).getService(GuessAndGreetDisableWrapped.class, "GuessAndGreetComponent");
        SendGuessAndName parameters = new SendGuessAndName();

        parameters.setGuess(1);
        parameters.setName("Petra");
        parameters.setPerson(null);
        
        SendGuessAndNameResponse response = service.sendGuessAndName(parameters); 
        
        assertEquals("Petra", response.getPerson().getFirstName());
        assertEquals("Winnder!", response.getPerson().getGreeting());
        assertEquals(null, response.getPerson().getChild());
    }
    
    @Ignore
    @Test
    public void testGetGreetingsComplex() throws Exception {        
        GuessAndGreetDisableWrapped service = SCAClientFactory.newInstance(URI.create("default")).getService(GuessAndGreetDisableWrapped.class, "GuessAndGreetComponent");

    }  
    
    @After
    public void tearDown() throws Exception {
        node.stop();
    }

}
