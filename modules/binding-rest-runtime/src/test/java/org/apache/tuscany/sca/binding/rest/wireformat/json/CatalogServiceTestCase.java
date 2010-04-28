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
import java.net.URLEncoder;

import org.apache.axiom.om.util.Base64;
import org.apache.tuscany.sca.node.Contribution;
import org.apache.tuscany.sca.node.ContributionLocationHelper;
import org.apache.tuscany.sca.node.Node;
import org.apache.tuscany.sca.node.NodeFactory;
import org.json.JSONObject;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import com.meterware.httpunit.GetMethodWebRequest;
import com.meterware.httpunit.WebConversation;
import com.meterware.httpunit.WebRequest;
import com.meterware.httpunit.WebResponse;

import services.Catalog;
import services.Item;

public class CatalogServiceTestCase {
    private static final String SERVICE_URL = "http://localhost:8085/Catalog";
    
    private static final String GET_RESPONSE = "[{\"price\":\"$2.99\",\"name\":\"Apple\",\"javaClass\":\"services.Item\"},{\"price\":\"$3.55\",\"name\":\"Orange\",\"javaClass\":\"services.Item\"},{\"price\":\"$1.55\",\"name\":\"Pear\",\"javaClass\":\"services.Item\"}]";
    
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
    
    @Test
    public void testGetInvocation() throws Exception {        
        WebConversation wc = new WebConversation();
        WebRequest request = new GetMethodWebRequest(SERVICE_URL);
        WebResponse response = wc.getResource(request);

        Assert.assertEquals(200, response.getResponseCode());
        Assert.assertEquals(GET_RESPONSE, response.getText());
    }
}
