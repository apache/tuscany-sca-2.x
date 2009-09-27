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

import java.text.ParseException;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

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
        } else if (source instanceof org.json.JSONObject) {
            try {
                json = new JSONObject(((org.json.JSONObject)source).toString());
            } catch (JSONException e) {
                throw new IllegalArgumentException(e);
            }
        }
        return json;
    }

    /**
     * Convert to org.json.JSONObject
     * @param source
     * @return
     */
    public static org.json.JSONObject toJSONOrg(Object source) {
        org.json.JSONObject json = null;
        if (source instanceof JSONObject) {
            try {
                json = new org.json.JSONObject(((JSONObject)source).toString());
            } catch (ParseException e) {
                throw new IllegalArgumentException(e);
            }
        } else if (source instanceof org.json.JSONObject) {
            json = (org.json.JSONObject)source;
        }
        return json;
    }

    public static <T> T toJSON(String json, Class<T> type) {
        if (type == JSONObject.class) {
            try {
                return type.cast(new JSONObject(json));
            } catch (JSONException e) {
                throw new IllegalArgumentException(e);
            }
        } else {
            if (type == null) {
                type = (Class<T>)org.json.JSONObject.class;
            }
            try {
                return type.cast(new org.json.JSONObject(json));
            } catch (ParseException e) {
                throw new IllegalArgumentException(e);
            }
        }
    }
}
