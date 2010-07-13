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

import calculator.CalculatorService;


/**
 * This client program shows how to create an embedded SCA runtime, load a contribution,
 * start it and, in some cases, locate and invoke an SCA component 
 */
public class SampleJSELauncher extends RuntimeIntegration {
    
    protected boolean waitBeforeStopping = false;
    
    public static void main(String[] args) throws Exception {
        
        // get the contribution name from the 1st argument it there is one
  
        
        // assume that more than one argument means that the caller wants to
        // keep the SCA application running while other clients use the services
        boolean waitBeforeStopping = false;
        
        if (args != null && args.length > 1 && args[1].equals("waitBeforeStopping")){
            waitBeforeStopping = true;
        }
        
        SampleJSELauncher launcher = new SampleJSELauncher(waitBeforeStopping);
        
        launcher.launchImplementationJavaCalculatorAsync();
               
    }
    
    public SampleJSELauncher(boolean waitBeforeStopping){
        this.waitBeforeStopping = waitBeforeStopping;
    }
    
    /**
     * Wait for user input. Allows us to keep the Tuscany runtime and the SCA application
     * running while other clients access the services provided 
     */
    public void waitBeforeStopping(){
        if (waitBeforeStopping){
            try {
                System.out.println("Press key to continue");
                int input = System.in.read();
            } catch (Exception ex) {
                // do nothing
            }
        }
    }
       
    /**
     * The contribution-binding-sca-calculator contribution includes a client component 
     * that calls the CalculatorServiceComponent from an operation marked by @Init. 
     */
    public void launchImplementationJavaCalculatorAsync(){
        Node node = startNode(new Contribution("c1", "../sample-contribution-implementation-java-calculator-async/target/sample-contribution-implementation-java-calculator-async.jar"));
        waitBeforeStopping();
        stopNode(node);
    }    
    

//    public void launchImplementationJavaCalculator(){
//        Node node = startNode(new Contribution("c1", "../contribution-implementation-java-calculator/target/classes"));
//        waitBeforeStopping();
//        stopNode(node);
//    }      
    
}
