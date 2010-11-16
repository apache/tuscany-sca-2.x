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

package org.apache.tuscany.sca.binding.rest.wireformat.xml;

import java.io.ByteArrayInputStream;
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
import com.meterware.httpunit.PostMethodWebRequest;
import com.meterware.httpunit.WebConversation;
import com.meterware.httpunit.WebRequest;
import com.meterware.httpunit.WebResponse;

public class CustomerServiceTestCase {
    private static final String SERVICE_URL = "http://localhost:8085/Customer";

    private static final String GET_RESPONSE = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><Customer xmlns:ns2=\"http://tuscany.apache.org/xmlns/sca/databinding/jaxb/1.0\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:type=\"customer\"><email>john@domain.com</email><id>John</id><name>John</name></Customer>";
    private static final String UPDATED_ITEM = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><Customer xmlns:ns2=\"http://customer.services/\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:type=\"customer\"><email>john@updated-domain.com</email><id>John</id><name>John</name></Customer>";
    private static final String GET_UPDATED_RESPONSE = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><Customer xmlns:ns2=\"http://tuscany.apache.org/xmlns/sca/databinding/jaxb/1.0\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:type=\"customer\"><email>john@updated-domain.com</email><id>John</id><name>John</name></Customer>";

    private static Node node;

    @BeforeClass
    public static void init() throws Exception {
        try {
            String contribution = ContributionLocationHelper.getContributionLocation(CustomerServiceTestCase.class);
            node = NodeFactory.newInstance().createNode("customer.composite", new Contribution("customer", contribution));
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
    public void testGetInvocation() throws Exception {
        WebConversation wc = new WebConversation();
        WebRequest request = new GetMethodWebRequest(SERVICE_URL);
        request.setHeaderField("Content-Type", "application/xml");
        WebResponse response = wc.getResource(request);

        //for debug purposes
        //list the response headers
        //for(String headerField : response.getHeaderFieldNames()) {
        //    System.out.println(">>> Header:" + headerField + " - " + response.getHeaderField(headerField));
        //}

        //for debug purposes
        System.out.println(">>>" + GET_RESPONSE);
        System.out.println(">>>" + response.getText());

        Assert.assertEquals(200, response.getResponseCode());
        Assert.assertEquals("no-cache", response.getHeaderField("Cache-Control"));
        Assert.assertEquals("tuscany", response.getHeaderField("X-Tuscany"));
        Assert.assertEquals(GET_RESPONSE, response.getText());

    }

    @Test
    public void testPutInvocation() throws Exception {
        //Add new item to catalog
        WebConversation wc = new WebConversation();
        WebRequest request   = new PostMethodWebRequest(SERVICE_URL, new ByteArrayInputStream(UPDATED_ITEM.getBytes("UTF-8")),"application/xml");
        WebResponse response = wc.getResource(request);

        Assert.assertEquals(201, response.getResponseCode());
        System.out.println(response.getHeaderField("Location"));

        //read new results and expect to get new item back in the response
        request = new GetMethodWebRequest(SERVICE_URL);
        request.setHeaderField("Content-Type", "application/xml");
        response = wc.getResource(request);

        //for debug purposes
        //System.out.println(">>>" + GET_UPDATED_RESPONSE);
        //System.out.println(">>>" + response.getText());

        Assert.assertEquals(200, response.getResponseCode());
        Assert.assertEquals(GET_UPDATED_RESPONSE, response.getText());
    }
}
