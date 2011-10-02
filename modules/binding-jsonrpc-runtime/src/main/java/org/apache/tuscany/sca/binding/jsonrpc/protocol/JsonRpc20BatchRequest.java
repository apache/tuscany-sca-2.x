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

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class JsonRpc20BatchRequest {
    private List<JsonRpc20Request> requests = new ArrayList<JsonRpc20Request>();
    // The corresponding batch response
    private JsonRpc20BatchResponse batchResponse;

    public JsonRpc20BatchRequest(JSONArray array) {
        super();
        batchResponse = new JsonRpc20BatchResponse();
        for (int i = 0; i < array.length(); i++) {
            Object req = null;
            try {
                req = array.get(i);
            } catch (JSONException e1) {
                // It shouldn't happen
                throw new IllegalArgumentException(e1);
            }
            if (req instanceof JSONObject) {
                try {
                    requests.add(new JsonRpc20Request((JSONObject)req));
                    batchResponse.getResponses().add(null);
                } catch (JSONException e) {
                    // We should return invalid request errors
                    JsonRpc20Error error =
                        new JsonRpc20Error(JSONObject.NULL, JsonRpc20Error.PARSE_ERROR, JsonRpc20Error.PARSE_ERROR_MSG,
                                           req);
                    batchResponse.getResponses().add(error);
                }
            } else {
                // We should return invalid request errors
                JsonRpc20Error error =
                    new JsonRpc20Error(JSONObject.NULL, JsonRpc20Error.INVALID_REQUEST,
                                       JsonRpc20Error.INVALID_REQUEST_MSG, req);
                batchResponse.getResponses().add(error);
            }
        }
    }

    public List<JsonRpc20Request> getRequests() {
        return requests;
    }

    public JsonRpc20BatchResponse getBatchResponse() {
        return batchResponse;
    }

}
