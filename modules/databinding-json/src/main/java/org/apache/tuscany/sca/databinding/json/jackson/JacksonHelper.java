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

package org.apache.tuscany.sca.databinding.json.jackson;

import java.io.IOException;
import java.io.StringWriter;

import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.map.AnnotationIntrospector;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.introspect.JacksonAnnotationIntrospector;
import org.codehaus.jackson.xc.JaxbAnnotationIntrospector;

/**
 * 
 */
public class JacksonHelper {
    public static ObjectMapper createObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        AnnotationIntrospector primary = new JaxbAnnotationIntrospector();
        AnnotationIntrospector secondary = new JacksonAnnotationIntrospector();
        AnnotationIntrospector pair = new AnnotationIntrospector.Pair(primary, secondary);
        mapper.getDeserializationConfig().setAnnotationIntrospector(pair);
        mapper.getSerializationConfig().setAnnotationIntrospector(pair);
        return mapper;
    }

    public static String toString(JsonNode node) {
        try {
            JsonFactory jsonFactory = new JsonFactory();
            StringWriter sw = new StringWriter();
            JsonGenerator generator = jsonFactory.createJsonGenerator(sw);
            generator.writeTree(node);
            return sw.toString();
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public static String toString(JsonParser parser) {
        try {
            JsonFactory jsonFactory = new JsonFactory();
            StringWriter sw = new StringWriter();
            JsonGenerator generator = jsonFactory.createJsonGenerator(sw);
            JsonNode node = parser.readValueAs(JsonNode.class);
            generator.writeTree(node);
            return sw.toString();
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public static JsonParser createJsonParser(String content) {
        JsonFactory jsonFactory = new JsonFactory();
        try {
            return jsonFactory.createJsonParser(content);
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
    }

}
