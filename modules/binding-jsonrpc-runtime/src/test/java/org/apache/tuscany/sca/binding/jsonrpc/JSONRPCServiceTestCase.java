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
package org.apache.tuscany.sca.binding.jsonrpc;

import java.io.ByteArrayInputStream;

import junit.framework.Assert;

import org.apache.tuscany.sca.node.Contribution;
import org.apache.tuscany.sca.node.ContributionLocationHelper;
import org.apache.tuscany.sca.node.Node;
import org.apache.tuscany.sca.node.NodeFactory;
import org.json.JSONObject;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.meterware.httpunit.PostMethodWebRequest;
import com.meterware.httpunit.WebConversation;
import com.meterware.httpunit.WebRequest;
import com.meterware.httpunit.WebResponse;

/**
 * @version $Rev$ $Date$
 */
public class JSONRPCServiceTestCase{

    private static final String SERVICE_PATH = "/EchoService";

    private static final String SERVICE_URL = "http://localhost:8085/SCADomain" + SERVICE_PATH;

    private static Node node;

    @BeforeClass
    public static void setUp() throws Exception {
        try {
            String contribution = ContributionLocationHelper.getContributionLocation(JSONRPCServiceTestCase.class);
            node = NodeFactory.newInstance().createNode("JSONRPCBinding.composite", new Contribution("test", contribution));
            node.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @AfterClass
    public static void tearDown() throws Exception {
        node.stop();
        node.destroy();
    }

    @Test
    public void testJSONRPCBinding() throws Exception {
        JSONObject jsonRequest = new JSONObject("{ \"method\": \"echo\", \"params\": [\"Hello JSON-RPC\"], \"id\": 1}");

        WebConversation wc = new WebConversation();
        WebRequest request   = new PostMethodWebRequest( SERVICE_URL, new ByteArrayInputStream(jsonRequest.toString().getBytes("UTF-8")),"application/json");
        WebResponse response = wc.getResource(request);

        Assert.assertEquals(200, response.getResponseCode());

        JSONObject jsonResp = new JSONObject(response.getText());
        Assert.assertEquals("echo: Hello JSON-RPC", jsonResp.getString("result"));
    }   
}