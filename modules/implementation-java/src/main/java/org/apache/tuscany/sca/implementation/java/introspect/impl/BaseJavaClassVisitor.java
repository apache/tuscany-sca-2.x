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
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.apache.tuscany.sca.assembly.AssemblyFactory;
import org.apache.tuscany.sca.implementation.java.IntrospectionException;
import org.apache.tuscany.sca.implementation.java.JavaImplementation;
import org.apache.tuscany.sca.implementation.java.impl.JavaParameterImpl;
import org.apache.tuscany.sca.implementation.java.introspect.JavaClassVisitor;

/**
 * A convenience class for annotation processors which alleviates the need to
 * implement unused callbacks
 * 
 * @version $Rev$ $Date$
 */
public abstract class BaseJavaClassVisitor implements JavaClassVisitor {
    protected AssemblyFactory assemblyFactory;
    
    protected BaseJavaClassVisitor(AssemblyFactory factory) {
        this.assemblyFactory = factory;
    }

    public <T> void visitClass(Class<T> clazz, JavaImplementation type) throws IntrospectionException {
    }

    public <T> void visitSuperClass(Class<T> clazz, JavaImplementation type) throws IntrospectionException {
    }

    public void visitMethod(Method method, JavaImplementation type) throws IntrospectionException {
    }

    public <T> void visitConstructor(Constructor<T> constructor, JavaImplementation type) throws IntrospectionException {
    }

    public void visitField(Field field, JavaImplementation type) throws IntrospectionException {
    }

    public <T> void visitEnd(Class<T> clazz, JavaImplementation type) throws IntrospectionException {
    }

    public void visitConstructorParameter(JavaParameterImpl parameter, JavaImplementation type) throws IntrospectionException {
    }
}
