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

package org.apache.tuscany.sca.binding.rest.provider;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HEAD;
import javax.ws.rs.HttpMethod;
import javax.ws.rs.OPTIONS;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;

import org.apache.tuscany.sca.binding.rest.RESTBinding;
import org.apache.tuscany.sca.interfacedef.Operation;
import org.apache.tuscany.sca.invocation.Invoker;
import org.apache.tuscany.sca.invocation.Message;
import org.apache.wink.client.ClientConfig;
import org.apache.wink.client.EntityType;
import org.apache.wink.client.Resource;
import org.apache.wink.client.RestClient;

/**
 * 
 */
public class RESTBindingInvoker implements Invoker {
    private RESTBinding binding;
    private Operation operation;

    public RESTBindingInvoker(RESTBinding binding, Operation operation) {
        super();
        this.binding = binding;
        this.operation = operation;
    }

    private static Map<Class<?>, String> mapping = new HashMap<Class<?>, String>();
    static {
        mapping.put(GET.class, HttpMethod.GET);
        mapping.put(POST.class, HttpMethod.POST);
        mapping.put(PUT.class, HttpMethod.PUT);
        mapping.put(DELETE.class, HttpMethod.DELETE);
        mapping.put(HEAD.class, HttpMethod.HEAD);
        mapping.put(OPTIONS.class, HttpMethod.OPTIONS);
    }

    public Message invoke(Message msg) {
        ClientConfig config = new ClientConfig();
        RestClient client = new RestClient();
        Resource resource = client.resource(binding.getURI());
        String method = null;
        for (Map.Entry<Class<?>, String> e : mapping.entrySet()) {
            if (operation.getAttributes().get(e.getKey()) != null) {
                method = e.getValue();
                break;
            }
        }
        EntityType<?> entityType = new EntityType() {

            @Override
            public Class getRawClass() {
                if (operation.getOutputType() != null) {
                    return operation.getOutputType().getPhysical();
                } else {
                    return null;
                }
            }

            @Override
            public Type getType() {
                if (operation.getOutputType() != null) {
                    return operation.getOutputType().getGenericType();
                } else {
                    return null;
                }
            }

        };
        Object result = resource.invoke(method, entityType, msg.getBody());
        msg.setBody(result);
        return msg;
    }

}
