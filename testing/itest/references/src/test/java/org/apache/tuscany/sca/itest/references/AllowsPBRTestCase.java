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
package org.apache.tuscany.sca.itest.references;

import junit.framework.Assert;

import org.apache.tuscany.sca.itest.allowspassbyreference.AServiceClient;
import org.apache.tuscany.sca.node.Contribution;
import org.apache.tuscany.sca.node.ContributionLocationHelper;
import org.apache.tuscany.sca.node.Node;
import org.apache.tuscany.sca.node.NodeFactory;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class AllowsPBRTestCase {
    private static Node node;

    @BeforeClass
    public static void init() throws Exception {
        String location = ContributionLocationHelper.getContributionLocation("AllowsPBRTest.composite");
        node = NodeFactory.newInstance().createNode("AllowsPBRTest.composite", new Contribution("c1", location));
        node.start();
    }

    @AfterClass
    public static void destroy() throws Exception {
        node.stop();
    }

    @Test
    public void testClientA1() {
        test("ClientA1Component");
    }

    @Test
    public void testClientA2() {
        test("ClientA2Component");
    }

    @Test
    public void testClientB1() {
        test("ClientB1Component");
    }

    @Test
    public void testClientB2() {
        test("ClientB2Component");
    }

    private void test(String serviceName) {
        AServiceClient client = node.getService(AServiceClient.class, serviceName);
        int id = client.create("A");
        String state = client.read(id);
        Assert.assertEquals("A", state);
        state = client.update(id, "B");
        Assert.assertEquals("B", state);
        client.delete(id);
    }

}
