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

package org.apache.tuscany.databinding.jaxb;

import java.beans.Introspector;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchema;
import javax.xml.bind.annotation.XmlType;
import javax.xml.namespace.QName;

import org.apache.tuscany.spi.databinding.extension.DataBindingExtension;
import org.apache.tuscany.spi.model.DataType;

/**
 * JAXB DataBinding
 */
public class JAXBDataBinding extends DataBindingExtension {
    
    public static final String NAME = JAXBElement.class.getName();

    @Override
    public DataType introspect(Class<?> javaType) {
        if (JAXBElement.class.isAssignableFrom(javaType)) {
            Type type = javaType.getGenericSuperclass();
            if (type instanceof ParameterizedType) {
                ParameterizedType parameterizedType = ((ParameterizedType) type);
                Type rawType = parameterizedType.getRawType();
                if (rawType == JAXBElement.class) {
                    Type actualType = parameterizedType.getActualTypeArguments()[0];
                    if (actualType instanceof Class) {
                        return introspect((Class<?>) actualType);
                    }
                }
            }
            return new DataType<QName>(getName(), javaType, null);
        }

        String namespace = null;
        String name = null;
        Package pkg = javaType.getPackage();
        if (pkg != null) {
            XmlSchema schema = pkg.getAnnotation(XmlSchema.class);
            namespace = schema.namespace();
        }
        XmlType type = javaType.getAnnotation(XmlType.class);
        if (type != null) {
            String typeNamespace = type.namespace();
            String typeName = type.name();

            if (typeNamespace.equals("##default") && typeName.equals("")) {
                XmlRootElement rootElement = javaType.getAnnotation(XmlRootElement.class);
                if (rootElement != null) {
                    namespace = rootElement.namespace();
                } else {
                    // FIXME: The namespace should be from the referencing property
                    namespace = null;
                }
            } else if (typeNamespace.equals("##default")) {
                // namespace is from the package
            } else {
                namespace = typeNamespace;
            }

            if (typeName.equals("##default")) {
                name = Introspector.decapitalize(javaType.getSimpleName());
            } else {
                name = typeName;
            }
        } else {
            XmlEnum xmlEnum = javaType.getAnnotation(XmlEnum.class);
            if (xmlEnum != null) {
                name = Introspector.decapitalize(javaType.getSimpleName());
            }
        }
        if (namespace == null && name == null) {
            return null;
        }
        QName xmlType = new QName(namespace, name);
        DataType<QName> dataType = new DataType<QName>(getName(), javaType, xmlType);
        return dataType;
    }

    public JAXBDataBinding() {
        super(NAME, JAXBElement.class);
    }

}
