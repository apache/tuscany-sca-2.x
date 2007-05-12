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
package org.apache.tuscany.databinding.xml;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamReader;

import org.apache.tuscany.databinding.impl.SimpleTypeMapperImpl;
import org.apache.tuscany.sca.interfacedef.util.TypeInfo;

public final class BeanUtil {
    private static final Object[] NULL = (Object[])null;
    private static int nsCount = 1;

    private static final SimpleTypeMapperImpl MAPPER = new SimpleTypeMapperImpl();

    private BeanUtil() {
    }

    private static boolean isSimpleType(Class javaType) {
        return MAPPER.getXMLType(javaType) != null;
    }

    private static String getStringValue(Object o) {
        if (o == null) {
            return null;
        }
        TypeInfo info = MAPPER.getXMLType(o.getClass());
        if (info != null) {
            return MAPPER.toXMLLiteral(info.getQName(), o, null);
        } else {
            return String.valueOf(o);
        }
    }

    /**
     * To Serilize Bean object this method is used, this will create an object
     * array using given bean object
     * 
     * @param beanObject
     * @param beanName
     */
    public static XMLStreamReader getXMLStreamReader(Object beanObject, QName beanName) {
        try {
            ClassLoader cl = beanObject.getClass().getClassLoader();
            if (cl == null) {
                cl = ClassLoader.getSystemClassLoader();
            }
            String beanNS = beanName.getNamespaceURI();
            String beanPrefix = beanName.getPrefix();
            BeanInfo beanInfo = Introspector.getBeanInfo(beanObject.getClass());
            PropertyDescriptor[] propDescs = beanInfo.getPropertyDescriptors();
            Map<String, PropertyDescriptor> propertMap = new HashMap<String, PropertyDescriptor>();
            for (int i = 0; i < propDescs.length; i++) {
                PropertyDescriptor propDesc = propDescs[i];
                propertMap.put(propDesc.getName(), propDesc);
            }
            List<String> properties = new ArrayList<String>(propertMap.keySet());
            Collections.sort(properties);
            List<NamedProperty> props = new ArrayList<NamedProperty>();
            for (int i = 0; i < properties.size(); i++) {
                String property = properties.get(i);
                PropertyDescriptor propDesc = (PropertyDescriptor)propertMap.get(property);
                if (propDesc == null) {
                    // JAM does bad thing so I need to add this
                    continue;
                }
                Class ptype = propDesc.getPropertyType();
                if ("class".equals(property)) {
                    continue;
                }
                if (isSimpleType(ptype)) {
                    Object value = propDesc.getReadMethod().invoke(beanObject, NULL);
                    NamedProperty prop =
                        new NamedProperty(new QName(beanNS, property, beanPrefix), getStringValue(value));
                    props.add(prop);
                } else if (ptype.isArray()) {
                    if (isSimpleType(ptype.getComponentType())) {
                        Object value = propDesc.getReadMethod().invoke(beanObject, NULL);
                        if (value != null) {
                            int i1 = Array.getLength(value);
                            for (int j = 0; j < i1; j++) {
                                Object o = Array.get(value, j);
                                NamedProperty prop =
                                    new NamedProperty(new QName(beanNS, property, beanPrefix), getStringValue(o));
                                props.add(prop);
                            }
                        } else {
                            NamedProperty prop = new NamedProperty(new QName(beanNS, property, beanPrefix), value);
                            props.add(prop);
                        }

                    } else {
                        Object value[] = (Object[])propDesc.getReadMethod().invoke(beanObject, NULL);
                        if (value != null) {
                            for (int j = 0; j < value.length; j++) {
                                Object o = value[j];
                                NamedProperty prop =
                                    new NamedProperty(new QName(beanNS, property, beanPrefix), getStringValue(o));
                                props.add(prop);
                            }
                        } else {
                            NamedProperty prop = new NamedProperty(new QName(beanNS, property, beanPrefix), value);
                            props.add(prop);
                        }
                    }
                } else if (Collection.class.isAssignableFrom(ptype)) {
                    Object value = propDesc.getReadMethod().invoke(beanObject, NULL);
                    Collection objList = (Collection)value;
                    if (objList != null && objList.size() > 0) {
                        // this was given error , when the array.size = 0
                        // and if the array contain simple type , then the
                        // ADBPullParser asked
                        // PullParser from That simpel type
                        for (Iterator j = objList.iterator(); j.hasNext();) {
                            Object o = j.next();
                            if (isSimpleType(o.getClass())) {
                                NamedProperty prop =
                                    new NamedProperty(new QName(beanNS, property, beanPrefix), getStringValue(o));
                                props.add(prop);
                            } else {
                                NamedProperty prop = new NamedProperty(new QName(beanNS, property, beanPrefix), o);
                                props.add(prop);
                            }
                        }

                    } else {
                        NamedProperty prop = new NamedProperty(new QName(beanNS, property, beanPrefix), value);
                        props.add(prop);
                    }
                } else {
                    Object value = propDesc.getReadMethod().invoke(beanObject, NULL);
                    NamedProperty prop = new NamedProperty(new QName(beanNS, property, beanPrefix), value);
                    props.add(prop);
                }
            }
            NamedProperty[] elements = new NamedProperty[props.size()];
            props.toArray(elements);
            return new XMLFragmentStreamReaderImpl(beanName, elements, null);
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }

    /**
     * to get the pull parser for a given bean object , generate the wrpper
     * element using class name
     * 
     * @param beanObject
     */
    public static XMLStreamReader getXMLStreamReader(Object beanObject) {
        String className = beanObject.getClass().getName();
        if (className.indexOf(".") > 0) {
            className = className.substring(className.lastIndexOf('.') + 1, className.length());
        }
        return getXMLStreamReader(beanObject, new QName(className));
    }

    /**
     * increments the namespace counter and returns a new prefix
     * 
     * @return unique prefix
     */
    public static String getUniquePrefix() {
        return "s" + nsCount++;
    }

}
