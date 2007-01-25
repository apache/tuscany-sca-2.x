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

package org.apache.tuscany.spi.databinding.extension;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.apache.tuscany.spi.databinding.TransformationContext;
import org.apache.tuscany.spi.idl.TypeInfo;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class XMLTypeMapperExtension<T> extends SimpleTypeMapperExtension<T> {

    protected Node getFragment(T source) {
        return ((Document) source).getDocumentElement();
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
                Map<Field, List<Object>> arrayFields =
                        new Hashtable<Field, List<Object>>();
                Map<Method, List<Object>> arraySetters =
                        new Hashtable<Method, List<Object>>();
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
                            + fieldName
                            + "> in Java class <"
                            + javaType.getName() + ">");
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

}
