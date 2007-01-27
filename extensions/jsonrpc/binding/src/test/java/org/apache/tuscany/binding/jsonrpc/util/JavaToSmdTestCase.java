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
package org.apache.tuscany.binding.jsonrpc.util;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import junit.framework.TestCase;

public class JavaToSmdTestCase extends TestCase {
    
    public void testInstantiate() {
        // this test is only really here to get 100% test coverage
        JavaToSmd test = new JavaToSmd();
        assertNotNull(test);
    }

    public void testInterfaceToSmd() {
        String serviceUrl = "/testServiceUrl";
        String smd = JavaToSmd.interfaceToSmd(java.util.Observer.class,serviceUrl);
        assertNotNull(smd);
        JSONObject json = new JSONObject(smd);        
        assertNotNull(json);
        assertEquals(".1", json.getString("SMDVersion"));
        assertEquals("Observer", json.getString("objectName"));
        assertEquals("JSON-RPC", json.getString("serviceType"));
        assertEquals(serviceUrl, json.getString("serviceURL"));
        JSONArray methodsArray = json.getJSONArray("methods");
        assertNotNull(methodsArray);
        assertEquals(1, methodsArray.length());
        JSONObject methodJson = methodsArray.getJSONObject(0);
        assertNotNull(methodJson);
        assertEquals("update", methodJson.getString("name"));
        JSONArray parametersArray = methodJson.getJSONArray("parameters");
        assertNotNull(parametersArray);
        assertEquals(2, parametersArray.length());
        JSONObject param0Json = parametersArray.getJSONObject(0);
        assertNotNull(param0Json);
        assertEquals("param0", param0Json.getString("name"));
        assertEquals("STRING", param0Json.getString("type"));
        JSONObject param1Json = parametersArray.getJSONObject(1);
        assertNotNull(param1Json);
        assertEquals("param1", param1Json.getString("name"));
        assertEquals("STRING", param1Json.getString("type"));
    }

}
