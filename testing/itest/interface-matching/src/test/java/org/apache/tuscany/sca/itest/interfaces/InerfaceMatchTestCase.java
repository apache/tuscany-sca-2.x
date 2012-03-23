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

package org.apache.tuscany.sca.itest.interfaces;

import java.net.URI;

import junit.framework.Assert;

import org.apache.tuscany.sca.TuscanyRuntime;
import org.apache.tuscany.sca.assembly.SCABinding;
import org.apache.tuscany.sca.binding.ws.WebServiceBinding;
import org.apache.tuscany.sca.node.Node;
import org.apache.tuscany.sca.node.NodeFactory;
import org.apache.tuscany.sca.node.impl.NodeImpl;
import org.junit.Ignore;
import org.junit.Test;
import org.oasisopen.sca.ServiceRuntimeException;
import org.oasisopen.sca.client.SCAClientFactory;

public class InerfaceMatchTestCase {
    
    /**
     * Non-remotable client and service interfaces where the interfaces match.
     * Components running in the same composite/JVM, i.e. no remote registry
     * 
     * @throws Exception
     */
    @Test
    public void testLocal() throws Exception {
        String [] contributions = {"./target/classes"};
        Node node1 = NodeFactory.newInstance().createNode(URI.create("uri:default"), 
                                                                     "org/apache/tuscany/sca/itest/interfaces/match/local/MatchLocal.composite", 
                                                                     contributions);
        node1.start();
        
        ClientComponent local = node1.getService(ClientComponent.class, "LocalClientComponent");
        ParameterObject po = new ParameterObject();
        
        try {
            String response = local.foo1(po);
            Assert.assertEquals("AComponent", response);
        } catch (ServiceRuntimeException ex){
            Assert.fail("Unexpected exception " + ex.toString());
        }
        
        node1.stop(); 
    }
  
    /**
     * Remotable client and service interfaces where the interfaces match.
     * Components running in the separate composite/JVM, i.e. there is a remote registry
     * 
     * @throws Exception
     */
    @Test
    public void testDistributedRemotable() throws Exception {
        
        // Force the remote default binding to be web services
        System.setProperty("org.apache.tuscany.sca.binding.sca.provider.SCABindingMapper.mappedBinding", 
                           "{http://docs.oasis-open.org/ns/opencsa/sca/200912}binding.ws");
        
        String [] contributions = {"./target/classes"};
        Node node1 = NodeFactory.newInstance().createNode(URI.create("uri:default"), 
                                                                     "org/apache/tuscany/sca/itest/interfaces/match/distributed/MatchDistributedClient.composite", 
                                                                     contributions);
        node1.start();

        Node node2 = NodeFactory.newInstance().createNode(URI.create("uri:default"), 
                                                                     "org/apache/tuscany/sca/itest/interfaces/match/distributed/MatchDistributedService.composite", 
                                                                     contributions);
        
        // force default binding on node2 to use a different port from node 1(which will default to 8080
        ((NodeImpl)node2).getConfiguration().addBinding(WebServiceBinding.TYPE, "http://localhost:8081/");
        ((NodeImpl)node2).getConfiguration().addBinding(SCABinding.TYPE, "http://localhost:8081/");
        node2.start();
        
        ClientComponent local = node1.getService(ClientComponent.class, "DistributedClientComponent");
        ParameterObject po = new ParameterObject();
        
        try {
            String response = local.foo1(po);
            Assert.assertEquals("AComponent", response);
        } catch (ServiceRuntimeException ex){
            Assert.fail("Unexpected exception with foo " + ex.toString());
        }
        
        try {
            local.callback("Callback");
            String response = local.getCallbackValue();
            Assert.assertEquals("Callback", response);
        } catch (ServiceRuntimeException ex){
            Assert.fail("Unexpected exception with callback" + ex.toString());
        }        
        
        node1.stop();
        node2.stop();
    }
    
