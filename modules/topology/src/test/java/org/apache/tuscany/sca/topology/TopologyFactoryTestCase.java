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
    
    Component calculatorServiceComponent;
    Component addServiceComponent;
    Component subtractServiceComponent;

    public void setUp() throws Exception {
        factory = new DefaultTopologyFactory();
        assemblyFactory = new DefaultAssemblyFactory();

        // Create test components, they would normally be given from an SCA domain 
        calculatorServiceComponent = assemblyFactory.createComponent();
        calculatorServiceComponent.setName("CalculatorServiceComponent");
        addServiceComponent = assemblyFactory.createComponent();
        addServiceComponent.setName("AddServiceComponent");
        subtractServiceComponent = assemblyFactory.createComponent();
        subtractServiceComponent.setName("SubtractServiceComponent");
    }

    public void tearDown() throws Exception {
        factory = null;
        assemblyFactory = null;
        calculatorServiceComponent = null;
        addServiceComponent = null;
        subtractServiceComponent = null;
    }

    public void testCreateTopology() {
        
        // Create a new topology composition
        Composite topology = assemblyFactory.createComposite();
        topology.setName(new QName("http://my.network", "MyTopology"));
        
        // Create SCA processor A implementation
        // Configure it to run CalculatorServiceComponent
        ProcessorImplementation implA = factory.createProcessorImplementation();
        implA.getComponents().add(calculatorServiceComponent);
        
        // Create SCA processor B implementation
        // Configure it to run Add and SubtractServiceComponent
        ProcessorImplementation implB = factory.createProcessorImplementation();
        implB.getComponents().add(addServiceComponent);
        implB.getComponents().add(subtractServiceComponent);

        // Create SCA processor A
        Component processorA = assemblyFactory.createComponent();
        processorA.setName("ProcessorA");
        processorA.setImplementation(implA);
        topology.getComponents().add(processorA);
        
//        // Create SCA processor APrime
//        Component processorAPrime = assemblyFactory.createComponent();
//        processorAPrime.setName("ProcessorAPrime");
//        processorAPrime.setImplementation(implA);
//        topology.getComponents().add(processorAPrime);
        
        // Create SCA processor B
        Component processorB = assemblyFactory.createComponent();
        processorB.setName("ProcessorB");
        processorB.setImplementation(implB);
        topology.getComponents().add(processorB);
    }

}
