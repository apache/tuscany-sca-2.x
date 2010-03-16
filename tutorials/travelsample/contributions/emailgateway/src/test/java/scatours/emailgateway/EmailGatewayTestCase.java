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

package scatours.emailgateway;

import org.apache.tuscany.sca.node.Node;
import org.apache.tuscany.sca.node.Contribution;
import org.apache.tuscany.sca.node.Node;
import org.apache.tuscany.sca.node.NodeFactory;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.tuscanyscatours.emailgateway.EmailGateway;
import com.tuscanyscatours.emailgateway.EmailType;
import com.tuscanyscatours.emailgateway.ObjectFactory;

/**
 * 
 */
public class EmailGatewayTestCase {
    private static Node node;

    /**
     * @throws java.lang.Exception
     */
    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        NodeFactory factory = NodeFactory.getInstance();
        node =
            factory.createNode(
                                  new Contribution("emailgateway", "./target/classes"),
                                  new Contribution("emailgateway-test", "./target/test-classes"));
        node.start();
    }

    @Test
    public void testEmailGateway() {
        Node client = (Node)node;
        EmailGateway cc = client.getService(EmailGateway.class, "EmailGatewayClient");
        ObjectFactory objectFactory = new ObjectFactory();
        EmailType email = objectFactory.createEmailType();
        email.setTo("Fred");
        email.setTitle("An email");
        email.setBody("A message");
        System.out.println(cc.sendEmail(email));
    }

    /**
     * @throws java.lang.Exception
     */
    @AfterClass
    public static void tearDownAfterClass() throws Exception {
        if (node != null) {
            node.stop();
            node = null;
        }
    }

}
