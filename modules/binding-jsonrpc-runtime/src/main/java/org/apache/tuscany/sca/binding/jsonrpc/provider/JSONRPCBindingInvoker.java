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

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;
import org.apache.tuscany.sca.assembly.EndpointReference;
import org.apache.tuscany.sca.binding.jsonrpc.JSONRPCBinding;
import org.apache.tuscany.sca.interfacedef.Operation;
import org.apache.tuscany.sca.invocation.DataExchangeSemantics;
import org.apache.tuscany.sca.invocation.Invoker;
import org.apache.tuscany.sca.invocation.Message;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Invoker for the JSONRPC Binding
 * 
 * @version $Rev$ $Date$
 */
public class JSONRPCBindingInvoker implements Invoker, DataExchangeSemantics {
    private EndpointReference endpointReference;
    private Operation operation;
    private String uri;
    
    private HttpClient httpClient;

    public JSONRPCBindingInvoker(EndpointReference endpointReference, Operation operation, HttpClient httpClient) {
        this.endpointReference = endpointReference;
        this.operation = operation;
        this.uri = ((JSONRPCBinding) endpointReference.getBinding()).getURI();
        
        this.httpClient = httpClient;
    }

    public Message invoke(Message msg) {
        HttpPost post = null;
        HttpResponse response = null;
        try {

            JSONObject jsonRequest = null;;
            String requestId = "1";
            Object[] args = null;
            try {
                // Extract the method
                jsonRequest = new JSONObject();
                jsonRequest.putOpt("method", "Service" + "." + msg.getOperation().getName());

                // Extract the arguments
                args = msg.getBody();
                JSONArray array = new JSONArray();
                for (int i = 0; i < args.length; i++) {
                    array.put(args[i]);
                }
                jsonRequest.putOpt("params", array);
                jsonRequest.put("id", requestId);

            } catch (Exception e) {
                throw new RuntimeException("Unable to parse JSON parameter", e);
            }

            post = new HttpPost(uri);
            String req = jsonRequest.toString();
            StringEntity entity = new StringEntity(req, "application/json; charset\"UTF-8\"");
            post.setEntity(entity);

            response = httpClient.execute(post);

            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                //success 
                JSONObject jsonResponse = null;
                try {

                    jsonResponse = new JSONObject(EntityUtils.toString(response.getEntity()));

                    //check requestId
                    if (! jsonResponse.getString("id").equalsIgnoreCase(requestId)) {
                        throw new RuntimeException("Invalid response id:" + requestId );
                    }

                    msg.setBody(jsonResponse.get("result"));
                } catch (Exception e) {
                    //FIXME Exceptions are not handled correctly here
                    // They should be reported to the client JavaScript as proper
                    // JavaScript exceptions.
                    throw new RuntimeException("Unable to parse response", e);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            msg.setFaultBody(e);
        }

        return msg;
    }
    
    private static JSONObject getJSONRequest(Message msg) {
        
        JSONObject jsonRequest = null;;
        Object[] args = null;
        Object id = null;
        try {
            // Extract the method
            jsonRequest = new JSONObject();
            jsonRequest.putOpt("method", "Service" + "." + msg.getOperation().getName());
            
            // Extract the arguments
            args = msg.getBody();
            JSONArray array = new JSONArray();
            for (int i = 0; i < args.length; i++) {
                array.put(args[i]);
            }
            jsonRequest.putOpt("params", array);
            id = jsonRequest.put("id", "1");

        } catch (Exception e) {
            throw new RuntimeException("Unable to parse JSON parameter", e);
        }

        return jsonRequest;
    }
    
    public boolean allowsPassByReference() {
        return true;
    }    
}
