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

package org.apache.tuscany.sca.binding.rest.wireformat.json;

import java.net.Socket;

import org.apache.tuscany.sca.node.Contribution;
import org.apache.tuscany.sca.node.ContributionLocationHelper;
import org.apache.tuscany.sca.node.Node;
import org.apache.tuscany.sca.node.NodeFactory;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import services.Catalog;
import services.Item;

public class CatalogServiceTestCase {
    private static Node node;
    private static Catalog catalogService;

    @BeforeClass
    public static void init() throws Exception {
        try {
            String contribution = ContributionLocationHelper.getContributionLocation(CatalogServiceTestCase.class);
            node = NodeFactory.newInstance().createNode("store.composite", new Contribution("catalog", contribution));
            node.start();

            catalogService = node.getService(Catalog.class, "Catalog");
            Assert.assertNotNull(catalogService);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @AfterClass
    public static void destroy() throws Exception {
        if(node != null) {
            node.stop();
        }
    }
    
    @Test
    public void testPing() throws Exception {
        new Socket("127.0.0.1", 8085);
        //System.in.read();
    }
    
    @Ignore
    public void testNewsService() throws Exception {
        Item[] items = catalogService.get();
        
        Assert.assertNotNull(items);
        Assert.assertTrue(items.length > 0);
        
        for(int pos = 0; pos < items.length; pos++) {
            System.out.println(">>> Item[" + pos + "] - " + items[pos].getName() + " - " + items[pos].getPrice());
        }
    }
}
