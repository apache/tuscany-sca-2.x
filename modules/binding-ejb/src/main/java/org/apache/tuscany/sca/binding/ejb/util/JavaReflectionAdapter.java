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
package org.apache.tuscany.sca.binding.ejb.util;

import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;

import org.osoa.sca.ServiceRuntimeException;

/**
 * An adapter for java classes, indexes the methods by name and provides an
 * invoke method that takes a method name.
 */
public final class JavaReflectionAdapter {

    private static Map<Class, JavaReflectionAdapter> adapters =
        Collections.synchronizedMap(new WeakHashMap<Class, JavaReflectionAdapter>());

    private static final Map<Class, Object> DEFAULT_VALUES = new HashMap<Class, Object>();
    static {
        DEFAULT_VALUES.put(boolean.class, Boolean.FALSE);
        DEFAULT_VALUES.put(byte.class, new Byte((byte)0));
        DEFAULT_VALUES.put(char.class, new Character((char)0));
        DEFAULT_VALUES.put(short.class, new Short((short)0));
        DEFAULT_VALUES.put(int.class, Integer.valueOf(0));
        DEFAULT_VALUES.put(long.class, new Long(0));
        DEFAULT_VALUES.put(float.class, new Float(0.0));
        DEFAULT_VALUES.put(double.class, new Double(0.0));
    }

    private Map<String, Method> methodMap = new HashMap<String, Method>();

    /**
     * Constructor
     * 
     * @param clazz
     */
    private JavaReflectionAdapter(final Class clazz) {
        // Index the methods on the implementation class
        Method[] methods = AccessController.doPrivileged(new PrivilegedAction<Method[]>() {
            public Method[] run() {
                return clazz.getMethods();
            }
        });
        for (int i = 0; i < methods.length; i++) {
            methodMap.put(methods[i].getName(), methods[i]);
        }
    }

    /**
     * Create a java reflection adapter
     * 
     * @param clazz
     */
    public static synchronized JavaReflectionAdapter createJavaReflectionAdapter(Class clazz) {
        JavaReflectionAdapter adapter = (JavaReflectionAdapter)adapters.get(clazz);
        if (adapter == null) {
            adapter = new JavaReflectionAdapter(clazz);
            adapters.put(clazz, adapter);
        }
        return adapter;
    }

    /**
     * Return the specified method
     * 
     * @param methodName
     * @return
     * @throws NoSuchMethodException
     */
    public Method getMethod(String methodName) throws NoSuchMethodException {

        Method method = (Method)methodMap.get(methodName);
        if (method == null) {
            throw new NoSuchMethodException(methodName);
        }
        return method;
    }

    /**
     * Returns a map containing the methods on the class, keyed by name
     * 
     * @return
     */
    public Map getMethods() {
        return methodMap;
    }

    /**
     * Invoke a method using Java reflection.
     * 
     * @param method
     * @param object
     * @param args
     * @return
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    public Object invoke(Method method, Object object, Object[] args) throws InvocationTargetException,
        IllegalAccessException {
        Class[] parameterTypes = method.getParameterTypes();
        for (int i = 0; i < parameterTypes.length; i++) {
            Class parameterType = parameterTypes[i];
            if (args[i] == null && parameterType.isPrimitive()) {
                args[i] = DEFAULT_VALUES.get(parameterType);
            }
        }
        return method.invoke(object, args);
    }

    /**
     * Set the java bean property
     * 
     * @param bean
     * @param propertyName
     * @param value
     * @return
     */
    public boolean setProperty(Object bean, String propertyName, Object value) {
        try {
            PropertyDescriptor propertyDescriptor = new PropertyDescriptor(propertyName, bean.getClass());
            Method writeMethod = propertyDescriptor.getWriteMethod();
            writeMethod.invoke(bean, new Object[] {value});
            return true;
        } catch (InvocationTargetException e) {
            throw new ServiceRuntimeException(e.getTargetException());
        } catch (Exception e) {
            return false;
        }
    }
}
