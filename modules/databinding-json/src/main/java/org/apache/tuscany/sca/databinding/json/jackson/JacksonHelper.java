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
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringWriter;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.Stack;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapters;

import org.apache.tuscany.sca.databinding.TransformationContext;
import org.codehaus.jackson.JsonEncoding;
import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.map.AnnotationIntrospector;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.MappingJsonFactory;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializerProvider;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.codehaus.jackson.map.deser.BeanDeserializerFactory;
import org.codehaus.jackson.map.deser.StdDeserializerProvider;
import org.codehaus.jackson.map.introspect.AnnotatedClass;
import org.codehaus.jackson.map.introspect.JacksonAnnotationIntrospector;
import org.codehaus.jackson.map.module.SimpleDeserializers;
import org.codehaus.jackson.map.ser.BeanPropertyWriter;
import org.codehaus.jackson.map.ser.CustomSerializerFactory;
import org.codehaus.jackson.map.ser.FilterProvider;
import org.codehaus.jackson.map.ser.impl.SimpleBeanPropertyFilter;
import org.codehaus.jackson.map.ser.impl.SimpleFilterProvider;
import org.codehaus.jackson.map.util.StdDateFormat;
import org.codehaus.jackson.xc.JaxbAnnotationIntrospector;
import org.codehaus.jackson.xc.XmlAdapterJsonDeserializer;
import org.codehaus.jackson.xc.XmlAdapterJsonSerializer;
import org.json.JSONObject;

import com.fasterxml.jackson.module.jsonorg.JsonOrgModule;

/**
 * Helper class for Jackson
 */
public class JacksonHelper {
    public static final String TUSCANY_FILTER = "tuscanyFilter";
    public static final String EXCLUDED_FIELDS = "excludedFields";
    public static final String INCLUDED_FIELDS = "includedFields";
    private final static SimpleBeanPropertyFilter DEFAULT_FILTER = SimpleBeanPropertyFilter.serializeAllExcept();

    /**
     * The default instance of Jackson ObjectMapper
     */
    public final static ObjectMapper MAPPER = createMapper();
    private final static JsonFactory FACTORY = new MappingJsonFactory(createMapper());

