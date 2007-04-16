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
package org.apache.tuscany.databinding.javabeans;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.namespace.QName;

import org.apache.tuscany.databinding.PullTransformer;
import org.apache.tuscany.databinding.TransformationContext;
import org.apache.tuscany.databinding.extension.SimpleTypeMapperExtension;
import org.apache.tuscany.databinding.extension.TransformerExtension;

/**
 * Transformer to convert data from a JavaBean object to xml
 */
public abstract class JavaBean2XMLTransformer<T> extends TransformerExtension<Object, T> implements
        PullTransformer<Object, T> {

    public static final String GET = "get";
    public static final String PREFIX = "n";
    public static final String PERIOD = ".";
    public static final String FWD_SLASH = "/";
    public static final String HTTP = "http://";
    private static int prefixCount = 1;
    
    protected SimpleTypeMapperExtension mapper;

    public JavaBean2XMLTransformer() {
        this.mapper = new SimpleTypeMapperExtension();
    }

    public T transform(Object source, TransformationContext context) {
        
        //FIXME See how/if we still need to get the metadata here
        //QName rootElementName = (QName)context.getTargetDataType().getMetadata("RootElementName");
        //if (rootElementName == null) {
        QName rootElementName = new QName(resolveRootElementName(source.getClass()));
        //}
        
        T root = createElement(rootElementName);
        appendChildElements(root,
                            resolveElementName(source.getClass()),
                            source.getClass(),
                            source,
                            context);
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
                appendChild(parent, createText(mapper.toXMLLiteral(null, javaObject, context)));
            } else if (javaType.isArray()) {
                boolean arrayDone = false;
                Object arrayObject = null;
                for (int count = 0; !arrayDone; ++count) {
                    try {
                        arrayObject = Array.get(javaObject, count);
                        element = createElement(elementName);
                        appendChild(parent, element);
                        appendChildElements(element, 
                                            elementName, 
                                            javaType.getComponentType(), 
                                            arrayObject, 
                                            context);
                    } catch (ArrayIndexOutOfBoundsException e1) {
                        arrayDone = true;
                    }
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

                Method[] methods = javaType.getDeclaredMethods();
                String fieldName = null;
                for (Method aMethod : methods) {
                    try {
                        if (Modifier.isPublic(aMethod.getModifiers()) && aMethod.getName().startsWith(GET)
                            && aMethod.getParameterTypes().length == 0) {
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

    public Class getSourceType() {
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
            || javaObject instanceof Double) {
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
    
    public abstract T createElement(QName qName) throws Java2XMLMapperException;
    public abstract T createText(String textData) throws Java2XMLMapperException;
    public abstract void appendChild(T parentElement, T childElement) throws Java2XMLMapperException;
}
