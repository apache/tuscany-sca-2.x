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
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Collections;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.Provider;

import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.interfacedef.DataType;
import org.apache.tuscany.sca.interfacedef.impl.DataTypeImpl;

/**
 * The generic JAX-RS message body reader based on Tuscany's databindingframework
 */
@Provider
public class DataBindingJAXRSReader<T> extends DataBindingJAXRSProvider implements MessageBodyReader<T> {

    public DataBindingJAXRSReader(ExtensionPointRegistry registry) {
        super(registry);
    }

    public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
//        DataType dataType = createDataType(type, genericType);
        return supports(type, genericType, annotations, mediaType);
    }

    public T readFrom(Class<T> type,
                      Type genericType,
                      Annotation[] annotations,
                      MediaType mediaType,
                      MultivaluedMap<String, String> httpHeaders,
                      InputStream entityStream) throws IOException, WebApplicationException {
        
        Object source = entityStream;
        DataType targetDataType = createDataType(type, genericType);
    
        String dataBinding = null;
        
        mediaType = new MediaType(mediaType.getType(), mediaType.getSubtype());
        // FIXME: [rfeng] This is a hack to handle application/json
        if (MediaType.APPLICATION_JSON_TYPE.equals(mediaType)) {
            dataBinding = mediaType.toString() + "#" + InputStream.class.getName();
        } else if (MediaType.APPLICATION_XML_TYPE.equals(mediaType) || MediaType.TEXT_XML_TYPE.equals(mediaType)) {
            dataBinding = InputStream.class.getName();
        } else {
            dataBinding = targetDataType.getDataBinding();
            source = convert(entityStream, mediaType.toString(), type);
            return (T) source;
        }
        DataType sourceDataType =
            new DataTypeImpl(dataBinding, InputStream.class, InputStream.class, InputStream.class);

        Object result = mediator.mediate(source, sourceDataType, targetDataType, Collections.<String, Object>emptyMap());
        return (T)result;
    }
    


}
