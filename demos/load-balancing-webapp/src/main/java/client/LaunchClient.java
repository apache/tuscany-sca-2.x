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

package client;

import java.io.File;

import helloworld.HelloWorldService;

import org.apache.tuscany.sca.node.SCAClient;
import org.apache.tuscany.sca.node.SCANode2;
import org.apache.tuscany.sca.node.SCANode2Factory;
import org.apache.tuscany.sca.node.SCANode2Factory.SCAContribution;
import org.osoa.sca.ServiceRuntimeException;

public class LaunchClient {
    public static void main(String[] args) throws Exception {
        
        SCANode2 node = null;
        try {
            
            SCANode2Factory nodeFactory = SCANode2Factory.newInstance();
            node = nodeFactory.createSCANode(new File("src/main/resources/client-contribution/helloworldwsclient.composite").toURL().toString(),
                                             new SCAContribution("TestContribution", 
                                                                 new File("src/main/resources/client-contribution").toURL().toString()));

            node.start();
            HelloWorldService helloWorldService = ((SCAClient)node).getService(HelloWorldService.class, "HelloWorldClientComponent");

            
            for (int i=0; i < 10; i++){
                System.out.println(helloWorldService.getGreetings("World "  + i));
            } 
            
            node.stop();
            
        } catch (Exception e) {
            throw new ServiceRuntimeException(e);
        }        
    }
}
