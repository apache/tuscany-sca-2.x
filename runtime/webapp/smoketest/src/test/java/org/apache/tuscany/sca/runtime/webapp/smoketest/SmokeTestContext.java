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
package org.apache.tuscany.sca.runtime.webapp.smoketest;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;

import junit.framework.TestCase;

/**
 * @version $Rev$ $Date$
 */
public class SmokeTestContext extends TestCase {
    private URL base;

    public void testContext() throws IOException {
        URL url = new URL(base, "smoketest?test=context");
        String result = getContent(url);
        assertEquals("component URI is http://locahost/sca/smoketest", result);
    }

    private String getContent(URL url) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        Reader reader = new InputStreamReader(connection.getInputStream());
        StringBuilder result = new StringBuilder();
        int ch;
        while ((ch = reader.read()) != -1) {
            result.append((char)ch);
        }
        reader.close();
        assertEquals(200, connection.getResponseCode());
        return result.toString();
    }

    protected void setUp() throws Exception {
        super.setUp();
        base = new URL("http://localhost:8088/webapp-smoketest/");
    }
}
