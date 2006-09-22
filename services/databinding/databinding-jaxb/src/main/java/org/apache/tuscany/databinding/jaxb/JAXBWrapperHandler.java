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
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;

import org.apache.tuscany.databinding.TransformationContext;
import org.apache.tuscany.databinding.TransformationException;
import org.apache.tuscany.databinding.idl.WrapperHandler;
import org.apache.tuscany.spi.idl.ElementInfo;

/**
 * JAXB WrapperHandler implementation
 */
public class JAXBWrapperHandler implements WrapperHandler<JAXBElement<?>> {

    public JAXBElement<?> create(ElementInfo element, TransformationContext context) {
        try {
            String packageName = null;
            String factoryClassName = packageName + ".ObjectFactory";
            Class<?> factoryClass = Class.forName(factoryClassName, true, context.getClassLoader());
            assert factoryClass.isAnnotationPresent(XmlRegistry.class);
            Object factory = factoryClass.newInstance();
            QName elementName = element.getQName();
            Method method = null;
            for (Method m : factoryClass.getMethods()) {
                XmlElementDecl xmlElement = m.getAnnotation(XmlElementDecl.class);
                QName name = new QName(xmlElement.namespace(), xmlElement.name());
                if (xmlElement != null && name.equals(elementName)) {
                    method = m;
                    break;
                }
            }
            if (method != null) {
                Class typeClass = method.getParameterTypes()[0];
                Object value = typeClass.newInstance();
                return (JAXBElement<?>) method.invoke(factory, new Object[] { value });
            } else {
                throw new TransformationException("ObjectFactory cannot be resolved.");
            }
        } catch (Exception e) {
            throw new TransformationException(e);
        }
    }

    public Object getChild(JAXBElement<?> wrapper, int i, ElementInfo element) {
        try {
            Object value = wrapper.getValue();
            PropertyDescriptor descriptors[] =
                    Introspector.getBeanInfo(wrapper.getDeclaredType()).getPropertyDescriptors();
            for (PropertyDescriptor d : descriptors) {
                if (d.getName().equals(element.getQName().getLocalPart())) {
                    return d.getReadMethod().invoke(value, new Object[] {});
                }
            }
            return null;
        } catch (Exception e) {
            throw new TransformationException(e);
        }
    }

    public void setChild(JAXBElement<?> wrapper, int i, ElementInfo childElement, Object value) {
        try {
            Object wrapperValue = wrapper.getValue();
            PropertyDescriptor descriptors[] =
                    Introspector.getBeanInfo(wrapper.getDeclaredType()).getPropertyDescriptors();
            for (PropertyDescriptor d : descriptors) {
                if (d.getName().equals(childElement.getQName().getLocalPart())) {
                    d.getWriteMethod().invoke(wrapperValue, new Object[] { value });
                    break;
                }
            }
        } catch (Exception e) {
            throw new TransformationException(e);
        }
    }

}
