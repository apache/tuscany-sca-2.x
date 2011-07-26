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
package org.apache.tuscany.sca.binding.websocket.runtime;

import java.util.ArrayList;
import java.util.List;

import org.apache.tuscany.sca.interfacedef.DataType;
import org.apache.tuscany.sca.interfacedef.Operation;

import com.google.gson.Gson;

/**
 * Utility class to handle JSON convertions.
 */
public class JSONUtil {

    private static Gson gson = new Gson();

    public static String encodeMessage(WebsocketBindingMessage message) {
        return gson.toJson(message);
    }

    public static WebsocketBindingMessage decodeMessage(String jsonMessage) {
        return gson.fromJson(jsonMessage, WebsocketBindingMessage.class);
    }

    public static String encodePayload(Object payload) {
        return gson.toJson(payload);
    }

    /**
     * Convert opeartion parameters from JSON to the appropriate parameter
     * types.
     */
    public static Object[] decodePayloadForOperation(String jsonData, Operation operation) {
        Object[] args = new Object[operation.getInputType().getLogical().size()];
        final String[] json = parseArray(jsonData);
        int index = 0;
        for (final DataType<?> dataType : operation.getInputType().getLogical()) {
            args[index] = gson.fromJson(json[index], dataType.getPhysical());
            index++;
        }
        return args;
    }

    /**
     * Split the JSON array containing the arguments for the opeartion in order
     * to avoid converting JSON to Object[]. Converting each object separately
     * to it's corresponding data type avoids type mismatch problems at service
     * invocation.
     */
    private static String[] parseArray(String jsonArray) {
        List<String> objects = new ArrayList<String>();
        int bracketNum = 0;
        int parNum = 0;
        int quoteNum = 0;
        int startPos = 1;
        for (int i = 0; i < jsonArray.length(); i++) {
            switch (jsonArray.charAt(i)) {
            case '{':
                bracketNum++;
                break;
            case '}':
                bracketNum--;
                break;
            case '[':
                parNum++;
                break;
            case ']':
                parNum--;
                break;
            case '\"':
                quoteNum++;
                break;
            case ',':
                if ((bracketNum == 0) && (parNum == 1) && quoteNum % 2 == 0) {
                    objects.add(jsonArray.substring(startPos, i));
                    startPos = i + 1;
                }
            }
        }
        objects.add(jsonArray.substring(startPos, jsonArray.length() - 1));
        return objects.toArray(new String[] {});
    }

    private JSONUtil() {
    }

}
