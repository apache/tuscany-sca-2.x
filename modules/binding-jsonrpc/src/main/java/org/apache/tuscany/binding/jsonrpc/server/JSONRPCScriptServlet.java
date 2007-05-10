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
package org.apache.tuscany.binding.jsonrpc.server;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class JSONRPCScriptServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    // private static final String SCA_INIT_JS = "SCA = new JSONRpcClient(\"serviceBindings/HelloWorldService\");";

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        URL url = getClass().getResource("jsonrpc.js");
        InputStream is = url.openStream();
        writeToStream(response, is);

        // writeToStream(response, new ByteArrayInputStream(SCA_INIT_JS.getBytes()));
    }

    private void writeToStream(HttpServletResponse response, InputStream is) throws IOException {
        ServletOutputStream os = response.getOutputStream();
        int i;
        while ((i = is.read()) != -1) { // NOPMD
            os.write(i);
        }
    }

}
