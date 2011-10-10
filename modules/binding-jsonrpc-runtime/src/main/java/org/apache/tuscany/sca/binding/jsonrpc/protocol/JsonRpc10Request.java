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

import java.io.OutputStream;

import org.apache.tuscany.sca.databinding.json.jackson.JacksonHelper;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.JsonNodeFactory;
import org.codehaus.jackson.node.ObjectNode;

/**
 * http://json-rpc.org/wiki/specification
 * A remote method is invoked by sending a request to a remote service. The request is a single object serialized using JSON.
 * <br>It has three properties:
 * <ul>
 * <li>method - A String containing the name of the method to be invoked.
 * <li>params - An Array of objects to pass as arguments to the method.
 * <li>id - The request id. This can be of any type. It is used to match the response with the request that it is replying to.
 * </ul> 
 */
public class JsonRpc10Request extends JsonRpcRequest {

    public JsonRpc10Request(String id, String method, Object[] params) {
        super(JsonNodeFactory.instance.textNode(id), method, params);
    }

    public JsonRpc10Request(ObjectNode req) {
        super(req);
        method = req.get("method").getTextValue();
        id = req.get("id");
        JsonNode args = req.get("params");
        if (args instanceof ArrayNode) {
            // Positional parameters
            ArrayNode array = (ArrayNode)args;
            params = new Object[array.size()];
            for (int i = 0; i < params.length; i++) {
                params[i] = array.get(i);
            }
        } else if (args == null) {
            params = new Object[0];
        } else {
            throw new IllegalArgumentException("Invalid request: params is not a JSON array - " + args);
        }
    }

    public void write(OutputStream os) throws Exception {
        JacksonHelper.MAPPER.writeValue(os, getJsonNode());

    }

}
