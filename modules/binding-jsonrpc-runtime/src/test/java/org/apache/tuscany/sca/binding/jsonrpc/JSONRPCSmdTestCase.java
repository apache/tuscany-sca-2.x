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

import junit.framework.Assert;

import org.apache.tuscany.sca.host.embedded.SCADomain;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.meterware.httpunit.GetMethodWebRequest;
import com.meterware.httpunit.WebConversation;
import com.meterware.httpunit.WebRequest;
import com.meterware.httpunit.WebResponse;

/**
 * @version $Rev$ $Date$
 */
public class JSONRPCSmdTestCase {

    private static final String SERVICE_PATH = "/EchoService";

    private static final String SERVICE_URL = "http://localhost:8085/SCADomain" + SERVICE_PATH;
    
    private static final String SMD_URL = SERVICE_URL + "?smd";

    private static SCADomain domain;

    @Before
    public void setUp() throws Exception {
            domain = SCADomain.newInstance("JSONRPCBinding.composite");
    }

    @After
    public void tearDown() throws Exception {
            domain.close();
    }

    @Test
    /**
     * This test make sure the JSON-RPC Binding can handle special characters when generating SMD
     */
    public void testJSONRPCSmdSpecialCharacters() throws Exception {
        WebConversation wc = new WebConversation();
        WebRequest request   = new GetMethodWebRequest(SMD_URL);
        WebResponse response = wc.getResource(request);

        Assert.assertEquals(200, response.getResponseCode());
        Assert.assertNotNull(response.getText());
        
        //System.out.println(">>>SMD:" + response.getText());
    }
}
