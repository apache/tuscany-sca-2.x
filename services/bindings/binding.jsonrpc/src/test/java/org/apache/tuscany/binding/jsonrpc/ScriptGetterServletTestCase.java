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

import java.io.IOException;

import junit.framework.TestCase;

import org.xml.sax.SAXException;

import com.meterware.httpunit.GetMethodWebRequest;
import com.meterware.httpunit.WebRequest;
import com.meterware.httpunit.WebResponse;
import com.meterware.servletunit.ServletRunner;
import com.meterware.servletunit.ServletUnitClient;

public class ScriptGetterServletTestCase extends TestCase {
        
    private static final String SERVICE_PATH = "JsonRpc/ScriptGetterTest";
    private static final String SERVICE_URL = "http://localhost/" + SERVICE_PATH;
    ServletRunner sr = null;
    
    
    protected void setUp() throws Exception {
        sr = new ServletRunner();
        sr.registerServlet( SERVICE_PATH, ScriptGetterServlet.class.getName() );                
    }    

    public void testGetScript() throws IOException {                
        ServletUnitClient sc = sr.newClient();
        WebRequest request   = new GetMethodWebRequest( SERVICE_URL ); 
        try {
            WebResponse response = sc.getResponse(request);
            assertEquals(200, response.getResponseCode());
        } catch (SAXException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
            fail(e1.toString());
        }        
    }
}
