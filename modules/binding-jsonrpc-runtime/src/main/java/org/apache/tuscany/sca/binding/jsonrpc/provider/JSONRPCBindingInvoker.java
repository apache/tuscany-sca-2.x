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

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapters;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentProducer;
import org.apache.http.entity.EntityTemplate;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;
import org.apache.tuscany.sca.assembly.EndpointReference;
import org.apache.tuscany.sca.databinding.json.JSONDataBinding;
import org.apache.tuscany.sca.interfacedef.DataType;
import org.apache.tuscany.sca.interfacedef.Operation;
import org.apache.tuscany.sca.invocation.DataExchangeSemantics;
import org.apache.tuscany.sca.invocation.Invoker;
import org.apache.tuscany.sca.invocation.Message;
import org.codehaus.jackson.map.AnnotationIntrospector;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.deser.CustomDeserializerFactory;
import org.codehaus.jackson.map.deser.StdDeserializerProvider;
import org.codehaus.jackson.map.introspect.JacksonAnnotationIntrospector;
import org.codehaus.jackson.map.ser.CustomSerializerFactory;
import org.codehaus.jackson.map.type.TypeFactory;
import org.codehaus.jackson.xc.JaxbAnnotationIntrospector;
import org.codehaus.jackson.xc.XmlAdapterJsonDeserializer;
import org.codehaus.jackson.xc.XmlAdapterJsonSerializer;
import org.json.JSONException;
import org.json.JSONObject;
import org.oasisopen.sca.ServiceRuntimeException;

/**
 * Invoker for the JSONRPC Binding
 * 
 * @version $Rev$ $Date$
 */
public class JSONRPCBindingInvoker implements Invoker, DataExchangeSemantics {
    private EndpointReference endpointReference;
    private Operation operation;
    private String uri;
    private ObjectMapper mapper;
    private HttpClient httpClient;

    public JSONRPCBindingInvoker(EndpointReference endpointReference, Operation operation, HttpClient httpClient) {
        this.endpointReference = endpointReference;
        this.operation = operation;
        this.uri = endpointReference.getBinding().getURI();
        this.mapper = createObjectMapper(null);
        this.httpClient = httpClient;
    }

    public Message invoke(Message msg) {
        HttpPost post = null;
        HttpResponse response = null;
        try {
            String requestId = "1";
            post = new HttpPost(uri);
            HttpEntity entity = null;
            Object[] args = msg.getBody();
            final String db = msg.getOperation().getWrapper().getDataBinding();

            if (!db.equals(JSONDataBinding.NAME)) {

                // Construct a map to hold JSON-RPC request
                final Map<String, Object> jsonRequest = new HashMap<String, Object>();
                jsonRequest.put("method", "Service" + "." + msg.getOperation().getName());

                List<Object> params = null;
                // Extract the arguments
                args = msg.getBody();

                if (args != null) {
                    params = Arrays.asList(args);
                } else {
                    params = Collections.emptyList();
                }

                jsonRequest.put("params", params);
                jsonRequest.put("id", requestId);

                // Create content producer so that we can stream the json result out
                ContentProducer cp = new ContentProducer() {
                    public void writeTo(OutputStream outstream) throws IOException {
                        mapper.writeValue(outstream, jsonRequest);
                    }
                };
                entity = new EntityTemplate(cp);
            } else {
                // Single string argument
                entity = new StringEntity((String)args[0], "UTF-8");
            }

            post.setEntity(entity);

            response = httpClient.execute(post);

            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                //success 
                try {
                    entity = response.getEntity();
                    String entityResponse = EntityUtils.toString(entity);
                    entity.consumeContent();
                    if (!db.equals(JSONDataBinding.NAME)) {
                        JSONObject jsonResponse = new JSONObject(entityResponse);

                        if (!jsonResponse.has("result")) {
                            processException(jsonResponse);
                        }
                        DataType<List<DataType>> outputType = operation.getOutputType();
                        DataType returnType =
                            (outputType != null && !outputType.getLogical().isEmpty()) ? outputType.getLogical().get(0)
                                : null;

                        if (returnType == null) {
                            msg.setBody(null);
                            return msg;
                        }

                        //check requestId
                        if (!requestId.equalsIgnoreCase(jsonResponse.optString("id"))) {
                            throw new ServiceRuntimeException("Invalid response id:" + requestId);
                        }

                        Object rawResult = jsonResponse.get("result");
                        if (rawResult == null) {
                            processException(jsonResponse);
                        }

                        Class<?> returnClass = returnType.getPhysical();
                        Type genericReturnType = returnType.getGenericType();

                        ObjectMapper mapper = createObjectMapper(returnClass);
                        String json = rawResult.toString();

                        // Jackson requires the quoted String so that readValue can work
                        if (returnClass == String.class) {
                            json = "\"" + json + "\"";
                        }
                        Object body = mapper.readValue(json, TypeFactory.type(genericReturnType));

                        msg.setBody(body);
                    } else {
                        msg.setBody(entityResponse);
                    }

                } catch (Exception e) {
                    //FIXME Exceptions are not handled correctly here
                    // They should be reported to the client JavaScript as proper
                    // JavaScript exceptions.
                    throw new ServiceRuntimeException("Unable to parse response", e);
                }
            }
        } catch (Exception e) {
            // e.printStackTrace();
            msg.setFaultBody(e);
        }

        return msg;
    }

    public static ObjectMapper createObjectMapper(Class<?> cls) {
        ObjectMapper mapper = new ObjectMapper();
        if (cls != null) {
            // Workaround for http://jira.codehaus.org/browse/JACKSON-413
            Package pkg = cls.getPackage();
            if (pkg != null) {
                XmlJavaTypeAdapters adapters = pkg.getAnnotation(XmlJavaTypeAdapters.class);
                if (adapters != null) {
                    CustomSerializerFactory serializerFactory = new CustomSerializerFactory();
                    CustomDeserializerFactory deserializerFactory = new CustomDeserializerFactory();
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
                            deserializerFactory.addSpecificMapping(a.type(), deserializer);
                            serializerFactory.addGenericMapping(a.type(), serializer);
                            StdDeserializerProvider deserializerProvider =
                                new StdDeserializerProvider(deserializerFactory);
                            mapper.setSerializerFactory(serializerFactory);
                            mapper.setDeserializerProvider(deserializerProvider);
                        }
                    }
                }
            }
        }
        AnnotationIntrospector primary = new JaxbAnnotationIntrospector();
        AnnotationIntrospector secondary = new JacksonAnnotationIntrospector();
        AnnotationIntrospector pair = new AnnotationIntrospector.Pair(primary, secondary);
        mapper.getDeserializationConfig().setAnnotationIntrospector(pair);
        // [rfeng] To avoid complaints about javaClass
        mapper.getDeserializationConfig().set(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, Boolean.FALSE);
        mapper.getSerializationConfig().setAnnotationIntrospector(pair);
        return mapper;
    }

    /**
     * Generate and throw exception based on the data in the 'responseMessage'
     */
    protected void processException(JSONObject responseMessage) throws JSONException {
        JSONObject error = (JSONObject)responseMessage.get("error");
        if (error != null) {
            throw new ServiceRuntimeException(error.toString());
        } else {
            throw new ServiceRuntimeException(responseMessage.toString());
        }
    }

    @Override
    public boolean allowsPassByReference() {
        return true;
    }

}