    /**
     * Remotable client and service interfaces where the interfaces match but
     * where there is a parameter that can't be converted to/from XML using JAXB
     * Components running in the separate composite/JVM, i.e. there is a remote registry
     * 
     * @throws Exception
     */
    @Test
    public void testDistributedRemotableNonJAXB() throws Exception {
        
        // Force the remote default binding to be rmi as I want something that doesn't depend on
        // WSDL so that I can test what happens when the registry tries to generate WSDL
        System.setProperty("org.apache.tuscany.sca.binding.sca.provider.SCABindingMapper.mappedBinding", 
                           "{http://tuscany.apache.org/xmlns/sca/1.1}binding.rmi");
        
        String [] contributions = {"./target/classes"};
        Node node1 = NodeFactory.newInstance().createNode(URI.create("uri:default"), 
                                                                     "org/apache/tuscany/sca/itest/interfaces/match/distributed/MatchNonJAXBDistributedClient.composite", 
                                                                     contributions);
        node1.start();

        Node node2 = NodeFactory.newInstance().createNode(URI.create("uri:default"), 
                                                                     "org/apache/tuscany/sca/itest/interfaces/match/distributed/MatchNonJAXBDistributedService.composite", 
                                                                     contributions);
        
        // force default binding on node2 to use a different port from node 1(which will default to 8080
        // Don't need to do this as not testing callbacks here
        //((NodeImpl)node2).getConfiguration().addBinding(WebServiceBinding.TYPE, "http://localhost:8081/");
        //((NodeImpl)node2).getConfiguration().addBinding(SCABinding.TYPE, "http://localhost:8081/");
        node2.start();
        
        ClientComponent local = node1.getService(ClientComponent.class, "DistributedClientComponent");
        ParameterObject po = new ParameterObject();
        po.field1 = "Test String";
        
        try {
            String response = local.foo1(po);
            Assert.assertEquals("Test String", response);
        } catch (ServiceRuntimeException ex){
            Assert.fail("Unexpected exception with foo " + ex.toString());
        }       
        
        node1.stop();
        node2.stop();
    }    
    
    /**
     * Remotable client and service interfaces where the interfaces match and the service has policy.
     * Components running in the separate composite/JVM, i.e. there is a remote registry
     * 
     * @throws Exception
     */
    @Test
    public void testPolicyDistributedRemotable() throws Exception {
        
        
        String [] contributions = {"./target/classes"};
        Node node1 = NodeFactory.newInstance().createNode(URI.create("uri:default"), 
                                                                     "org/apache/tuscany/sca/itest/interfaces/match/distributed/MatchPolicyDistributedClient.composite", 
                                                                     contributions);
        node1.start();

        Node node2 = NodeFactory.newInstance().createNode(URI.create("uri:default"), 
                                                                     "org/apache/tuscany/sca/itest/interfaces/match/distributed/MatchPolicyDistributedService.composite", 
                                                                     contributions);
        // force binding.ws on node2 to use a different port from node 1(which will default to 8080
        ((NodeImpl)node2).getConfiguration().addBinding(WebServiceBinding.TYPE, "http://localhost:8081/");
        node2.start();
        
        ClientComponent local = node1.getService(ClientComponent.class, "DistributedClientComponent");
        ParameterObject po = new ParameterObject();
        
        try {
            String response = local.foo1(po);
            Assert.assertEquals("AComponent", response);
        } catch (ServiceRuntimeException ex){
            Assert.fail("Unexpected exception with foo " + ex.toString());
        }
        
        try {
            local.callback("Callback");
            String response = local.getCallbackValue();
            Assert.assertEquals("Callback", response);
        } catch (ServiceRuntimeException ex){
            Assert.fail("Unexpected exception with callback" + ex.toString());
        }        
        
        node1.stop();
        node2.stop();
    }
    
    /**
     * Remotable client and service interfaces where the interfaces match.
     * Components running in the separate composite/JVM, i.e. there is a remote registry
     * and with binding.ws explicitly configured at the service
     * 
     * @throws Exception
     */
    @Test
    public void testWSDistributedRemotable() throws Exception {
        
        
        String [] contributions = {"./target/classes"};
        Node node1 = NodeFactory.newInstance().createNode(URI.create("uri:default"), 
                                                                     "org/apache/tuscany/sca/itest/interfaces/match/distributed/MatchWSDistributedClient.composite", 
                                                                     contributions);
        node1.start();

        Node node2 = NodeFactory.newInstance().createNode(URI.create("uri:default"), 
                                                                     "org/apache/tuscany/sca/itest/interfaces/match/distributed/MatchWSDistributedService.composite", 
                                                                     contributions);
        
        // force default binding on node2 to use a different port from node 1(which will default to 8080)
        ((NodeImpl)node2).getConfiguration().addBinding(WebServiceBinding.TYPE, "http://localhost:8081/");
        ((NodeImpl)node2).getConfiguration().addBinding(SCABinding.TYPE, "http://localhost:8081/");
        node2.start();
        
        ClientComponent local = node1.getService(ClientComponent.class, "DistributedClientComponent");
        ParameterObject po = new ParameterObject();
        
        try {
            String response = local.foo1(po);
            Assert.assertEquals("AComponent", response);
        } catch (ServiceRuntimeException ex){
            Assert.fail("Unexpected exception with foo " + ex.toString());
        }
        
        try {
            local.callback("Callback");
            String response = local.getCallbackValue();
            Assert.assertEquals("Callback", response);
        } catch (ServiceRuntimeException ex){
            Assert.fail("Unexpected exception with callback" + ex.toString());
        }        
        
        node1.stop();
        node2.stop();
    }  
    
