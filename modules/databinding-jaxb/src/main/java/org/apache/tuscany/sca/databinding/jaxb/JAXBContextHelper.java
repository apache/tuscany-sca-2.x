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

import java.beans.Introspector;
import java.io.IOException;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.JAXBIntrospector;
import javax.xml.bind.Marshaller;
import javax.xml.bind.SchemaOutputResolver;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchema;
import javax.xml.bind.annotation.XmlType;
import javax.xml.namespace.QName;
import javax.xml.transform.Result;
import javax.xml.transform.dom.DOMResult;

import org.apache.tuscany.sca.databinding.TransformationContext;
import org.apache.tuscany.sca.databinding.TransformationException;
import org.apache.tuscany.sca.databinding.impl.SimpleTypeMapperImpl;
import org.apache.tuscany.sca.databinding.jaxb.JAXBContextCache.LRUCache;
import org.apache.tuscany.sca.interfacedef.DataType;
import org.apache.tuscany.sca.interfacedef.Interface;
import org.apache.tuscany.sca.interfacedef.Operation;
import org.apache.tuscany.sca.interfacedef.util.WrapperInfo;
import org.apache.tuscany.sca.interfacedef.util.XMLType;
import org.w3c.dom.Node;

/**
 *
 * @version $Rev$ $Date$
 */
// FIXME: [rfeng] We probably should turn this into a pluggable system service
public class JAXBContextHelper {
    // TODO: Do we need to set them for source and target?
    public static final String JAXB_CLASSES = "jaxb.classes";

    public static final String JAXB_CONTEXT_PATH = "jaxb.contextPath";

    private static final JAXBContextCache cache = new JAXBContextCache();

    private JAXBContextHelper() {
    }

    /**
     * Create a JAXBContext for a given class
     * @param cls
     * @return
     * @throws JAXBException
     */
    public static JAXBContext createJAXBContext(Class<?> cls) throws JAXBException {
        return cache.getJAXBContext(cls);
    }

    public static JAXBContext createJAXBContext(TransformationContext tContext, boolean source) throws JAXBException {
        if (tContext == null)
            throw new TransformationException("JAXB context is not set for the transformation.");

        // TODO: [rfeng] Need to figure out what's the best grantularity to create the JAXBContext
        // per interface, operation or parameter

        /*
        Operation op = source ? tContext.getSourceOperation() : tContext.getTargetOperation();
        if (op != null) {
            return createJAXBContext(getDataTypes(op, true));
        }
        */

        DataType<?> dataType = source ? tContext.getSourceDataType() : tContext.getTargetDataType();
        return createJAXBContext(dataType);

    }

    public static JAXBContext createJAXBContext(DataType dataType) throws JAXBException {
        return createJAXBContext(findClasses(dataType));
    }

    public static Unmarshaller getUnmarshaller(JAXBContext context) throws JAXBException {
        return cache.getUnmarshaller(context);
    }

    public static Marshaller getMarshaller(JAXBContext context) throws JAXBException {
        return cache.getMarshaller(context);
    }

