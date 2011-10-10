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
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.tuscany.sca.databinding.json.jackson.JacksonHelper;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.JsonNodeFactory;
import org.codehaus.jackson.node.ObjectNode;
import org.json.JSONException;

public class JsonRpc20Request extends JsonRpcRequest {

    protected ObjectNode mappedParams;

    public JsonRpc20Request(String id, String method, Object[] params) {
        super(JsonNodeFactory.instance.textNode(id), method, params);
        this.jsonNode.put("jsonrpc", "2.0");
        this.mappedParams = null;
    }

    public JsonRpc20Request(String id, String method, ObjectNode mappedParams) {
        super(JsonNodeFactory.instance.textNode(id), method, null);
        this.jsonNode.put("jsonrpc", "2.0");
        this.mappedParams = mappedParams;
        this.jsonNode.put("params", mappedParams);
    }

    public JsonRpc20Request(ObjectNode req) throws JSONException {
        super(req);
        JsonNode node = req.get("jsonrpc");
        boolean v20 = node != null && "2.0".equals(node.getTextValue());
        if (!v20) {
            throw new IllegalArgumentException("Invalid request: jsonrpc attribute must be \"2.0\"");
        }
        method = req.get("method").getTextValue();
        JsonNode idNode = req.get("id");
        if (idNode != null) {
            id = idNode;
        }
        JsonNode args = req.get("params");
        if (args instanceof ArrayNode) {
            // Positional parameters
            ArrayNode array = (ArrayNode)args;
            params = new Object[array.size()];
            for (int i = 0; i < params.length; i++) {
                params[i] = array.get(i);
            }
        } else if (args instanceof ObjectNode) {
            ObjectNode map = (ObjectNode)args;
            mappedParams = map;
            params = null;
        } else if (args == null) {
            params = new Object[0];
        } else {
            throw new IllegalArgumentException("Invalid request: params is not a JSON array - " + args);
        }
    }

    public void write(OutputStream os) throws Exception {
        // Construct a map to hold JSON-RPC request
        final Map<String, Object> jsonRequest = new HashMap<String, Object>();
        jsonRequest.put("jsonrpc", "2.0");
        jsonRequest.put("id", id);
        jsonRequest.put("method", method);

        if (mappedParams != null) {
            jsonRequest.put("params", mappedParams);
        }

        else {
            List<Object> parameters = null;

            if (params != null) {
                parameters = Arrays.asList(params);
            } else {
                parameters = Collections.emptyList();
            }

            jsonRequest.put("params", parameters);
        }
        JacksonHelper.MAPPER.writeValue(os, jsonRequest);

    }

    public ObjectNode getMappedParams() {
        return mappedParams;
    }
}
