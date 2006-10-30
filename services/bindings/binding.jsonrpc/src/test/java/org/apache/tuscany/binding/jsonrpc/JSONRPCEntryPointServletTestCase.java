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
package org.apache.tuscany.binding.jsonrpc;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.text.ParseException;
import java.util.HashMap;

import javax.servlet.ServletException;

import junit.framework.TestCase;

import org.json.JSONArray;
import org.json.JSONObject;

import com.meterware.httpunit.GetMethodWebRequest;
import com.meterware.httpunit.PostMethodWebRequest;
import com.meterware.httpunit.WebRequest;
import com.meterware.httpunit.WebResponse;
import com.meterware.servletunit.InvocationContext;
import com.meterware.servletunit.ServletRunner;
import com.meterware.servletunit.ServletUnitClient;

public class JSONRPCEntryPointServletTestCase extends TestCase implements TestInterface{
        
    private static final String MESSAGE = "Testing..1..2..3";
    private static final String SERVICE_PATH = "JsonRpc/Test";
    private static final String SERVICE_URL = "http://localhost/" + SERVICE_PATH;
    ServletRunner sr = null;
    
    
    protected void setUp() throws Exception
    {
        sr = new ServletRunner();
        sr.registerServlet( SERVICE_PATH, JSONRPCEntryPointServlet.class.getName() );                
    }

    public void testJSONRPCEntryPointServletContruct() {
        JSONRPCEntryPointServlet servlet = new JSONRPCEntryPointServlet("serviceName",TestInterface.class, this);
        assertEquals("serviceName", servlet.serviceName);
        assertEquals(TestInterface.class, servlet.serviceInterface);
        assertEquals(this, servlet.serviceInstance);
    }

    public void testGetSmd() throws IOException {                
        ServletUnitClient sc = sr.newClient();
        WebRequest request   = new GetMethodWebRequest( SERVICE_URL );
        request.setParameter( "smd", "" );
        InvocationContext ic = sc.newInvocation( request );
        try {
            JSONRPCEntryPointServlet jsonServlet = (JSONRPCEntryPointServlet)ic.getServlet();
            jsonServlet.serviceName = "Test";
            jsonServlet.serviceInterface = TestInterface.class;
            jsonServlet.serviceInstance = this;
            
            ic.service();
            WebResponse response = ic.getServletResponse();
            assertEquals(200, response.getResponseCode());
            JSONObject jsonResp = new JSONObject(response.getText());                       
            assertEquals("TestInterface", jsonResp.getString("objectName"));
            assertEquals("JSON-RPC", jsonResp.getString("serviceType"));
            assertEquals(SERVICE_URL, jsonResp.getString("serviceURL"));
            JSONArray jsonMethods = jsonResp.getJSONArray("methods");
            assertNotNull(jsonMethods);
            assertEquals(2, jsonMethods.length());
            HashMap<String, JSONObject> hashMethods = new HashMap<String, JSONObject>();
            for(int i = 0; i < jsonMethods.length(); i++)
            {
                JSONObject method = jsonMethods.getJSONObject(i);
                assertNotNull(method);
                assertNotNull(method.getString("name"));
                hashMethods.put(method.getString("name"), method);
            }
            JSONObject echoMethod = hashMethods.get("echo");
            assertNotNull(echoMethod);
            JSONArray echoParameters = echoMethod.getJSONArray("parameters");
            assertNotNull(echoParameters);
            assertEquals(1,echoParameters.length());
            assertEquals("param0",echoParameters.getJSONObject(0).getString("name"));
            assertEquals("STRING",echoParameters.getJSONObject(0).getString("type"));
            JSONObject getMessageMethod = hashMethods.get("getMessage");
            assertNotNull(getMessageMethod);
            JSONArray getMessageParameters = getMessageMethod.getJSONArray("parameters");
            assertNotNull(getMessageParameters);
            assertEquals(0,getMessageParameters.length());
            
        } catch (ServletException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            fail(e.toString());
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            fail(e.toString());
        }
        
        
    }
    
    public void testZeroParameterMethodCall() throws IOException {                
        ServletUnitClient sc = sr.newClient();
                
        try {
            JSONObject jsonRequest = new JSONObject("{ \"method\": \"getMessage\", \"params\": [], \"id\": 1}");
            
            WebRequest request   = new PostMethodWebRequest( SERVICE_URL, new ByteArrayInputStream(jsonRequest.toString().getBytes("UTF-8")),"application/json");        
            InvocationContext ic = sc.newInvocation( request );
        
            JSONRPCEntryPointServlet jsonServlet = (JSONRPCEntryPointServlet)ic.getServlet();
            jsonServlet.serviceName = "Test";
            jsonServlet.serviceInterface = TestInterface.class;
            jsonServlet.serviceInstance = this;
            
            ic.service();
            WebResponse response = ic.getServletResponse();
            assertEquals(200, response.getResponseCode());
            JSONObject jsonResp = new JSONObject(response.getText());                                   
            assertEquals(MESSAGE, jsonResp.getString("result"));
        } catch (ServletException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            fail(e.toString());
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            fail(e.toString());
        }
        
        
    }
    
    public void testMalformedJSON() throws IOException {                
        ServletUnitClient sc = sr.newClient();
                
        try {
            String badJsonRequest = "{\"no close brace\"";
            
            WebRequest request   = new PostMethodWebRequest( SERVICE_URL, new ByteArrayInputStream(badJsonRequest.getBytes("UTF-8")),"application/json");        
            InvocationContext ic = sc.newInvocation( request );
        
            JSONRPCEntryPointServlet jsonServlet = (JSONRPCEntryPointServlet)ic.getServlet();
            jsonServlet.serviceName = "Test";
            jsonServlet.serviceInterface = TestInterface.class;
            jsonServlet.serviceInstance = this;
            
            ic.service();
            fail("Expected runtime exception from malformed JSON resquest");                                   
            
        } catch (ServletException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            fail(e.toString());
        } catch (RuntimeException e) {
            // expected runtime exception from malformed JSON request
            assertEquals(true, e.getCause() instanceof ParseException);
        }
        
        
    }

    public String echo(String message) {        
        return message;
    }

    public String getMessage() {        
        return MESSAGE;
    }

}
