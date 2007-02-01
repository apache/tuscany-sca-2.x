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
package org.apache.tuscany.spi.implementation.java;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Map;

import org.apache.tuscany.api.annotation.DataType;
import org.apache.tuscany.spi.component.CompositeComponent;
import org.apache.tuscany.spi.databinding.DataBinding;
import org.apache.tuscany.spi.deployer.DeploymentContext;

/**
 * Base class for ImplementationProcessors that handle annotations that add Properties.
 *
 * @version $Rev$ $Date$
 */
public abstract class AbstractPropertyProcessor<A extends Annotation> extends ImplementationProcessorExtension {
    private final Class<A> annotationClass;
    private ImplementationProcessorService service;

    protected AbstractPropertyProcessor(Class<A> annotationClass, ImplementationProcessorService service) {
        this.annotationClass = annotationClass;
        this.service = service;
    }

    public void visitMethod(CompositeComponent parent,
                            Method method,
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
        Class<?> javaType = paramTypes[0];

        String name = getName(annotation);
        if (name == null || name.length() == 0) {
            name = method.getName();
            if (name.startsWith("set")) {
                name = toPropertyName(method.getName());
            }
        }

        Map<String, JavaMappedProperty<?>> properties = type.getProperties();
        if (properties.containsKey(name)) {
            throw new DuplicatePropertyException(name);
        }

        Class<?> baseType = getBaseType(javaType, method.getGenericParameterTypes()[0]);
        JavaMappedProperty<?> property = createProperty(name, baseType, method);
        if (javaType.isArray() || Collection.class.isAssignableFrom(javaType)) {
            property.setMany(true);
        }
        
        //add databinding available as annotations, as extensions
        DataType propertyDataBinding = method.getAnnotation(DataType.class);
        if (propertyDataBinding != null) {
            property.getExtensions().put(DataBinding.class.getName(), propertyDataBinding.name());
        }
        initProperty(property, annotation, parent, context);
        properties.put(name, property);
    }

    public void visitField(CompositeComponent parent,
                           Field field,
                           PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>> type,
                           DeploymentContext context) throws ProcessingException {
        
        A annotation = field.getAnnotation(annotationClass);
        if (annotation == null) {
            return;
        }

        Class<?> javaType = field.getType();

        String name = getName(annotation);
        if (name == null || name.length() == 0) {
            name = field.getName();
        }

        Map<String, JavaMappedProperty<?>> properties = type.getProperties();
        if (properties.containsKey(name)) {
            throw new DuplicatePropertyException(name);
        }

        Class<?> baseType = getBaseType(javaType, field.getGenericType());
        JavaMappedProperty<?> property = createProperty(name, baseType, field);
        if (javaType.isArray() || Collection.class.isAssignableFrom(javaType)) {
            property.setMany(true);
        }        
        
        //add databinding available as annotations, as extensions
        DataType propertyDataBinding = field.getAnnotation(DataType.class);
        if (propertyDataBinding != null) {
            property.getExtensions().put(DataBinding.class.getName(), propertyDataBinding.name());
        }

        initProperty(property, annotation, parent, context);
        properties.put(name, property);
    }

    public <T> void visitConstructor(CompositeComponent parent, Constructor<T> constructor,
                                     PojoComponentType<JavaMappedService,
                                         JavaMappedReference, JavaMappedProperty<?>> type,
                                     DeploymentContext context) throws ProcessingException {

        ConstructorDefinition<?> definition = type.getConstructorDefinition();
        Class[] params = constructor.getParameterTypes();
        Map<String, JavaMappedProperty<?>> properties = type.getProperties();
        Annotation[][] annotations = constructor.getParameterAnnotations();
        for (int i = 0; i < params.length; i++) {
            Class<?> param = params[i];
            Annotation[] paramAnnotations = annotations[i];
            JavaMappedProperty<?> property = null;
            DataType propertyDataBinding = null;
            A monitorAnnot = null;
            String name = null;
            for (Annotation annotation : paramAnnotations) {
                if (annotation.annotationType().equals(annotationClass)) {
                    if (definition == null) {
                        definition = new ConstructorDefinition<T>(constructor);
                        type.setConstructorDefinition(definition);
                    }
                    monitorAnnot = annotationClass.cast(annotation);
                    name = getName(monitorAnnot);
                    if (name == null || name.length() == 0) {
                        name = param.getName();
                    }
                    
                    Class<?> baseType = getBaseType(param, constructor.getGenericParameterTypes()[i]);
                    property = createProperty(name, baseType, constructor);
                    if (param.isArray() || Collection.class.isAssignableFrom(param)) {
                        property.setMany(true);
                    }
                } else if (annotation.annotationType().equals(DataType.class)) {
                    propertyDataBinding = DataType.class.cast(annotation);
                }
            }
            //if there has been a databinding annotation along with a property annotation then 
            //add that information to the property
            if (property != null && propertyDataBinding != null) {
                if (propertyDataBinding != null) {
                    property.getExtensions().put(DataBinding.class.getName(), propertyDataBinding.name());
                }
                initProperty(property, monitorAnnot, parent, context);
                properties.put(name, property);
                service.addName(definition.getInjectionNames(), i, name);
            }
        }
    }

    protected abstract String getName(A annotation);

    protected <T> void initProperty(JavaMappedProperty<T> property,
                                    A annotation,
                                    CompositeComponent parent,
                                    DeploymentContext context) throws ProcessingException {
    }

    protected <T> JavaMappedProperty<T> createProperty(String name, Class<T> javaType, Member member)
        throws ProcessingException {
        return new JavaMappedProperty<T>(name, null, javaType, member);
    }


    public static String toPropertyName(String name) {
        if (!name.startsWith("set")) {
            return name;
        }
        return Character.toLowerCase(name.charAt(3)) + name.substring(4);
    }
    
}
