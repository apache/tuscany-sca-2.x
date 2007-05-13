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
package org.apache.tuscany.sca.binding.jsonrpc.server;

import java.io.BufferedReader;
import java.io.CharArrayWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.text.ParseException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.tuscany.sca.binding.jsonrpc.util.JavaToSmd;
import org.json.JSONObject;

import com.metaparadigm.jsonrpc.JSONRPCBridge;
import com.metaparadigm.jsonrpc.JSONRPCResult;
import com.metaparadigm.jsonrpc.JSONRPCServlet;

/**
 * 
 * 
 */
public class JSONRPCEntryPointServlet extends JSONRPCServlet {
    private static final long serialVersionUID = 1L;

    private static final int BUF_SIZE = 4096;

    String serviceName;

    Object serviceInstance;

    Class<?> serviceInterface;

    // default constructor for unit testing
    public JSONRPCEntryPointServlet() {
    }

    public JSONRPCEntryPointServlet(String serviceName, Class<?> serviceInterface, Object serviceInstance) {
        this.serviceName = serviceName;
        this.serviceInterface = serviceInterface;
        this.serviceInstance = serviceInstance;
    }
    
    @Override
    public void service(HttpServletRequest request, HttpServletResponse response) 
        throws IOException, ClassCastException {

        // if there is a smd parameter, we should retrun the SMD for this service
        if (request.getParameter("smd") != null) {
            String serviceUrl = request.getRequestURL().toString();
            String smd = JavaToSmd.interfaceToSmd(serviceInterface, serviceUrl);

            response.setContentType("text/plain;charset=utf-8");
            OutputStream out = response.getOutputStream();
            byte[] bout = smd.getBytes("UTF-8");

            out.write(bout);
            out.flush();
            out.close();
        } else {
            /*
             * Create a new bridge for every request to aviod all the problems with 
             * JSON-RPC-Java storing the bridge in the session
             */
            HttpSession session = request.getSession();

            JSONRPCBridge jsonrpcBridge = new JSONRPCBridge();
            jsonrpcBridge.registerObject(serviceName, serviceInstance, serviceInterface);
            session.setAttribute("JSONRPCBridge", jsonrpcBridge);

            // Encode using UTF-8, although We are actually ASCII clean as
            // all unicode data is JSON escaped using backslash u. This is
            // less data efficient for foreign character sets but it is
            // needed to support naughty browsers such as Konqueror and Safari
            // which do not honour the charset set in the response
            response.setContentType("text/plain;charset=utf-8");
            OutputStream out = response.getOutputStream();

            // Decode using the charset in the request if it exists otherwise
            // use UTF-8 as this is what all browser implementations use.
            // The JSON-RPC-Java JavaScript client is ASCII clean so it
            // although here we can correctly handle data from other clients
            // that do not escape non ASCII data
            String charset = request.getCharacterEncoding();
            if (charset == null) {
                charset = "UTF-8";
            }
            BufferedReader in = new BufferedReader(new InputStreamReader(request.getInputStream(), charset));

            // Read the request
            CharArrayWriter data = new CharArrayWriter();
            char buf[] = new char[BUF_SIZE];
            int ret;
            while ((ret = in.read(buf, 0, BUF_SIZE)) != -1) {
                data.write(buf, 0, ret);
            }

            // Process the request
            JSONObject jsonReq = null;
            JSONRPCResult jsonResp = null;
            try {
                jsonReq = new JSONObject(data.toString());
                String method = jsonReq.getString("method");
                if ((method != null) && (method.indexOf('.') < 0)) {
                    jsonReq.putOpt("method", serviceName + "." + method);
                }
                jsonResp = jsonrpcBridge.call(new Object[] {request}, jsonReq);
            } catch (ParseException e) {
                throw new RuntimeException("Unable to parse request", e);
            }

            byte[] bout = jsonResp.toString().getBytes("UTF-8");

            out.write(bout);
            out.flush();
            out.close();

        }
    }

}
