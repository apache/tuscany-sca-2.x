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
package helloworld;

import org.apache.tuscany.sca.host.embedded.SCADomain;
import org.apache.activemq.broker.BrokerService;


/**
 * This server program shows how to create an SCA runtime, and start it which
 * activates the helloworld Web service endpoint.
 */
public class HelloWorldJmsServer {

    public static void main(String[] args) {

        BrokerService broker = new BrokerService();
        try {
            broker.addConnector("tcp://localhost:61616");
            broker.start();
        } catch ( Exception e) {
            e.printStackTrace();
        }

        SCADomain scaDomain = SCADomain.newInstance("helloworldwsjms.composite");

        try {
            System.out.println("HelloWorld server started (press enter to shutdown)");
            System.in.read();
        } catch ( Exception e) {
            e.printStackTrace();
        }

        scaDomain.close();
        System.out.println("HelloWorld server stopped");
        
        try {
            broker.stop();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
