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
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class JsonRpc20Request {
    private final String method;
    private final Object id;
    private final Object[] params;
    private final Map<String, Object> mappedParams;

    public JsonRpc20Request(Object id, String method, Object[] params) {
        super();
        this.id = id;
        this.method = method;
        this.params = params;
        this.mappedParams = null;
    }

    public JsonRpc20Request(Object id, String method, Map<String, Object> mappedParams) {
        super();
        this.id = id;
        this.method = method;
        this.params = null;
        this.mappedParams = mappedParams;
    }

    public JSONObject toJSONObject() throws JSONException {
        JSONObject req = new JSONObject();
        req.put("jsonrpc", "2.0");
        req.put("id", id);
        req.put("method", method);
        if (params != null) {
            JSONArray args = new JSONArray(Arrays.asList(params));
            req.put("params", args);
        } else {
            JSONObject args = new JSONObject(mappedParams);
            req.put("params", args);
        }
        return req;
    }

    public JsonRpc20Request(JSONObject req) throws JSONException {
        super();
        if (req.has("jsonrpc") && "2.0".equals(req.getString("jsonrpc"))) {
            method = req.getString("method");
            id = req.opt("id");
            Object args = req.opt("params");
            if (args instanceof JSONArray) {
                // Positional parameters
                JSONArray array = (JSONArray)args;
                params = new Object[array.length()];
                mappedParams = null;
                for (int i = 0; i < params.length; i++) {
                    params[i] = array.get(i);
                }
            } else if (args instanceof JSONObject) {
                JSONObject map = (JSONObject)args;
                mappedParams = new HashMap<String, Object>();
                params = null;
                Iterator<String> keys = map.keys();
                while (keys.hasNext()) {
                    String key = keys.next();
                    Object value = map.get(key);
                    mappedParams.put(key, value);
                }
            } else if (args == null) {
                params = new Object[0];
                mappedParams = null;
            } else {
                throw new IllegalArgumentException("Invalid request: params is not a JSON array or object - " + args);
            }
        } else {
            throw new IllegalArgumentException("Invalid request: missing jsonrpc attribute");
        }
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

    public Map<String, Object> getMappedParams() {
        return mappedParams;
    }
}
