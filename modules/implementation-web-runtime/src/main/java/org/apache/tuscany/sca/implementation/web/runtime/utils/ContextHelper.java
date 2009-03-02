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

package org.apache.tuscany.sca.implementation.web.runtime.utils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import javax.servlet.ServletContext;

import org.apache.tuscany.sca.assembly.ComponentProperty;
import org.apache.tuscany.sca.runtime.RuntimeComponent;
import org.oasisopen.sca.ComponentContext;
import org.oasisopen.sca.ServiceReference;
import org.oasisopen.sca.ServiceRuntimeException;
import org.oasisopen.sca.annotation.ComponentName;
import org.oasisopen.sca.annotation.Context;
import org.oasisopen.sca.annotation.Property;
import org.oasisopen.sca.annotation.Reference;

public class ContextHelper {

    public static final String COMPONENT_ATTR = "org.apache.tuscany.sca.implementation.web.RuntimeComponent";

    public static ComponentContext getComponentContext(ServletContext sc) {
        RuntimeComponent rc = (RuntimeComponent)sc.getAttribute(COMPONENT_ATTR);
        return rc.getComponentContext();
    }

    public static <T> T getReference(String name, Class<T> type, ServletContext sc) {
        ServiceReference<T> sr = getComponentContext(sc).getServiceReference(type, name);
        if (sr == null) {
            throw new ServiceRuntimeException("Reference '" + name + "' undefined");
        }
        return sr.getService();
    }

    public static Object getProperty(String name, ServletContext sc) {
        RuntimeComponent rc = (RuntimeComponent)sc.getAttribute(COMPONENT_ATTR);
        for (ComponentProperty p : rc.getProperties()) {
            if (name.equals(p.getName())) {
                return p.getValue();
            }
        }
        return null;
    }

    public static void inject(Object instance, ServletContext sc) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {

        Class<?> clazz = instance.getClass();
        for (Field field : clazz.getDeclaredFields()) {
            if (field.isAnnotationPresent(Reference.class)) {
                Reference ref = field.getAnnotation(Reference.class);
                String name = ref.name() != null && !ref.name().equals("") ? ref.name() : field.getName();
                Object value = getReference(name, field.getType(), sc);
                field.set(instance, value);
            } else if (field.isAnnotationPresent(Property.class)) {
                Property prop = field.getAnnotation(Property.class);
                String name = prop.name() != null && !prop.name().equals("") ? prop.name() : field.getName();
                Object value = getProperty(name, sc);
                field.set(instance, value);
            } else if (field.isAnnotationPresent(ComponentName.class)) {
                RuntimeComponent rc = (RuntimeComponent)sc.getAttribute(COMPONENT_ATTR);
                field.set(instance, rc.getName());
            } else if (field.isAnnotationPresent(Context.class)) {
                field.set(instance, getComponentContext(sc));
            }
        }

        for (Method method : clazz.getDeclaredMethods()) {
            if (!method.getName().startsWith("set") || method.getParameterTypes().length != 1) {
                continue;
            }
            String targetName = method.getName().substring(3);
            Class<?> type = method.getParameterTypes()[0];

            if (method.isAnnotationPresent(Reference.class)) {
                Reference ref = method.getAnnotation(Reference.class);
                String name = ref.name() != null && !ref.name().equals("") ? ref.name() : targetName;
                Object value = getReference(name, type, sc);
                method.invoke(instance, new Object[] {value});
            } else if (method.isAnnotationPresent(Property.class)) {
                Property prop = method.getAnnotation(Property.class);
                String name = prop.name() != null && !prop.name().equals("") ? prop.name() : targetName;
                Object value = getProperty(name, sc);
                method.invoke(instance, new Object[] {value});
            } else if (method.isAnnotationPresent(ComponentName.class)) {
                RuntimeComponent rc = (RuntimeComponent)sc.getAttribute(COMPONENT_ATTR);
                method.invoke(instance, new Object[] {rc.getName()});
            } else if (method.isAnnotationPresent(Context.class)) {
                method.invoke(instance, new Object[] {getComponentContext(sc)});
            }
        }
    }

}
