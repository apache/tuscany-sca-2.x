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

import java.lang.reflect.Type;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.namespace.QName;

import org.apache.tuscany.spi.databinding.TransformationContext;
import org.apache.tuscany.spi.databinding.TransformationException;
import org.apache.tuscany.spi.model.DataType;
import org.apache.tuscany.spi.model.Operation;

public class JAXBContextHelper {
    private static final QName JAXB_ELEMENT = new QName("http://jaxb", "element");
    // TODO: Do we need to set them for source and target?
    public static final String JAXB_CLASSES = "jaxb.classes";

    public static final String JAXB_CONTEXT_PATH = "jaxb.contextPath";

    private JAXBContextHelper() {
    }

    public static JAXBContext createJAXBContext(TransformationContext tContext, boolean source)
        throws JAXBException {
        if (tContext == null)
            throw new TransformationException("JAXB context is not set for the transformation.");

        // FIXME: We should check the context path or classes
        // FIXME: What should we do if JAXB is an intermediate node?
        DataType<?> bindingContext = source ? tContext.getSourceDataType() : tContext.getTargetDataType();
        String contextPath = (String)bindingContext.getMetadata(JAXB_CONTEXT_PATH);
        if (contextPath == null) {
            Operation op = (Operation)bindingContext.getMetadata(Operation.class.getName());
            contextPath = op != null ? (String)op.getMetaData().get(JAXB_CONTEXT_PATH) : null;
        }
        JAXBContext context = null;
        if (contextPath != null) {
            context = JAXBContext.newInstance(contextPath);
        } else {
            Class[] classes = (Class[])bindingContext.getMetadata(JAXB_CLASSES);
            if (classes != null) {
                context = JAXBContext.newInstance(classes);
            } else {
                Type type = bindingContext.getPhysical();
                if (type instanceof Class) {
                    Class cls = (Class)type;
                    if (cls.getPackage() != null) {
                        contextPath = cls.getPackage().getName();
                        context = JAXBContext.newInstance(contextPath);
                    }
                }
            }
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
            Object logical = dataType.getLogical();
            if (!(logical instanceof QName)) {
                logical = JAXB_ELEMENT;
            }
            return new JAXBElement((QName)logical, (Class)dataType.getPhysical(), value);
        }
    }

    @SuppressWarnings("unchecked")
    public static Object createReturnValue(DataType dataType, Object value) {
        Class<?> cls = (Class)dataType.getPhysical();
        XmlRootElement element = cls.getAnnotation(XmlRootElement.class);
        if (element == null) {
            if (value instanceof JAXBElement) {
                return ((JAXBElement)value).getValue();
            } else {
                return value;
            }
        } else {
            QName root = new QName(element.namespace(), element.name());
            return new JAXBElement(root, (Class)dataType.getPhysical(), value);
        }
    }

}
