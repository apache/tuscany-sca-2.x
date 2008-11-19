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
package org.apache.tuscany.sca.implementation.java.introspect.impl;

import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.tuscany.sca.assembly.AssemblyFactory;
import org.apache.tuscany.sca.assembly.Property;
import org.apache.tuscany.sca.implementation.java.IntrospectionException;
import org.apache.tuscany.sca.implementation.java.JavaImplementation;
import org.apache.tuscany.sca.implementation.java.impl.JavaElementImpl;
import org.apache.tuscany.sca.implementation.java.impl.JavaParameterImpl;
import org.apache.tuscany.sca.interfacedef.util.JavaXMLMapper;

/**
 * Base class for ImplementationProcessors that handle annotations that add
 * Properties.
 * 
 * @version $Rev$ $Date$
 */
public abstract class AbstractPropertyProcessor<A extends Annotation> extends BaseJavaClassVisitor {
    private final Class<A> annotationClass;

    protected AbstractPropertyProcessor(AssemblyFactory assemblyFactory, Class<A> annotationClass) {
        super(assemblyFactory);
        this.annotationClass = annotationClass;
    }
    
    private boolean removeProperty(JavaElementImpl prop, JavaImplementation type) {
        if(prop==null) {
            return false;
        }
        List<Property> props = type.getProperties();
        for(int i=0;i<props.size();i++) {
            if(props.get(i).getName().equals(prop.getName())) {
                props.remove(i);
                return true;
            }
        }
        return false;
    }

    @Override
    public void visitMethod(Method method, JavaImplementation type) throws IntrospectionException {
        A annotation = method.getAnnotation(annotationClass);
        if (annotation == null) {
            return;
        }

        if (!JavaIntrospectionHelper.isSetter(method)) {
            throw new IllegalPropertyException("Annotated method is not a setter: " + method, method);
        }

        String name = getName(annotation);
        if (name == null || "".equals(name)) {
            name = method.getName();
            if (name.startsWith("set")) {
                name = JavaIntrospectionHelper.toPropertyName(method.getName());
            }
        }

        Map<String, JavaElementImpl> properties = type.getPropertyMembers();
        JavaElementImpl prop = properties.get(name);
        // Setter override field
        if (prop != null && prop.getElementType() != ElementType.FIELD) {
            throw new DuplicatePropertyException(name);
        }

        removeProperty(prop, type);
        
        JavaElementImpl element = new JavaElementImpl(method, 0);
        Property property = createProperty(name, element);

        // add databinding available as annotations, as extensions

        initProperty(property, annotation);
        type.getProperties().add(property);
        properties.put(name, element);
    }

    @Override
    public void visitField(Field field, JavaImplementation type) throws IntrospectionException {

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

        Map<String, JavaElementImpl> properties = type.getPropertyMembers();
        JavaElementImpl prop = properties.get(name);
        // Setter override field
        if (prop != null && prop.getElementType() == ElementType.FIELD) {
            throw new DuplicatePropertyException(name);
        }

        if (prop == null) {
            JavaElementImpl element = new JavaElementImpl(field);
            Property property = createProperty(name, element);
            initProperty(property, annotation);
            type.getProperties().add(property);
            properties.put(name, element);
        }
    }

    @Override
    public void visitConstructorParameter(JavaParameterImpl parameter, JavaImplementation type)
        throws IntrospectionException {

        Map<String, JavaElementImpl> properties = type.getPropertyMembers();
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

            JavaElementImpl prop = properties.get(name);
            // Setter override field
            if (prop != null && prop.getElementType() != ElementType.FIELD) {
                throw new DuplicatePropertyException(name);
            }
            removeProperty(prop, type);
            
            parameter.setName(name);
            parameter.setClassifer(annotationClass);
            Property property = createProperty(name, parameter);
            initProperty(property, annotation);
            type.getProperties().add(property);
            properties.put(name, parameter);
        }
    }

    protected abstract String getName(A annotation);

    protected abstract void initProperty(Property property, A annotation) throws IntrospectionException;

    @SuppressWarnings("unchecked")
    protected Property createProperty(String name, JavaElementImpl element) throws IntrospectionException {

        Property property = assemblyFactory.createProperty();
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
