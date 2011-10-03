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

import java.io.PrintWriter;
import java.io.StringWriter;

import org.json.JSONException;
import org.json.JSONObject;

public class JsonRpc20Error extends JsonRpc20Result {
    private final int code; // The error code
    private final String messaege; // The error message
    private final Object data; // The json data for the error

    public static final int PARSE_ERROR = 32700;
    public static final String PARSE_ERROR_MSG =
        "Parse error: Invalid JSON was received by the server. " + "An error occurred on the server while parsing the JSON text.";

    public static final int INVALID_REQUEST = -32600;
    public static final String INVALID_REQUEST_MSG = "Invalid Request: The JSON sent is not a valid Request object.";

    public static final int METHOD_NOT_FOUND = -32601;
    public static final String METHOD_NOT_FOUND_MSG = "Method not found: The method does not exist / is not available.";

    public static final int INVALID_PARAMS = -32602;
    public static final String INVALID_PARAMS_MSG = "Invalid params  Invalid method parameter(s).";

    public static final int INTERNAL_ERROR = -32603;
    public static final String INTERNAL_ERROR_MSG = "Internal error  Internal JSON-RPC error.";

    // -32099 to -32000        Server error    Reserved for implementation-defined server-errors.

    public JsonRpc20Error(Object id, int code, String messaege, Object data) {
        super(id);
        this.code = code;
        this.messaege = messaege;
        this.data = data;
    }

    public JsonRpc20Error(Object id, Throwable t) {
        super(id);
        this.code = INTERNAL_ERROR;
        this.messaege = t.getMessage();
        this.data = stackTrace(t);
    }

    public static String stackTrace(Throwable t) {
        StringWriter sw = new StringWriter();
        t.printStackTrace(new PrintWriter(sw));
        return sw.toString();
    }

    public JSONObject toJSONObject() throws JSONException {
        JSONObject response = new JSONObject();
        response.put("jsonrpc", "2.0");
        response.put("id", id);
        JSONObject error = new JSONObject();
        error.put("code", code);
        error.put("message", messaege);
        error.put("data", data);
        response.put("error", error);

        return response;
    }

}
