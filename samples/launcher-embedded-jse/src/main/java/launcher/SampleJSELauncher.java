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

package launcher;

import org.apache.tuscany.sca.node.Contribution;
import org.apache.tuscany.sca.node.Node;
import org.apache.tuscany.sca.node.NodeFactory;
import org.oasisopen.sca.ServiceRuntimeException;

import calculator.CalculatorService;


/**
 * This client program shows how to create an embedded SCA runtime, start it,
 * and locate and invoke a SCA component 
 */
public class SampleJSELauncher {
    
    public static void main(String[] args) throws Exception {
        SampleJSELauncher launcher = new SampleJSELauncher();
        
        String contribution = null;
        
        if (args == null || args.length != 1){
            System.out.println("Please provide the name of the sample contribution to run as a parameter");
            System.out.println("Running binding-ws-calculator by default");
            contribution = "binding-ws-calculator";
        } else {
            contribution = args[0];
        }   
        
        if (contribution.equals("binding-ws-calculator")){
            launcher.launchBindingWSCalculator();
        } else {
            System.out.println("Sample contribution " + contribution + "not found");
        }
        
    }
    
    public Node startNode(Contribution... contributions){
        Node node = NodeFactory.newInstance().createNode(contributions);
        node.start();
        return node;
    }
    
    public void stopNode(Node node){
        node.stop();
    }
    
    public void launchBindingWSCalculator(){
        Node node = startNode(new Contribution("c1", "../binding-ws-calculator/target/classes"));
        CalculatorService calculator = node.getService(CalculatorService.class, "CalculatorServiceComponent");
        
        // TODO - could use JUnit assertions but don't want to have to handle JUnit dependency from Ant script
        if (calculator.add(3, 2) != 5.0){
            throw new SampleLauncherException();
        }
        
        stopNode(node);
    }
    
}
