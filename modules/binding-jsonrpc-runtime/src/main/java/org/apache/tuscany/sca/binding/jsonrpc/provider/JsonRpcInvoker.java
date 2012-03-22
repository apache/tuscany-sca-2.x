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
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.List;
import java.util.UUID;

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
import org.apache.tuscany.sca.binding.jsonrpc.JSONRPCBinding;
import org.apache.tuscany.sca.binding.jsonrpc.protocol.JsonRpc10Request;
import org.apache.tuscany.sca.binding.jsonrpc.protocol.JsonRpc20Request;
import org.apache.tuscany.sca.binding.jsonrpc.protocol.JsonRpcRequest;
import org.apache.tuscany.sca.databinding.json.JSONDataBinding;
import org.apache.tuscany.sca.databinding.json.jackson.JacksonHelper;
import org.apache.tuscany.sca.interfacedef.DataType;
import org.apache.tuscany.sca.interfacedef.Operation;
import org.apache.tuscany.sca.interfacedef.java.JavaOperation;
import org.apache.tuscany.sca.invocation.DataExchangeSemantics;
import org.apache.tuscany.sca.invocation.Invoker;
import org.apache.tuscany.sca.invocation.Message;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.type.TypeFactory;
import org.codehaus.jackson.node.NullNode;
import org.codehaus.jackson.node.ObjectNode;
import org.oasisopen.sca.ServiceRuntimeException;

/**
 * Invoker for the JSONRPC Binding
 * 
 * @version $Rev$ $Date$
 */
public class JsonRpcInvoker implements Invoker, DataExchangeSemantics {
    private EndpointReference endpointReference;
    private Operation operation;
    private String uri;
    private HttpClient httpClient;

    public JsonRpcInvoker(EndpointReference endpointReference, Operation operation, HttpClient httpClient) {
        this.endpointReference = endpointReference;
        this.operation = operation;
        this.uri = endpointReference.getDeployedURI();
        this.httpClient = httpClient;
    }

    public Message invoke(Message msg) {
        HttpPost post = null;
        HttpResponse response = null;
        try {
            String requestId = UUID.randomUUID().toString();
            post = new HttpPost(uri);
            HttpEntity entity = null;
            Object[] args = msg.getBody();
            final String db = msg.getOperation().getInputWrapper().getDataBinding();

            if (!db.equals(JSONDataBinding.NAME)) {
                Object[] params = new Object[0];
                // Extract the arguments
                args = msg.getBody();

                if (args instanceof Object[]) {
                    params = (Object[])args;
                }

                JsonRpcRequest req = null;
                if (JSONRPCBinding.VERSION_20.equals(((JSONRPCBinding)endpointReference.getBinding()).getVersion())) {
                    req = new JsonRpc20Request(requestId, msg.getOperation().getName(), params);
                } else {
                    req = new JsonRpc10Request(requestId, msg.getOperation().getName(), params);
                }
                final JsonRpcRequest json = req;

                // Create content producer so that we can stream the json result out
                ContentProducer cp = new ContentProducer() {
                    public void writeTo(OutputStream outstream) throws IOException {
                        // mapper.writeValue(outstream, req.toJSONObject().toString());
                        try {
                            json.write(outstream);
                        } catch (Exception e) {
                            throw new IOException(e);
                        }
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

                entity = response.getEntity();
                String entityResponse = EntityUtils.toString(entity);
                entity.consumeContent();
                if (!db.equals(JSONDataBinding.NAME)) {
                    ObjectNode jsonResponse = (ObjectNode)JacksonHelper.MAPPER.readTree(entityResponse);

                    if (jsonResponse.has("error") && jsonResponse.get("error") != NullNode.instance) {
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
                    if (!requestId.equalsIgnoreCase(jsonResponse.get("id").getTextValue())) {
                        throw new ServiceRuntimeException("Invalid response id:" + requestId);
                    }

                    JsonNode rawResult = jsonResponse.get("result");

                    Class<?> returnClass = returnType.getPhysical();
                    Type genericReturnType = returnType.getGenericType();

                    ObjectMapper mapper = createObjectMapper(returnClass);
                    String json = mapper.writeValueAsString(rawResult);

                    Object body = mapper.readValue(json, TypeFactory.type(genericReturnType));

                    msg.setBody(body);
                } else {
                    msg.setBody(entityResponse);
                }

            } else {
                // Consume the content so the connection can be released
                response.getEntity().consumeContent();
                throw new ServiceRuntimeException("Abnormal HTTP response: " + response.getStatusLine().toString());
            }
        } catch (RuntimeException e) {
            throw e;
        } catch (Error e) {
            throw e;
        } catch (Exception e) {
            // e.printStackTrace();
            msg.setFaultBody(e);
        } catch (Throwable e) {
            throw new ServiceRuntimeException(e);
        }

        return msg;
    }

    public static ObjectMapper createObjectMapper(Class<?> cls) {
        return JacksonHelper.createObjectMapper(cls);
    }

    private String opt(ObjectNode node, String name) {
        JsonNode value = node.get(name);
        if (value == null) {
            return null;
        } else {
            return value.getValueAsText();
        }
    }

    /**
     * Generate and throw exception based on the data in the 'responseMessage'
     */
    protected void processException(ObjectNode responseMessage) throws Throwable {
        // FIXME: We need to find a way to build Java exceptions out of the json-rpc error
        JsonNode error = responseMessage.get("error");
        if (error != null) {
            Object data = error.get("data");
            if (data instanceof ObjectNode) {
                ObjectNode fault = (ObjectNode)data;
                String javaClass = opt(fault, "class");
                String message = opt(fault, "message");
                String stackTrace = opt(fault, "stackTrace");
                if (javaClass != null) {
                    if (stackTrace != null) {
                        message = message + "\n" + stackTrace;
                    }
                    ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
                    if (operation instanceof JavaOperation) {
                        Method method = ((JavaOperation)operation).getJavaMethod();
                        classLoader = method.getDeclaringClass().getClassLoader();
                    }
                    Class<? extends Throwable> exceptionClass =
                        (Class<? extends Throwable>)Class.forName(javaClass, false, classLoader);
                    Constructor<? extends Throwable> ctor = null;
                    Throwable ex = null;
                    try {
                        ctor = exceptionClass.getConstructor(String.class, Throwable.class);
                        ex = ctor.newInstance(message, null);
                    } catch (NoSuchMethodException e1) {
                        try {
                            ctor = exceptionClass.getConstructor(String.class);
                            ex = ctor.newInstance(message);
                        } catch (NoSuchMethodException e2) {
                            try {
                                ctor = exceptionClass.getConstructor(Throwable.class);
                                ex = ctor.newInstance(null);
                            } catch (NoSuchMethodException e3) {
                                ctor = exceptionClass.getConstructor();
                                ex = ctor.newInstance();
                            }
                        }
                    }
                    throw ex;
                }
            }
            throw new ServiceRuntimeException(error.toString());
        }
    }

    @Override
    public boolean allowsPassByReference() {
        return true;
    }

}
