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

import org.apache.tuscany.sca.node.SCAClient;
import org.apache.tuscany.sca.node.SCANode;
import org.apache.tuscany.sca.node.launcher.NodeLauncher;
import org.osoa.sca.ServiceRuntimeException;

import calculator.CalculatorService;

public class LaunchCalculatorNodeA {
    public static void main(String[] args) throws Exception {
        
        SCANode node = null;
        try {
            
            NodeLauncher nodeLauncher = NodeLauncher.newInstance();
            node = nodeLauncher.createNodeFromURL("http://localhost:9990/node-config/NodeA");

            node.start();
            
            // get a reference to the calculator component
            SCAClient client = (SCAClient)node;
            CalculatorService calculatorService = 
                client.getService(CalculatorService.class, "CalculatorServiceComponentA");
            
            // Calculate
            System.out.println("3 + 2=" + calculatorService.add(3, 2));
            System.out.println("3 - 2=" + calculatorService.subtract(3, 2));
            System.out.println("3 * 2=" + calculatorService.multiply(3, 2));
            System.out.println("3 / 2=" + calculatorService.divide(3, 2));
            
            if (args.length > 1){
                for (int i=0; i < 1000; i++){
                    // Calculate
                    System.out.println("3 + 2=" + calculatorService.add(3, 2));
                    System.out.println("3 - 2=" + calculatorService.subtract(3, 2));
                    System.out.println("3 * 2=" + calculatorService.multiply(3, 2));
                    System.out.println("3 / 2=" + calculatorService.divide(3, 2));
                }
            } 
            
            node.stop();
            
        } catch (Exception e) {
            throw new ServiceRuntimeException(e);
        }        
    }
}
