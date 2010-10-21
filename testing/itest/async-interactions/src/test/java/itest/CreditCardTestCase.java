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
package itest;

import java.util.concurrent.ExecutionException;

import org.apache.tuscany.sca.node.Contribution;
import org.apache.tuscany.sca.node.ContributionLocationHelper;
import org.apache.tuscany.sca.node.Node;
import org.apache.tuscany.sca.node.NodeFactory;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import test.client.CreditCardClient;

/**
 * A test case for two SCA components exchange data via binding.jms
 */
public class CreditCardTestCase {

    private static Node node;
    private static CreditCardClient client;

    @BeforeClass
    public static void init() throws Exception {
        String contribution = ContributionLocationHelper.getContributionLocation(CreditCardClient.class);
        node =
            NodeFactory.newInstance().createNode("test/client/creditcard-client.composite",
                                                 new Contribution("cc", contribution));
        node.start();

        client = node.getService(CreditCardClient.class, "CreditCardClientComponent/CreditCardClient");
    }

    @AfterClass
    public static void destroy() throws Exception {
        if (node != null) {
            node.stop();
        }
    }

    @Test
    public void testCreditCard() {
        String status = client.authorize("123", "John", 100.0f);
        Assert.assertEquals("OK", status);
    }

    @Test
    public void testCreditCardAsync() throws InterruptedException, ExecutionException {
        String result = client.authorizeAsync("456", "Mary", 120.0f);
        Assert.assertEquals("OK", result);
    }

    @Test
    public void testCreditCardAsyncWithCallback() throws InterruptedException, ExecutionException {
        String result = client.authorizeAsyncWithCallback("789", "Smith", 180.0f);
        Assert.assertEquals("OK", result);
    }
    
    @Test
    public void testCreditCardSCAAsyncWithCallback() throws InterruptedException, ExecutionException {
        String result = client.authorizeSCAAsyncWithCallback("888", "Jane", 110.0f);
        Assert.assertEquals("OK", result);
    }
    
    @Test
    public void testCreditCardSCAWithCallback() throws InterruptedException, ExecutionException {
        String result = client.authorizeSCAWithCallback("999", "Steve", 210.0f);
        Assert.assertEquals("OK", result);
    }
}
