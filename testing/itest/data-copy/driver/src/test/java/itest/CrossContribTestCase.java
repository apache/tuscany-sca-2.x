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

package itest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import itest.common.intf.ClientIntf;

import java.net.URI;

import org.apache.tuscany.sca.assembly.Binding;
import org.apache.tuscany.sca.assembly.SCABinding;
import org.apache.tuscany.sca.binding.ws.WebServiceBinding;
import org.apache.tuscany.sca.node.Node;
import org.apache.tuscany.sca.node.NodeFactory;
import org.apache.tuscany.sca.node.impl.NodeImpl;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

/**
 * This shows how to test the Calculator service component.
 */
public class CrossContribTestCase {

    private static URI domainURI = URI.create("CrossContribTestCase");
    private static Node node;

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        node = NodeFactory.getInstance().createNode(domainURI, "../service/target/classes", "../client/target/classes", "../common/target/classes");
        node.start();
    }

    @Test
    public void testJAXBCrossContributionSCA() throws Exception {
        ClientIntf client = node.getService(ClientIntf.class, "ClientSCA");
        assertNotNull(client);
        client.callJAXBCrossContribution();
    }
    
    // TUSCANY-3941 - make sure binding.sca is matched properly when 
    //                it's used to carry the target URI. Not much to do with
    //                JAXB but this is a convenient test
    @Test
    public void testJAXBCrossContributionSCAAlternative() throws Exception {
        ClientIntf client = node.getService(ClientIntf.class, "ClientSCAAlternative");
        assertNotNull(client);
        client.callJAXBCrossContribution();
        
        // Get the binding from the ClientSCA component
        Binding binding = ((NodeImpl)node).getDomainComposite().getComponents().get(2).getReferences().get(0).getEndpointReferences().get(0).getBinding();
        assertEquals(true, binding instanceof SCABinding);
        
        // Get the binding from the ClientSCAAlternative component
        Binding alternativeBinding = ((NodeImpl)node).getDomainComposite().getComponents().get(1).getReferences().get(0).getEndpointReferences().get(0).getBinding();
        assertEquals(true, alternativeBinding instanceof WebServiceBinding);
    }


    @Test
    public void testJAXBCrossContributionWS() throws Exception {
        ClientIntf client = node.getService(ClientIntf.class, "ClientWS");
        assertNotNull(client);
        client.callJAXBCrossContribution();
    }

    @Test
    public void testSameObjectSCA() throws Exception {
        ClientIntf client = node.getService(ClientIntf.class, "ClientSCA");
        assertNotNull(client);
        client.callObjectGraphCheck(1);
    }

    @Test
    public void testDifferentObjectSCA() throws Exception {
        ClientIntf client = node.getService(ClientIntf.class, "ClientSCA");
        assertNotNull(client);
        client.callObjectGraphCheck(2);
    }

    @Test
    public void testSameObjectRMI() throws Exception {
        ClientIntf client = node.getService(ClientIntf.class, "ClientRMI");
        assertNotNull(client);
        client.callObjectGraphCheck(1);
    }

    @Test
    public void testDifferentObjectRMI() throws Exception {
        ClientIntf client = node.getService(ClientIntf.class, "ClientRMI");
        assertNotNull(client);
        client.callObjectGraphCheck(2);
    }

    // We expect that object identity will be different across binding.ws, 
    // so remove this test.
    //  public void testSameObjectWS() throws Exception {

    @Test
    public void testDifferentObjectWS() throws Exception {
        ClientIntf client = node.getService(ClientIntf.class, "ClientWS");
        assertNotNull(client);
        client.callObjectGraphCheck(2);
    }

    @Test  // Fails for 3894, (rename test)
    public void testDOMSCA() throws Exception {
        ClientIntf client = node.getService(ClientIntf.class, "ClientSCA");
        assertNotNull(client);
        client.callDOM();
    }

    @Test    // Output DB treated as DOM, rather than String
    public void testDOMWS() throws Exception {
        ClientIntf client = node.getService(ClientIntf.class, "ClientWS");
        assertNotNull(client);
        client.callDOM();
    }
    
    @Test
    @Ignore
    public void testJSONSCA() throws Exception {
        ClientIntf client = node.getService(ClientIntf.class, "ClientSCA");
        assertNotNull(client);
        client.callJSON();
    }

    @Test
    @Ignore
    public void testJSONWS() throws Exception {
        ClientIntf client = node.getService(ClientIntf.class, "ClientWS");
        assertNotNull(client);
        client.callJSON();
    }



    @Test
    public void testDOMIdentitySCA() throws Exception {
        ClientIntf client = node.getService(ClientIntf.class, "ClientSCA");
        assertNotNull(client);
        client.testRoundTripDOMIdentity();
    }

    @Test
    public void testDOMIdentityWS() throws Exception {
        ClientIntf client = node.getService(ClientIntf.class, "ClientSCA");
        assertNotNull(client);
        client.testRoundTripDOMIdentity();
    }


    @AfterClass
    public static void tearDownAfterClass() throws Exception {
        if (node != null) {
            node.stop();
        }
    }
}
