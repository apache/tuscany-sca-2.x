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
package helloworldrest;

import org.apache.tuscany.sca.node.Node;
import org.apache.tuscany.sca.node.NodeFactory;

/*
 * To test, simply run the program
 * Access the service by invoking the getName() method of HelloWorldService
 */

public class ClientJavaTestService {

    /**
     * @param args
     */
    public static void main(String[] args) {
        NodeFactory factory = NodeFactory.newInstance();
        Node node = factory.createNode("rest.composite", ClientJavaTestService.class.getClassLoader()).start();
        HelloWorldService helloService = node.getService(HelloWorldService.class, "HelloWorldRESTServiceComponent");

        //HelloWorldService helloService = new HelloWorldServiceImpl();
        System.out.println("### Message from REST service " + helloService.getName());

        node.stop();
        node.destroy();
        factory.destroy();
    }

}
