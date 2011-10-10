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

import java.util.Arrays;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
public class JsonRpc10Request {
    private final String method;
    private final Object id;
    private final Object[] params;

    public JsonRpc10Request(Object id, String method, Object[] params) {
        super();
        this.id = id;
        this.method = method;
        this.params = params;
    }

    public JsonRpc10Request(JSONObject req) throws JSONException {
        super();

        method = req.getString("method");
        id = req.opt("id");
        Object args = req.opt("params");
        if (args instanceof JSONArray) {
            // Positional parameters
            JSONArray array = (JSONArray)args;
            params = new Object[array.length()];
            for (int i = 0; i < params.length; i++) {
                params[i] = array.get(i);
            }
        } else if (args == null) {
            params = new Object[0];
        } else {
            throw new IllegalArgumentException("Invalid request: params is not a JSON array - " + args);
        }

    }

    public JSONObject toJSONObject() throws JSONException {
        JSONObject req = new JSONObject();
        req.put("id", id);
        req.put("method", method);
        if (params != null) {
            JSONArray args = new JSONArray(Arrays.asList(params));
            req.put("params", args);
        }
        return req;
    }

    public boolean isNotification() {
        return id == null;
    }

    public String getMethod() {
        return method;
    }

    public Object getId() {
        return id;
    }

    public Object[] getParams() {
        return params;
    }

}