    @SuppressWarnings("unchecked")
    public static Object createJAXBElement(JAXBContext context, DataType dataType, Object value) {
        Class<?> type = dataType == null ? value.getClass() : dataType.getPhysical();
        QName name = JAXBDataBinding.ROOT_ELEMENT;
        if (context != null) {
            Object logical = dataType == null ? null : dataType.getLogical();
            if (logical instanceof XMLType) {
                XMLType xmlType = (XMLType)logical;
                if (xmlType.isElement()) {
                    name = xmlType.getElementName();
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
        }

        JAXBIntrospector introspector = context.createJAXBIntrospector();
        Object element = null;
        if (value != null && introspector.isElement(value)) {
            // NOTE: [rfeng] We cannot wrap an element in a JAXBElement
            element = value;
        }
        if (element == null) {
            element = new JAXBElement(name, type, value);
        }
        return element;
    }

    @SuppressWarnings("unchecked")
    public static Object createReturnValue(JAXBContext context, DataType dataType, Object value) {
        Class<?> cls = getJavaType(dataType);
        if (cls == JAXBElement.class) {
            return createJAXBElement(context, dataType, value);
        } else {
            if (value instanceof JAXBElement) {
                return ((JAXBElement)value).getValue();
            } else {
                return value;
            }
        }
    }

    /**
     * Create a JAXContext for an array of classes
     * @param classes
     * @return
     * @throws JAXBException
     */
    public static JAXBContext createJAXBContext(Class<?>[] classes) throws JAXBException {
        return cache.getJAXBContext(classes);
    }

    public static JAXBContext createJAXBContext(Set<Class<?>> classes) throws JAXBException {
        return cache.getJAXBContext(classes);
    }

    /**
     * Create a JAXBContext for a given java interface
     * @param intf
     * @return
     * @throws JAXBException
     */
    public static JAXBContext createJAXBContext(Interface intf, boolean useWrapper) throws JAXBException {
        synchronized (cache) {
            LRUCache<Object, JAXBContext> map = cache.getCache();
            Integer key = new Integer(System.identityHashCode(intf));
            JAXBContext context = map.get(key);
            if (context != null) {
                return context;
            }
            List<DataType> dataTypes = getDataTypes(intf, useWrapper);
            context = createJAXBContext(dataTypes);
            map.put(key, context);
            return context;
        }
    }

    public static JAXBContext createJAXBContext(List<DataType> dataTypes) throws JAXBException {
        JAXBContext context;
        Set<Class<?>> classes = new HashSet<Class<?>>();
        Set<Type> visited = new HashSet<Type>();
        for (DataType d : dataTypes) {
            findClasses(d, classes, visited);
        }

        context = createJAXBContext(classes);
        return context;
    }

    private static Set<Class<?>> findClasses(DataType d) {
        Set<Class<?>> classes = new HashSet<Class<?>>();
        Set<Type> visited = new HashSet<Type>();
        findClasses(d, classes, visited);
        return classes;
    }

    private static void findClasses(DataType d, Set<Class<?>> classes, Set<Type> visited) {
        if (d == null) {
            return;
        }
        String db = d.getDataBinding();
        if (JAXBDataBinding.NAME.equals(db) || (db != null && db.startsWith("java:")) || db == null) {
            if (!d.getPhysical().isInterface() && !JAXBElement.class.isAssignableFrom(d.getPhysical())) {
                classes.add(d.getPhysical());
            }
        }
        if (d.getPhysical() != d.getGenericType()) {
            findClasses(d.getGenericType(), classes, visited);
        }
    }

    /**
     * Find referenced classes in the generic type
     * @param type
     * @param classSet
     * @param visited
     */
    private static void findClasses(Type type, Set<Class<?>> classSet, Set<Type> visited) {
        if (visited.contains(type) || type == null) {
            return;
        }
        visited.add(type);
        if (type instanceof Class) {
            Class<?> cls = (Class<?>)type;
            if (!cls.isInterface()) {
                classSet.add(cls);
            }
            return;
        } else if (type instanceof ParameterizedType) {
            ParameterizedType pType = (ParameterizedType)type;
            findClasses(pType.getRawType(), classSet, visited);
            for (Type t : pType.getActualTypeArguments()) {
                findClasses(t, classSet, visited);
            }
        } else if (type instanceof TypeVariable) {
            TypeVariable<?> tv = (TypeVariable<?>)type;
            for (Type t : tv.getBounds()) {
                findClasses(t, classSet, visited);
            }
        } else if (type instanceof GenericArrayType) {
            GenericArrayType gType = (GenericArrayType)type;
            findClasses(gType, classSet, visited);
        } else if (type instanceof WildcardType) {
            WildcardType wType = (WildcardType)type;
            for (Type t : wType.getLowerBounds()) {
                findClasses(t, classSet, visited);
            }
            for (Type t : wType.getUpperBounds()) {
                findClasses(t, classSet, visited);
            }
        }
    }

    public static JAXBContext createJAXBContext(Interface intf) throws JAXBException {
        return createJAXBContext(intf, true);
    }

    /**
     * @param intf
     * @param useWrapper Use wrapper classes?
     * @return
     */
    private static List<DataType> getDataTypes(Interface intf, boolean useWrapper) {
        List<DataType> dataTypes = new ArrayList<DataType>();
        for (Operation op : intf.getOperations()) {
            getDataTypes(dataTypes, op, useWrapper);
        }
        return dataTypes;
    }

    private static List<DataType> getDataTypes(Operation op, boolean useWrapper) {
        List<DataType> dataTypes = new ArrayList<DataType>();
        getDataTypes(dataTypes, op, useWrapper);
        return dataTypes;
    }

    private static void getDataTypes(List<DataType> dataTypes, Operation op, boolean useWrapper) {
        WrapperInfo wrapper = op.getWrapper();
        if (useWrapper && wrapper != null) {
            DataType dt1 = wrapper.getInputWrapperType();
            if (dt1 != null) {
                dataTypes.add(dt1);
            }
            DataType dt2 = wrapper.getOutputWrapperType();
            if (dt2 != null) {
                dataTypes.add(dt2);
            }
        }
        // FIXME: [rfeng] We may need to find the referenced classes in the child types
        // else 
        {
            for (DataType dt1 : op.getInputType().getLogical()) {
                dataTypes.add(dt1);
            }
            DataType dt2 = op.getOutputType();
            if (dt2 != null) {
                dataTypes.add(dt2);
            }
        }
        for (DataType<DataType> dt3 : op.getFaultTypes()) {
            DataType dt4 = dt3.getLogical();
            if (dt4 != null) {
                dataTypes.add(dt4);
            }
        }
    }

    public static Class<?> getJavaType(DataType<?> dataType) {
        if (dataType == null) {
            return null;
        }
        Class type = dataType.getPhysical();
        if (JAXBElement.class.isAssignableFrom(type)) {
            Type generic = dataType.getGenericType();
            type = Object.class;
        }
        if (type == Object.class && dataType.getLogical() instanceof XMLType) {
            XMLType xType = (XMLType)dataType.getLogical();
            Class javaType = SimpleTypeMapperImpl.getJavaType(xType.getTypeName());
            if (javaType != null) {
                type = javaType;
            }
        }
        return type;
    }

    public static XMLType getXmlTypeName(Class<?> javaType) {
        if (javaType.isInterface()) {
            // JAXB doesn't support interfaces
            return null;
        }
        String namespace = null;
        String name = null;
        Package pkg = javaType.getPackage();
        if (pkg != null) {
            XmlSchema schema = pkg.getAnnotation(XmlSchema.class);
            if (schema != null) {
                namespace = schema.namespace();
            }
        }

        QName elementQName = null;
        QName typeQName = null;
        XmlRootElement rootElement = javaType.getAnnotation(XmlRootElement.class);
        if (rootElement != null) {
            String elementName = rootElement.name();
            String elementNamespace = rootElement.namespace();
            if (elementNamespace.equals("##default")) {
                elementNamespace = namespace;
            }
            if (elementName.equals("##default")) {
                elementName = Introspector.decapitalize(javaType.getSimpleName());
            }
            elementQName = new QName(elementNamespace, elementName);
        }
        XmlType type = javaType.getAnnotation(XmlType.class);
        if (type != null) {
            String typeNamespace = type.namespace();
            String typeName = type.name();

            if (typeNamespace.equals("##default")) {
                // namespace is from the package
                typeNamespace = namespace;
            }

            if (typeName.equals("##default")) {
                typeName = Introspector.decapitalize(javaType.getSimpleName());
            }
            typeQName = new QName(typeNamespace, typeName);
        } else {
            XmlEnum xmlEnum = javaType.getAnnotation(XmlEnum.class);
            if (xmlEnum != null) {
                name = Introspector.decapitalize(javaType.getSimpleName());
                typeQName = new QName(namespace, name);
            }
        }
        if (elementQName == null && typeQName == null) {
            return null;
        }
        return new XMLType(elementQName, typeQName);
    }

    public static Node generateSchema(JAXBContext context) throws Exception {
        SchemaOutputResolverImpl resolver = new SchemaOutputResolverImpl();
        context.generateSchema(resolver);
        return resolver.getSchema();
    }

    public static class SchemaOutputResolverImpl extends SchemaOutputResolver {
        private DOMResult result = new DOMResult();

        @Override
        public Result createOutput(String ns, String file) throws IOException {
            result.setSystemId("sca:dom");
            return result;
        }

        public Node getSchema() {
            return result != null ? result.getNode() : null;
        }

    }

}
