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
package org.apache.tuscany.sca.binding.comet.runtime;

import java.util.ArrayList;
import java.util.List;

import org.apache.tuscany.sca.interfacedef.DataType;
import org.apache.tuscany.sca.interfacedef.Operation;

import com.google.gson.Gson;

/**
 * Helper class to facilitate JSON convertions.
 */
public class JSONUtil {

    private static Gson gson = new Gson();

    /**
     * Convert request parameters from JSON to operation parameter types.
     * 
     * @param jsonData
     *            parameters in JSON array format
     * @param operation
     *            the operation to invoke
     * @return an array of objects
     */
    public static Object[] decodeJsonParamsForOperation(String jsonData, Operation operation) {
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
     * Split the JSON array containing the arguments for the method call in
     * order to avoid converting JSON to Object[]. Converting each object
     * separately to it's corresponding type avoids type mismatch problems at
     * service invocation.
     * 
     * @param jsonArray
     *            the JSON array
     * @return an array of JSON formatted strings
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

    /**
     * Converts a Java object to JSON format.
     * 
     * @param response
     *            the response to convert
     * @return the object in JSON format
     */
    public static String encodeResponse(Object response) {
        return gson.toJson(response);
    }

    /**
     * Convert request parameters as JSON array.
     * 
     * @param params
     *            request parameters
     * @return request parameters as JSON array
     */
    public static String encodeRequestParams(Object[] params) {
        StringBuilder builder = new StringBuilder();
        for (int index = 0; index < params.length; index++) {
            Object param = params[index];
            builder.append(index == 0 ? "[" : ",");
            builder.append(gson.toJson(param));
        }
        builder.append("]");
        return builder.toString();
    }

    /**
     * Decode JSON to a given Java type.
     * 
     * @param responseJSON
     *            the json to convert
     * @param returnType
     *            the return type to convert to
     * @return the converted object
     */
    public static Object decodeResponse(String responseJSON, Class<?> returnType) {
        return gson.fromJson(responseJSON, returnType);
    }

}
