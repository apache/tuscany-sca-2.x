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
package org.apache.tuscany.sca.databinding.javabeans;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.namespace.QName;

import org.apache.tuscany.sca.databinding.PullTransformer;
import org.apache.tuscany.sca.databinding.TransformationContext;
import org.apache.tuscany.sca.databinding.impl.BaseTransformer;
import org.apache.tuscany.sca.databinding.impl.SimpleTypeMapperImpl;
import org.apache.tuscany.sca.interfacedef.DataType;
import org.apache.tuscany.sca.interfacedef.util.XMLType;

/**
 * Transformer to convert data from a JavaBean object to xml
 *
 * @version $Rev$ $Date$
 */
public abstract class JavaBean2XMLTransformer<T> extends BaseTransformer<Object, T> implements
    PullTransformer<Object, T> {

    public static final String GET = "get";
    public static final String PREFIX = "n";
    public static final String PERIOD = ".";
    public static final String FWD_SLASH = "/";
    public static final String HTTP = "http://";
    private static int prefixCount = 1;

    protected SimpleTypeMapperImpl mapper;

    public JavaBean2XMLTransformer() {
        this.mapper = new SimpleTypeMapperImpl();
    }

    public T transform(Object source, TransformationContext context) {
        QName rootElement = null;
        if (context != null) {
            DataType<?> type = context.getTargetDataType();
            if (type != null) {
                Object logical = type.getLogical();
                if (logical instanceof XMLType) {
                    rootElement = ((XMLType)logical).getElementName();
                }
            }
        }
        //FIXME See how/if we still need to get the metadata here
        //QName rootElementName = (QName)context.getTargetDataType().getMetadata("RootElementName");
        //if (rootElementName == null) {
        QName rootElementName = new QName(resolveRootElementName(source.getClass()));
        //}

        T root = createElement(rootElementName);
        appendChildElements(root, resolveElementName(source.getClass()), source.getClass(), source, context);
        return root;
    }

    private void appendChildElements(T parent,
                                     QName elementName,
                                     Class javaType,
                                     Object javaObject,
                                     TransformationContext context) {
        T element = null;
        if (javaObject != null) {
            if (javaType.isPrimitive() || isSimpleJavaType(javaObject)) {
                appendText(parent, mapper.toXMLLiteral(null, javaObject, context));
            } else if (javaType.isArray()) {
                int size = Array.getLength(javaObject);
                for (int count = 0; count < size; ++count) {
                    Object item = Array.get(javaObject, count);
                    element = createElement(elementName);
                    appendChild(parent, element);
                    appendChildElements(element, elementName, javaType.getComponentType(), item, context);
                }
            } else {
                Field[] javaFields = javaType.getFields();
                for (Field aField : javaFields) {
                    try {
                        QName fieldElementName = new QName(aField.getName());
                        if (!aField.getType().isArray()) {
                            element = createElement(fieldElementName);
                            appendChild(parent, element);
                            appendChildElements(element,
                                                fieldElementName,
                                                aField.getType(),
                                                aField.get(javaObject),
                                                context);
                        } else {
                            appendChildElements(parent,
                                                fieldElementName,
                                                aField.getType(),
                                                aField.get(javaObject),
                                                context);
                        }
                    } catch (IllegalAccessException e) {
                        Java2XMLMapperException java2xmlEx = new Java2XMLMapperException(e);
                        java2xmlEx.setJavaFieldName(aField.getName());
                        java2xmlEx.setJavaType(javaType);
                        throw java2xmlEx;
                    }
                }

                Method[] methods = javaType.getMethods();
                String fieldName = null;
                for (Method aMethod : methods) {
                    try {
                        if (Modifier.isPublic(aMethod.getModifiers()) && aMethod.getName().startsWith(GET)
                            && aMethod.getParameterTypes().length == 0
                            && isMappedGetter(aMethod.getName())) {
                            fieldName = resolveFieldFromMethod(aMethod.getName());
                            try {
                                javaType.getField(fieldName);
                            } catch (NoSuchFieldException e) {
                                QName fieldElementName = new QName(fieldName);
                                if (aMethod.getReturnType().isArray()) {
                                    appendChildElements(parent, fieldElementName, aMethod.getReturnType(), aMethod
                                        .invoke(javaObject, new Object[0]), context);
                                } else {
                                    element = createElement(fieldElementName);
                                    appendChild(parent, element);
                                    appendChildElements(element, fieldElementName, aMethod.getReturnType(), aMethod
                                        .invoke(javaObject, new Object[0]), context);
                                }
                            }
                        }
                    } catch (IllegalAccessException e) {
                        Java2XMLMapperException java2xmlEx = new Java2XMLMapperException(e);
                        java2xmlEx.setJavaFieldName(fieldName);
                        java2xmlEx.setJavaType(javaType);
                        throw java2xmlEx;
                    } catch (InvocationTargetException e) {
                        Java2XMLMapperException java2xmlEx = new Java2XMLMapperException(e);
                        java2xmlEx.setJavaFieldName(fieldName);
                        java2xmlEx.setJavaType(javaType);
                        throw java2xmlEx;
                    }
                }
            }
        }
    }

    /*
     * Subclasses can override this method to prevent some getter methods
     * from being mapped.  The default implementation provided by this class
     * maps all getter methods.
     */
    protected boolean isMappedGetter(String methodName) {
        return true;
    }

    @Override
    public String getSourceDataBinding() {
        return JavaBeansDataBinding.NAME;
    }

    @Override
    public Class<Object> getSourceType() {
        return Object.class;
    }

    private boolean isSimpleJavaType(Object javaObject) {
        if (javaObject instanceof String) {
            return true;
        }
        if (javaObject instanceof Byte || javaObject instanceof Character
            || javaObject instanceof Short
            || javaObject instanceof Integer
            || javaObject instanceof Long
            || javaObject instanceof Float
            || javaObject instanceof Double
            || javaObject instanceof Boolean) {
            return true;
        }
        if (javaObject instanceof GregorianCalendar || javaObject instanceof Date
            || javaObject instanceof XMLGregorianCalendar
            || javaObject instanceof byte[]
            || javaObject instanceof QName) {
            return true;
        }
        return false;
    }

    private String resolveRootElementName(Class javaType) {
        if (javaType.isArray()) {
            return javaType.getComponentType().getSimpleName() + "_collection";
        } else {
            return javaType.getSimpleName() + "_instance";
        }
    }

    private QName resolveElementName(Class javaType) {
        if (javaType.isArray()) {
            return new QName(javaType.getComponentType().getSimpleName());
        } else {
            return new QName(javaType.getSimpleName());
        }
    }

    private String resolveFieldFromMethod(String methodName) {
        StringBuffer fieldName = new StringBuffer();
        fieldName.append(Character.toLowerCase(methodName.charAt(GET.length())));
        fieldName.append(methodName.substring(GET.length() + 1));
        return fieldName.toString();
    }

    public String getNexPrefix() {
        return PREFIX + prefixCount++;
    }

    @Override
    public int getWeight() {
        return JavaBeansDataBinding.HEAVY_WEIGHT;
    }

    /**
     * Create an element with the given name
     * @param qName
     * @return
     * @throws Java2XMLMapperException
     */
    public abstract T createElement(QName qName) throws Java2XMLMapperException;

    /**
     * Create a text node and add it to the parent
     * @param parentElement
     * @param textData
     * @throws Java2XMLMapperException
     */
    public abstract void appendText(T parentElement, String textData) throws Java2XMLMapperException;

    /**
     * Add the child element to the parent
     * @param parentElement
     * @param childElement
     * @throws Java2XMLMapperException
     */
    public abstract void appendChild(T parentElement, T childElement) throws Java2XMLMapperException;
}
