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

package org.apache.tuscany.sca.demos.aggregator;

import java.io.ByteArrayInputStream;

import junit.framework.TestCase;

import org.apache.tuscany.sca.host.embedded.SCADomain;
import org.json.JSONObject;

import com.meterware.httpunit.PostMethodWebRequest;
import com.meterware.httpunit.WebConversation;
import com.meterware.httpunit.WebRequest;
import com.meterware.httpunit.WebResponse;


/**
 */
public class AlertsTestCase extends TestCase  {
    private SCADomain scaDomain;
    /**
     * Runs before each test method
     */
    protected void setUp() throws Exception {
        scaDomain = SCADomain.newInstance("alerts.composite");
        super.setUp();
    }

    /**
     * Runs after each test method
     */
    protected void tearDown() {
        scaDomain.close();
    }
    
/* Use this if you want to test locally without deploying to a web container
    public void testWait() throws Exception { 
        System.out.println("Feed aggregator server started (press enter to shutdown)");
        System.in.read();
        System.out.println("Feed aggregator server stopped");
    }
*/    

    public void testGetAllNewAlerts() throws Exception {   
        JSONObject jsonRequest = new JSONObject("{\"params\":[\"sometext\"],\"method\":\"getAllNewAlerts\",\"id\":2}");
        JSONObject jsonResp    = callService ("http://localhost:8085/SCADomain/AlertsServiceJSONRPC",
                                              jsonRequest);                                  
    }   
    
    public void testAddAlertSources() throws Exception {   
        JSONObject jsonRequest = new JSONObject("{\"params\":[{\"name\":\"news\",\"id\":\"2\",\"address\":\"www.news.com\",\"feedAddress\":\"http://news.com.com/2547-1_3-0-20.xml\",\"feedType\":\"rss\",\"lastChecked\":\"lastChecked\",\"javaClass\":\"org.apache.tuscany.sca.demos.aggregator.types.impl.SourceTypeImpl\"}],\"method\":\"addAlertSource\",\"id\":2}");
        JSONObject jsonResp    = callService ("http://localhost:8085/SCADomain/AlertsSourcesServiceJSONRPC",
                                              jsonRequest);  
    }
  
    public void testGetAlertSources() throws Exception {  
        JSONObject jsonRequest = new JSONObject("{\"params\":[\"sometext\"],\"method\":\"getAlertSources\",\"id\":2}");
        JSONObject jsonResp    = callService ("http://localhost:8085/SCADomain/AlertsSourcesServiceJSONRPC",
                                              jsonRequest);                                 
        assertEquals("BBC News", jsonResp.getJSONObject("result").getJSONObject("source").optJSONArray("list").getJSONObject(0).getString("name")); 
    }    
  
    public JSONObject callService(String url, JSONObject jsonRequest) throws Exception {
        System.out.println("Request = " + jsonRequest.toString());
        WebConversation wc   = new WebConversation();
        WebRequest request   = new PostMethodWebRequest( url, 
                                                         new ByteArrayInputStream(jsonRequest.toString().getBytes("UTF-8")),"application/json");
        WebResponse response = wc.getResource(request);
        System.out.println("Response= " + response.getText());               
        assertEquals(200, response.getResponseCode());
        return new JSONObject(response.getText()); 
    }
}
