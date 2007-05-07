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
package org.apache.tuscany.http.tomcat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.tuscany.work.NotificationListener;
import org.apache.tuscany.work.WorkScheduler;

import junit.framework.TestCase;

/**
 * @version $Rev$ $Date$
 */
public class TomcatServerTestCase extends TestCase {

    private static final String REQUEST1_HEADER =
        "GET /foo HTTP/1.0\n" + "Host: localhost\n"
            + "Content-Type: text/xml\n"
            + "Connection: close\n"
            + "Content-Length: ";
    private static final String REQUEST1_CONTENT = "";
    private static final String REQUEST1 =
        REQUEST1_HEADER + REQUEST1_CONTENT.getBytes().length + "\n\n" + REQUEST1_CONTENT;

    private static final int HTTP_PORT = 8080;
    
    private WorkScheduler workScheduler = new WorkScheduler() {
        
        public <T extends Runnable> void scheduleWork(T work) {
            Thread thread = new Thread(work);
            thread.start();
        }
        
        public <T extends Runnable> void scheduleWork(T work, NotificationListener<T> listener) {
            scheduleWork(work);
        }
    };
    
    /**
     * Verifies requests are properly routed according to the servlet mapping
     */
    public void testRegisterServletMapping() throws Exception {
        TomcatServer service = new TomcatServer(workScheduler);
        service.init();
        TestServlet servlet = new TestServlet();
        service.addServletMapping("http://127.0.0.1:" + HTTP_PORT + "/foo", servlet);
        Socket client = new Socket("127.0.0.1", HTTP_PORT);
        OutputStream os = client.getOutputStream();
        os.write(REQUEST1.getBytes());
        os.flush();
        read(client);
        service.destroy();
        assertTrue(servlet.invoked);
    }

    public void testUnregisterMapping() throws Exception {
        TomcatServer service = new TomcatServer(workScheduler);
        service.init();
        TestServlet servlet = new TestServlet();
        service.addServletMapping("http://127.0.0.1:" + HTTP_PORT + "/foo", servlet);
        service.removeServletMapping("http://127.0.0.1:" + HTTP_PORT + "/foo");
        Socket client = new Socket("127.0.0.1", HTTP_PORT);
        OutputStream os = client.getOutputStream();
        os.write(REQUEST1.getBytes());
        os.flush();
        read(client);
        service.destroy();
        assertFalse(servlet.invoked);
    }

    public void testRequestSession() throws Exception {
        TomcatServer service = new TomcatServer(workScheduler);
        service.init();
        TestServlet servlet = new TestServlet();
        service.addServletMapping("http://127.0.0.1:" + HTTP_PORT + "/foo", servlet);
        Socket client = new Socket("127.0.0.1", HTTP_PORT);
        OutputStream os = client.getOutputStream();
        os.write(REQUEST1.getBytes());
        os.flush();
        read(client);
        service.destroy();
        assertTrue(servlet.invoked);
        assertNotNull(servlet.sessionId);
    }

    public void testRestart() throws Exception {
        TomcatServer service = new TomcatServer(workScheduler);
        service.init();
        service.destroy();
        service.init();
        service.destroy();
    }

    public void testNoMappings() throws Exception {
        TomcatServer service = new TomcatServer(workScheduler);
        service.init();
        Exception ex = null;
        try {
            Socket client = new Socket("127.0.0.1", HTTP_PORT);
            OutputStream os = client.getOutputStream();
            os.write(REQUEST1.getBytes());
            os.flush();
        } catch (Exception e) {
            ex = e;
        }
        assertNotNull(ex);
        service.destroy();
    }

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

    private class TestServlet extends HttpServlet {
        private static final long serialVersionUID = 1L;
        boolean invoked;
        String sessionId;

        protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
            invoked = true;
            sessionId = req.getSession().getId();
            OutputStream writer = resp.getOutputStream();
            try {
                writer.write("result".getBytes());
            } finally {
                writer.close();
            }
        }

    }
}
