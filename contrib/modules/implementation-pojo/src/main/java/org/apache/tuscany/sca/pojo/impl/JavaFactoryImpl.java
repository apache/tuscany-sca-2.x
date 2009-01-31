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
package org.apache.tuscany.sca.pojo.impl;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.apache.tuscany.sca.pojo.JavaConstructor;
import org.apache.tuscany.sca.pojo.JavaElement;
import org.apache.tuscany.sca.pojo.JavaFactory;
import org.apache.tuscany.sca.pojo.JavaParameter;
import org.apache.tuscany.sca.pojo.JavaResource;

/**
 * Factory for the Java model used during introspection of java class/interfaces
 * 
 * @version $Rev$ $Date$
 */

public class JavaFactoryImpl implements JavaFactory {

	public void JavaFactory() {
		
	}
	
	public JavaConstructor<?> createJavaConstructor(Constructor<?> constructor){
    	return new JavaConstructorImpl(constructor);
    }

	public JavaElement createJavaElement(Class<?> clazz) {
		return new JavaElementImpl(clazz);
	}
	
	public JavaElement createJavaElement(Field field){
		return new JavaElementImpl(field);

	}

	public JavaElement createJavaElement(Constructor<?> constructor, int index) {
		return new JavaElementImpl(constructor, index);

	}

	public JavaElement createJavaElement(Method method, int index) {
		return new JavaElementImpl(method, index);
	}

    public JavaParameter createJavaParameter(Constructor<?> constructor, int index) {
    	return new JavaParameterImpl(constructor, index);
    }
    
    public JavaResource createJavaResource(JavaElement element) {
    	return new JavaResourceImpl(element);
    }
    
}

