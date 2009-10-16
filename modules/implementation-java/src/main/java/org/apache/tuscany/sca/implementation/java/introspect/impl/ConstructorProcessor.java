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

import java.lang.reflect.Constructor;

import org.apache.tuscany.sca.assembly.AssemblyFactory;
import org.apache.tuscany.sca.implementation.java.IntrospectionException;
import org.apache.tuscany.sca.implementation.java.JavaConstructorImpl;
import org.apache.tuscany.sca.implementation.java.JavaImplementation;
import org.apache.tuscany.sca.implementation.java.JavaParameterImpl;
import org.apache.tuscany.sca.implementation.java.introspect.BaseJavaClassVisitor;
import org.oasisopen.sca.annotation.Property;
import org.oasisopen.sca.annotation.Reference;

/**
 * Handles processing of a constructor decorated with
 * {@link org.oasisopen.sca.annotation.Constructor}
 * 
 * @version $Rev$ $Date$
 */
@SuppressWarnings("unchecked")
public class ConstructorProcessor extends BaseJavaClassVisitor {
    
    public ConstructorProcessor(AssemblyFactory factory) {
        super(factory);
    }

    @Override
    public <T> void visitClass(Class<T> clazz, JavaImplementation type) throws IntrospectionException {
        Constructor[] ctors = clazz.getConstructors();
        boolean found = false;
        for (Constructor constructor : ctors) {
            JavaConstructorImpl<?> definition = new JavaConstructorImpl(constructor);
            type.getConstructors().put(constructor, definition);
            if (constructor.getAnnotation(org.oasisopen.sca.annotation.Constructor.class) != null) {
                if (found) {
                    throw new DuplicateConstructorException("Multiple constructors marked with @Constructor", constructor);
                }
                found = true;
                type.setConstructor(definition);
            }
        }
    }

    @Override
    public <T> void visitConstructor(Constructor<T> constructor, JavaImplementation type)
        throws IntrospectionException {
        org.oasisopen.sca.annotation.Constructor annotation = constructor
            .getAnnotation(org.oasisopen.sca.annotation.Constructor.class);
        if (annotation == null) {
            return;
        }
        JavaConstructorImpl<?> definition = type.getConstructor();
        if (definition == null) {
            definition = new JavaConstructorImpl(constructor);
            type.setConstructor(definition);
        }
        JavaParameterImpl[] parameters = definition.getParameters();
        String[] value = annotation.value();
        boolean isDefault = value.length == 0 || (value.length == 1 && "".equals(value[0]));
        if (!isDefault && value.length != parameters.length) {
            throw new InvalidConstructorException("Invalid Number of names in @Constructor");
        }
        
        for (JavaParameterImpl p : parameters) {
            if (!hasAnnotation(p)) {
                throw new InvalidConstructorException("JCA90003 constructor parameters must have @Property or @Reference annotation");
            }
        }

        for (int i = 0; i < parameters.length; i++) {
            parameters[i].setName(i < value.length ? value[i] : "");
        }
        type.setConstructor(definition);
    }

    private boolean hasAnnotation(JavaParameterImpl p) {
        if (p.getAnnotations() != null && p.getAnnotations().length > 0) {
            return true;
        }
// TODO: need to verify JCA90003 as it seems like any annotation should be ok not just SCA ref or prop        
//        if (p.getAnnotation(Reference.class) != null) {
//            return true;
//        }
//        if (p.getAnnotation(Property.class) != null) {
//            return true;
//        }
        return false;
    }
}
