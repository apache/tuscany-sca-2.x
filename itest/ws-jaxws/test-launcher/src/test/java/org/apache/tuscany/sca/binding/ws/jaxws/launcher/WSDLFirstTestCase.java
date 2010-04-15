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

package org.apache.tuscany.sca.binding.ws.jaxws.launcher;

import junit.framework.TestCase;

import org.apache.tuscany.sca.binding.ws.jaxws.external.HelloWorldClientLauncher;
import org.apache.tuscany.sca.binding.ws.jaxws.external.HelloWorldServiceLauncher;
import org.apache.tuscany.sca.node.Contribution;
import org.apache.tuscany.sca.node.Node;
import org.apache.tuscany.sca.node.NodeFactory;

public class WSDLFirstTestCase extends TestCase {

    private Node node;

    @Override
    protected void setUp() throws Exception {
        // Start the external service
        HelloWorldServiceLauncher.main(null);
       
        // Start the SCA contribution
        node = NodeFactory.newInstance().createNode(new Contribution("common", "../common-contribution/target/classes"),
                                                    new Contribution("wsdl-first", "../wsdl-first-contribution/target/classes"));
        node.start();
    }
    
    public void testCalculator() throws Exception {
        HelloWorldClientLauncher.main(null);
    }  
    
    public void testWait1() throws Exception {
        System.out.println("Press a key");
        System.in.read();
    }     
    
    public void testCalculator1() throws Exception {
        HelloWorldClientLauncher.main(null);
    }     
    
    public void testCalculator2() throws Exception {
        HelloWorldClientLauncher.main(null);
    }  
    
    public void testCalculator3() throws Exception {
        HelloWorldClientLauncher.main(null);
    }  
    
    public void testCalculator4() throws Exception {
        HelloWorldClientLauncher.main(null);
    }    
    
    public void testCalculator5() throws Exception {
        HelloWorldClientLauncher.main(null);
    } 
    
    public void testCalculator6() throws Exception {
        HelloWorldClientLauncher.main(null);
    } 
    
    public void testCalculator7() throws Exception {
        HelloWorldClientLauncher.main(null);
    } 
    
    public void testCalculator8() throws Exception {
        HelloWorldClientLauncher.main(null);
    } 
    
    public void testCalculator9() throws Exception {
        HelloWorldClientLauncher.main(null);
    } 
    
    public void testCalculator10() throws Exception {
        HelloWorldClientLauncher.main(null);
    } 
    
    public void testCalculator11() throws Exception {
        HelloWorldClientLauncher.main(null);
    }  
    
    public void testCalculator12() throws Exception {
        HelloWorldClientLauncher.main(null);
    }  
    
    public void testCalculator13() throws Exception {
        HelloWorldClientLauncher.main(null);
    }  
    
    public void testCalculator14() throws Exception {
        HelloWorldClientLauncher.main(null);
    }    
    
    public void testCalculator15() throws Exception {
        HelloWorldClientLauncher.main(null);
    } 
    
    public void testCalculator16() throws Exception {
        HelloWorldClientLauncher.main(null);
    } 
    
    public void testCalculator17() throws Exception {
        HelloWorldClientLauncher.main(null);
    } 
    
    public void testCalculator18() throws Exception {
        HelloWorldClientLauncher.main(null);
    } 
    
    public void testCalculator19() throws Exception {
        HelloWorldClientLauncher.main(null);
    } 
    
    public void testCalculator20() throws Exception {
        HelloWorldClientLauncher.main(null);
    } 
    
   
    
    @Override
    protected void tearDown() throws Exception {
        node.stop();
        node = null;
    }

}
