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

package org.apache.tuscany.sca.test;

import org.apache.tuscany.sca.node.Contribution;
import org.apache.tuscany.sca.node.ContributionLocationHelper;
import org.apache.tuscany.sca.node.Node;
import org.apache.tuscany.sca.node.NodeFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class CallBackBasicTestCase {

    private static Node node;
    private CallBackBasicClient aCallBackClient;

    @Test
    public void testCallBackBasic() {
        aCallBackClient.run();
    }

    @Before
    public void setUp() throws Exception {
        if (node == null) {
            String location = ContributionLocationHelper.getContributionLocation("CallBackBasicTest.composite");
            node = NodeFactory.newInstance().createNode("CallBackBasicTest.composite", new Contribution("c1", location));
            node.start();
        }

        aCallBackClient = node.getService(CallBackBasicClient.class, "CallBackBasicClient");
    }

    @After
    public void tearDown() throws Exception {
        node.stop();
    }

}
