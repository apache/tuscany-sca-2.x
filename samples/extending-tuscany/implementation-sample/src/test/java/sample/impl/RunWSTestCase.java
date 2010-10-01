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

package sample.impl;

import static java.lang.System.out;
import static org.junit.Assert.assertTrue;
import static sample.impl.TestUtil.here;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.http.jetty.JettyServer;
import org.apache.tuscany.sca.node.Contribution;
import org.apache.tuscany.sca.node.Node;
import org.apache.tuscany.sca.node.NodeFactory;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Test a component that provides and consumes SOAP Web services.
 * 
 * @version $Rev$ $Date$
 */
public class RunWSTestCase {
    static Node node;
    static JettyServer jetty;
    
    @BeforeClass
    public static void setUp() throws Exception {
        // Start test composite on a Tuscany node
        final NodeFactory nf = NodeFactory.newInstance();
        node = nf.createNode(new Contribution("test", here()));
        node.start();
        
        // Mock up a test Web service on http://localhost:8086/wsupper
        jetty = new JettyServer((ExtensionPointRegistry)nf.getExtensionPointRegistry());
        jetty.start();
        jetty.addServletMapping("http://localhost:8086/wsupper", new HttpServlet() {
            private static final long serialVersionUID = 1L;
            protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
                assertTrue(read(req.getInputStream()).contains("Hello SOAP"));
                final String soapresp =
                    "<?xml version='1.0' encoding='UTF-8'?><soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\">" +
                    "<soapenv:Body><upperResponse xmlns=\"http://sample/upper\">" +
                    "<result xmlns=\"\">HELLO SOAP</result>" +
                    "</upperResponse></soapenv:Body></soapenv:Envelope>";

                write(soapresp, resp.getOutputStream());
            }
        });
    }

    @AfterClass
    public static void tearDown() throws Exception {
        jetty.stop();
        node.stop();
    }

    @Test
    public void wsello() throws Exception {
        out.println("RunWSTestCase.wsello");
        // Send a SOAP request to the Web service provided by SCA component wsello-test
        // on http://localhost:8085/wsello
        final Socket s = new Socket("localhost", 8085);
        final String soapreq =
            "POST /wsello HTTP/1.0\r\n" +
            "Content-Type: text/xml; charset=UTF-8\r\n" +
            "Content-length: 231\r\n\r\n" +
            "<?xml version='1.0' encoding='UTF-8'?><soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\">" +
            "<soapenv:Body><hello xmlns=\"http://sample/hello\">" +
            "<name xmlns=\"\">SOAP</name>" +
            "</hello></soapenv:Body></soapenv:Envelope>";
        write(soapreq, s.getOutputStream());
        assertTrue(read(s.getInputStream()).contains("HELLO SOAP"));
    }
    
    static void write(final String s, final OutputStream o) throws IOException {
        final OutputStreamWriter w = new OutputStreamWriter(o);
        w.write(s);
        w.flush();
    }
    
    static String read(final InputStream i) throws IOException {
        return read(new BufferedReader(new InputStreamReader(i)));
    }

    static String read(final BufferedReader r) throws IOException {
        final String s = r.readLine();
        return s == null? "" : s + read(r);
    }
}