    public static ObjectMapper createMapper() {
        return createObjectMapper(null);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public static ObjectMapper createObjectMapper(Class<?> cls) {
        ObjectMapper mapper = null;
        if (cls != null) {
            // Workaround for http://jira.codehaus.org/browse/JACKSON-413
            Package pkg = cls.getPackage();
            if (pkg != null) {
                XmlJavaTypeAdapters adapters = pkg.getAnnotation(XmlJavaTypeAdapters.class);
                if (adapters != null) {
                    CustomSerializerFactory serializerFactory = new CustomSerializerFactory();
                    BeanDeserializerFactory deserializerFactory = new BeanDeserializerFactory(null);
                    for (XmlJavaTypeAdapter a : adapters.value()) {
                        XmlAdapter xmlAdapter = null;
                        try {
                            xmlAdapter = a.value().newInstance();
                        } catch (Throwable e) {
                            // Ignore
                        }
                        if (xmlAdapter != null) {
                            XmlAdapterJsonDeserializer deserializer = new XmlAdapterJsonDeserializer(xmlAdapter);
                            XmlAdapterJsonSerializer serializer = new XmlAdapterJsonSerializer(xmlAdapter);
                            SimpleDeserializers deserializers = new SimpleDeserializers();
                            deserializers.addDeserializer(a.type(), deserializer);
                            deserializerFactory.withAdditionalDeserializers(deserializers);
                            serializerFactory.addGenericMapping(a.type(), serializer);
                            StdDeserializerProvider deserializerProvider =
                                new StdDeserializerProvider(deserializerFactory);
                            mapper = new ObjectMapper();
                            mapper.registerModule(new JsonOrgModule());

                            mapper.setSerializerFactory(serializerFactory);
                            mapper.setDeserializerProvider(deserializerProvider);
                        }
                    }
                }
            }
        }
        if (cls != null && mapper == null) {
            return MAPPER;
        }
        if (mapper == null) {
            mapper = new ObjectMapper();
            mapper.registerModule(new JsonOrgModule());
        }
        // Let's honor the Jackson annotations first
        AnnotationIntrospector primary = new JacksonAnnotationIntrospector() {

            @Override
            public Object findFilterId(AnnotatedClass annotatedClass) {
                Object filterId = super.findFilterId(annotatedClass);
                return filterId == null ? TUSCANY_FILTER : filterId;
            }

        };
        AnnotationIntrospector secondary = new JaxbAnnotationIntrospector();
        AnnotationIntrospector pair = new AnnotationIntrospector.Pair(primary, secondary);
        mapper.setDeserializationConfig(mapper.getDeserializationConfig().withAnnotationIntrospector(pair)
            .without(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES)
            .withDateFormat(StdDateFormat.getBlueprintISO8601Format()));
        mapper.setSerializationConfig(mapper.getSerializationConfig().withAnnotationIntrospector(pair)
            .withSerializationInclusion(JsonSerialize.Inclusion.NON_NULL)
            .withDateFormat(StdDateFormat.getBlueprintISO8601Format()));

        mapper.setFilters(new SimpleFilterProvider().addFilter(TUSCANY_FILTER, DEFAULT_FILTER));
        return mapper;
    }

    public static JsonFactory getJsonFactory() {
        return FACTORY;
    }

    public static String toString(JsonNode node) {
        try {
            return MAPPER.writeValueAsString(node);
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public static String toString(JsonParser parser) {
        try {
            JsonFactory jsonFactory = getJsonFactory();
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
        JsonFactory jsonFactory = getJsonFactory();
        try {
            return jsonFactory.createJsonParser(content);
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public static JsonParser createJsonParser(InputStream content) {
        JsonFactory jsonFactory = getJsonFactory();
        try {
            return jsonFactory.createJsonParser(content);
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public static JsonParser createJsonParser(Reader content) {
        JsonFactory jsonFactory = getJsonFactory();
        try {
            return jsonFactory.createJsonParser(content);
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public static void write(JsonNode node, OutputStream out) {
        try {
            JsonFactory jsonFactory = getJsonFactory();
            JsonGenerator generator = jsonFactory.createJsonGenerator(out, JsonEncoding.UTF8);
            generator.writeTree(node);
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public static void write(JsonParser parser, OutputStream out) {
        try {
            JsonFactory jsonFactory = getJsonFactory();
            JsonGenerator generator = jsonFactory.createJsonGenerator(out, JsonEncoding.UTF8);
            JsonNode node = parser.readValueAs(JsonNode.class);
            generator.writeTree(node);
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public static JSONObject read(InputStream is) throws IOException {
        try {
            return MAPPER.readValue(is, JSONObject.class);
        } catch (Exception e) {
            throw new IOException(e);
        }
    }

    /**
     * Read from String into a org.json.JSONObject
     * @param json
     * @return
     */
    public static JSONObject read(String json) {
        try {
            return MAPPER.readValue(json, JSONObject.class);
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }

    public static void write(JSONObject json, OutputStream out) throws IOException {
        try {
            MAPPER.writeValue(out, json);
        } catch (Exception e) {
            throw new IOException(e);
        }
    }

    public static String write(JSONObject json) throws IOException {
        try {
            return MAPPER.writeValueAsString(json);
        } catch (Exception e) {
            throw new IOException(e);
        }
    }

    public static FilterProvider configureFilterProvider(TransformationContext context) {
        SimpleBeanPropertyFilter filter = DEFAULT_FILTER;
        if (context != null) {
            Set<String> included = (Set<String>)context.getMetadata().get(INCLUDED_FIELDS);
            Set<String> excluded = (Set<String>)context.getMetadata().get(EXCLUDED_FIELDS);
            // Class<?> type = context.getSourceDataType() == null ? null : context.getSourceDataType().getPhysical();
            filter = new TuscanyBeanPropertyFilter(included, excluded);
        }
        FilterProvider filters = new SimpleFilterProvider().addFilter(TUSCANY_FILTER, filter);
        return filters;
    }

    private static class TuscanyBeanPropertyFilter extends SimpleBeanPropertyFilter {
        private Set<String> includedFields;
        private Set<String> excludedFields;

        private Stack<String> path = new Stack<String>();

        public TuscanyBeanPropertyFilter(Set<String> includedFields, Set<String> excludedFields) {
            if (includedFields == null) {
                includedFields = Collections.emptySet();
            }
            if (excludedFields == null) {
                excludedFields = Collections.emptySet();
            }
            this.includedFields = includedFields;
            this.excludedFields = excludedFields;
        }

        @Override
        public void serializeAsField(Object bean,
                                     JsonGenerator jgen,
                                     SerializerProvider provider,
                                     BeanPropertyWriter writer) throws Exception {
            path.push(writer.getName());
            try {
                // System.out.println(path);
                if (matches(path, includedFields, true)) {
                    writer.serializeAsField(bean, jgen, provider);
                } else if (includedFields.isEmpty() && !matches(path, excludedFields, false)) {
                    writer.serializeAsField(bean, jgen, provider);
                }
            } finally {
                path.pop();
            }
        }

        /**
         * Check the target string is a prefix of the source separated by .
         * @param source
         * @param target
         * @return
         */
        private boolean isPrefix(String source, String target) {
            int index = source.indexOf(target);
            if (index == -1) {
                return false;
            }
            if (target.length() == source.length() || source.charAt(target.length()) == '.') {
                return true;
            }
            return false;
        }

        /**
         * Check if the path matches the one of the patterns
         * @param path
         * @param patterns
         * @param included
         * @return
         */
        private boolean matches(Stack<String> path, Set<String> patterns, boolean included) {
            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < path.size(); i++) {
                builder.append(path.get(i));
                if (i != path.size() - 1) {
                    builder.append(".");
                }
            }
            String qname = builder.toString();
            for (String p : patterns) {
                if ((included && isPrefix(p, qname)) || ((!included) && isPrefix(qname, p))) {
                    return true;
                }
            }
            return false;
        }

    }

}
