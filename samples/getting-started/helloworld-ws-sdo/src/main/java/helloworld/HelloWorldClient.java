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

import org.apache.tuscany.sca.node.Contribution;
import org.apache.tuscany.sca.node.ContributionLocationHelper;
import org.apache.tuscany.sca.node.Node;
import org.apache.tuscany.sca.node.NodeFactory;

/**
 * This client program shows how to create an SCA runtime, start it, locate the
 * HelloWorld service and invoke it.
 */
public class HelloWorldClient {

    public final static void main(String[] args) throws Exception {

        NodeFactory factory = NodeFactory.newInstance();
        String contribution = ContributionLocationHelper.getContributionLocation(HelloWorldClient.class);
        Node node =
            factory.createNode("helloworldwsclient.composite", new Contribution("helloworld", contribution)).start();

        HelloWorldService helloWorldService = node.getService(HelloWorldService.class, "HelloWorldServiceComponent");

        Name name = HelloworldFactory.INSTANCE.createName();

        name.setFirst("David");
        name.setLast("Haney");

        String value = helloWorldService.getGreetings(name);
        System.out.println(value);

        node.stop();
    }
}
