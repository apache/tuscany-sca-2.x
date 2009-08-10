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

package org.apache.tuscany.sca.binding.jsonp.runtime;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.apache.tuscany.sca.assembly.EndpointReference;
import org.apache.tuscany.sca.interfacedef.Operation;
import org.apache.tuscany.sca.invocation.Invoker;
import org.apache.tuscany.sca.invocation.Message;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

public class JSONPInvoker implements Invoker {
    
    protected Operation operation;
    protected EndpointReference endpoint;

    protected ObjectMapper mapper; // TODO: share mapper btw invoker and servlet or move to databinding

    public JSONPInvoker(Operation operation, EndpointReference endpoint) {
        this.operation = operation;
        this.endpoint = endpoint;
        this.mapper = new ObjectMapper();
    }

    public Message invoke(Message msg) {
        try {

            return doInvoke(msg);
            
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Message doInvoke(Message msg) throws JsonGenerationException, JsonMappingException, IOException {
        String uri = endpoint.getBinding().getURI() + "/" + operation.getName();
        String[] jsonArgs = objectsToJSON((Object[])msg.getBody());

        String responseJSON = invokeHTTPRequest(uri, jsonArgs);

        Object response = jsonToObjects(responseJSON)[0];
        msg.setBody(response);

        return msg;
    }

    protected String invokeHTTPRequest(String uri, String[] jsonArgs) {
        // TODO Auto-generated method stub
        return null;
    }

    protected String[] objectsToJSON(Object[] msgArgs) throws JsonGenerationException, JsonMappingException, IOException {
        String[] jsonArgs = new String[msgArgs.length];
        for (int i=0; i<msgArgs.length; i++) {
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            mapper.writeValue(os , msgArgs[i]);
            jsonArgs[i] = os.toString();
        }
        return jsonArgs;
    }

    protected Object[] jsonToObjects(String jsonRequest) throws JsonParseException, JsonMappingException, IOException {
        Class<?> c = new Object[0].getClass();
        Object[] args = (Object[])mapper.readValue(jsonRequest, c);
        return args;
    }

}
