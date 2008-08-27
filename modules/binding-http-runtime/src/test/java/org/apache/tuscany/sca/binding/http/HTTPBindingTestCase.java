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
package org.apache.tuscany.sca.binding.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.text.MessageFormat;

import junit.framework.TestCase;

import org.apache.tuscany.sca.host.embedded.SCADomain;

/**
 * HTTP binding unit tests.
 * 
 * @version $Rev$ $Date$
 */
public class HTTPBindingTestCase extends TestCase {

    private static final String REQUEST1_HEADER =
        "GET /httpservice/test HTTP/1.0\n" + "Host: localhost\n"
            + "Content-Type: text/xml\n"
            + "Connection: close\n"
            + "Content-Length: ";
    private static final String REQUEST1_CONTENT = "";
    private static final String REQUEST1 =
        REQUEST1_HEADER + REQUEST1_CONTENT.getBytes().length + "\n\n" + REQUEST1_CONTENT;

    private static final String REQUEST2_HEADER =
        "GET /webcontent/test.html HTTP/1.0\n" + "Host: localhost\n"
            + "Content-Type: text/xml\n"
            + "Connection: close\n"
            + "Content-Length: ";
    private static final String REQUEST2_CONTENT = "";
    private static final String REQUEST2 =
        REQUEST2_HEADER + REQUEST2_CONTENT.getBytes().length + "\n\n" + REQUEST2_CONTENT;

    private static final String REQUEST3_HEADER =
        "GET /httpget/{0} HTTP/1.0\n" + "Host: localhost\n"
            + "Content-Type: text/xml\n"
            + "Connection: close\n"
            + "Content-Length: ";
    private static final String REQUEST3_CONTENT = "";
    private static final String REQUEST3 =
        REQUEST3_HEADER + REQUEST3_CONTENT.getBytes().length + "\n\n" + REQUEST3_CONTENT;

    private static final int HTTP_PORT = 8085;

    private SCADomain scaDomain;
    
    @Override
    protected void setUp() throws Exception {
        scaDomain = SCADomain.newInstance("test.composite");
    }

    @Override
    protected void tearDown() throws Exception {
        scaDomain.close();
    }

    /**
     * Test invoking a POJO service implementation using the HTTP binding. 
     * @throws Exception
     */
    public void testServiceImplementation() throws Exception {
        Socket client = new Socket("127.0.0.1", HTTP_PORT);
        OutputStream os = client.getOutputStream();
        os.write(REQUEST1.getBytes());
        os.flush();
        
        String document = read(client);
        assertTrue(document.indexOf("<body><p>hey</body>") != -1);
    }

    /**
     * Test invoking a POJO get method implementation using the HTTP binding. 
     * @throws Exception
     */
    public void testGetImplementation() throws Exception {
        Socket client = new Socket("127.0.0.1", HTTP_PORT);
        OutputStream os = client.getOutputStream();
        int index = 0;
        String request = MessageFormat.format( REQUEST3, index ); 
        os.write( request.getBytes());
        os.flush();
        
        String document = read(client);
        assertTrue(document.indexOf("<body><p>item=" + index) != -1);
    }

    /**
     * Test getting a static resource provided using the HTTP binding. 
     * @throws Exception
     */
    public void testStaticResourceImplementation() throws Exception {
        Socket client = new Socket("127.0.0.1", HTTP_PORT);
        OutputStream os = client.getOutputStream();
        os.write(REQUEST2.getBytes());
        os.flush();
        
        String document = read(client);
        assertTrue(document.indexOf("<body><p>hello</body>") != -1);
    }

    /**
     * Read response stream from the given socket.
     * @param socket
     * @return
     * @throws IOException
     */
    private static String read(Socket socket) throws IOException {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            StringBuffer sb = new StringBuffer();
            String str;
            while ((str = reader.readLine()) != null) {
                sb.append(str);
            }
            return sb.toString();
        } finally {
            if (reader != null) {
                reader.close();
            }
        }
    }

}
