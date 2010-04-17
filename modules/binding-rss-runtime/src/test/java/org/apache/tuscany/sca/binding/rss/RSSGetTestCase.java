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

package org.apache.tuscany.sca.binding.rss;

import junit.framework.Assert;

import org.apache.tuscany.sca.node.Contribution;
import org.apache.tuscany.sca.node.ContributionLocationHelper;
import org.apache.tuscany.sca.node.Node;
import org.apache.tuscany.sca.node.NodeFactory;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

/**
 * Basic test case that will get the feed entries from an RSS feed.
 */
public class RSSGetTestCase {
    protected static Node scaConsumerNode;
    protected static Node scaProviderNode;
    protected static CustomerClient testService;

    @BeforeClass
    public static void init() throws Exception {
        System.out.println(">>>RSSGetTestCase.init entry");
        String contribution = ContributionLocationHelper.getContributionLocation(RSSGetTestCase.class);

        scaProviderNode = NodeFactory.newInstance().createNode("org/apache/tuscany/sca/binding/rss/Provider.composite", new Contribution("provider", contribution));
        scaProviderNode.start();

        scaConsumerNode = NodeFactory.newInstance().createNode("org/apache/tuscany/sca/binding/rss/Consumer.composite", new Contribution("consumer", contribution));
        scaConsumerNode.start();

        testService = scaConsumerNode.getService(CustomerClient.class, "CustomerClient");
    }

    @AfterClass
    public static void destroy() throws Exception {
        // System.out.println(">>>RSSGetTestCase.destroy entry");
        if (scaConsumerNode != null) {
            scaConsumerNode.stop();
        }
        if (scaProviderNode != null) {
            scaProviderNode.stop();
        }
    }

    @Test
    public void testPrelim() throws Exception {
        Assert.assertNotNull(scaProviderNode);
        Assert.assertNotNull(scaConsumerNode);
        Assert.assertNotNull(testService);
    }

    @Test
    @Ignore("TUSCANY-3537")
    public void testRSSGet() throws Exception {
        testService.testCustomerCollection();
    }
}
