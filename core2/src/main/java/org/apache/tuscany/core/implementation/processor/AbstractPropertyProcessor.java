/**
 *
 * Copyright 2006 The Apache Software Foundation or its licensors as applicable
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
package org.apache.tuscany.core.implementation.processor;

import java.lang.reflect.Method;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.annotation.Annotation;
import java.util.Map;

import org.apache.tuscany.core.implementation.ImplementationProcessorSupport;
import org.apache.tuscany.core.implementation.JavaMappedService;
import org.apache.tuscany.core.implementation.JavaMappedReference;
import org.apache.tuscany.core.implementation.JavaMappedProperty;
import org.apache.tuscany.core.implementation.PojoComponentType;
import org.apache.tuscany.core.implementation.ProcessingException;
import org.apache.tuscany.core.util.JavaIntrospectionHelper;
import org.apache.tuscany.spi.component.CompositeComponent;
import org.apache.tuscany.spi.deployer.DeploymentContext;

/**
 * Base class for ImplementationProcessors that handle annotations that add Property's.
 *
 * @version $Rev$ $Date$
 */
public abstract class AbstractPropertyProcessor<A extends Annotation> extends ImplementationProcessorSupport {
    private final Class<A> annotationClass;

    protected AbstractPropertyProcessor(Class<A> annotationClass) {
        this.annotationClass = annotationClass;
    }

    public void visitMethod(CompositeComponent<?> parent,
                            Method method,
                            PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>> type,
                            DeploymentContext context) throws ProcessingException {
        A annotation = method.getAnnotation(annotationClass);
        if (annotation == null) {
            return;
        }

        if (!Void.TYPE.equals(method.getReturnType())) {
            IllegalPropertyException ipe = new IllegalPropertyException("Method does not have void return type");
            ipe.setIdentifier(method.toString());
            throw ipe;
        }
        Class[] paramTypes = method.getParameterTypes();
        if (paramTypes.length != 1) {
            IllegalPropertyException ipe = new IllegalPropertyException("Method must have 1 parameter");
            ipe.setIdentifier(method.toString());
            throw ipe;
        }
        Class<?> javaType = paramTypes[0];

        String name = getName(annotation);
        if (name == null || name.length() == 0) {
            name = method.getName();
            if (name.startsWith("set")) {
                name = JavaIntrospectionHelper.toPropertyName(method.getName());
            }
        }

        Map<String, JavaMappedProperty<?>> properties = type.getProperties();
        if (properties.containsKey(name)) {
            throw new DuplicatePropertyException(name);
        }

        JavaMappedProperty<?> property = createProperty(name, javaType, method);
        initProperty(property, annotation, parent, context);
        properties.put(name, property);
    }

    public void visitField(CompositeComponent<?> parent,
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

        JavaMappedProperty<?> property = createProperty(name, javaType, field);
        initProperty(property, annotation, parent, context);
        properties.put(name, property);
    }

    protected abstract String getName(A annotation);

    protected <T> void initProperty(JavaMappedProperty<T> property, 
                                A annotation,
                                CompositeComponent<?> parent,
                                DeploymentContext context) {
    }

    protected <T> JavaMappedProperty<T> createProperty(String name, Class<T> javaType, Member member) {
        return new JavaMappedProperty<T>(name, null, javaType, member);
    }
}
