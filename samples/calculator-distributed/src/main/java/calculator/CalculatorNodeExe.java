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

package calculator;

import java.io.IOException;

import org.apache.tuscany.sca.host.embedded.SCADomain;
import org.apache.tuscany.sca.topology.xml.Constants;

/**
 * This client program shows how to run a distributed SCA node. In this case a 
 * calculator node has been constructed specifically for running the calculator 
 * composite. 
 */
public class CalculatorNodeExe {
    
    public static void main(String[] args) throws Exception {
        
        // Check that the correct arguments have been provided
        if (null == args || args.length != 1) {
             System.err.println("Useage: java CalculatorNodeExe nodename");   
             System.exit(1);
        }    
        
        String nodeName = args[0];
                
        // create and start the node. 
        // this creates domains based on the runtime.topology file that
        // must be present on the classpath
        CalculatorNode node = new CalculatorNode(Constants.DEFAULT_DOMAIN,nodeName);
        SCADomain domain = node.start(); 
        
        // nodeA is the head node and runs some tests while all other nodes
        // simply listen for incoming messages
        if ( nodeName.equals("nodeA") ) {            
            // do some application stuff
            CalculatorService calculatorService = 
                domain.getService(CalculatorService.class, "CalculatorServiceComponent");
    
            // Calculate
            System.out.println("3 + 2=" + calculatorService.add(3, 2));
            System.out.println("3 - 2=" + calculatorService.subtract(3, 2));
            System.out.println("3 * 2=" + calculatorService.multiply(3, 2));
            System.out.println("3 / 2=" + calculatorService.divide(3, 2));
        } else {
            // start up and wait for messages
            try {
                System.out.println("Node started (press enter to shutdown)");
                System.in.read();
            } catch (IOException e) {
                e.printStackTrace();
            }  
        }
        
        // stop the node and all the domains in it 
        node.stop(); 
    }
}
