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
package org.apache.tuscany.sca.pojo;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Factory for the Java model used during introspection of java class/interfaces
 * 
 * @version $Rev$ $Date$
 */

public interface JavaFactory {

	/**
	 * Create a model representing a java constructor
	 * @param clazz
	 * @return the java constructor
	 */
    JavaConstructor<?> createJavaConstructor(Constructor<?> constructor);
    
    /**
     * Create model representing a java class
     * @param clazz the class 
     * @return the java element representing the class
     */
    JavaElement createJavaElement(Class<?> clazz);
    
    /**
     * Create a model representing a java property 
     * @param field the property
     * @return the java element representing the property
     */
    JavaElement createJavaElement(Field field);
    
    /**
     * Create a model representing a java constructor
     * @param constructor the constructor
     * @param index the constructor position
     * @return the java element representing the constructor
     */
    JavaElement createJavaElement(Constructor<?> constructor, int index);
    
    /**
     * Create a model representing a java method
     * @param method the method
     * @param index the method position
     * @return the java element representing the constructor
     */
    JavaElement createJavaElement(Method method, int index);
    
    /**
     * Create a model representing a java parameter
     * @return the parameter
     */
    JavaParameter createJavaParameter(Constructor<?> constructor, int index);
    
    /**
     * Create a model representing a java resource
     * @return
     */
    JavaResource createJavaResource(JavaElement element);
    
}