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
package org.apache.tuscany.sca.binding.jsonrpc.provider;

import java.lang.reflect.Method;
import java.util.Collection;

import org.apache.wink.json4j.JSONArray;
import org.apache.wink.json4j.JSONObject;

/**
 * Utility class to create a Simple Method Description (SMD) descriptor
 * from a Java class. See http://dojo.jot.com/SMD.
 * 
 * TODO: Change to work from the Tuscany Interface instead of a Java class
 * 
 * @version $Rev$ $Date$
 */
public class JavaToSmd {

    public static String interfaceToSmd(Class<?> klazz, String serviceUrl) {
        try {
            String name = klazz.getSimpleName();
            Method[] methods = klazz.getMethods();

            JSONObject smd = new JSONObject();
            smd.put("SMDVersion", ".1");
            smd.put("objectName", name);
            smd.put("serviceType", "JSON-RPC");
            smd.put("serviceURL", serviceUrl);

            JSONArray services = new JSONArray();
            for (int i = 0; i < methods.length; i++) {
                JSONObject service = new JSONObject();
                Class<?>[] params = methods[i].getParameterTypes();
                JSONArray paramArray = new JSONArray();
                for (int j = 0; j < params.length; j++) {
                    JSONObject param = new JSONObject();
                    param.put("name", "param" + j);
                    param.put("type", getJSONType(params[j]));
                    paramArray.put(param);
                }
                service.put("name", methods[i].getName());
                service.put("parameters", paramArray);
                services.put(service);
            }

            smd.put("methods", services);

            return smd.toString(2);
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }

    }

    public static String interfaceToSmd20(Class<?> klazz, String serviceUrl) {
        try {
            String name = klazz.getSimpleName();
            Method[] methods = klazz.getMethods();

            JSONObject smd = new JSONObject();
            smd.put("SMDVersion", "2.0");
            smd.put("transport", "POST");
            smd.put("envelope", "JSON-RPC-1.0");
            smd.put("target", serviceUrl);
            smd.put("id", klazz.getName());
            smd.put("description", "JSON-RPC service provided by Tuscany: " + name);

            JSONObject services = new JSONObject();
            for (int i = 0; i < methods.length; i++) {
                JSONObject service = new JSONObject();
                Class<?>[] params = methods[i].getParameterTypes();
                JSONArray paramArray = new JSONArray();
                for (int j = 0; j < params.length; j++) {
                    JSONObject param = new JSONObject();
                    param.put("name", "param" + j);
                    param.put("type", getJSONType(params[j]));
                    paramArray.put(param);
                }
                service.put("parameters", paramArray);
                services.put(methods[i].getName(), service);
            }

            smd.put("services", services);

            return smd.toString(2);
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }

    }

    private static String getJSONType(Class<?> type) {
        if (type == boolean.class || type == Boolean.class) {
            return "boolean";
        }
        if (type == String.class) {
            return "string";
        }
        if (byte.class == type || short.class == type
            || int.class == type
            || long.class == type
            || float.class == type
            || double.class == type
            || Number.class.isAssignableFrom(type)) {
            return "number";
        }
        if (type.isArray() || Collection.class.isAssignableFrom(type)) {
            return "array";
        }
        return "object";
    }

}
