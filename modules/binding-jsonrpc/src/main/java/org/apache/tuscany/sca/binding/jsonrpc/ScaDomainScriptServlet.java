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
package org.apache.tuscany.sca.binding.jsonrpc;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet to handle requests for the scaDomain.js script.
 * 
 * This script wrappers the JSON-RPC-Java jsonrpc.js script
 * adding in at the bottom the JavaScript to create and initialize
 * the variables representing the SCA services.
 */
public class ScaDomainScriptServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected transient List<String> serviceNames;

    public ScaDomainScriptServlet() {
        serviceNames = new ArrayList<String>();
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

        ServletOutputStream os = response.getOutputStream();

        os.println();
        os.println("/* Apache Tuscany scaDomain.js Header */");
        os.println();

        writeJSONRPCJavaScript(os);
        writeScaDomainCode(os, request.getServletPath());
    }

    /**
     * Creates a JavaScript variable within the scaDomain script for each SCA service and
     * initializes its value with the JSON-RPC client to invoke the serverside service.
     */
    protected void writeScaDomainCode(ServletOutputStream out, String path) throws IOException {
        out.println();
        out.println("/* Apache Tuscany scaDomain.js Footer  */");
        out.println();
        out.println("function scaDomain() {}");

        // remove the leading slash '/' character
        path = path.substring(1);

        for (String serviceName : serviceNames) {
            out.println();

            // A slight hack to make the service function available with a variable named 'serviceName'
            // to do that the JSONRpcClient is added to the scaDomain and then the service function is got from that
            out.println("scaDomain." + serviceName + " = " + "new JSONRpcClient(\"" + path + "/" + serviceName + "\");");
            out.println(serviceName + " = scaDomain." + serviceName + "." + serviceName + ";");
        }

        out.println();
        out.println("/** End of Apache Tuscany scaDomain.js */");
        out.println();
    }

    /**
     * Reads the jsonrpc.js script from the classpath and adds its contents to the output stream.
     */
    protected void writeJSONRPCJavaScript(ServletOutputStream os) throws IOException {
        URL url = getClass().getResource("jsonrpc.js");
        InputStream is = url.openStream();
        int i;
        while ((i = is.read()) != -1) {
            os.write(i);
        }
    }

    public void addService(String serviceName) {
        serviceNames.add(serviceName);
    }

    public void removeService(String serviceName) {
        serviceNames.remove(serviceName);
    }

    public List<String> getServiceNames() {
        return serviceNames;
    }

}
