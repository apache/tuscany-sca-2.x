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

package org.apache.tuscany.sca.databinding.json;

import java.util.Collection;

import org.apache.tuscany.sca.databinding.json.jackson.JacksonHelper;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.apache.wink.json4j.JSONArray;

/**
 * @version $Rev$ $Date$
 */
public class JSONHelper {
    private JSONHelper() {

    }

    /**
     * Convert to Jettison JSONObject
     * @param source
     * @return
     */
    public static JSONObject toJettison(Object source) {
        JSONObject json = null;
        if (source instanceof JSONObject) {
            json = (JSONObject)source;
        } else if (source instanceof org.apache.wink.json4j.JSONObject || source instanceof String) {
            json = stringToJettision(source.toString());
        } else if (source instanceof JsonNode) {
            json = stringToJettision(JacksonHelper.toString((JsonNode)source));
        } else if (source instanceof JsonParser) {
            json = stringToJettision(JacksonHelper.toString((JsonParser)source));
        }
        return json;
    }

    private static JSONObject stringToJettision(String content) {
        try {
            return new JSONObject(content);
        } catch (JSONException e) {
            throw new IllegalArgumentException(e);
        }
    }

    /**
     * Convert to org.apache.wink.json4j.JSONObject
     * @param source
     * @return
     */
    public static org.apache.wink.json4j.JSONObject toJSONOrg(Object source) {
        org.apache.wink.json4j.JSONObject json = null;
        if (source instanceof JSONObject) {
            try {
                json = new org.apache.wink.json4j.JSONObject(((JSONObject)source).toString());
            } catch (org.apache.wink.json4j.JSONException e) {
                throw new IllegalArgumentException(e);
            }
        } else if (source instanceof org.apache.wink.json4j.JSONObject) {
            json = (org.apache.wink.json4j.JSONObject)source;
        }
        return json;
    }

    public static Object toJSON(String json, Class<?> type) {
        if (type == JSONObject.class) {
            try {
                return new JSONObject(json);
            } catch (JSONException e) {
                throw new IllegalArgumentException(e);
            }
        } else {
            if (type == null) {
                type = org.apache.wink.json4j.JSONObject.class;
            }
            try {
                if (type == JSONArray.class || type.isArray() || Collection.class.isAssignableFrom(type)) {
                    return new JSONArray(json);
                }
                return JacksonHelper.MAPPER.readValue(json, org.apache.wink.json4j.JSONObject.class);
            } catch (Exception e) {
                throw new IllegalArgumentException(e);
            }
        }
    }
}
