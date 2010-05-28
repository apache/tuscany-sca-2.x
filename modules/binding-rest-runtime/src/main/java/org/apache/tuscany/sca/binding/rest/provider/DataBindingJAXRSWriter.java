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

import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Collections;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;

import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.interfacedef.DataType;
import org.apache.tuscany.sca.interfacedef.impl.DataTypeImpl;

/**
 * The generic JAX-RS message body writer based on Tuscany's databindingframework
 */
@Provider
public class DataBindingJAXRSWriter<T> extends DataBindingJAXRSProvider implements MessageBodyWriter<T> {

    public DataBindingJAXRSWriter(ExtensionPointRegistry registry) {
        super(registry);
    }

    public long getSize(T t, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return -1;
    }

    public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        //        DataType dataType = createDataType(type, genericType);
        return supports(type, genericType, annotations, mediaType);
    }

    public void writeTo(T t,
                        Class<?> type,
                        Type genericType,
                        Annotation[] annotations,
                        MediaType mediaType,
                        MultivaluedMap<String, Object> httpHeaders,
                        OutputStream entityStream) throws IOException, WebApplicationException {
        DataType dataType = createDataType(type, genericType);
        mediaType = new MediaType(mediaType.getType(), mediaType.getSubtype());
        String dataBinding = OutputStream.class.getName();
        // FIXME: [rfeng] This is a hack to handle application/json
        if (MediaType.APPLICATION_JSON_TYPE.equals(mediaType)) {
            dataBinding = mediaType.toString() + "#" + OutputStream.class.getName();
        } else if (MediaType.APPLICATION_XML_TYPE.equals(mediaType) || MediaType.TEXT_XML_TYPE.equals(mediaType)) {
            dataBinding = OutputStream.class.getName();
        } else {
            dataBinding = dataType.getDataBinding();
            write(entityStream, t, type);
            return;
        }
        DataType targetDataType =
            new DataTypeImpl(dataBinding, OutputStream.class, OutputStream.class, OutputStream.class);
        // dataBindingExtensionPoint.introspectType(targetDataType, null);

        introspectAnnotations(annotations, targetDataType);

        mediator.mediate(t, entityStream, dataType, targetDataType, Collections.<String, Object> emptyMap());
    }

}
