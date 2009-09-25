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
package org.apache.tuscany.sca.binding.jsonrpc.provider;

import java.io.BufferedReader;
import java.io.CharArrayWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.List;

import javax.servlet.ServletConfig;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.tuscany.sca.assembly.Binding;
import org.apache.tuscany.sca.interfacedef.InterfaceContract;
import org.apache.tuscany.sca.interfacedef.Operation;
import org.apache.tuscany.sca.invocation.Message;
import org.apache.tuscany.sca.invocation.MessageFactory;
import org.apache.tuscany.sca.runtime.RuntimeComponentService;
import org.apache.tuscany.sca.runtime.RuntimeWire;
import org.json.JSONArray;
import org.json.JSONObject;
import org.osoa.sca.ServiceRuntimeException;

import com.metaparadigm.jsonrpc.JSONRPCBridge;
import com.metaparadigm.jsonrpc.JSONRPCResult;
import com.metaparadigm.jsonrpc.JSONRPCServlet;

/**
 * Servlet that handles JSON-RPC requests invoking SCA services.
 * 
 * There is an instance of this Servlet for each <binding.jsonrpc>
 *
 * @version $Rev$ $Date$
 */
public class JSONRPCServiceServlet extends JSONRPCServlet {
    private static final long serialVersionUID = 1L;

    transient MessageFactory messageFactory;
    
    transient Binding binding;
    transient String serviceName;
    transient Object serviceInstance;
    transient RuntimeComponentService componentService;
    transient InterfaceContract serviceContract;
    transient Class<?> serviceInterface;

    public JSONRPCServiceServlet(MessageFactory messageFactory, 
                                 Binding binding,
                                 RuntimeComponentService componentService,
                                 InterfaceContract serviceContract,
                                 Class<?> serviceInterface,
                                 Object serviceInstance) {
        this.messageFactory = messageFactory;
        this.binding = binding;
        this.serviceName = binding.getName();
        this.componentService = componentService;
        this.serviceContract = serviceContract;
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
        if ("smd".equals(request.getQueryString())) {
            handleSMDRequest(request, response);
        } else {
            try {
                handleServiceRequest(request, response);
                
            } catch(RuntimeException re) {
                if (re.getCause() instanceof javax.security.auth.login.LoginException) {
                    response.setHeader("WWW-Authenticate", "BASIC realm=\"" + "ldap-realm" + "\"");
                    response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
                }
            } finally {
                HttpSession session = request.getSession(false);
                if (session != null) {
                    session.removeAttribute("JSONRPCBridge");
                }
            }
        }
    }

