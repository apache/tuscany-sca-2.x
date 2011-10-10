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

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;

import org.apache.tuscany.sca.databinding.json.jackson.JacksonHelper;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.JsonNodeFactory;
import org.codehaus.jackson.node.ObjectNode;

/**
 * When the method invocation completes, the service must reply with a response. The response is a single object serialized using JSON.
 * <br>
 * It has three properties:
 * 
 * <ul>
 * <li>result - The Object that was returned by the invoked method. This must be null in case there was an error invoking the method.
 * <li>error - An Error object if there was an error invoking the method. It must be null if there was no error.
 * <li>id - This must be the same id as the request it is responding to.
 * </ul> 
 */
public abstract class JsonRpcResponse {
    public static final int REMOTE_EXCEPTION = 490;

    protected ObjectNode jsonNode;

    public JsonRpcResponse(ObjectNode response) {
        super();
        this.jsonNode = response;
    }

    public JsonRpcResponse(JsonNode id, Throwable t) {
        super();
        ObjectNode response = JsonNodeFactory.instance.objectNode();
        response.put("id", id);
        // response.put("result", JsonNodeFactory.instance.nullNode());
        response.put("error", mapError(t));
        this.jsonNode = response;
    }

    public JsonRpcResponse(JsonNode id, JsonNode result) {
        super();
        ObjectNode response = JsonNodeFactory.instance.objectNode();
        response.put("id", id);
        response.put("result", result);
        // response.put("error", JsonNodeFactory.instance.nullNode());
        this.jsonNode = response;
    }

    private ObjectNode mapError(Throwable t) {
        ObjectNode obj = JsonNodeFactory.instance.objectNode();
        // obj.put("msg", t.getMessage());
        obj.put("code", REMOTE_EXCEPTION);
        obj.put("message", t.getMessage());
        ObjectNode exception = JsonNodeFactory.instance.objectNode();
        exception.put("class", t.getClass().getName());
        exception.put("message", t.getMessage());
        exception.put("stackTrace", JsonRpc20Error.stackTrace(t));
        obj.put("data", exception);
        return obj;
    }

    public void write(Writer writer) throws IOException {
        JacksonHelper.MAPPER.writeValue(writer, jsonNode);
    }

    public static String stackTrace(Throwable t) {
        StringWriter sw = new StringWriter();
        t.printStackTrace(new PrintWriter(sw));
        return sw.toString();
    }

    public ObjectNode getJsonNode() {
        return jsonNode;
    }

}
