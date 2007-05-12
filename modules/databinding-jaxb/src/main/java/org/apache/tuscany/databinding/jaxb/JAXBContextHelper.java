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

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchema;
import javax.xml.bind.annotation.XmlType;
import javax.xml.namespace.QName;

import org.apache.tuscany.databinding.TransformationContext;
import org.apache.tuscany.databinding.TransformationException;
import org.apache.tuscany.sca.interfacedef.DataType;
import org.apache.tuscany.sca.interfacedef.util.XMLType;

public class JAXBContextHelper {
    // TODO: Do we need to set them for source and target?
    public static final String JAXB_CLASSES = "jaxb.classes";

    public static final String JAXB_CONTEXT_PATH = "jaxb.contextPath";

    private JAXBContextHelper() {
    }

    public static JAXBContext createJAXBContext(TransformationContext tContext, boolean source) throws JAXBException {
        if (tContext == null)
            throw new TransformationException("JAXB context is not set for the transformation.");

        DataType<?> bindingContext = source ? tContext.getSourceDataType() : tContext.getTargetDataType();
        // FIXME: We should check the context path or classes
        // FIXME: What should we do if JAXB is an intermediate node?

        String contextPath = null;
        JAXBContext context = null;
        Class cls = bindingContext.getPhysical();
        if (cls.getPackage() != null) {
            contextPath = cls.getPackage().getName();
            context = JAXBContext.newInstance(contextPath);
        }
        if (context == null) {
            throw new TransformationException("JAXB context is not set for the transformation.");
        }
        return context;
    }

    @SuppressWarnings("unchecked")
    public static JAXBElement createJAXBElement(DataType dataType, Object value) {
        if (value instanceof JAXBElement) {
            return (JAXBElement)value;
        } else {
            Class type = (Class)dataType.getPhysical();
            Object logical = dataType.getLogical();
            QName elementName = JAXBDataBinding.ROOT_ELEMENT;
            if (logical instanceof XMLType) {
                XMLType xmlType = (XMLType)logical;
                QName element = xmlType.getElementName();
                if (element != null) {
                    elementName = element;
                } else {
                    /**
                     * Set the declared type to Object.class so that xsi:type
                     * will be produced
                     */
                    type = Object.class;
                }
            } else {
                type = Object.class;
            }
            return new JAXBElement(elementName, type, value);
        }
    }

    @SuppressWarnings("unchecked")
    public static Object createReturnValue(DataType dataType, Object value) {
        Class<?> cls = getJavaType(dataType);
        if (cls == JAXBElement.class) {
            return createJAXBElement(dataType, value);
        } else {
            if (value instanceof JAXBElement) {
                return ((JAXBElement)value).getValue();
            } else {
                return value;
            }
        }
    }

    public static Class<?> getJavaType(DataType<?> dataType) {
        if (dataType == null) {
            return null;
        }
        Type type = dataType.getPhysical();
        if (type instanceof Class) {
            Class cls = (Class)type;
            if (JAXBElement.class.isAssignableFrom(cls)) {
                return null;
            } else {
                return cls;
            }
        } else if (type instanceof ParameterizedType) {
            ParameterizedType pType = (ParameterizedType)type;
            return (Class)pType.getRawType();
        }
        return null;
    }

    public static XMLType getXmlTypeName(Class<?> javaType) {
        String namespace = null;
        String name = null;
        Package pkg = javaType.getPackage();
        if (pkg != null) {
            XmlSchema schema = pkg.getAnnotation(XmlSchema.class);
            if (schema != null) {
                namespace = schema.namespace();
            }
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
                    // FIXME: The namespace should be from the referencing
                    // property
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
        if (name == null) {
            return null;
        }
        return new XMLType(null, new QName(namespace, name));
    }

}
