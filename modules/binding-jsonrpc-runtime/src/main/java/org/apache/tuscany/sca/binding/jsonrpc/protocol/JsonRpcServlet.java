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

package org.apache.tuscany.sca.binding.jsonrpc.protocol;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.tuscany.sca.assembly.Binding;
import org.apache.tuscany.sca.binding.jsonrpc.provider.JavaToSmd;
import org.apache.tuscany.sca.interfacedef.Operation;
import org.apache.tuscany.sca.invocation.Message;
import org.apache.tuscany.sca.invocation.MessageFactory;
import org.apache.tuscany.sca.runtime.RuntimeEndpoint;
import org.json.JSONArray;
import org.json.JSONObject;
import org.oasisopen.sca.ServiceRuntimeException;

public class JsonRpcServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    transient MessageFactory messageFactory;

    transient Binding binding;
    transient String serviceName;
    transient Object serviceInstance;
    transient RuntimeEndpoint endpoint;
    transient Class<?> serviceInterface;

    public JsonRpcServlet(MessageFactory messageFactory,
                          RuntimeEndpoint endpoint,
                          Class<?> serviceInterface,
                          Object serviceInstance) {
        this.endpoint = endpoint;
        this.messageFactory = messageFactory;
        this.binding = endpoint.getBinding();
        this.serviceName = binding.getName();
        this.serviceInterface = serviceInterface;
        this.serviceInstance = serviceInstance;
    }

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException,
        IOException {

        if ("smd".equals(request.getQueryString())) {
            handleSMDRequest(request, response);
            return;
        }
        try {
            handleJsonRpcInvocation(request, response);

        } catch (RuntimeException re) {
            if (re.getCause() instanceof javax.security.auth.login.LoginException) {
                response.setHeader("WWW-Authenticate", "BASIC realm=\"" + "ldap-realm" + "\"");
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            } else {
                throw re;
            }
        }

    }

    private void handleJsonRpcInvocation(HttpServletRequest request, HttpServletResponse response)
        throws UnsupportedEncodingException, IOException, ServletException {
        // Decode using the charset in the request if it exists otherwise
        // use UTF-8 as this is what all browser implementations use.
        // The JSON-RPC-Java JavaScript client is ASCII clean so it
        // although here we can correctly handle data from other clients
        // that do not escape non ASCII data
        String charset = request.getCharacterEncoding();
        if (charset == null) {
            charset = "UTF-8";
        }
        // default POST style
        BufferedReader in = new BufferedReader(new InputStreamReader(request.getInputStream(), charset));
        StringWriter data = new StringWriter();
        // Read the request into charArray
        char[] buf = new char[4096];
        int ret;
        while ((ret = in.read(buf, 0, 4096)) != -1) {
            data.write(buf, 0, ret);
        }

        String json = data.toString().trim();
        try {
            if (json.startsWith("[")) {
                JSONArray input = new JSONArray(json);
                JsonRpc20BatchRequest batchReq = new JsonRpc20BatchRequest(input);
                for (int i = 0; i < batchReq.getRequests().size(); i++) {
                    JsonRpc20Result result = batchReq.getBatchResponse().getResponses().get(i);
                    if (result == null) {
                        result = invoke(batchReq.getRequests().get(i));
                        batchReq.getBatchResponse().getResponses().set(i, result);
                    }
                }
                JSONArray responses = batchReq.getBatchResponse().toJSONArray();
                responses.write(response.getWriter());
            } else {
                JSONObject input = new JSONObject(data.toString());
                if (input.has("jsonrpc")) {
                    JsonRpc20Request jsonReq = new JsonRpc20Request(input);
                    JsonRpc20Result jsonResult = invoke(jsonReq);
                    jsonResult.write(response.getWriter());
                } else {
                    JsonRpc10Request jsonReq = new JsonRpc10Request(input);
                    JsonRpc10Response jsonResult = invoke(jsonReq);
                    jsonResult.write(response.getWriter());
                }
            }
        } catch (Throwable e) {
            throw new ServletException(e);
        }
    }

    private JsonRpc20Result invoke(JsonRpc20Request request) {

        // invoke the request
        String method = request.getMethod();
        Object[] params = request.getParams();

        Object result = null;
        Operation jsonOperation = findOperation(method);

        // Invoke the get operation on the service implementation
        Message requestMessage = messageFactory.createMessage();
        requestMessage.setOperation(jsonOperation);

        requestMessage.getHeaders().put("RequestMessage", request);
        requestMessage.setBody(params);

        Message responseMessage = null;
        try {

            //result = wire.invoke(jsonOperation, args);
            responseMessage = endpoint.getInvocationChain(jsonOperation).getHeadInvoker().invoke(requestMessage);
        } catch (RuntimeException re) {
            if (re.getCause() instanceof javax.security.auth.login.LoginException) {
                throw re;
            } else {
                JsonRpc20Error error = new JsonRpc20Error(request.getId(), re);
                return error;
            }
        }

        if (!responseMessage.isFault()) {

            if (jsonOperation.getOutputType().getLogical().size() == 0) {
                // void operation (json-rpc notification)
                try {
                    JsonRpc20Response response = new JsonRpc20Response(request.getId(), null);
                    return response;
                } catch (Exception e) {
                    throw new ServiceRuntimeException("Unable to create JSON response", e);
                }

            } else {
                // regular operation returning some value
                try {
                    result = responseMessage.getBody();
                    JsonRpc20Response response = new JsonRpc20Response(request.getId(), result);
                    //get response to send to client
                    return response;
                } catch (Exception e) {
                    throw new ServiceRuntimeException("Unable to create JSON response", e);
                }
            }

        } else {
            //exception thrown while executing the invocation
            Throwable exception = (Throwable)responseMessage.getBody();

            JsonRpc20Error error = new JsonRpc20Error(request.getId(), exception);
            return error;
        }

    }

    private JsonRpc10Response invoke(JsonRpc10Request request) {

        // invoke the request
        String method = request.getMethod();
        Object[] params = request.getParams();

        Object result = null;
        Operation jsonOperation = findOperation(method);

        // Invoke the get operation on the service implementation
        Message requestMessage = messageFactory.createMessage();
        requestMessage.setOperation(jsonOperation);

        requestMessage.getHeaders().put("RequestMessage", request);
        requestMessage.setBody(params);

        Message responseMessage = null;
        try {

            //result = wire.invoke(jsonOperation, args);
            responseMessage = endpoint.getInvocationChain(jsonOperation).getHeadInvoker().invoke(requestMessage);
        } catch (RuntimeException re) {
            if (re.getCause() instanceof javax.security.auth.login.LoginException) {
                throw re;
            } else {
                JsonRpc10Response error = new JsonRpc10Response(request.getId(), null, re.toString());
                return error;
            }
        }

        if (!responseMessage.isFault()) {

            if (jsonOperation.getOutputType().getLogical().size() == 0) {
                // void operation (json-rpc notification)
                try {
                    JsonRpc10Response response = new JsonRpc10Response(request.getId(), JSONObject.NULL, null);
                    return response;
                } catch (Exception e) {
                    throw new ServiceRuntimeException("Unable to create JSON response", e);
                }

            } else {
                // regular operation returning some value
                try {
                    result = responseMessage.getBody();
                    JsonRpc10Response response = new JsonRpc10Response(request.getId(), result, null);
                    //get response to send to client
                    return response;
                } catch (Exception e) {
                    throw new ServiceRuntimeException("Unable to create JSON response", e);
                }
            }

        } else {
            //exception thrown while executing the invocation
            Throwable exception = (Throwable)responseMessage.getBody();

            JsonRpc10Response error =
                new JsonRpc10Response(request.getId(), null, JsonRpc20Error.stackTrace(exception));
            return error;
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

        List<Operation> operations = endpoint.getComponentServiceInterfaceContract().getInterface().getOperations();
        //endpoint.getComponentTypeServiceInterfaceContract().getInterface().getOperations();
        //componentService.getBindingProvider(binding).getBindingInterfaceContract().getInterface().getOperations();

        Operation result = null;
        for (Operation o : operations) {
            if (o.isDynamic())
                return o;
            if (o.getName().equalsIgnoreCase(method)) {
                result = o;
                break;
            }
        }

        return result;
    }

    /**
     * handles requests for the SMD descriptor for a service
     */
    protected void handleSMDRequest(HttpServletRequest request, HttpServletResponse response) throws IOException,
        UnsupportedEncodingException {
        String serviceUrl = request.getRequestURL().toString();
        String smd = JavaToSmd.interfaceToSmd20(serviceInterface, serviceUrl);

        response.setContentType("application/json;charset=utf-8");
        OutputStream out = response.getOutputStream();
        byte[] bout = smd.getBytes("UTF-8");
        out.write(bout);
        out.flush();
        out.close();
    }

}
