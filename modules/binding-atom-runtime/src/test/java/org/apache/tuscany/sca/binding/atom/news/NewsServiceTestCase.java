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

package org.apache.tuscany.sca.binding.atom.news;

import java.net.Socket;

import org.apache.tuscany.sca.binding.atom.aggregator.AggregatorTestCase;
import org.apache.tuscany.sca.data.collection.Entry;
import org.apache.tuscany.sca.node.Contribution;
import org.apache.tuscany.sca.node.ContributionLocationHelper;
import org.apache.tuscany.sca.node.Node;
import org.apache.tuscany.sca.node.NodeFactory;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class NewsServiceTestCase {
    private static Node node;
    private static NewsService newsService;

    @BeforeClass
    public static void init() throws Exception {
        try {
            String contribution = ContributionLocationHelper.getContributionLocation(AggregatorTestCase.class);
            node = NodeFactory.newInstance().createNode("org/apache/tuscany/sca/binding/atom/news/news.composite", new Contribution("test", contribution));
            node.start();

            newsService = node.getService(NewsService.class, "NewsService");
            Assert.assertNotNull(newsService);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @AfterClass
    public static void destroy() throws Exception {
        if(node != null) {
            node.stop();
            node.destroy();
        }
    }
    
    @Test
    public void testPing() throws Exception {
        new Socket("127.0.0.1", 8085);
        // System.in.read();
    }
    
    @Test
    public void testNewsService() throws Exception {
        Entry<String, Headline>[] entries = newsService.getAll();
        
        Assert.assertNotNull(entries);
        Assert.assertTrue(entries.length > 0);
        
        for(int pos = 0; pos < entries.length; pos++) {
            System.out.println(">>> Entry[" + pos + "] - " + entries[pos].getData().getText());
        }
    }
}
