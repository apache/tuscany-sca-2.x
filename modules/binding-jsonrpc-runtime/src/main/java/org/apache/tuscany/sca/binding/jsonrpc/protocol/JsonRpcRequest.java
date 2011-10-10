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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;

import org.apache.tuscany.sca.databinding.json.jackson.JacksonHelper;
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
public abstract class JsonRpcRequest {
    protected String method;
    protected Object id;
    protected Object[] params;

    protected JSONObject jsonObject;

    public JsonRpcRequest(Object id, String method, Object[] params) {
        super();
        this.id = id;
        this.method = method;
        this.params = params;
    }

    protected JsonRpcRequest(JSONObject jsonObject) {
        super();
        this.jsonObject = jsonObject;
    }

    public JSONObject toJSONObject() throws Exception {
        if (jsonObject != null) {
            return jsonObject;
        } else {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            write(bos);
            ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
            jsonObject = JacksonHelper.MAPPER.readValue(bis, JSONObject.class);
        }
        return jsonObject;
    }

    public abstract void write(OutputStream os) throws Exception;

    public boolean isNotification() {
        return id == null || id == JSONObject.NULL;
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
