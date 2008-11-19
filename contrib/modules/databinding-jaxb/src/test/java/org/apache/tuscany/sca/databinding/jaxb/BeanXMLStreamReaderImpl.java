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

package org.apache.tuscany.sca.databinding.jaxb;

import java.lang.reflect.Array;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.namespace.QName;

import org.apache.tuscany.sca.databinding.impl.SimpleTypeMapperImpl;
import org.apache.tuscany.sca.databinding.xml.SimpleXmlNodeImpl;
import org.apache.tuscany.sca.databinding.xml.XmlNode;
import org.apache.tuscany.sca.databinding.xml.XmlTreeStreamReaderImpl;
import org.apache.tuscany.sca.interfacedef.util.TypeInfo;
import org.jvnet.jaxb.reflection.model.annotation.RuntimeInlineAnnotationReader;
import org.jvnet.jaxb.reflection.model.core.Ref;
import org.jvnet.jaxb.reflection.model.impl.RuntimeModelBuilder;
import org.jvnet.jaxb.reflection.model.runtime.RuntimeClassInfo;
import org.jvnet.jaxb.reflection.model.runtime.RuntimePropertyInfo;
import org.jvnet.jaxb.reflection.model.runtime.RuntimeTypeInfoSet;
import org.jvnet.jaxb.reflection.runtime.IllegalAnnotationsException;
import org.jvnet.jaxb.reflection.runtime.JAXBContextImpl;



/**
 * @version $Rev$ $Date$
 */
public class BeanXMLStreamReaderImpl extends XmlTreeStreamReaderImpl {
    private static final Comparator<Accessor> COMPARATOR = new Comparator<Accessor>() {
        public int compare(Accessor o1, Accessor o2) {
            return o1.getName().compareTo(o2.getName());
        }
    };

    private static final String XSI_PREFIX = "xsi";
    private static final String XSI_NS = "http://www.w3.org/2001/XMLSchema-instance";

    private static XmlNode getXSIType(QName realType) {
        QName xsiType = new QName(XSI_NS, "type", XSI_PREFIX);
        String prefix = realType.getPrefix();
        String typeName = realType.getLocalPart();
        if (prefix != null && !prefix.equals("")) {
            typeName = prefix + ":" + realType.getLocalPart();
        }
        return new SimpleXmlNodeImpl(xsiType, XmlNode.Type.ATTRIBUTE);
    }

    /**
     * Represent a Map.Entry XML node
     * @version $Rev$ $Date$
     */
    public static class MapEntryXmlNodeImpl extends SimpleXmlNodeImpl implements XmlNode {
        private Map.Entry entry;

        public MapEntryXmlNodeImpl(Entry entry) {
            super(new QName("", "entry"), entry);
            this.entry = entry;
        }

        @Override
        public Iterator<XmlNode> children() {
            List<XmlNode> nodes = new ArrayList<XmlNode>();
            XmlNode key = new BeanXmlNodeImpl(new QName("", "key"), entry.getKey());
            XmlNode value = new BeanXmlNodeImpl(new QName("", "value"), entry.getValue());
            nodes.add(key);
            nodes.add(value);
            return nodes.iterator();
        }
    }

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
                Map map = (Map)value;
                if (map != null) {
                    for (Object e : map.entrySet()) {
                        Map.Entry entry = (Map.Entry)e;
                        entries.add(new MapEntryXmlNodeImpl(entry));
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

    private static RuntimeTypeInfoSet create(Class... classes) throws Exception {
        IllegalAnnotationsException.Builder errorListener = new IllegalAnnotationsException.Builder();
        RuntimeInlineAnnotationReader reader = new RuntimeInlineAnnotationReader();
        JAXBContextImpl context =
            new JAXBContextImpl(classes, null, Collections.<Class, Class> emptyMap(), null, false, reader, false, false);
        RuntimeModelBuilder builder =
            new RuntimeModelBuilder(context, reader, Collections.<Class, Class> emptyMap(), null);
        builder.setErrorHandler(errorListener);
        for (Class c : classes)
            builder.getTypeInfo(new Ref<Type, Class>(c));

        RuntimeTypeInfoSet r = builder.link();
        errorListener.check();
        return r;
    }

    private static class JAXBAccessor implements Accessor {
        private Object target;
        private RuntimePropertyInfo prop;

        public JAXBAccessor(Object target, RuntimePropertyInfo field) {
            super();
            this.target = target;
            this.prop = field;
        }

        public String getName() {
            return prop.getName();
        }

        public Object getValue() throws Exception {
            return prop.getAccessor().get(target);
        }

        public void setValue(Object value) throws Exception {
            prop.getAccessor().set(target, value);
        }

        public Class<?> getType() {
            Type type = prop.getRawType();
            if (type instanceof Class) {
                return (Class<?>)type;
            } else if (type instanceof ParameterizedType) {
                ParameterizedType pType = (ParameterizedType)type;
                type = pType.getRawType();
                if (type instanceof Class) {
                    return (Class<?>)type;
                }
            }
            return Object.class;
        }

    }

    private static Map<String, Accessor> getAccessors(Object target) throws Exception {
        if (target == null) {
            return Collections.emptyMap();
        }
        Map<String, Accessor> map = new HashMap<String, Accessor>();
        Class<?> type = target.getClass();
        RuntimeTypeInfoSet set = create(type);
        RuntimeClassInfo clsInfo = (RuntimeClassInfo)set.getTypeInfo(type);
        for (RuntimePropertyInfo f : clsInfo.getProperties()) {
            map.put(f.getName(), new JAXBAccessor(target, f));
        }
        return map;
    }

}
