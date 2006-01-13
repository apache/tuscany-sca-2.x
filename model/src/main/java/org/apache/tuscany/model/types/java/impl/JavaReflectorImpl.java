/**
 *
 *  Copyright 2005 The Apache Software Foundation or its licensors, as applicable.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.apache.tuscany.model.types.java.impl;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.HashMap;
import java.util.Map;

import commonj.sdo.DataObject;

import org.apache.tuscany.common.resource.loader.ResourceLoader;
import org.apache.tuscany.model.assembly.AssemblyConstants;

/**
 * This class is used to reflect Java classes in the runtime environment
 */
public class JavaReflectorImpl implements JavaReflector {

    private final static Map PRIMITIVES = new HashMap();

    static {
        PRIMITIVES.put("boolean", boolean.class);
        PRIMITIVES.put("byte", byte.class);
        PRIMITIVES.put("char", char.class);
        PRIMITIVES.put("short", short.class);
        PRIMITIVES.put("int", int.class);
        PRIMITIVES.put("long", long.class);
        PRIMITIVES.put("float", float.class);
        PRIMITIVES.put("double", double.class);
    }

    private ResourceLoader resourceLoader;

    /**
     * Constructor
     */
    public JavaReflectorImpl(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    /**
     * @see org.apache.tuscany.model.types.java.impl.JavaReflector#getClassForName(java.lang.String)
     */
    public Class getClassForName(final String className) throws Exception {
        try {
            // SECURITY
            return (Class) AccessController.doPrivileged(new PrivilegedExceptionAction() {
                public Object run() throws ClassNotFoundException {
                    String name = className;
                    if (className.startsWith(AssemblyConstants.DEFAULT_JAVA_PACKAGE_NAME + '.'))
                        name = className.substring(AssemblyConstants.DEFAULT_JAVA_PACKAGE_NAME.length() + 1);
                    Class clazz = (Class) PRIMITIVES.get(name);
                    if (clazz == null) {
                        clazz = resourceLoader.loadClass(name);
                    }
                    return clazz;
                }
            });
        } catch (PrivilegedActionException e1) {
            throw e1.getException();
        }
    }

    /**
     * @see org.apache.tuscany.model.types.java.impl.JavaReflector#getFullyQualifiedClassName(java.lang.Object)
     */
    public String getFullyQualifiedClassName(Class clazz) {
        return clazz.getName();
    }

    /**
     * @see org.apache.tuscany.model.types.java.impl.JavaReflector#getClassName(java.lang.Object)
     */
    public String getClassName(Class clazz) {
        if (clazz.isArray())
            return clazz.getName();
        String className = getFullyQualifiedClassName(clazz);
        int index = className.lastIndexOf('.');
        return index == -1 ? className : className.substring(index + 1);
    }

    /**
     * @see org.apache.tuscany.model.types.java.impl.JavaReflector#getPackageName(java.lang.Object)
     */
    public String getPackageName(Class clazz) {
        if (clazz.getPackage() != null) {
            String packageName = clazz.getPackage().getName();
            if (!"".equals(packageName))
                return packageName;
        }
        return AssemblyConstants.DEFAULT_JAVA_PACKAGE_NAME;
    }

    /**
     * @see org.apache.tuscany.model.types.java.impl.JavaReflector#isClassSerializable(java.lang.Object)
     */
    public boolean isClassSerializable(Class clazz) {
        return Serializable.class.isAssignableFrom(clazz);
    }

    /**
     * @see org.apache.tuscany.model.types.java.impl.JavaReflector#isDataObjectClass(java.lang.Object)
     */
    public boolean isDataObjectClass(Class clazz) {

        // First check if the type actually implements commonj.sdo.DataObject
        if (DataObject.class.isAssignableFrom(clazz))
            return true;

        // Then look for a generated implementation class for this interface and check if
        // it implements DataObject
        String packageName = getPackageName(clazz);
        if (packageName != null) {
            String className = getClassName(clazz);
            try {
                Class impl = getClassForName(packageName + ".impl." + className + "Impl");
                if (impl != null)
                    return DataObject.class.isAssignableFrom(impl);
            } catch (Exception e) {
            }
        }
        return false;
    }

    /**
     * @see org.apache.tuscany.model.types.java.impl.JavaReflector#getMethods(java.lang.Object)
     */
    public Method[] getMethods(Class clazz) {
        final Class cls = clazz;
        Method[] methods = (Method[]) AccessController.doPrivileged(new PrivilegedAction() {
            public Object run() {
                return cls.getMethods();
            }
        });
        return methods;
    }

    /**
     * @see org.apache.tuscany.model.types.java.impl.JavaReflector#getMethodName(java.lang.Object)
     */
    public String getMethodName(Method method) {
        return method.getName();
    }

    /**
     * @see org.apache.tuscany.model.types.java.impl.JavaReflector#getMethodParameterTypes(java.lang.Object)
     */
    public Class[] getMethodParameterTypes(Method method) {
        return method.getParameterTypes();
    }

    /**
     * @see org.apache.tuscany.model.types.java.impl.JavaReflector#getMethodReturnType(java.lang.Object)
     */
    public Class getMethodReturnType(Method method) {
        return method.getReturnType();
    }

    /**
     * @see org.apache.tuscany.model.types.java.impl.JavaReflector#getMethodExceptionTypes(java.lang.Object)
     */
    public Class[] getMethodExceptionTypes(Method method) {
        return method.getExceptionTypes();
    }
}