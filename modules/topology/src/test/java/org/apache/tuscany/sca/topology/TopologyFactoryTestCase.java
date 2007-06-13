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

import javax.xml.namespace.QName;

import org.apache.tuscany.sca.assembly.AssemblyFactory;
import org.apache.tuscany.sca.assembly.Component;
import org.apache.tuscany.sca.assembly.Composite;
import org.apache.tuscany.sca.assembly.DefaultAssemblyFactory;

import junit.framework.TestCase;


/**
 * Test building of assembly model instances using the assembly factory.
 * 
 * @version $Rev$ $Date$
 */
public class TopologyFactoryTestCase extends TestCase {

    TopologyFactory factory;
    AssemblyFactory assemblyFactory;
    
    public void setUp() throws Exception {
        factory = new DefaultTopologyFactory();
        assemblyFactory = new DefaultAssemblyFactory();
    }

    public void tearDown() throws Exception {
        factory = null;
        assemblyFactory = null;
    }

    public void testCreateTopology() {
        
        // Create a new topology composition
        Composite topology = assemblyFactory.createComposite();
        topology.setName(new QName("http://my.network", "MyTopology"));
        
        // Create SCA node A
        Component nodeA = assemblyFactory.createComponent();
        nodeA.setName("NodeA");
        NodeImplementation implA = factory.createNodeImplementation();
        nodeA.setImplementation(implA);
        topology.getComponents().add(nodeA);
        
        // Create SCA node B
        Component nodeB = assemblyFactory.createComponent();
        nodeB.setName("NodeB");
        NodeImplementation implB = factory.createNodeImplementation();
        nodeB.setImplementation(implB);
        topology.getComponents().add(nodeB);
    }

}
