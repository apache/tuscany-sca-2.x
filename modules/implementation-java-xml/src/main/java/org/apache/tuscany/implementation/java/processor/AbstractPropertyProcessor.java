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
package org.apache.tuscany.implementation.java.processor;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Map;

import org.apache.tuscany.assembly.Property;
import org.apache.tuscany.idl.util.JavaXMLMapper;
import org.apache.tuscany.implementation.java.impl.JavaElement;
import org.apache.tuscany.implementation.java.impl.JavaImplementationDefinition;
import org.apache.tuscany.implementation.java.impl.Parameter;
import org.apache.tuscany.implementation.java.introspection.DuplicatePropertyException;
import org.apache.tuscany.implementation.java.introspection.IllegalPropertyException;
import org.apache.tuscany.implementation.java.introspection.ImplementationProcessorExtension;
import org.apache.tuscany.implementation.java.introspection.ProcessingException;

/**
 * Base class for ImplementationProcessors that handle annotations that add
 * Properties.
 * 
 * @version $Rev$ $Date$
 */
public abstract class AbstractPropertyProcessor<A extends Annotation> extends ImplementationProcessorExtension {
    private final Class<A> annotationClass;

    protected AbstractPropertyProcessor(Class<A> annotationClass) {
        this.annotationClass = annotationClass;
    }

    public void visitMethod(Method method, JavaImplementationDefinition type) throws ProcessingException {
        A annotation = method.getAnnotation(annotationClass);
        if (annotation == null) {
            return;
        }

        if (!Void.TYPE.equals(method.getReturnType())) {
            throw new IllegalPropertyException("Method does not have void return type", method);
        }
        Class[] paramTypes = method.getParameterTypes();
        if (paramTypes.length != 1) {
            throw new IllegalPropertyException("Method must have a single parameter", method);
        }

        String name = getName(annotation);
        if (name == null || "".equals(name)) {
            name = method.getName();
            if (name.startsWith("set")) {
                name = JavaIntrospectionHelper.toPropertyName(method.getName());
            }
        }

        Map<String, JavaElement> properties = type.getPropertyMembers();
        if (properties.containsKey(name)) {
            throw new DuplicatePropertyException(name);
        }

        JavaElement element = new JavaElement(method, 0);
        Property property = createProperty(name, element);

        // add databinding available as annotations, as extensions

        initProperty(property, annotation);
        type.getProperties().add(property);
        properties.put(name, element);
    }

    public void visitField(Field field, JavaImplementationDefinition type) throws ProcessingException {

        A annotation = field.getAnnotation(annotationClass);
        if (annotation == null) {
            return;
        }

        String name = getName(annotation);
        if (name == null) {
            name = "";
        }
        if ("".equals(name) || name.equals(field.getType().getName())) {
            name = field.getName();
        }

        Map<String, JavaElement> properties = type.getPropertyMembers();
        if (properties.containsKey(name)) {
            throw new DuplicatePropertyException(name);
        }

        JavaElement element = new JavaElement(field);
        Property property = createProperty(name, element);
        initProperty(property, annotation);
        type.getProperties().add(property);
        properties.put(name, element);    
    }

    public void visitConstructorParameter(Parameter parameter, JavaImplementationDefinition type)
        throws ProcessingException {

        Map<String, JavaElement> properties = type.getPropertyMembers();
        A annotation = parameter.getAnnotation(annotationClass);
        if (annotation != null) {
            String name = getName(annotation);
            if (name == null) {
                name = parameter.getType().getName();
            }
            if (!"".equals(name) && !"".equals(parameter.getName()) && !name.equals(parameter.getName())) {
                throw new InvalidConstructorException("Mismatched property name: " + parameter);
            }
            if ("".equals(name) && "".equals(parameter.getName())) {
                throw new InvalidPropertyException("Missing property name: " + parameter);
            }
            if ("".equals(name)) {
                name = parameter.getName();
            }

            if (properties.containsKey(name)) {
                throw new DuplicatePropertyException("Duplication property: " + name);
            }
            parameter.setName(name);
            parameter.setClassifer(annotationClass);
            Property property = createProperty(name, parameter);
            initProperty(property, annotation);
            type.getProperties().add(property);
            properties.put(name, parameter);
        }
    }

    protected abstract String getName(A annotation);

    protected abstract void initProperty(Property property, A annotation) throws ProcessingException;

    @SuppressWarnings("unchecked")
    protected  Property createProperty(String name, JavaElement element) throws ProcessingException {

        Property property = factory.createProperty();
        property.setName(name);
        Class<?> baseType = JavaIntrospectionHelper.getBaseType(element.getType(), element.getGenericType());
        property.setXSDType(JavaXMLMapper.getXMLType(baseType));

        Class<?> javaType = element.getType();
        if (javaType.isArray() || Collection.class.isAssignableFrom(javaType)) {
            property.setMany(true);
        }
        return property;

    }

}
