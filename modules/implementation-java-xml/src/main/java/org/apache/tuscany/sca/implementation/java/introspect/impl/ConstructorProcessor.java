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

import org.apache.tuscany.implementation.java.JavaImplementation;
import org.apache.tuscany.implementation.java.impl.JavaConstructorImpl;
import org.apache.tuscany.implementation.java.impl.JavaParameterImpl;
import org.apache.tuscany.implementation.java.introspect.IntrospectionException;
import org.apache.tuscany.sca.assembly.AssemblyFactory;

/**
 * Handles processing of a constructor decorated with
 * {@link org.osoa.sca.annotations.Constructor}
 * 
 * @version $Rev$ $Date$
 */
@SuppressWarnings("unchecked")
public class ConstructorProcessor extends BaseJavaClassVisitor {
    
    public ConstructorProcessor(AssemblyFactory factory) {
        super(factory);
    }

    public <T> void visitClass(Class<T> clazz, JavaImplementation type) throws IntrospectionException {
        Constructor[] ctors = clazz.getConstructors();
        boolean found = false;
        for (Constructor constructor : ctors) {
            JavaConstructorImpl<?> definition = new JavaConstructorImpl(constructor);
            type.getConstructors().put(constructor, definition);
            if (constructor.getAnnotation(org.osoa.sca.annotations.Constructor.class) != null) {
                if (found) {
                    throw new DuplicateConstructorException("Multiple constructors marked with @Constructor", constructor);
                }
                found = true;
                type.setConstructor(definition);
            }
        }
    }

    public <T> void visitConstructor(Constructor<T> constructor, JavaImplementation type)
        throws IntrospectionException {
        org.osoa.sca.annotations.Constructor annotation = constructor
            .getAnnotation(org.osoa.sca.annotations.Constructor.class);
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
            throw new InvalidConstructorException("Invalid Nubmer of names in @Constructor");
        }
        for (int i = 0; i < parameters.length; i++) {
            parameters[i].setName(i < value.length ? value[i] : "");
        }
        type.setConstructor(definition);
    }
}
