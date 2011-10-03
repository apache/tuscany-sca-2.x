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
import java.io.Writer;

import org.json.JSONException;
import org.json.JSONObject;

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
public class JsonRpc10Response {
    private Object id;
    private Object result;
    private Object error;

    public JsonRpc10Response(Object id, Object result, Object error) {
        super();
        this.id = id;
        this.result = result;
        this.error = error;
        if (result != null && error != null) {
            throw new IllegalArgumentException("Either result or error has to be null");
        }
    }

    public JSONObject toJSONObject() throws JSONException {
        JSONObject response = new JSONObject();
        response.put("id", id);
        if (result != null) {
            response.put("result", result);
        } else {
            response.put("result", JSONObject.NULL);
        }

        if (error != null) {
            response.put("error", error);
        } else {
            response.put("error", JSONObject.NULL);
        }
        return response;
    }

    public void write(Writer writer) throws IOException {
        try {
            toJSONObject().write(writer);
        } catch (JSONException e) {
            throw new IOException(e);
        }
    }

}
