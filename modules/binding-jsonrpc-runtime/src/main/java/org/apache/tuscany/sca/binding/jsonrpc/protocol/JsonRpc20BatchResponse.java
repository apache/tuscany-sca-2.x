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

import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.JsonNodeFactory;

public class JsonRpc20BatchResponse {
    private List<JsonRpcResponse> results = new ArrayList<JsonRpcResponse>();

    public JsonRpc20BatchResponse() {
        super();
    }

    public List<JsonRpcResponse> getResponses() {
        return results;
    }

    public ArrayNode toJSONArray() {
        ArrayNode jsonArray = JsonNodeFactory.instance.arrayNode();
        for (JsonRpcResponse result : results) {
            jsonArray.add(result.getJsonNode());
        }
        return jsonArray;
    }

}
