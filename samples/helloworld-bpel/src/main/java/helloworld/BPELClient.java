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
 * Simple BPEL sample application invoking a helloworld 
 * 
 * @version $Rev$ $Date$
 */
public class BPELClient {
    public static void main(String[] args) throws Exception {

        String contribution = ContributionLocationHelper.getContributionLocation(BPELClient.class);
        Node node = NodeFactory.newInstance().createNode("helloworld.composite", new Contribution("calculator", contribution));
        node.start();
        
        Hello bpelService = node.getService(Hello.class, "BPELHelloWorldComponent");
        
        String result = bpelService.hello("Hello");
        System.out.println(result);
        
        node.stop();

        System.exit(0);
    }
}
