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

package node;

import java.io.IOException;

import javax.xml.namespace.QName;

import org.apache.tuscany.sca.domain.SCADomain;
import org.apache.tuscany.sca.node.SCANode;
import org.apache.tuscany.sca.node.SCANodeFactory;

import calculator.CalculatorService;

/**
 * This client program shows how to run a distributed SCA node. In this case a 
 * calculator node has been constructed specifically for running the calculator 
 * composite. Internally it creates a representation of a node and associates a 
 * distributed domain with the node. This separation is made different implementations
 * of the distributed domain can be provided. 
 */
public class CalculatorNode {
     
    
    public static void main(String[] args) throws Exception {
        
        // Check that the correct arguments have been provided
        if (null == args || args.length < 2) {
             System.err.println("Useage: java CalculatorNode domainname nodename");   
             System.exit(1);
        }    
        
        try {
            String domainName = args[0];
            String nodeName   = args[1];
            
            ClassLoader cl = CalculatorNode.class.getClassLoader();
             
            SCANodeFactory nodeFactory = SCANodeFactory.newInstance();
            SCANode node = nodeFactory.createSCANode(null, domainName);
            node.addContribution(nodeName, cl.getResource(nodeName + "/"));
            node.addToDomainLevelComposite((QName)null);           
                                         
            // nodeA is the head node and runs some tests while all other nodes
            // simply listen for incoming messages
            if ( nodeName.equals("nodeA") ) {  
                
                // start the domain 
                node.getDomain().start();
                
                // do some application stuff
                CalculatorService calculatorService = 
                    node.getDomain().getService(CalculatorService.class, "CalculatorServiceComponentA");
                
                // Calculate
                System.out.println("3 + 2=" + calculatorService.add(3, 2));
                System.out.println("3 - 2=" + calculatorService.subtract(3, 2));
                System.out.println("3 * 2=" + calculatorService.multiply(3, 2));
                System.out.println("3 / 2=" + calculatorService.divide(3, 2));
                
                // a little hidden loop test to put some load on the nodes
                if (args.length > 2){
                    for (int i=0; i < 1000; i++){
                        // Calculate
                        System.out.println("3 + 2=" + calculatorService.add(3, 2));
                        System.out.println("3 - 2=" + calculatorService.subtract(3, 2));
                        System.out.println("3 * 2=" + calculatorService.multiply(3, 2));
                        System.out.println("3 / 2=" + calculatorService.divide(3, 2));
                    }
                }
                
                // stop all the nodes
                node.getDomain().stop(); 
            } else {
                // start up and wait for messages
                try {
                    System.out.println("Node started (press enter to shutdown)");
                    System.in.read();
                } catch (IOException e) {
                    e.printStackTrace();
                }  
            }
            
            node.destroy();
        
        } catch(Exception ex) {
            System.err.println("Exception in node - " + ex.getMessage());
            ex.printStackTrace(System.err);
        }
    }
}
