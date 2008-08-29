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

import org.apache.tuscany.sca.host.embedded.SCADomain;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.meterware.httpunit.PostMethodWebRequest;
import com.meterware.httpunit.WebConversation;
import com.meterware.httpunit.WebRequest;
import com.meterware.httpunit.WebResponse;

/**
 * @version $Rev$ $Date$
 */
public class JSONRPCDataTypeTestCase {

	private static final String SERVICE_PATH = "/EchoService";
	private static final String SERVICE_URL = "http://localhost:8085/SCADomain" + SERVICE_PATH;
	private SCADomain domain;

	@Before
	public void setUp() throws Exception {
		domain = SCADomain.newInstance("JSONRPCBinding.composite");
	}

	@After
	public void tearDown() throws Exception {
		domain.close();
	}

	@Test
	public void testInt() throws Exception {
		JSONObject jsonRequest = new JSONObject(
				"{ \"method\": \"echoInt\", \"params\": [12345], \"id\": 4}");

		WebConversation wc = new WebConversation();
		WebRequest request = new PostMethodWebRequest(SERVICE_URL,
				new ByteArrayInputStream(jsonRequest.toString().getBytes("UTF-8")), "application/json");
		WebResponse response = wc.getResource(request);

		Assert.assertEquals(200, response.getResponseCode());

		JSONObject jsonResp = new JSONObject(response.getText());

		Assert.assertEquals(12345, jsonResp.getInt("result"));
	}

	@Test
	public void testBoolean() throws Exception {
		JSONObject jsonRequest = new JSONObject(
				"{ \"method\": \"echoBoolean\", \"params\": [true], \"id\": 5}");
		
		WebConversation wc = new WebConversation();
		WebRequest request = new PostMethodWebRequest(SERVICE_URL,
				new ByteArrayInputStream(jsonRequest.toString().getBytes("UTF-8")), "application/json");
		WebResponse response = wc.getResource(request);

		Assert.assertEquals(200, response.getResponseCode());

		JSONObject jsonResp = new JSONObject(response.getText());

		Assert.assertEquals(true, jsonResp.getBoolean("result"));
	}

	@Test
	public void testMap() throws Exception {
		JSONObject jsonRequest = new JSONObject(
				"{ \"method\": \"echoMap\", \"params\": [ {\"javaClass\": \"java.util.HashMap\", \"map\": { \"Binding\": \"JSON-RPC\"}}], \"id\": 6}");

		WebConversation wc = new WebConversation();
		WebRequest request = new PostMethodWebRequest(SERVICE_URL,
				new ByteArrayInputStream(jsonRequest.toString().getBytes("UTF-8")), "application/json");
		WebResponse response = wc.getResource(request);

		Assert.assertEquals(200, response.getResponseCode());

		JSONObject jsonResp = new JSONObject(response.getText());

		Assert.assertEquals("JSON-RPC", jsonResp.getJSONObject("result").getJSONObject("map").getString("Binding"));
	}
	
	@Test
	public void testBean() throws Exception {
		JSONObject jsonRequest = new JSONObject(
				"{ \"method\": \"echoBean\", \"params\": [ {\"javaClass\": \"bean.TestBean\", \"testString\": \"JSON-RPC\", \"testInt\":1234}], \"id\": 7}");

		WebConversation wc = new WebConversation();
		WebRequest request = new PostMethodWebRequest(SERVICE_URL,
				new ByteArrayInputStream(jsonRequest.toString().getBytes("UTF-8")), "application/json");
		WebResponse response = wc.getResource(request);

		Assert.assertEquals(200, response.getResponseCode());

		JSONObject jsonResp = new JSONObject(response.getText());

		Assert.assertEquals("JSON-RPC", jsonResp.getJSONObject("result").getString("testString"));
	}	

	@Test
	public void testList() throws Exception {
		JSONObject jsonRequest = new JSONObject(
				"{ \"method\": \"echoList\", \"params\": [ {\"javaClass\": \"java.util.ArrayList\", \"list\": [0,1,2,3,4]}], \"id\": 8}");

		WebConversation wc = new WebConversation();
		WebRequest request = new PostMethodWebRequest(SERVICE_URL,
				new ByteArrayInputStream(jsonRequest.toString().getBytes("UTF-8")), "application/json");
		WebResponse response = wc.getResource(request);

		Assert.assertEquals(200, response.getResponseCode());

		JSONObject jsonResp = new JSONObject(response.getText());

		Assert.assertEquals(0, jsonResp.getJSONObject("result").getJSONArray("list").get(0));
	}
	
	@Test
	public void testArrayString() throws Exception {
		JSONObject jsonRequest = new JSONObject(
				"{\"params\":[[\"1\",\"2\"]],\"method\":\"echoArrayString\",\"id\":9}");

		WebConversation wc = new WebConversation();
		WebRequest request = new PostMethodWebRequest(SERVICE_URL,
				new ByteArrayInputStream(jsonRequest.toString().getBytes("UTF-8")), "application/json");
		WebResponse response = wc.getResource(request);

		Assert.assertEquals(200, response.getResponseCode());

		JSONObject jsonResp = new JSONObject(response.getText());

		Assert.assertEquals(1, jsonResp.getJSONArray("result").getInt(0));
	}	

	
	@Test
	public void testArrayInt() throws Exception {
		JSONObject jsonRequest = new JSONObject(
				"{\"params\":[[1,2]],\"method\":\"echoArrayInt\",\"id\":10}");

		WebConversation wc = new WebConversation();
		WebRequest request = new PostMethodWebRequest(SERVICE_URL,
				new ByteArrayInputStream(jsonRequest.toString().getBytes("UTF-8")), "application/json");
		WebResponse response = wc.getResource(request);

		Assert.assertEquals(200, response.getResponseCode());

		JSONObject jsonResp = new JSONObject(response.getText());

		Assert.assertEquals(1, jsonResp.getJSONArray("result").getInt(0));
	}	

	
	@Test
	public void testSet() throws Exception {
		JSONObject jsonRequest = new JSONObject(
				"{ \"method\": \"echoSet\", \"params\": [ {\"javaClass\": \"java.util.HashSet\", \"set\": {\"1\": \"red\", \"2\": \"blue\"}}],\"id\": 11}");

		WebConversation wc = new WebConversation();
		WebRequest request = new PostMethodWebRequest(SERVICE_URL,
				new ByteArrayInputStream(jsonRequest.toString().getBytes("UTF-8")), "application/json");
		WebResponse response = wc.getResource(request);

		Assert.assertEquals(200, response.getResponseCode());

		JSONObject jsonResp = new JSONObject(response.getText());

		Assert.assertEquals("red", jsonResp.getJSONObject("result").getJSONObject("set").getString("red"));
	}
}