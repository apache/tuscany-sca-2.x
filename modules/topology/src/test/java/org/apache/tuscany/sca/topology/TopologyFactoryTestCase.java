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
package org.apache.tuscany.sca.topology;

import java.util.List;

import junit.framework.TestCase;


/**
 * Test building of assembly model instances using the assembly factory.
 * 
 * @version $Rev$ $Date$
 */
public class TopologyFactoryTestCase extends TestCase {

    TopologyFactory topologyFactory;
    
    @Override
    public void setUp() throws Exception {
        topologyFactory = new DefaultTopologyFactory();
    }

    @Override
    public void tearDown() throws Exception {
        topologyFactory = null;
    }

    public void testCreateRuntime() {
        Runtime runtime = topologyFactory.createRuntime();
        
        List<Node> nodeList = runtime.getNodes();
        assertNotNull(nodeList);
    }

    public void testCreateNode() {
        Node node = topologyFactory.createNode();
        
        node.setName("nodeA");
        assertEquals(node.getName(), "nodeA");
        
        List<Scheme> schemeList = node.getSchemes("domainA");
        assertNotNull(schemeList);   
        
        List<Component> componentList = node.getComponents("domainA");
        assertNotNull(componentList); 
    }
    
    public void testCreateScheme() {
        Scheme scheme = topologyFactory.createScheme();
        
        scheme.setName("http");
        assertEquals(scheme.getName(), "http");
        
        scheme.setBaseURL("http://localhost:8080");
        assertEquals(scheme.getBaseURL(), "http://localhost:8080");
        
        scheme.setDomainName("domainA");
        assertEquals(scheme.getDomainName(), "domainA");
        
        Node node = topologyFactory.createNode();
        List<Scheme> schemeList = node.getSchemes("domainA");
        schemeList.add(scheme);
        List<Scheme> schemeList1 = node.getSchemes("domainA");
        Scheme scheme1 = schemeList1.get(0);
        
        assertEquals(scheme1.getName(), "http");
        
    }
    
    public void testCreateComponent() {
        Component component = topologyFactory.createComponent();
        
        component.setName("componentA");
        assertEquals(component.getName(), "componentA");
               
        component.setDomainName("domainA");
        assertEquals(component.getDomainName(), "domainA");
        
        Node node = topologyFactory.createNode();
        List<Component> componentList = node.getComponents("domainA");
        componentList.add(component);
        List<Component> componentList1 = node.getComponents("domainA");
        Component component1 = componentList1.get(0);
        
        assertEquals(component1.getName(), "componentA");        
    }    
    
}