    /**
     * Remotable client and service interfaces where the interfaces match.
     * Components running in the separate composite/JVM, i.e. there is a remote registry
     * Access from an SCALient call to make sure that it is able to connect to the remote
     * registry.
     * 
     * 
     * @throws Exception
     */
    @Test
    public void testDistributedRemotableSCAClient() throws Exception {
        
        // Force the remote default binding to be web services
        System.setProperty("org.apache.tuscany.sca.binding.sca.provider.SCABindingMapper.mappedBinding", 
                           "{http://docs.oasis-open.org/ns/opencsa/sca/200912}binding.ws");
        
        TuscanyRuntime runtime = TuscanyRuntime.newInstance();
        
/*        
        org.apache.tuscany.sca.Node nodeA = runtime.createNode("default");    
        nodeA.installContribution("nodeAContrib", "./target/classes", null, null);
        nodeA.startComposite("node1Contrib", "org/apache/tuscany/sca/itest/interfaces/match/distributed/MatchDistributedClient.composite");
        
        org.apache.tuscany.sca.Node nodeB = runtime.createNode("default");    
        nodeB.installContribution("nodeAContrib", "./target/classes", null, null);
        nodeB.startComposite("node1Contrib", "org/apache/tuscany/sca/itest/interfaces/match/distributed/MatchDistributedService.composite");
*/
        
        String [] contributions = {"./target/classes"};
        Node node1 = NodeFactory.newInstance().createNode(URI.create("uri:default"), 
                                                                     "org/apache/tuscany/sca/itest/interfaces/match/distributed/MatchDistributedClient.composite", 
                                                                     contributions);
        node1.start();

        Node node2 = NodeFactory.newInstance().createNode(URI.create("uri:default"), 
                                                                     "org/apache/tuscany/sca/itest/interfaces/match/distributed/MatchDistributedService.composite", 
                                                                     contributions);
        
        // force default binding on node2 to use a different port from node 1(which will default to 8080
        ((NodeImpl)node2).getConfiguration().addBinding(WebServiceBinding.TYPE, "http://localhost:8081/");
        ((NodeImpl)node2).getConfiguration().addBinding(SCABinding.TYPE, "http://localhost:8081/");
        node2.start();
        
        SCAClientFactory clientFactory = SCAClientFactory.newInstance(URI.create("default"));
        ClientComponent local  = clientFactory.getService(ClientComponent.class, "LocalClientClientComponent");

        ParameterObject po = new ParameterObject();
        
        try {
            String response = local.foo1(po);
            Assert.assertEquals("AComponent", response);
        } catch (ServiceRuntimeException ex){
            Assert.fail("Unexpected exception with foo " + ex.toString());
        }         

/* Used to keep the composites alive when running the next (ignored) test manually
        System.out.println("Press a key to end");
        try {
            System.in.read();
        } catch (Exception ex) {
        }
        System.out.println("Continuing");
*/   
        node1.stop();
        node2.stop();
    }  
    
    /**
     * Allows you to manually call the previous test from a separate VM to 
     * ensure that endpoint serialization to the client work OK. It's not 
     * intended to run as part of the test suite. 
     * 
     * 
     * @throws Exception
     */
    @Test
    @Ignore
    public void testDistributedRemotableSCAClientSeparateVM() throws Exception {
        // Force the remote default binding to be web services
        System.setProperty("org.apache.tuscany.sca.binding.sca.provider.SCABindingMapper.mappedBinding", 
                           "{http://docs.oasis-open.org/ns/opencsa/sca/200912}binding.ws");
        
        // Make a reference target point across VMs to a component that has callback services
        String [] contributions = {"./target/classes"};
        
        Node node3 = NodeFactory.newInstance().createNode(URI.create("uri:default"), 
                "org/apache/tuscany/sca/itest/interfaces/match/distributed/MatchDistributedClientClient.composite", 
                contributions);
        node3.start();
        
        SCAClientFactory clientFactory = SCAClientFactory.newInstance(URI.create("default"));
        ClientComponent clientClient  = clientFactory.getService(ClientComponent.class, "DistributedClientClientComponent");

        ParameterObject po = new ParameterObject();
        
        try {
            String response = clientClient.foo1(po);
            Assert.assertEquals("AComponent", response);
        } catch (ServiceRuntimeException ex){
            Assert.fail("Unexpected exception with foo " + ex.toString());
        } 
        
        // Make an SCAClient point across VMs to a component that has callback services
        ClientComponent client  = clientFactory.getService(ClientComponent.class, "DistributedClientComponent");
        
        try {
            String response = client.foo1(po);
            Assert.assertEquals("AComponent", response);
        } catch (ServiceRuntimeException ex){
            Assert.fail("Unexpected exception with foo " + ex.toString());
        }  
        
        node3.stop();
    }
}
