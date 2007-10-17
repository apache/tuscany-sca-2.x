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

package org.apache.tuscany.sca.databinding.xml;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import javax.xml.namespace.QName;

import org.apache.tuscany.sca.databinding.impl.SimpleTypeMapperImpl;
import org.apache.tuscany.sca.interfacedef.util.TypeInfo;

/**
 * @version $Rev$ $Date$
 */
public class BeanXMLStreamReaderImpl extends XmlTreeStreamReaderImpl {
    private final static Comparator<PropertyDescriptor> COMPARATOR = new Comparator<PropertyDescriptor>() {
        public int compare(PropertyDescriptor o1, PropertyDescriptor o2) {
            return o1.getName().compareTo(o2.getName());
        }
    };

    public static class BeanXmlNodeImpl extends SimpleXmlNodeImpl implements XmlNode {
        private static final Object[] NULL = null;
        private static final SimpleTypeMapperImpl MAPPER = new SimpleTypeMapperImpl();

        public BeanXmlNodeImpl(Object bean) {
            super(getName(bean == null ? null : bean.getClass()), bean);
        }

        public BeanXmlNodeImpl(QName name, Object bean) {
            super(name, bean);
        }

        private static boolean isSimpleType(Class<?> javaType) {
            return SimpleTypeMapperImpl.getXMLType(javaType) != null;
        }

        private static String getStringValue(Object o) {
            if (o == null) {
                return null;
            }
            TypeInfo info = SimpleTypeMapperImpl.getXMLType(o.getClass());
            if (info != null) {
                return MAPPER.toXMLLiteral(info.getQName(), o, null);
            } else {
                return String.valueOf(o);
            }
        }

        public Iterator<XmlNode> children() {
            if (name == null) {
                return null;
            }
            if (value == null) {
                return super.children();
            }
            if (isSimpleType(value.getClass())) {
                XmlNode textNode = new BeanXmlNodeImpl(null, value);
                return Arrays.asList(textNode).iterator();
            }
            try {
                BeanInfo beanInfo = Introspector.getBeanInfo(value.getClass());
                PropertyDescriptor[] propDescs = beanInfo.getPropertyDescriptors();
                Collections.sort(Arrays.asList(propDescs), COMPARATOR);
    
                List<XmlNode> props = new ArrayList<XmlNode>();
                for (int i = 0; i < propDescs.length; i++) {
                    PropertyDescriptor propDesc = propDescs[i];
                    Class<?> pType = propDesc.getPropertyType();
                    if ("class".equals(propDesc.getName())) {
                        continue;
                    }
                    QName pName = new QName(name.getNamespaceURI(), propDesc.getName());
                    Object pValue = propDesc.getReadMethod().invoke(value, NULL);
                    if (pType.isArray()) {
                        if (pValue != null) {
                            int i1 = Array.getLength(pValue);
                            for (int j = 0; j < i1; j++) {
                                Object o = Array.get(pValue, j);
                                props.add(new BeanXmlNodeImpl(pName, getStringValue(o)));
                            }
                        } else {
                            // TODO: How to handle null?
                        }
                    } else if (Collection.class.isAssignableFrom(pType)) {
                        Collection objList = (Collection)pValue;
                        if (objList != null && objList.size() > 0) {
                            for (Iterator j = objList.iterator(); j.hasNext();) {
                                Object o = j.next();
                                if (isSimpleType(o.getClass())) {
                                    props.add(new BeanXmlNodeImpl(pName, getStringValue(o)));
                                } else {
                                    props.add(new BeanXmlNodeImpl(pName, o));
                                }
                            }
    
                        } else {
                            // How to handle null
                        }
                    } else {
                        props.add(new BeanXmlNodeImpl(pName, pValue));
                    }
                }
                return props.iterator();
            } catch (Exception e) {
                throw new IllegalArgumentException(e);
            }
        }

        public QName getName() {
            return name;
        }

        public String getValue() {
            return getStringValue(value);
        }

        public static QName getName(Class<?> cls) {
            if (cls == null) {
                return null;
            }
            Package pkg = cls.getPackage();
            if (pkg == null) {
                return new QName("", cls.getSimpleName());
            }
            StringBuffer ns = new StringBuffer("http://");
            String[] names = pkg.getName().split("\\.");
            for (int i = names.length - 1; i >= 0; i--) {
                ns.append(names[i]);
                if (i != 0) {
                    ns.append('.');
                }
            }
            ns.append('/');
            return new QName(ns.toString(), cls.getSimpleName());
        }

    }

    public BeanXMLStreamReaderImpl(QName name, Object bean) {
        super(getXmlNode(name, bean));
    }

    private static BeanXmlNodeImpl getXmlNode(QName name, Object bean) {
        BeanXmlNodeImpl root = null;
        if (name != null) {
            root = new BeanXmlNodeImpl(name, bean);
        } else {
            root = new BeanXmlNodeImpl(bean);
        }
        return root;
    }

}
