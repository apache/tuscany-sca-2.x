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

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.JsonNodeFactory;
import org.codehaus.jackson.node.NullNode;
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
public abstract class JsonRpcRequest {
    protected String method;
    protected JsonNode id;
    protected Object[] params;

    protected ObjectNode jsonNode;

    public JsonRpcRequest(ObjectNode jsonNode) {
        super();
        this.jsonNode = jsonNode;
    }

    public JsonRpcRequest(JsonNode id, String method, Object[] params) {
        super();
        this.id = id;
        this.method = method;
        this.params = params;
        ObjectNode req = JsonNodeFactory.instance.objectNode();
        req.put("method", method);
        req.put("id", id);
        if (params != null) {
            ArrayNode args = JsonNodeFactory.instance.arrayNode();
            for (Object p : params) {
                args.add(JsonNodeFactory.instance.POJONode(p));
            }
            req.put("params", args);
        }
        this.jsonNode = req;
    }

    public abstract void write(OutputStream os) throws Exception;

    public boolean isNotification() {
        return id == null || (id instanceof NullNode);
    }

    public String getMethod() {
        return method;
    }

    public JsonNode getId() {
        return id;
    }

    public Object[] getParams() {
        return params;
    }

    public ObjectNode getJsonNode() {
        return jsonNode;
    }

}
