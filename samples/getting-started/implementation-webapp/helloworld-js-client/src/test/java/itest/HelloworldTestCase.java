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

package itest;

import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

import org.junit.Ignore;
import org.junit.Test;

/**
 * Verfiy the serverside code is working, not sure how to easily itest the browser javascript code 
 */
@Ignore("TUSCANY-3688")
public class HelloworldTestCase {

    @Test
    public void testA() throws MalformedURLException, IOException {
        
        URL url = new URL("http://localhost:8085/helloworld-js-client/org.oasisopen.sca.componentContext.js/foo/call/plaincall/service.sayHello.dwr");
        URLConnection conn = url.openConnection();
        conn.setDoOutput(true);
        OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());

        // to find this run the sample through TCPMON to capture the messages        
        
        String data = URLEncoder.encode("callCount", "UTF-8") + "=" + URLEncoder.encode("1", "UTF-8");
        data += "&" + URLEncoder.encode("page", "UTF-8") + "=" + URLEncoder.encode("/helloworld-js-client/", "UTF-8");       
        data += "&" + URLEncoder.encode("httpSessionId", "UTF-8") + "=" + URLEncoder.encode("", "UTF-8");       
        data += "&" + URLEncoder.encode("scriptSessionId", "UTF-8") + "=" + URLEncoder.encode("A023DA664E56F075491BE1B87B37B02671", "UTF-8");       
        data += "&" + URLEncoder.encode("c0-scriptName", "UTF-8") + "=" + URLEncoder.encode("service", "UTF-8");       
        data += "&" + URLEncoder.encode("c0-methodName", "UTF-8") + "=" + URLEncoder.encode("sayHello", "UTF-8");       
        data += "&" + URLEncoder.encode("c0-id", "UTF-8") + "=" + URLEncoder.encode("0", "UTF-8");       
        data += "&" + URLEncoder.encode("c0-param0", "UTF-8") + "=" + URLEncoder.encode("string:petra", "UTF-8");       
        data += "&" + URLEncoder.encode("batchId", "UTF-8") + "=" + URLEncoder.encode("0", "UTF-8");       
        
        wr.write(data);
        wr.flush();
        
        BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        rd.readLine(); // throw 'allowScriptTagRemoting is false.';
        rd.readLine(); //#DWR-INSERT
        rd.readLine(); //#DWR-REPLY
        String line = rd.readLine(); // dwr.engine._remoteHandleCallback('0','0',"Hello petra");
        wr.close();
        rd.close();
        
        assertTrue(line.endsWith("\"Hello petra\");"));

    }

}
