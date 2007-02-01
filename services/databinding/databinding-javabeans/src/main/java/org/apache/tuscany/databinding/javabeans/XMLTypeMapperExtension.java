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
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.namespace.QName;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.tuscany.spi.databinding.TransformationContext;
import org.apache.tuscany.spi.databinding.extension.DOMHelper;
import org.apache.tuscany.spi.databinding.extension.SimpleTypeMapperExtension;
import org.apache.tuscany.spi.idl.TypeInfo;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * This class maps JavaBeans objects to XML data represented physically as DOM Nodes and vice versa. 
 * It uses JavaBeans Introspection for this mapping.
 */
public class XMLTypeMapperExtension<T> extends SimpleTypeMapperExtension<T> {

    public static final String GET = "get";
    private Document factory;

    protected Node getFragment(T source) {
        return ((Document) source).getDocumentElement();
    }

    public Node toDOMNode(Object javaObject, TransformationContext context) {
        QName rootElementName = (QName) context.getTargetDataType().getMetadata("RootElementName");
        if (rootElementName == null) {
            rootElementName = new QName(resolveRootElementName(javaObject.getClass()));
        }
        try {
            factory = DOMHelper.newDocument();
            Node root = DOMHelper.createElement(factory, rootElementName);
            appendChildElements(root,
                                resolveElementName(javaObject.getClass()),
                                javaObject.getClass(),
                                javaObject,
                                context);
            return root;
        } catch (ParserConfigurationException e) {
            Java2XMLMapperException java2xmlEx = new Java2XMLMapperException(e);
            java2xmlEx.addContextName("tranforming to xml, a java instance of"
                    + javaObject.getClass().getName());
            throw java2xmlEx;
        }
    }

