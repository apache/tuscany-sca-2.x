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

package org.apache.tuscany.sca.binding.rest.rpc;

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

import com.meterware.httpunit.GetMethodWebRequest;
import com.meterware.httpunit.WebConversation;
import com.meterware.httpunit.WebRequest;
import com.meterware.httpunit.WebResponse;

@Ignore
public class EchoServiceTestCase {
    private static final String SERVICE_URL = "http://localhost:8085/EchoService";

    private static final String GET_RESPONSE = "[{\"price\":\"$1.55\",\"name\":\"Pear\",\"javaClass\":\"services.store.Item\"},{\"price\":\"$2.99\",\"name\":\"Apple\",\"javaClass\":\"services.store.Item\"},{\"price\":\"$3.55\",\"name\":\"Orange\",\"javaClass\":\"services.store.Item\"}]";
    private static final String NEW_ITEM = "{\"price\":\"$4.35\",\"name\":\"Grape\"}\"";
    private static final String GET_NEW_RESPONSE = "[{\"price\":\"$1.55\",\"name\":\"Pear\",\"javaClass\":\"services.store.Item\"},{\"price\":\"$2.99\",\"name\":\"Apple\",\"javaClass\":\"services.store.Item\"},{\"price\":\"$3.55\",\"name\":\"Orange\",\"javaClass\":\"services.store.Item\"},{\"price\":\"$4.35\",\"name\":\"Grape\",\"javaClass\":\"services.store.Item\"}]";
    private static final String UPDATED_ITEM = "{\"price\":\"$1.35\",\"name\":\"Grape\"}\"";
    private static final String GET_UPDATED_RESPONSE = "[{\"price\":\"$1.55\",\"name\":\"Pear\",\"javaClass\":\"services.store.Item\"},{\"price\":\"$2.99\",\"name\":\"Apple\",\"javaClass\":\"services.store.Item\"},{\"price\":\"$3.55\",\"name\":\"Orange\",\"javaClass\":\"services.store.Item\"},{\"price\":\"$1.35\",\"name\":\"Grape\",\"javaClass\":\"services.store.Item\"}]";    
    
    private static Node node;

    @BeforeClass
    public static void init() throws Exception {
        try {
            String contribution = ContributionLocationHelper.getContributionLocation(EchoServiceTestCase.class);
            node = NodeFactory.newInstance().createNode("echo.composite", new Contribution("echo", contribution));
            node.start();
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
    public void testRPCGetOperation() throws Exception {
        String queryString = "?method=echo&msg=Hello RPC";

        WebConversation wc = new WebConversation();
        WebRequest request = new GetMethodWebRequest(SERVICE_URL + queryString);
        request.setHeaderField("Content-Type", "application/json");
        WebResponse response = wc.getResource(request);

        Assert.assertEquals(200, response.getResponseCode());
        Assert.assertEquals("Hello RPC", response.getText());
    }
    
    @Test
    public void testRPCGetArrayOperation() throws Exception {
        String queryString = "?method=echoArrayString&msgArray=Hello RPC1&msgArray=Hello RPC2";

        WebConversation wc = new WebConversation();
        WebRequest request = new GetMethodWebRequest(SERVICE_URL + queryString);
        request.setHeaderField("Content-Type", "application/json");
        WebResponse response = wc.getResource(request);

        Assert.assertEquals(200, response.getResponseCode());
        Assert.assertEquals("[\"Hello RPC1\",\"Hello RPC2\"]", response.getText());
    }
    
    
}
