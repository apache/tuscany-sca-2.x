/**
 *
 * Copyright 2006 The Apache Software Foundation
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.apache.tuscany.tomcat.integration;

import junit.framework.TestCase;
import org.apache.catalina.Host;
import org.apache.catalina.Globals;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.connector.Request;
import org.apache.catalina.connector.Response;
import org.apache.catalina.core.StandardEngine;
import org.apache.catalina.core.ApplicationFilterFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.util.Map;
import java.util.HashMap;
import java.util.Enumeration;
import java.util.Collections;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletOutputStream;

/**
 * @version $Rev$ $Date$
 */
public class AbstractTomcatTest extends TestCase {
    protected Map<String, Class<?>> classes;
    protected Host host;
    protected MockRequest request;
    protected MockResponse response;
    protected StandardEngine engine;

    protected void setUp() throws Exception {
        super.setUp();
        classes = new HashMap<String, Class<?>>();
        classes.put(TestServlet.class.getName(), TestServlet.class);
        classes.put(HelloWorldService.class.getName(), HelloWorldService.class);
        classes.put(HelloWorldImpl.class.getName(), HelloWorldImpl.class);
    }

    protected void setupTomcat(File baseDir, Host host) throws Exception {
        File appBase = new File(baseDir, "webapps").getCanonicalFile();

        // Configure a Tomcat Engine
        engine = new StandardEngine();
        engine.setName("Catalina");
        engine.setDefaultHost("localhost");
        engine.setBaseDir(baseDir.getAbsolutePath());

        this.host = host;
        host.setName("localhost");
        host.setAppBase(appBase.getAbsolutePath());
        engine.addChild(host);

        // build a empty request/response
        Connector connector = new Connector("HTTP/1.1");
        request = new MockRequest();
        request.setConnector(connector);
        response = new MockResponse();
        request.setResponse(response);
        request.setMethod("POST");
        request.setScheme("http");
    }

    public static class MockRequest extends Request {
        private String method;
        private String scheme;
        private String requestURI;
        private String contentType;
        private Map<String,String> headers = new HashMap();
        private ServletInputStream inputStream;

        public void setScheme(String scheme) {
            this.scheme = scheme;
        }

        public void setMethod(String method) {
            this.method = method;
        }

        public void setRequestURI(String requestURI) {
            this.requestURI = requestURI;
        }

        public String getScheme() {
            return scheme;
        }

        public String getMethod() {
            return method;
        }

        public int getServerPort() {
            return 80;
        }

        public String getServerName() {
            return "localhost";
        }

        public String getRequestURI() {
            return requestURI;
        }

        public void setAttribute(String name, Object value) {
            if (name.startsWith("org.apache.tomcat.")) {
                return;
            }
            super.setAttribute(name, value);
        }

        public Object getAttribute(String name) {
            if (name.equals(Globals.DISPATCHER_TYPE_ATTR)) {
                return (dispatcherType == null)
                    ? ApplicationFilterFactory.REQUEST_INTEGER
                    : dispatcherType;
            } else if (name.equals(Globals.DISPATCHER_REQUEST_PATH_ATTR)) {
                return (requestDispatcherPath == null)
                    ? getRequestPathMB().toString()
                    : requestDispatcherPath.toString();
            }

            return attributes.get(name);
        }

        public String getHeader(String name) {
            return headers.get(name);
        }

        public Enumeration getHeaderNames() {
            return Collections.enumeration(headers.keySet());
        }

        public void setContentType(String contentType) {
            this.contentType = contentType;
        }

        public String getContentType() {
            return contentType;
        }

        public ServletInputStream getInputStream() throws IOException {
            return inputStream;
        }

        public InputStream getStream() {
            return inputStream;
        }

        public void setStream(ServletInputStream stream) {
            inputStream = stream;
        }
    }

    public static class MockResponse extends Response {
        private boolean suspended;
        private String contentType;
        private int status;
        private Map headers = new HashMap();
        private MockOutputStream outputStream = new MockOutputStream();

        public boolean isCommitted() {
            return false;
        }

        public boolean isAppCommitted() {
            return false;
        }

        public void sendAcknowledgement() {
        }

        public void setSuspended(boolean suspended) {
            this.suspended = suspended;
        }

        public boolean isSuspended() {
            return suspended;
        }

        public void setStatus(int status, String message) {
            this.status = status;
        }

        public int getStatus() {
            return status;
        }

        public void reset() {
        }

        public void addHeader(String name, String value) {
            headers.put(name, value);
        }

        public String[] getHeaderNames() {
            return (String[]) headers.keySet().toArray(new String[headers.size()]);
        }

        public String getContentType() {
            return contentType;
        }

        public void setContentType(String contentType) {
            this.contentType = contentType;
        }

        public OutputStream getStream() {
            return outputStream;
        }

        public ServletOutputStream getOutputStream() throws IOException {
            return outputStream;
        }
    }

    public static class MockInputStream extends ServletInputStream {
        private final byte[] bytes;
        private int index;

        public MockInputStream(byte[] bytes) {
            this.bytes = bytes;
        }

        public int read() throws IOException {
            if (index == bytes.length) {
                return -1;
            }
            else {
                return bytes[index++];
            }
        }
    }

    public static class MockOutputStream extends ServletOutputStream {
        private ByteArrayOutputStream os = new ByteArrayOutputStream();

        public void write(int b) throws IOException {
            os.write(b);
        }

        public String toString() {
            return os.toString();
        }
    }
}
