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
package org.apache.tuscany.sca.http.tomcat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import junit.framework.TestCase;

import org.apache.tuscany.sca.host.http.DefaultResourceServlet;
import org.apache.tuscany.sca.work.NotificationListener;
import org.apache.tuscany.sca.work.WorkScheduler;

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

    private static final String REQUEST2_HEADER =
        "GET /webcontent/test.html HTTP/1.0\n" + "Host: localhost\n"
            + "Content-Type: text/xml\n"
            + "Connection: close\n"
            + "Content-Length: ";
    private static final String REQUEST2_CONTENT = "";
    private static final String REQUEST2 =
        REQUEST2_HEADER + REQUEST2_CONTENT.getBytes().length + "\n\n" + REQUEST2_CONTENT;

    private static final int HTTP_PORT = 8085;

    private WorkScheduler workScheduler = new WorkScheduler() {

        public <T extends Runnable> void scheduleWork(T work) {
            Thread thread = new Thread(work);
            thread.start();
        }

        public <T extends Runnable> void scheduleWork(T work, NotificationListener<T> listener) {
            scheduleWork(work);
        }

        public void destroy() {
        }
    };

    /**
     * Verifies requests are properly routed according to the servlet mapping
     */
    public void testRegisterServletMapping() throws Exception {
        TomcatServer service = new TomcatServer(workScheduler);
        TestServlet servlet = new TestServlet();
        service.addServletMapping("http://127.0.0.1:" + HTTP_PORT + "/foo", servlet);
        Socket client = new Socket("127.0.0.1", HTTP_PORT);
        OutputStream os = client.getOutputStream();
        os.write(REQUEST1.getBytes());
        os.flush();
        read(client);
        service.stop();
        assertTrue(servlet.invoked);
    }

    /**
     * Verifies that servlets can be registered with multiple ports
     */
    public void testRegisterMultiplePorts() throws Exception {
        TomcatServer service = new TomcatServer(workScheduler);
        TestServlet servlet = new TestServlet();
        service.addServletMapping("http://127.0.0.1:" + HTTP_PORT + "/", servlet);
        TestServlet servlet2 = new TestServlet();
        service.addServletMapping("http://127.0.0.1:" + (HTTP_PORT + 1) + "/", servlet2);
        {
            Socket client = new Socket("127.0.0.1", HTTP_PORT);
            OutputStream os = client.getOutputStream();
            os.write(REQUEST1.getBytes());
            os.flush();
            read(client);
        }
        {
            Socket client = new Socket("127.0.0.1", HTTP_PORT + 1);
            OutputStream os = client.getOutputStream();
            os.write(REQUEST1.getBytes());
            os.flush();
            read(client);
        }
        
        service.stop();
        assertTrue(servlet.invoked);
        assertTrue(servlet2.invoked);
    }

    public void testUnregisterMapping() throws Exception {
        TomcatServer service = new TomcatServer(workScheduler);
        TestServlet servlet = new TestServlet();
        service.addServletMapping("http://127.0.0.1:" + HTTP_PORT + "/foo", servlet);
        service.removeServletMapping("http://127.0.0.1:" + HTTP_PORT + "/foo");
        Socket client = new Socket("127.0.0.1", HTTP_PORT);
        OutputStream os = client.getOutputStream();
        os.write(REQUEST1.getBytes());
        os.flush();
        read(client);
        service.stop();
        assertFalse(servlet.invoked);
    }

    public void testRequestSession() throws Exception {
        TomcatServer service = new TomcatServer(workScheduler);
        TestServlet servlet = new TestServlet();
        service.addServletMapping("http://127.0.0.1:" + HTTP_PORT + "/foo", servlet);
        Socket client = new Socket("127.0.0.1", HTTP_PORT);
        OutputStream os = client.getOutputStream();
        os.write(REQUEST1.getBytes());
        os.flush();
        read(client);
        service.stop();
        assertTrue(servlet.invoked);
        assertNotNull(servlet.sessionId);
    }

    public void testRestart() throws Exception {
        TomcatServer service = new TomcatServer(workScheduler);
        service.stop();
        service.stop();
    }

    public void testNoMappings() throws Exception {
        TomcatServer service = new TomcatServer(workScheduler);
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
        service.stop();
    }

    public void testResourceServlet() throws Exception {
        TomcatServer service = new TomcatServer(workScheduler);
        
        String documentRoot = getClass().getClassLoader().getResource("content/test.html").toString();
        documentRoot = documentRoot.substring(0, documentRoot.lastIndexOf('/'));
        DefaultResourceServlet resourceServlet = new DefaultResourceServlet(documentRoot);
        TestResourceServlet servlet = new TestResourceServlet(resourceServlet);
        service.addServletMapping("http://127.0.0.1:" + HTTP_PORT + "/webcontent/*", servlet);
        
        Socket client = new Socket("127.0.0.1", HTTP_PORT);
        OutputStream os = client.getOutputStream();
        os.write(REQUEST2.getBytes());
        os.flush();
        
        String document = read(client);
        assertTrue(document.indexOf("<body><p>hello</body>") != -1);
        
        service.stop();
    }

    public void testDefaultServlet() throws Exception {
        TomcatServer service = new TomcatServer(workScheduler);
        
        String documentRoot = getClass().getClassLoader().getResource("content/test.html").toString();
        documentRoot = documentRoot.substring(0, documentRoot.lastIndexOf('/'));
        DefaultResourceServlet resourceServlet = new DefaultResourceServlet(documentRoot);
        service.addServletMapping("http://127.0.0.1:" + HTTP_PORT + "/webcontent/*", resourceServlet);
        
        Socket client = new Socket("127.0.0.1", HTTP_PORT);
        OutputStream os = client.getOutputStream();
        os.write(REQUEST2.getBytes());
        os.flush();
        
        String document = read(client);
        assertTrue(document.indexOf("<body><p>hello</body>") != -1);
        
        service.stop();
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

        @Override
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

    private class TestResourceServlet extends HttpServlet {
        private static final long serialVersionUID = 1L;
        private HttpServlet delegate;
        
        public TestResourceServlet(HttpServlet delegate) {
            this.delegate = delegate;
        }
        
        @Override
        public void init() throws ServletException {
            super.init();
            delegate.init();
        }

        @Override
        public void init(ServletConfig config) throws ServletException {
            super.init();
            delegate.init(config);
        }
        
        @Override
        protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
            delegate.service(req, resp);
        }
        
        @Override
        public void destroy() {
            super.destroy();
            delegate.destroy();
        }
    }
}
