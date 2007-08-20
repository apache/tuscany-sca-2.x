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

import junit.framework.TestCase;

import org.apache.tuscany.sca.host.embedded.SCADomain;
import org.json.JSONObject;

import com.meterware.httpunit.PostMethodWebRequest;
import com.meterware.httpunit.WebConversation;
import com.meterware.httpunit.WebRequest;
import com.meterware.httpunit.WebResponse;

/**
 * @version $Rev: 536083 $ $Date: 2007-05-08 02:18:29 -0400 (Tue, 08 May 2007) $
 */
public class JSONRPCServiceTestCase extends TestCase {

    private static final String SERVICE_PATH = "/EchoService";

    private static final String SERVICE_URL = "http://localhost:8085/SCADomain" + SERVICE_PATH;

    private SCADomain domain;

    @Override
    protected void setUp() throws Exception {
        domain = SCADomain.newInstance("JSONRPCBinding.composite");
    }

    @Override
    protected void tearDown() throws Exception {
    	domain.close();
    }

    public void testJSONRPCBinding() throws Exception {
        JSONObject jsonRequest = new JSONObject("{ \"method\": \"echo\", \"params\": [\"Hello JSON-RPC\"], \"id\": 1}");

        WebConversation wc = new WebConversation();
        WebRequest request   = new PostMethodWebRequest( SERVICE_URL, new ByteArrayInputStream(jsonRequest.toString().getBytes("UTF-8")),"application/json");
        WebResponse response = wc.getResource(request);

        assertEquals(200, response.getResponseCode());
        JSONObject jsonResp = new JSONObject(response.getText());
        assertEquals("echo: Hello JSON-RPC", jsonResp.getString("result"));
    }


}
