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
import org.junit.Test;

import com.meterware.httpunit.GetMethodWebRequest;
import com.meterware.httpunit.WebConversation;
import com.meterware.httpunit.WebRequest;
import com.meterware.httpunit.WebResponse;

public class EchoServiceTestCase {
    private static final String SERVICE_URL_JSON = "http://localhost:8085/EchoService/json";
    private static final String SERVICE_URL_XML = "http://localhost:8085/EchoService/xml";

    private static final String XML_RESPONSE = "" +
           "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
           "<return xmlns:ns2=\"http://echo.services/\" "+
           "xmlns:xs=\"http://www.w3.org/2001/XMLSchema\" " +
           "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" "+
           "xsi:type=\"xs:string\">Hello RPC</return>";

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
        // System.in.read();
    }

    @Test
    public void testJSONRPCGetOperation() throws Exception {
        String queryString = "?method=echo&msg=Hello RPC";

        WebConversation wc = new WebConversation();
        WebRequest request = new GetMethodWebRequest(SERVICE_URL_JSON + queryString);
        request.setHeaderField("Content-Type", "application/json");
        WebResponse response = wc.getResource(request);

        Assert.assertEquals(200, response.getResponseCode());
        Assert.assertEquals("Hello RPC", response.getText());
    }

    @Test
    public void testRPCGetArrayOperation() throws Exception {
        String queryString = "?method=echoArrayString&msgArray=Hello RPC1&msgArray=Hello RPC2";

        WebConversation wc = new WebConversation();
        WebRequest request = new GetMethodWebRequest(SERVICE_URL_JSON + queryString);
        request.setHeaderField("Content-Type", "application/json");
        WebResponse response = wc.getResource(request);

        Assert.assertEquals(200, response.getResponseCode());
        Assert.assertEquals("[\"Hello RPC1\",\"Hello RPC2\"]", response.getText());
    }


    @Test
    public void testXMLRPCGetOperation() throws Exception {
        String queryString = "?method=echo&msg=Hello RPC";

        WebConversation wc = new WebConversation();
        WebRequest request = new GetMethodWebRequest(SERVICE_URL_XML + queryString);
        request.setHeaderField("Content-Type", "application/xml");
        WebResponse response = wc.getResource(request);

        System.out.println("Expected>>" + XML_RESPONSE);
        System.out.println("Received>>" + response.getText());

        Assert.assertEquals(200, response.getResponseCode());
        Assert.assertEquals(XML_RESPONSE, response.getText());
    }


}
