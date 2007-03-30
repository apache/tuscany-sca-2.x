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
package org.apache.tuscany.core.implementation.processor;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Map;

import org.apache.tuscany.api.annotation.DataType;
import org.apache.tuscany.core.util.JavaIntrospectionHelper;
import org.apache.tuscany.spi.deployer.DeploymentContext;
import org.apache.tuscany.spi.implementation.java.DuplicatePropertyException;
import org.apache.tuscany.spi.implementation.java.IllegalPropertyException;
import org.apache.tuscany.spi.implementation.java.ImplementationProcessorExtension;
import org.apache.tuscany.spi.implementation.java.JavaElement;
import org.apache.tuscany.spi.implementation.java.JavaMappedProperty;
import org.apache.tuscany.spi.implementation.java.JavaMappedReference;
import org.apache.tuscany.spi.implementation.java.JavaMappedService;
import org.apache.tuscany.spi.implementation.java.Parameter;
import org.apache.tuscany.spi.implementation.java.PojoComponentType;
import org.apache.tuscany.spi.implementation.java.ProcessingException;

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

    public void visitMethod(Method method,
                            PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>> type,
                            DeploymentContext context) throws ProcessingException {
        A annotation = method.getAnnotation(annotationClass);
        if (annotation == null) {
            return;
        }

        if (!Void.TYPE.equals(method.getReturnType())) {
            throw new IllegalPropertyException("Method does not have void return type", method.toString());
        }
        Class[] paramTypes = method.getParameterTypes();
        if (paramTypes.length != 1) {
            throw new IllegalPropertyException("Method must have a single parameter", method.toString());
        }

        String name = getName(annotation);
        if (name == null || "".equals(name)) {
            name = method.getName();
            if (name.startsWith("set")) {
                name = JavaIntrospectionHelper.toPropertyName(method.getName());
            }
        }

        Map<String, JavaMappedProperty<?>> properties = type.getProperties();
        if (properties.containsKey(name)) {
            throw new DuplicatePropertyException(name);
        }

        JavaElement element = new JavaElement(method, 0);
        JavaMappedProperty<?> property = createProperty(name, element);

        // add databinding available as annotations, as extensions
        DataType propertyDataBinding = method.getAnnotation(DataType.class);
        if (propertyDataBinding != null) {
            property.getExtensions().put("databinding", propertyDataBinding.name());
        }
        initProperty(property, annotation, context);
        properties.put(name, property);
    }

    public void visitField(Field field,
                           PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>> type,
                           DeploymentContext context) throws ProcessingException {

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

        Map<String, JavaMappedProperty<?>> properties = type.getProperties();
        if (properties.containsKey(name)) {
            throw new DuplicatePropertyException(name);
        }

        JavaElement element = new JavaElement(field);
        JavaMappedProperty<?> property = createProperty(name, element);

        // add databinding available as annotations, as extensions
        DataType propertyDataBinding = field.getAnnotation(DataType.class);
        if (propertyDataBinding != null) {
            property.getExtensions().put("databinding", propertyDataBinding.name());
        }

        initProperty(property, annotation, context);
        properties.put(name, property);
    }

    public void visitConstructorParameter(Parameter parameter,
                                          PojoComponentType<JavaMappedService, 
                                          JavaMappedReference, JavaMappedProperty<?>> type,
                                          DeploymentContext context) throws ProcessingException {

        Map<String, JavaMappedProperty<?>> properties = type.getProperties();
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
            JavaMappedProperty<?> property = createProperty(name, parameter);
            initProperty(property, annotation, context);
            properties.put(name, property);
        }
    }

    protected abstract String getName(A annotation);

    protected <T> void initProperty(JavaMappedProperty<T> property, A annotation, DeploymentContext context)
        throws ProcessingException {
    }

    @SuppressWarnings("unchecked")
    protected <T> JavaMappedProperty<T> createProperty(String name, JavaElement element) throws ProcessingException {

        Class<?> baseType = getBaseType(element.getType(), element.getGenericType());
        JavaMappedProperty<T> property = new JavaMappedProperty<T>(name, null, (Class<T>)baseType, (Member)element
            .getAnchor());
        Class<?> javaType = element.getType();
        if (javaType.isArray() || Collection.class.isAssignableFrom(javaType)) {
            property.setMany(true);
        }
        return property;

    }

}
