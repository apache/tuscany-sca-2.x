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

import java.net.URI;

import org.apache.tuscany.sca.node.Contribution;
import org.apache.tuscany.sca.node.Node;
import org.oasisopen.sca.NoSuchDomainException;
import org.oasisopen.sca.NoSuchServiceException;
import org.oasisopen.sca.client.SCAClientFactory;

import calculator.CalculatorService;


/**
 * This client program shows how to create an embedded SCA runtime, start it,
 * and locate and invoke a SCA component 
 */
public class SampleOSGILauncherBindingWS extends RuntimeIntegration {
    
    public static void main(String[] args) throws Exception {
        SampleOSGILauncherBindingWS launcher = new SampleOSGILauncherBindingWS();
        
        // launcher.launchBindingWSCalculator();
        
        Node node = launcher.startNode(new Contribution("c1", "../../learning-more/binding-ws/calculator-contribution/target/sample-binding-ws-calculator-contribution.jar"));
        
        CalculatorService calculator = node.getService(CalculatorService.class, "CalculatorServiceComponent");
               
        double result = calculator.add(3, 2);
        System.out.println("3 + 2 = " + result);
        if (result != 5.0){
            throw new SampleLauncherException();
        }
        
        launcher.stopNode(node);
    }
       
    /**
     * The contribution-binding-sca-calculator contribution includes a client component 
     * that calls the CalculatorServiceComponent from an operation marked by @Init. 
     */
    public void launchBindingSCACalculator(){
        Node node = startNode(new Contribution("c1", "../../learning-more/binding-sca/contribution-calculator/target/sample-contribution-binding-sca-calculator.jar"));
        
        stopNode(node);
    }    
    
    /*
     * Using a Tuscany specific mechanism for getting at local service proxies
     */
    public void launchBindingWSCalculator() throws NoSuchDomainException, NoSuchServiceException{
        Node node = startNode(new Contribution("c1", "../../learning-more/binding-ws/contribution-calculator/target/sample-contribution-binding-ws-calculator.jar"));
        
        CalculatorService calculator = node.getService(CalculatorService.class, "CalculatorServiceComponent");
               
        double result = calculator.add(3, 2);
        System.out.println("3 + 2 = " + result);
        if (result != 5.0){
            throw new SampleLauncherException();
        }
        
        stopNode(node);
    }
    
}
