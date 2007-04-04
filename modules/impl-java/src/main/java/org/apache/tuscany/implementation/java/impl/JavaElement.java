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

package org.apache.tuscany.implementation.java.impl;

import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;

/**
 * This class represents a java element such as a Package, Class, Constructor, Field, Method or
 * Parameter.
 * 
 * @version $Rev$ $Date$
 */
public class JavaElement {
    private AnnotatedElement anchor;
    private ElementType elementType;
    private Class<?> type;
    private Type genericType;
    private int index = -1;
    private String name;

    public JavaElement(Package pkg) {
        this.anchor = pkg;
        this.elementType = ElementType.PACKAGE;
        this.name = pkg.getName();
    }

    public JavaElement(Class<?> cls) {
        this.anchor = cls;
        this.elementType = ElementType.TYPE;
        this.type = cls;
        this.genericType = cls;
        this.name = cls.getName();
    }

    public JavaElement(Field field) {
        this.anchor = field;
        this.elementType = ElementType.FIELD;
        this.type = field.getType();
        this.genericType = field.getGenericType();
        this.name = field.getName();
    }

    public JavaElement(Method method) {
        this.anchor = method;
        this.elementType = ElementType.METHOD;
        this.type = method.getReturnType();
        this.genericType = method.getGenericReturnType();
        this.name = method.getName();
    }

    public JavaElement(Constructor<?> constructor) {
        this.anchor = constructor;
        this.elementType = ElementType.CONSTRUCTOR;
        this.type = null;
        this.genericType = null;
        this.name = constructor.getName();
    }

    public JavaElement(Constructor<?> constructor, int index) {
        this.anchor = constructor;
        this.elementType = ElementType.PARAMETER;
        this.type = constructor.getParameterTypes()[index];
        this.genericType = constructor.getGenericParameterTypes()[index];
        this.index = index;
        this.name = "";
    }

    public JavaElement(Method method, int index) {
        this.anchor = method;
        this.elementType = ElementType.PARAMETER;
        this.type = method.getParameterTypes()[index];
        this.genericType = method.getGenericParameterTypes()[index];
        this.index = index;
        this.name = "";
    }

    /**
     * @return the anchor
     */
    public AnnotatedElement getAnchor() {
        return anchor;
    }

    /**
     * @return the elementType
     */
    public ElementType getElementType() {
        return elementType;
    }

    /**
     * @return the genericType
     */
    public Type getGenericType() {
        return genericType;
    }

    /**
     * @return the index
     */
    public int getIndex() {
        return index;
    }

    /**
     * @return the type
     */
    public Class<?> getType() {
        return type;
    }

    public Annotation[] getAnnotations() {
        if (elementType == ElementType.PARAMETER) {
            if (anchor instanceof Method) {
                // We only care about the method-level annotaions
                return ((Method)anchor).getAnnotations();
            }
            if (anchor instanceof Constructor) {
                return ((Constructor)anchor).getParameterAnnotations()[index];
            }
        }
        return anchor.getAnnotations();
    }

    public <T extends Annotation> T getAnnotation(Class<T> annotationType) {
        for (Annotation a : getAnnotations()) {
            if (a.annotationType() == annotationType) {
                return annotationType.cast(a);
            }
        }
        return null;
    }

    public String toString() {
        return anchor.toString() + (elementType == ElementType.PARAMETER ? "[" + index + "]" : "");
    }

    @Override
    public int hashCode() {
        final int PRIME = 31;
        int result = 1;
        result = PRIME * result + ((anchor == null) ? 0 : anchor.hashCode());
        result = PRIME * result + index;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final JavaElement other = (JavaElement)obj;
        if (anchor == null) {
            if (other.anchor != null) {
                return false;
            }
        } else if (!anchor.equals(other.anchor)) {
            return false;
        }
        if (index != other.index) {
            return false;
        }
        return true;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

}
