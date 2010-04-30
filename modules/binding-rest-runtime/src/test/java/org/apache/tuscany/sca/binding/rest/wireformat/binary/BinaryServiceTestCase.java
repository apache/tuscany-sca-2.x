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

package org.apache.tuscany.sca.binding.rest.wireformat.binary;

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
import com.meterware.httpunit.PutMethodWebRequest;
import com.meterware.httpunit.WebConversation;
import com.meterware.httpunit.WebRequest;
import com.meterware.httpunit.WebResponse;

public class BinaryServiceTestCase {
    private static final String SERVICE_URL = "http://localhost:8085/Binary";

    private static final String CONTENT = "ABCDefgh";
    private static final String UPDATED_CONTENT = "abcdEFGH";

    private static Node node;

    @BeforeClass
    public static void init() throws Exception {
        try {
            String contribution = ContributionLocationHelper.getContributionLocation(BinaryServiceTestCase.class);
            node = NodeFactory.newInstance().createNode("binary.composite", new Contribution("binary", contribution));
            node.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @AfterClass
    public static void destroy() throws Exception {
        if (node != null) {
            node.stop();
        }
    }

    @Test
    public void testPing() throws Exception {
        new Socket("127.0.0.1", 8085);
        //System.in.read();
    }

    @Test
    public void testMethods() throws Exception {
        WebConversation wc = new WebConversation();

        // Create content
        WebRequest request =
            new PostMethodWebRequest(SERVICE_URL, new ByteArrayInputStream(CONTENT.getBytes("UTF-8")),
                                     "application/octet-stream");
        WebResponse response = wc.getResource(request);

        Assert.assertEquals(200, response.getResponseCode());
        
        // Read the content
        request = new GetMethodWebRequest(SERVICE_URL);
        response = wc.getResource(request);

        Assert.assertEquals(200, response.getResponseCode());
        Assert.assertEquals(CONTENT, response.getText());

        request =
            new PutMethodWebRequest(SERVICE_URL, new ByteArrayInputStream(UPDATED_CONTENT.getBytes("UTF-8")),
                                     "application/octet-stream");
        response = wc.getResource(request);

        Assert.assertEquals(200, response.getResponseCode());

        //read new results and expect to get new item back in the response
        request = new GetMethodWebRequest(SERVICE_URL);
        response = wc.getResource(request);

        Assert.assertEquals(200, response.getResponseCode());
        Assert.assertEquals(UPDATED_CONTENT, response.getText());
    }
}
