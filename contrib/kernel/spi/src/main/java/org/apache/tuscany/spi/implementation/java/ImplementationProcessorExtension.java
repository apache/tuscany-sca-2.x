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

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.Collection;

import org.osoa.sca.annotations.Destroy;
import org.osoa.sca.annotations.EagerInit;
import org.osoa.sca.annotations.Init;
import org.osoa.sca.annotations.Reference;

import org.apache.tuscany.spi.deployer.DeploymentContext;
import org.apache.tuscany.spi.idl.java.JavaInterfaceProcessorRegistry;

/**
 * A convenience class for annotation processors which alleviates the need to
 * implement unused callbacks
 * 
 * @version $Rev$ $Date$
 */
@EagerInit
public abstract class ImplementationProcessorExtension implements ImplementationProcessor {
    protected IntrospectionRegistry registry;
    protected JavaInterfaceProcessorRegistry interfaceProcessorRegistry;

    @Reference
    public void setRegistry(IntrospectionRegistry registry) {
        this.registry = registry;
    }

    @Reference
    public void setInterfaceProcessorRegistry(JavaInterfaceProcessorRegistry interfaceProcessorRegistry) {
        this.interfaceProcessorRegistry = interfaceProcessorRegistry;
    }

    @Init
    public void init() {
        registry.registerProcessor(this);
    }

    @Destroy
    public void destroy() {
        registry.unregisterProcessor(this);
    }

    public <T> void visitClass(Class<T> clazz,
                               PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>> type,
                               DeploymentContext context) throws ProcessingException {
    }

    public <T> void visitSuperClass(Class<T> clazz,
                                    PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>> type,
                                    DeploymentContext context) throws ProcessingException {
    }

    public void visitMethod(Method method,
                            PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>> type,
                            DeploymentContext context) throws ProcessingException {
    }

    public <T> void visitConstructor(Constructor<T> constructor,
                                     PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>> type,
                                     DeploymentContext context) throws ProcessingException {
    }

    public void visitField(Field field,
                           PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>> type,
                           DeploymentContext context) throws ProcessingException {
    }

    public <T> void visitEnd(Class<T> clazz,
                             PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>> type,
                             DeploymentContext context) throws ProcessingException {

    }

    protected static Class<?> getBaseType(Class<?> cls, Type genericType) {
        if (cls.isArray()) {
            return cls.getComponentType();
        } else if (Collection.class.isAssignableFrom(cls)) {
            if (genericType == cls) {
                return Object.class;
            } else {
                ParameterizedType parameterizedType = (ParameterizedType)genericType;
                Type baseType = parameterizedType.getActualTypeArguments()[0];
                if (baseType instanceof Class) {
                    return (Class<?>)baseType;
                } else if (baseType instanceof ParameterizedType) {
                    return (Class<?>)((ParameterizedType)baseType).getRawType();
                } else {
                    return null;
                }
            }
        } else {
            return cls;
        }
    }

    public void visitConstructorParameter(Parameter parameter,
                                          PojoComponentType<JavaMappedService, 
                                          JavaMappedReference, JavaMappedProperty<?>> type,
                                          DeploymentContext context) throws ProcessingException {
    }
}
