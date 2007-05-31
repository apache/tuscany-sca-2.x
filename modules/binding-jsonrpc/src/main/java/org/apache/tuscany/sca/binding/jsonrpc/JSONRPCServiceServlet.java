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

import java.io.BufferedReader;
import java.io.CharArrayWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;

import javax.servlet.ServletConfig;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.JSONObject;

import com.metaparadigm.jsonrpc.JSONRPCBridge;
import com.metaparadigm.jsonrpc.JSONRPCResult;
import com.metaparadigm.jsonrpc.JSONRPCServlet;

/**
 * Servlet that handles JSON-RPC requests invoking SCA services.
 * 
 * There is an instance of this servlet for each <binding.jsonrpc>
 */
public class JSONRPCServiceServlet extends JSONRPCServlet {
    private static final long serialVersionUID = 1L;

    transient String serviceName;
    transient Object serviceInstance;
    transient Class<?> serviceInterface;

    public JSONRPCServiceServlet(String serviceName, Class<?> serviceInterface, Object serviceInstance) {
        this.serviceName = serviceName;
        this.serviceInterface = serviceInterface;
        this.serviceInstance = serviceInstance;
    }

    /**
     * Override to do nothing as the JSONRPCServlet is setup by the
     * service method in this class.
     */
    @Override
    public void init(ServletConfig config) {
    }

    @Override
    public void service(HttpServletRequest request, HttpServletResponse response) throws IOException {

        if (request.getParameter("smd") != null) {
            handleSMDRequest(request, response);
        } else {
            try {
                handleServiceRequest(request, response);
            } finally {
                HttpSession session = request.getSession();
                if (session != null) {
                    session.removeAttribute("JSONRPCBridge");
                }
            }
        }
    }

    private void handleServiceRequest(HttpServletRequest request, HttpServletResponse response) throws IOException {
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
        char buf[] = new char[4096];
        int ret;
        while ((ret = in.read(buf, 0, 4096)) != -1) {
            data.write(buf, 0, ret);
        }

        JSONObject jsonReq = null;
        JSONRPCResult jsonResp = null;
        try {
            jsonReq = new JSONObject(data.toString());
        } catch (ParseException e) {
            throw new RuntimeException("Unable to parse request", e);
        }

        String method = jsonReq.getString("method");
        if ((method != null) && (method.indexOf('.') < 0)) {
            jsonReq.putOpt("method", serviceName + "." + method);
        }

        // invoke the request
        jsonResp = jsonrpcBridge.call(new Object[] {request}, jsonReq);

        byte[] bout = jsonResp.toString().getBytes("UTF-8");

        out.write(bout);
        out.flush();
        out.close();
    }

    /**
     * handles requests for the SMD descriptor for a service
     */
    protected void handleSMDRequest(HttpServletRequest request, HttpServletResponse response) throws IOException, UnsupportedEncodingException {
        String serviceUrl = request.getRequestURL().toString();
        String smd = JavaToSmd.interfaceToSmd(serviceInterface, serviceUrl);

        response.setContentType("text/plain;charset=utf-8");
        OutputStream out = response.getOutputStream();
        byte[] bout = smd.getBytes("UTF-8");

        out.write(bout);
        out.flush();
        out.close();
    }

}
