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

import java.lang.reflect.Method;

/**
 *         <p/>
 *         TODO To change the template for this generated type comment go to
 *         Window - Preferences - Java - Code Style - Code Templates
 */
public interface JavaReflector {

    /**
     * Load a class
     *
     * @param className
     * @return
     * @throws Exception
     */
    Class getClassForName(String className) throws Exception;

    /**
     * Returns the fully qualified name of a type
     */
    String getFullyQualifiedClassName(Class clazz);

    /**
     * Returns the name of a type
     */
    String getClassName(Class clazz);

    /**
     * Returns the exception types of a method.
     *
     * @param method
     * @return
     */
    Class[] getMethodExceptionTypes(Method method);

    /**
     * Returns the name of a method.
     *
     * @param method
     * @return
     */
    String getMethodName(Method method);

    /**
     * Returns the types of the parameters of a method.
     *
     * @param method
     * @return
     */
    Class[] getMethodParameterTypes(Method method);

    /**
     * Returns the return type of a method.
     *
     * @param method
     * @return
     */
    Class getMethodReturnType(Method method);

    /**
     * Get the methods of the given class.
     *
     * @param clazz
     * @return
     */
    Method[] getMethods(Class clazz);

    /**
     * Returns the package name for a type
     *
     * @param type
     * @return
     */
    String getPackageName(Class clazz);

    /**
     * Returns true if a type is serializable
     *
     * @param type
     * @return
     */
    boolean isClassSerializable(Class clazz);

    /**
     * Returns true if a type is a DataObject type
     *
     * @param clazz
     * @return
     */
    boolean isDataObjectClass(Class clazz);

}

