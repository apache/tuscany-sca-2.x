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

package org.apache.tuscany.sca.binding.corba.impl.util;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * @version $Rev$ $Date$
 * Utility for finding method in given class.
 */
public class MethodFinder {

    private static Map<Class<?>, Class<?>> boxingMapping;

    static {
        boxingMapping = new HashMap<Class<?>, Class<?>>();
        boxingMapping.put(boolean.class, Boolean.class);
        boxingMapping.put(byte.class, Byte.class);
        boxingMapping.put(short.class, Short.class);
        boxingMapping.put(char.class, Character.class);
        boxingMapping.put(int.class, Integer.class);
        boxingMapping.put(long.class, Long.class);
        boxingMapping.put(float.class, Float.class);
        boxingMapping.put(double.class, Double.class);
    }

    /**
     * Converts primitive class to its object equivalent.
     * 
     * @param parameter class to convert
     * @return object equivalent for primitive type. If parameter wasn't
     *         primitive then returns parameter.
     */
    private static Class<?> normalizePrimitive(Class<?> parameter) {
        Class<?> result = boxingMapping.get(parameter);
        if (result != null) {
            return result;
        } else {
            // not a primitive - no need to normalize
            return parameter;
        }
    }

    /**
     * Finds appropriate method. This method ignores difference between
     * primitive types and theirs object equivalents. Ie. if we want to find
     * method "get" in java.util.List with only one parameter, which type is
     * Integer then we'll obtain method get(int).
     * 
     * @param forClass class which possibly contains desired method 
     * @param methodName desired methods name
     * @param parameterTypes desired methods parameter types
     * @return desired method, if no method was found then null will be returned
     */
    public static Method findMethod(Class<?> forClass, String methodName, Class<?>[] parameterTypes) {
        Method[] methods = forClass.getMethods();
        for (int i = 0; i < methods.length; i++) {
            if (methods[i].getName().equals(methodName)) {
                Class<?>[] methodPTypes = methods[i].getParameterTypes();
                if (methodPTypes.length == parameterTypes.length) {
                    boolean parameterMatch = true;
                    for (int j = 0; j < methodPTypes.length; j++) {
                        Class<?> nMethodPType = normalizePrimitive(methodPTypes[j]);
                        Class<?> nParameterType = normalizePrimitive(parameterTypes[j]);
                        if (!nMethodPType.equals(nParameterType)) {
                            parameterMatch = false;
                            break;
                        }
                    }
                    if (parameterMatch) {
                        return methods[i];
                    }
                }
            }
        }
        return null;
    }
}
