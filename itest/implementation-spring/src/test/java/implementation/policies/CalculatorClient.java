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

package implementation.policies;

import javax.security.auth.login.Configuration;
import org.apache.tuscany.sca.node.Client;
import org.apache.tuscany.sca.node.Contribution;
import org.apache.tuscany.sca.node.ContributionLocationHelper;
import org.apache.tuscany.sca.node.Node;
import org.apache.tuscany.sca.node.NodeFactory;
import calculator.CalculatorService;


/**
 * This client program shows how to create an SCA runtime, start it,
 * and locate and invoke a SCA component
 */
public class CalculatorClient {
    public static void main(String[] args) throws Exception {
        try {
            Configuration secConf = Configuration.getConfiguration();
        } catch (java.lang.SecurityException e) {
            System.setProperty("java.security.auth.login.config", CalculatorClient.class.getClassLoader()
                .getResource("implementation/policies/CalculatorJass.config").toString());
        }

        String location = ContributionLocationHelper.getContributionLocation("implementation/policies/ImplementationPolicies.composite");
        Node node = NodeFactory.newInstance().createNode("implementation/policies/ImplementationPolicies.composite", new Contribution("c1", location));
        node.start();   
              
        CalculatorService calculatorService = 
            ((Client)node).getService(CalculatorService.class, "CalculatorServiceComponent");

        // Calculate
        System.out.println("Calling CalculatorServiceComponent configured with 'logging' " +
                "policy for subtract and divide operations...");
        System.out.println("3 + 2=" + calculatorService.add(3, 2));
        System.out.println("3 - 2=" + calculatorService.subtract(3, 2));
        System.out.println("3 * 2=" + calculatorService.multiply(3, 2));
        System.out.println("3 / 2=" + calculatorService.divide(3, 2));
        
        calculatorService = 
            ((Client)node).getService(CalculatorService.class, "AnotherCalculatorServiceComponent");

        // Calculate
        System.out.println("Calling CalculatorServiceComponent configured with 'logging' " +
                "for all operations in the implementation...");
        System.out.println("3 + 2=" + calculatorService.add(3, 2));
        System.out.println("3 - 2=" + calculatorService.subtract(3, 2));
        System.out.println("3 * 2=" + calculatorService.multiply(3, 2));
        System.out.println("3 / 2=" + calculatorService.divide(3, 2));

        node.stop();
        System.out.println("Bye");
    }

}