    private void appendChildElements(Node parent,
                                     String elementName,
                                     Class javaType,
                                     Object javaObject,
                                     TransformationContext context) {
        Node elementNode = null;
        if (javaObject != null) {
            if (javaType.isPrimitive() || isSimpleJavaType(javaObject)) {
                parent.appendChild(factory.createTextNode(toXMLLiteral(null, javaObject, context)));
            } else if (javaType.isArray()) {
                boolean arrayDone = false;
                Object arrayObject = null;
                for (int count = 0; !arrayDone; ++count) {
                    try {
                        arrayObject = Array.get(javaObject, count);
                        elementNode = factory.createElement(elementName);
                        parent.appendChild(elementNode);
                        appendChildElements(elementNode,
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
                        if (!aField.getType().isArray()) {
                            elementNode = factory.createElement(aField.getName());
                            parent.appendChild(elementNode);
                            appendChildElements(elementNode,
                                                aField.getName(),
                                                aField.getType(),
                                                aField.get(javaObject),
                                                context);
                        } else {
                            appendChildElements(parent,
                                                aField.getName(),
                                                aField.getType(),
                                                aField.get(javaObject),
                                                context);
                        }
                    } catch (IllegalAccessException e) {
                        Java2XMLMapperException java2xmlEx = new Java2XMLMapperException(e);
                        java2xmlEx.addContextName("tranforming " + aField.getName() + " of "
                                + javaType.getName());
                        throw java2xmlEx;
                    }
                }
                
                Method[] methods = javaType.getDeclaredMethods();
                String fieldName = null;
                StringBuffer fieldNameBuffer = null;
                for (Method aMethod : methods) {
                    try {
                        if (aMethod.getName().startsWith(GET) && aMethod.getParameterTypes().length == 0) {
                            fieldName = resolveFieldFromMethod(aMethod.getName());
                            try {
                                javaType.getField(fieldName);
                            } catch (NoSuchFieldException e) {
                                if ( aMethod.getReturnType().isArray() ) {
                                    appendChildElements(parent,
                                                        fieldName,
                                                        aMethod.getReturnType(),
                                                        aMethod.invoke(javaObject, new Object[0]),
                                                        context);
                                } else {
                                    elementNode = factory.createElement(fieldName);
                                    parent.appendChild(elementNode);
                                    appendChildElements(elementNode,
                                                        fieldName,
                                                        aMethod.getReturnType(),
                                                        aMethod.invoke(javaObject, new Object[0]),
                                                        context);
                                }
                            }
                        }
                    } catch (IllegalAccessException e) {
                        Java2XMLMapperException java2xmlEx = new Java2XMLMapperException(e);
                        java2xmlEx.addContextName("tranforming " + fieldName + " of "
                                + javaType.getName());
                        throw java2xmlEx;
                    } catch (InvocationTargetException e) {
                        Java2XMLMapperException java2xmlEx = new Java2XMLMapperException(e);
                        java2xmlEx.addContextName("tranforming " + fieldName + " of "
                                + javaType.getName());
                        throw java2xmlEx;
                    }
                }
            }
        }
    }

    @Override
    public Object toJavaObject(TypeInfo xmlType, T xmlNode, TransformationContext context) {
        if (xmlType.isSimpleType()) {
            return super.toJavaObject(xmlType, xmlNode, context);
        } else {
            Class<?> javaType = (Class<?>) context.getTargetDataType().getLogical();
            return createJavaObject(getFragment(xmlNode), javaType, context);
        }
    }

    @SuppressWarnings("unchecked")
    private <L> L createJavaObject(Node valueFragment,
                                   Class<L> javaType,
                                   TransformationContext context) throws XML2JavaMapperException {
        NodeList childNodes = valueFragment.getChildNodes();
        if (childNodes.getLength() == 1 && childNodes.item(0).getNodeType() == 3) {

            return (L) super.toJavaObject(getXMLType(javaType), (T) childNodes.item(0), context);
        } else {
            try {
                L javaInstance = javaType.newInstance();
                Map<Field, List<Object>> arrayFields = new Hashtable<Field, List<Object>>();
                Map<Method, List<Object>> arraySetters = new Hashtable<Method, List<Object>>();
                for (int count = 0; count < childNodes.getLength(); ++count) {
                    if (childNodes.item(count).getNodeType() != 3) {
                        String fieldName = childNodes.item(count).getNodeName();
                        try {
                            Field javaField = javaType.getField(fieldName);
                            setFieldValue(javaInstance,
                                          javaField,
                                          childNodes.item(count),
                                          arrayFields,
                                          context);

                        } catch (NoSuchFieldException e1) {
                            setFieldValueUsingSetter(javaType,
                                                     javaInstance,
                                                     fieldName,
                                                     childNodes.item(count),
                                                     arraySetters,
                                                     context);
                        }
                    }
                }

                setArrayValues(javaInstance, arrayFields, arraySetters);
                return javaInstance;
            } catch (XML2JavaMapperException e3) {
                throw e3;
            } catch (Exception e2) {
                XML2JavaMapperException xml2JavaEx = new XML2JavaMapperException(e2);
                xml2JavaEx.addContextName("configuring " + javaType.getName());
                throw xml2JavaEx;
            }
        }
    }

    @SuppressWarnings("unchecked")
    private void setFieldValueUsingSetter(Class javaType,
                                          Object javaInstance,
                                          String fieldName,
                                          Node fieldValue,
                                          Map<Method, List<Object>> arraySetters,
                                          TransformationContext context) throws IllegalAccessException,
                                                                        InvocationTargetException {
        char firstChar = Character.toUpperCase(fieldName.charAt(0));
        StringBuilder methodName = new StringBuilder(SET + fieldName);
        methodName.setCharAt(SET.length(), firstChar);
        boolean methodNotFound = true;

        for (int methodCount = 0; methodNotFound && methodCount < javaType.getMethods().length; ++methodCount) {
            Method aMethod = javaType.getMethods()[methodCount];
            if (aMethod.getName().equals(methodName.toString())
                    && aMethod.getParameterTypes().length == 1) {
                Class paramType = aMethod.getParameterTypes()[0];

                if (paramType.isArray()) {
                    Class componentType = paramType.getComponentType();
                    List<Object> setterValueArray = null;
                    if ((setterValueArray = arraySetters.get(aMethod)) == null) {
                        setterValueArray = new ArrayList();
                        arraySetters.put(aMethod, setterValueArray);
                    }
                    setterValueArray.add(createJavaObject(fieldValue, componentType, context));
                } else {
                    aMethod.invoke(javaInstance, new Object[] {createJavaObject(fieldValue,
                                                                                 paramType,
                                                                                 context)});
                }
                methodNotFound = false;
            }
        }

        if (methodNotFound) {
            XML2JavaMapperException xml2JavaEx =
                    new XML2JavaMapperException("no field or setter method to configure property value <"
                            + fieldName + "> in Java class <" + javaType.getName() + ">");
            xml2JavaEx.addContextName("configuring " + javaType.getName());
            throw xml2JavaEx;
        }
    }

    @SuppressWarnings("unchecked")
    private void setFieldValue(Object javaInstance,
                               Field javaField,
                               Node fieldValue,
                               Map<Field, List<Object>> arrayFields,
                               TransformationContext context) throws IllegalAccessException {
        Class<?> javaFieldType = (Class<?>) javaField.getType();

        if (javaFieldType.isArray()) {
            Class componentType = javaFieldType.getComponentType();
            List<Object> fldValueArray = null;
            if ((fldValueArray = arrayFields.get(javaField)) == null) {
                fldValueArray = new ArrayList();
                arrayFields.put(javaField, fldValueArray);
            }
            fldValueArray.add(createJavaObject(fieldValue, componentType, context));
        } else {
            javaField.setAccessible(true);
            javaField.set(javaInstance, createJavaObject(fieldValue, javaFieldType, context));
        }
    }

    private void setArrayValues(Object javaInstance,
                                Map<Field, List<Object>> arrayFields,
                                Map<Method, List<Object>> arraySetters) throws IllegalAccessException,
                                                                       InvocationTargetException {
        if (arrayFields.size() > 0) {
            for (Field javaField : arrayFields.keySet()) {
                javaField.setAccessible(true);

                if (javaField.getType().getComponentType().isPrimitive()) {
                    javaField.set(javaInstance, createPrimitiveArray(javaField.getType()
                                                                              .getComponentType(),
                                                                     arrayFields.get(javaField)));
                } else {
                    javaField.set(javaInstance,
                                  createNonPrimitiveArray(javaField.getType().getComponentType(),
                                                          arrayFields.get(javaField)));
                }
            }
        }

        if (arraySetters.size() > 0) {
            for (Method aMethod : arraySetters.keySet()) {
                Class paramType = aMethod.getParameterTypes()[0];
                if (paramType.getComponentType().isPrimitive()) {
                    aMethod.invoke(javaInstance,
                                   new Object[] {createPrimitiveArray(paramType.getComponentType(),
                                                                       arraySetters.get(aMethod))});
                } else {
                    aMethod.invoke(javaInstance,
                                   new Object[] {createNonPrimitiveArray(paramType.getComponentType(),
                                                                          arraySetters.get(aMethod))});
                }
            }
        }
    }

    private Object createNonPrimitiveArray(Class fieldType, List values) {
        Object objectArray = Array.newInstance(fieldType, values.size());
        for (int count = 0; count < values.size(); ++count) {
            Array.set(objectArray, count, values.get(count));
        }
        return objectArray;
    }

    private Object createPrimitiveArray(Class fieldType, List values) {
        if (fieldType.isPrimitive()) {
            if (fieldType.getName().equals("int")) {
                int[] primitiveValues = new int[values.size()];
                for (int count = 0; count < values.size(); ++count) {
                    primitiveValues[count] = ((Integer) values.get(count)).intValue();
                }
                return primitiveValues;
            } else if (fieldType.getName().equals("float")) {
                float[] primitiveValues = new float[values.size()];
                for (int count = 0; count < values.size(); ++count) {
                    primitiveValues[count] = ((Float) values.get(count)).floatValue();
                }
                return primitiveValues;
            } else if (fieldType.getName().equals("boolean")) {
                boolean[] primitiveValues = new boolean[values.size()];
                for (int count = 0; count < values.size(); ++count) {
                    primitiveValues[count] = ((Boolean) values.get(count)).booleanValue();
                }
                return primitiveValues;
            } else if (fieldType.getName().equals("char")) {
                char[] primitiveValues = new char[values.size()];
                for (int count = 0; count < values.size(); ++count) {
                    primitiveValues[count] = ((Character) values.get(count)).charValue();
                }
                return primitiveValues;
            } else if (fieldType.getName().equals("byte")) {
                byte[] primitiveValues = new byte[values.size()];
                for (int count = 0; count < values.size(); ++count) {
                    primitiveValues[count] = ((Byte) values.get(count)).byteValue();
                }
                return primitiveValues;
            } else if (fieldType.getName().equals("short")) {
                short[] primitiveValues = new short[values.size()];
                for (int count = 0; count < values.size(); ++count) {
                    primitiveValues[count] = ((Short) values.get(count)).shortValue();
                }
                return primitiveValues;
            } else if (fieldType.getName().equals("long")) {
                long[] primitiveValues = new long[values.size()];
                for (int count = 0; count < values.size(); ++count) {
                    primitiveValues[count] = ((Long) values.get(count)).longValue();
                }
                return primitiveValues;
            } else if (fieldType.getName().equals("double")) {
                double[] primitiveValues = new double[values.size()];
                for (int count = 0; count < values.size(); ++count) {
                    primitiveValues[count] = ((Double) values.get(count)).doubleValue();
                }
                return primitiveValues;
            }
        }
        return values;
    }

    private String resolveRootElementName(Class javaType) {
        if (javaType.isArray()) {
            return javaType.getComponentType().getSimpleName() + "_collection";
        } else {
            return javaType.getSimpleName() + "_instance";
        }
    }

    private String resolveElementName(Class javaType) {
        if (javaType.isArray()) {
            return javaType.getComponentType().getSimpleName();
        } else {
            return javaType.getSimpleName();
        }
    }

    private boolean isSimpleJavaType(Object javaObject) {
        if (javaObject instanceof String || javaObject instanceof Float
                || javaObject instanceof Double || javaObject instanceof GregorianCalendar
                || javaObject instanceof Date || javaObject instanceof XMLGregorianCalendar
                || javaObject instanceof byte[] || javaObject instanceof QName) {
            return true;
        }
        return false;
    }
    
    private String resolveFieldFromMethod(String methodName) {
        StringBuffer fieldName = new StringBuffer();
        fieldName.append(Character.toLowerCase(methodName.charAt(GET.length())));
        fieldName.append(methodName.substring(GET.length() + 1));
        return fieldName.toString();
    }

}
