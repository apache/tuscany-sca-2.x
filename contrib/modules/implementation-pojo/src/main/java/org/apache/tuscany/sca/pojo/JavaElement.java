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

import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Type;


/**
 * This class represents a java element such as a Package, Class, Constructor,
 * Field, Method or Parameter.
 * 
 * @version $Rev$ $Date$
 */
public interface JavaElement {

	/**
	 * @return the anchor
	 */
	AnnotatedElement getAnchor();

	/**
	 * @return the elementType
	 */
	ElementType getElementType();

	/**
	 * @return the genericType
	 */
	Type getGenericType();

	/**
	 * @return the index
	 */
	int getIndex();

	/**
	 * @return the type
	 */
	Class<?> getType();

	/**
	 * @return the annotations
	 */
	Annotation[] getAnnotations();

	/**
	 * Return a given annotation
	 * @param annotationType the annotation type
	 * @return the annotation
	 */
	<T extends Annotation> T getAnnotation(Class<T> annotationType);

	/**
	 * @return the name
	 */
	String getName();

	/**
	 * @param name the name to set
	 */
	void setName(String name);

	/**
	 * @return the classifier
	 */
	Class<? extends Annotation> getClassifer();

	/**
	 * @param classifer the classifier to set
	 */
	void setClassifer(Class<? extends Annotation> classifer);

}