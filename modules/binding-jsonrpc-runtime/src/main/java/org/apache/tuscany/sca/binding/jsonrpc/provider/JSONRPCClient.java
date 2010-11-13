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
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Iterator;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapters;

import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonParser;
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
import org.jabsorb.JSONRPCBridge;
import org.jabsorb.JSONRPCResult;
import org.jabsorb.JSONSerializer;
import org.jabsorb.client.ClientError;
import org.jabsorb.client.ErrorResponse;
import org.jabsorb.client.Session;
import org.jabsorb.serializer.FixUp;
import org.jabsorb.serializer.SerializerState;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class JSONRPCClient implements InvocationHandler {
    // private static Logger log = LoggerFactory.getLogger(JsonrpcClient.class);

    private Session session;
    private JSONSerializer serializer;

    /**
     * Maintain a unique id for each message
     */
    private int id = 0;

    /**
     * Allow access to the serializer
     * 
     * @return The serializer for this class
     */
    public JSONSerializer getSerializer() {
        return serializer;
    }

    /**
     * Create a client given a session
     * 
     * @param session --
     *          transport session to use for this connection
     */
    public JSONRPCClient(Session session) {
        try {
            this.session = session;
            serializer = new JSONSerializer();
            serializer.registerDefaultSerializers();
            serializer.setMarshallClassHints(false);
            serializer.setMarshallNullAttributes(false);
        } catch (Exception e) {
            throw new ClientError(e);
        }
    }

    private synchronized int getId() {
        return id++;
    }

    /** Manual instantiation of HashMap<String, Object> */
    private static class ProxyMap extends HashMap<Object, Object> {
        public String getString(Object key) {
            return (String)super.get(key);
        }

        public Object putString(String key, Object value) {
            return super.put(key, value);
        }
    }

    private ProxyMap proxyMap = new ProxyMap();

    /**
     * Create a proxy for communicating with the remote service.
     * 
     * @param key
     *          the remote object key
     * @param klass
     *          the class of the interface the remote object should adhere to
     * @return created proxy
     */
    public Object openProxy(String key, Class klass) {
        Object result = java.lang.reflect.Proxy.newProxyInstance(klass.getClassLoader(), new Class[] {klass}, this);
        proxyMap.put(result, key);
        return result;
    }

    /**
     * Dispose of the proxy that is no longer needed
     * 
     * @param proxy
     */
    public void closeProxy(Object proxy) {
        proxyMap.remove(proxy);
    }

    /**
     * This method is public because of the inheritance from the
     * InvokationHandler -- should never be called directly.
     */
    public Object invoke(Object proxyObj, Method method, Object[] args) throws Exception {
        String methodName = method.getName();
        if (methodName.equals("hashCode")) {
            return new Integer(System.identityHashCode(proxyObj));
        } else if (methodName.equals("equals")) {
            return (proxyObj == args[0] ? Boolean.TRUE : Boolean.FALSE);
        } else if (methodName.equals("toString")) {
            return proxyObj.getClass().getName() + '@' + Integer.toHexString(proxyObj.hashCode());
        }
        return invoke(proxyMap.getString(proxyObj),
                      method.getName(),
                      args,
                      method.getReturnType(),
                      method.getGenericReturnType());
    }

    private Object invoke(String objectTag, String methodName, Object[] args, Class returnType, Type genericReturnType)
        throws Exception {
        final int id = getId();
        JSONObject message = new JSONObject();
        String methodTag = objectTag == null ? "" : objectTag + ".";
        methodTag += methodName;
        message.put("method", methodTag);

        {
            SerializerState state = new SerializerState();

            if (args != null) {

                JSONArray params = marshal(args); // (JSONArray)serializer.marshall(state, /* parent */ null, args, "params");

                if ((state.getFixUps() != null) && (state.getFixUps().size() > 0)) {
                    JSONArray fixups = new JSONArray();
                    for (Iterator i = state.getFixUps().iterator(); i.hasNext();) {
                        FixUp fixup = (FixUp)i.next();
                        fixups.put(fixup.toJSONArray());
                    }
                    message.put("fixups", fixups);
                }
                message.put("params", params);
            } else {
                message.put("params", new JSONArray());
            }
        }
        message.put("id", id);

        JSONObject responseMessage = session.sendAndReceive(message);

        if (!responseMessage.has("result")) {
            processException(responseMessage);
        }
        Object rawResult = responseMessage.get("result");
        if (rawResult == null) {
            processException(responseMessage);
        }
        if (returnType.equals(Void.TYPE)) {
            return null;
        }

        {
            JSONArray fixups = responseMessage.optJSONArray("fixups");

            if (fixups != null) {
                for (int i = 0; i < fixups.length(); i++) {
                    JSONArray assignment = fixups.getJSONArray(i);
                    JSONArray fixup = assignment.getJSONArray(0);
                    JSONArray original = assignment.getJSONArray(1);
                    JSONRPCBridge.applyFixup(rawResult, fixup, original);
                }
            }
        }
        if (returnType.isInterface()) {
            ObjectMapper mapper = createObjectMapper(returnType);
            return mapper.readValue(rawResult.toString(), TypeFactory.type(genericReturnType));
        }
        return serializer.unmarshall(new SerializerState(), returnType, rawResult);
    }

    private JSONArray marshal(Object[] args) throws Exception {
        if(args==null) {
            return new JSONArray();
        }
        ObjectMapper mapper = createObjectMapper(null);
        String json = mapper.writeValueAsString(args);
        return new JSONArray(json);
    }

    public static JsonParser createJsonParser(String content) {
        JsonFactory jsonFactory = new JsonFactory();
        try {
            return jsonFactory.createJsonParser(content);
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
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
            Integer code = new Integer(error.has("code") ? error.getInt("code") : 0);
            String trace = error.has("trace") ? error.getString("trace") : null;
            String msg = error.has("msg") ? error.getString("msg") : null;
            throw new ErrorResponse(code, msg, trace);
        } else
            throw new ErrorResponse(new Integer(JSONRPCResult.CODE_ERR_PARSE),
                                    "Unknown response:" + responseMessage.toString(2), null);
    }

}
