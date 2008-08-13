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
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

import org.apache.tuscany.sca.databinding.impl.SimpleTypeMapperImpl;
import org.apache.tuscany.sca.interfacedef.util.TypeInfo;

/**
 * @version $Rev$ $Date$
 */
public class BeanXMLStreamReaderImpl extends XmlTreeStreamReaderImpl {
    private static final Comparator<Accessor> COMPARATOR = new Comparator<Accessor>() {
        public int compare(Accessor o1, Accessor o2) {
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

        @Override
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
            if (Map.class.isAssignableFrom(value.getClass())) {
                List<XmlNode> entries = new ArrayList<XmlNode>();
                QName entryName = new QName(name.getNamespaceURI(), "entry");
                Map map = (Map)value;
                if (map != null) {
                    for (Object e : map.entrySet()) {
                        Map.Entry entry = (Map.Entry)e;
                        entries.add(new BeanXmlNodeImpl(entryName, entry));
                    }
                }
                return entries.iterator();
            }
            try {
                Map<String, Accessor> accessorMap = getAccessors(value);
                List<Accessor> accessorList = new ArrayList<Accessor>(accessorMap.values());
                Collections.sort(accessorList, COMPARATOR);

                List<XmlNode> props = new ArrayList<XmlNode>();
                for (Accessor accessor : accessorList) {
                    Class<?> pType = accessor.getType();

                    QName pName = new QName(name.getNamespaceURI(), accessor.getName());
                    Object pValue = accessor.getValue();
                    if (pType.isArray()) {
                        if (pValue != null) {
                            int i1 = Array.getLength(pValue);
                            for (int j = 0; j < i1; j++) {
                                Object o = Array.get(pValue, j);
                                props.add(new BeanXmlNodeImpl(pName, o));
                            }
                        } else {
                            // TODO: How to handle null?
                        }
                    } else if (Collection.class.isAssignableFrom(pType)) {
                        Collection objList = (Collection)pValue;
                        if (objList != null && objList.size() > 0) {
                            for (Iterator j = objList.iterator(); j.hasNext();) {
                                Object o = j.next();
                                props.add(new BeanXmlNodeImpl(pName, o));
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

        @Override
        public QName getName() {
            return name;
        }

        @Override
        public String getValue() {
            return getStringValue(value);
        }
        
        private static String getPackageName(Class<?> cls) {
            String name = cls.getName();
            int index = name.lastIndexOf('.');
            return index == -1 ? "" : name.substring(0, index);
        }

        public static QName getName(Class<?> cls) {
            if (cls == null) {
                return null;
            }

            String packageName = getPackageName(cls);

            if ("".equals(packageName)) {
                return new QName("", cls.getSimpleName());
            }
            StringBuffer ns = new StringBuffer("http://");
            String[] names = packageName.split("\\.");
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

    public static interface Accessor {
        String getName();

        Class<?> getType();

        Object getValue() throws Exception;

        void setValue(Object value) throws Exception;
    }

    private static class FieldAccessor implements Accessor {
        private Object target;
        private Field field;

        public FieldAccessor(Object target, Field field) {
            super();
            this.target = target;
            this.field = field;
            this.field.setAccessible(true);
        }

        public String getName() {
            return field.getName();
        }

        public Object getValue() throws Exception {
            return field.get(target);
        }

        public void setValue(Object value) throws Exception {
            field.set(target, value);
        }

        public Class<?> getType() {
            return field.getType();
        }

    }

    private static class PropertyAccessor implements Accessor {
        private Object target;
        private PropertyDescriptor prop;

        public PropertyAccessor(Object target, PropertyDescriptor prop) {
            super();
            this.target = target;
            this.prop = prop;
        }

        public String getName() {
            return prop.getName();
        }

        public Class<?> getType() {
            return prop.getPropertyType();
        }

        public Object getValue() throws Exception {
            Method getter = prop.getReadMethod();
            if (getter != null) {
                getter.setAccessible(true);
                return getter.invoke(target);
            }
            throw new IllegalAccessException("The property cannot be read: " + getName());
        }

        public void setValue(Object value) throws Exception {
            Method setter = prop.getWriteMethod();
            if (setter != null) {
                setter.setAccessible(true);
                setter.invoke(target);
            }
            throw new IllegalAccessException("The property cannot be written: " + getName());
        }

    }

    private static Map<String, Accessor> getAccessors(Object target) throws Exception {
        if (target == null) {
            return Collections.emptyMap();
        }
        Map<String, Accessor> map = new HashMap<String, Accessor>();
        Class<?> type = target.getClass();
        for (Field f : type.getFields()) {
            map.put(f.getName(), new FieldAccessor(target, f));
        }
        BeanInfo info = Introspector.getBeanInfo(type, Object.class);
        for (PropertyDescriptor p : info.getPropertyDescriptors()) {
            // if (p.getReadMethod() != null && p.getWriteMethod() != null) {
                map.put(p.getName(), new PropertyAccessor(target, p));
            // }
        }
        return map;
    }

}
