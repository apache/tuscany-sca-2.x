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

package org.apache.tuscany.sca.node.impl;


import javax.xml.namespace.QName;

import junit.framework.Assert;

import org.apache.tuscany.sca.node.SCANode;
import org.apache.tuscany.sca.node.SCANodeFactory;
import org.junit.Test;

import calculator.CalculatorService;
import calculator.SubtractService;

/**
 * Runs a distributed domain in a single VM by using and in memory 
 * implementation of the distributed domain. The test repeatedly
 * creates and destroys the node to see if memory is being leaked.
 * Looking for leaked memory is a manual task.
 *
 * @version $Rev$ $Date$
 */
public class NodeMemoryTestCaseOFF {
    
    @Test
    public void testDoNothing() throws Exception {
        
    }
    
    //@Test
    public void testNodeMemoryUseage() throws Exception {   
        
        ClassLoader cl = NodeMemoryTestCaseOFF.class.getClassLoader();
        SCANodeFactory nodeFactory;
        SCANode   node;
        CalculatorService calculatorServiceB;
        SubtractService subtractServiceC;

        for(int i=0; i < 40; i++) {
            
            nodeFactory = SCANodeFactory.newInstance();
            node = nodeFactory.createSCANode("http://localhost:8200/node", null);
            node.addContribution("nodeB", cl.getResource("nodeB/"));
            node.addContribution("nodeC", cl.getResource("nodeC/"));
            node.addToDomainLevelComposite(new QName("http://sample", "CalculatorB"));
            node.addToDomainLevelComposite(new QName("http://sample", "CalculatorC"));
            node.start();   
            
            calculatorServiceB = node.getDomain().getService(CalculatorService.class, "CalculatorServiceComponentB");
            subtractServiceC = node.getDomain().getService(SubtractService.class, "SubtractServiceComponentC");
            
            for(int j=0; j < 20; j++){
                Assert.assertEquals(calculatorServiceB.subtract(3, 2), 1.0); 
                Assert.assertEquals(subtractServiceC.subtract(3, 2), 1.0); 
            }
            
            node.destroy();
        }
        
        //com.ibm.jvm.Dump.HeapDump();          
    }          
}