    private void handleServiceRequest(HttpServletRequest request, HttpServletResponse response) throws IOException {
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
        char[] buf = new char[4096];
        int ret;
        while ((ret = in.read(buf, 0, 4096)) != -1) {
            data.write(buf, 0, ret);
        }

        JSONObject jsonReq = null;
        String method = null;
        try {
            jsonReq = new JSONObject(data.toString());
            method = jsonReq.getString("method");
        } catch (Exception e) {
            //FIXME Exceptions are not handled correctly here
            // They should be reported to the client JavaScript as proper
            // JavaScript exceptions.
            throw new RuntimeException("Unable to parse request", e);
        }

        
        // check if it's a system request 
        // or a method invocation
        byte[] bout;
        if (method.startsWith("system.")) {
            bout = handleJSONRPCSystemInvocation(request, response, data.toString());
        } else {
            bout = handleJSONRPCMethodInvocation(request, response, jsonReq);
        }

        // Send response to client
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
    
    protected byte[] handleJSONRPCSystemInvocation(HttpServletRequest request, HttpServletResponse response, String requestData) throws IOException,
    UnsupportedEncodingException {
        /*
         * Create a new bridge for every request to avoid all the problems with 
         * JSON-RPC-Java storing the bridge in the session
         */
        HttpSession session = request.getSession();

        JSONRPCBridge jsonrpcBridge = new JSONRPCBridge();
        jsonrpcBridge.registerObject("Service", serviceInstance, serviceInterface);
        session.setAttribute("JSONRPCBridge", jsonrpcBridge);
        
        org.json.JSONObject jsonReq = null;
        com.metaparadigm.jsonrpc.JSONRPCResult jsonResp = null;
        try {
            jsonReq = new org.json.JSONObject(requestData);
        } catch (java.text.ParseException e) {
            throw new RuntimeException("Unable to parse request", e);
        }

        String method = jsonReq.getString("method");
        if ((method != null) && (method.indexOf('.') < 0)) {
            jsonReq.putOpt("method", "Service" + "." + method);
        }

        // invoke the request
        jsonResp = jsonrpcBridge.call(new Object[] {request}, jsonReq);

        return jsonResp.toString().getBytes("UTF-8");
    }
    
    protected byte[] handleJSONRPCMethodInvocation(HttpServletRequest request, HttpServletResponse response, JSONObject jsonReq) throws IOException,
    UnsupportedEncodingException {

        String method = null;
        Object[] args = null;
        Object id = null;
        try {
            // Extract the method
            method = jsonReq.getString("method");
            if ((method != null) && (method.indexOf('.') < 0)) {
                jsonReq.putOpt("method", "Service" + "." + method);
            }
            
            // Extract the arguments
            JSONArray array = jsonReq.getJSONArray("params");
            args = new Object[array.length()];
            for (int i = 0; i < args.length; i++) {
                args[i] = array.get(i);
            }
            id = jsonReq.get("id");

        } catch (Exception e) {
            throw new RuntimeException("Unable to find json method name", e);
        }

        // invoke the request
        RuntimeWire wire = componentService.getRuntimeWire(binding, serviceContract);
        Operation jsonOperation = findOperation(method);
        Object result = null;
      

        // Invoke the get operation on the service implementation
        Message requestMessage = messageFactory.createMessage();
        requestMessage.setOperation(jsonOperation);

        requestMessage.getHeaders().add(request);

        requestMessage.setBody(args);

        //result = wire.invoke(jsonOperation, args);
        Message responseMessage = null;
        try {
            responseMessage = wire.getInvocationChain(jsonOperation).getHeadInvoker().invoke(requestMessage);
        } catch (RuntimeException re) {
            if (re.getCause() instanceof javax.security.auth.login.LoginException) {
                throw re;
            } else {
                //some other exception
                JSONRPCResult errorResult = new JSONRPCResult(JSONRPCResult.CODE_REMOTE_EXCEPTION, id, re);
                return errorResult.toString().getBytes("UTF-8");
            }
        }

        if (!responseMessage.isFault()) {
            //successful execution of the invocation
            try {
                result = responseMessage.getBody();
                JSONObject jsonResponse = new JSONObject();
                jsonResponse.put("result", result);
                jsonResponse.putOpt("id", id);
                //get response to send to client
                return jsonResponse.toString().getBytes("UTF-8");
            } catch (Exception e) {
                throw new ServiceRuntimeException("Unable to create JSON response", e);
            }
        } else {
            //exception thrown while executing the invocation
            Throwable exception = (Throwable)responseMessage.getBody();
            JSONRPCResult errorResult = new JSONRPCResult(JSONRPCResult.CODE_REMOTE_EXCEPTION, id, exception );
            return errorResult.toString().getBytes("UTF-8");
        }
 
   }

    /**
     * Find the operation from the component service contract
     * @param componentService
     * @param method
     * @return
     */
    private Operation findOperation(String method) {
        if (method.contains(".")) {
            method = method.substring(method.lastIndexOf(".") + 1);
        }
    
        List<Operation> operations = serviceContract.getInterface().getOperations();
            //componentService.getBindingProvider(binding).getBindingInterfaceContract().getInterface().getOperations();

        
        Operation result = null;
        for (Operation o : operations) {
            if (o.getName().equalsIgnoreCase(method)) {
                result = o;
                break;
            }
        }

        return result;
    }    
}
