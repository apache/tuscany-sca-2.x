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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringWriter;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import javax.activation.DataSource;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.Provider;
import javax.xml.namespace.QName;

import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.core.UtilityExtensionPoint;
import org.apache.tuscany.sca.databinding.DataBindingExtensionPoint;
import org.apache.tuscany.sca.databinding.Mediator;
import org.apache.tuscany.sca.interfacedef.DataType;
import org.apache.tuscany.sca.interfacedef.impl.DataTypeImpl;
import org.apache.tuscany.sca.interfacedef.util.XMLType;

/**
 * A JAX-RS provider that leverages Tuscany's databinding framework to handle read/write
 * for JAX-RS runtime
 */
@Provider
public abstract class DataBindingJAXRSProvider {
    protected DataBindingExtensionPoint dataBindingExtensionPoint;
    protected Mediator mediator;

    public DataBindingJAXRSProvider(ExtensionPointRegistry registry) {
        this.dataBindingExtensionPoint = registry.getExtensionPoint(DataBindingExtensionPoint.class);
        UtilityExtensionPoint utilities = registry.getExtensionPoint(UtilityExtensionPoint.class);
        this.mediator = utilities.getUtility(Mediator.class);
    }

    protected <A extends Annotation> A getAnnotation(Annotation[] annotations, Class<A> type) {
        for (Annotation a : annotations) {
            if (a.annotationType() == type) {
                return type.cast(a);
            }
        }
        return null;
    }

    protected void introspectAnnotations(Annotation[] annotations, DataType targetDataType) {
        WebResult result = getAnnotation(annotations, WebResult.class);
        if (result != null) {
            QName name = new QName(result.targetNamespace(), result.name());
            targetDataType.setLogical(new XMLType(name, null));
        }

        WebParam param = getAnnotation(annotations, WebParam.class);
        if (param != null) {
            QName name = new QName(param.targetNamespace(), param.name());
            targetDataType.setLogical(new XMLType(name, null));
        }
    }

    protected DataType createDataType(Class<?> type, Type genericType) {
        DataType dataType = new DataTypeImpl(null, type, type, genericType);
        dataBindingExtensionPoint.introspectType(dataType, null);
        return dataType;
    }

    protected boolean supports(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return MediaType.APPLICATION_JSON_TYPE.equals(mediaType) || MediaType.APPLICATION_XML_TYPE.equals(mediaType)
        || MediaType.TEXT_XML_TYPE.equals(mediaType);
    }

    protected Object convert(InputStream content, String contentType, Class<?> type) throws IOException {
        if (type == DataSource.class) {
            return type.cast(new InputStreamDataSource(content, contentType));
        } else if (type == InputStream.class) {
            return type.cast(content);
        } else if (type == Reader.class) {
            return type.cast(new InputStreamReader(content, "UTF-8"));
        } else if (type == String.class) {
            try {
                StringWriter sw = new StringWriter();
                InputStreamReader reader = new InputStreamReader(content, "UTF-8");
                char[] buf = new char[8192];
                while (true) {
                    int size = reader.read(buf);
                    if (size < 0) {
                        break;
                    }
                    sw.write(buf, 0, size);
                }
                return type.cast(sw.toString());
            } catch (Exception e) {
                throw new IllegalArgumentException(e);
            }
        } else if (type == byte[].class) {
            try {
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                byte[] buf = new byte[8192];
                while (true) {
                    int size = content.read(buf);
                    if (size < 0) {
                        break;
                    }
                    bos.write(buf, 0, size);
                }
                return type.cast(bos.toByteArray());
            } catch (Exception e) {
                throw new IllegalArgumentException(e);
            }
        } else {
            return content;
        }
    }

    protected void write(OutputStream out, Object content, Class<?> type) throws IOException {
        if (content == null) {
            return;
        }
        InputStream in = null;
        if (DataSource.class.isAssignableFrom(type)) {
            in = ((DataSource)content).getInputStream();
        } else if (InputStream.class.isAssignableFrom(type)) {
            in = (InputStream)content;
        } else if (type == String.class) {
            in = new ByteArrayInputStream(((String)content).getBytes("UTF-8"));
        } else if (type == byte[].class) {
            in = new ByteArrayInputStream((byte[])content);
        }
        if (in == null) {
            throw new IllegalArgumentException("Type is not supported: " + type);
        }
        byte[] buf = new byte[8192];
        while (true) {
            int len = in.read(buf);
            if (len < 0) {
                in.close();
                break;
            }
            out.write(buf, 0, len);
        }
    }

    public static final class InputStreamDataSource implements DataSource {

        public static final String DEFAULT_TYPE = "application/octet-stream";

        private final InputStream in;
        private final String ctype;

        public InputStreamDataSource(InputStream in) {
            this(in, null);
        }

        public InputStreamDataSource(InputStream in, String ctype) {
            this.in = in;
            this.ctype = (ctype != null) ? ctype : DEFAULT_TYPE;
        }

        public String getContentType() {
            return ctype;
        }

        public String getName() {
            return null;
        }

        public InputStream getInputStream() throws IOException {
            return in;
        }

        public OutputStream getOutputStream() throws IOException {
            return null;
        }

    }
}
