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
package org.apache.tuscany.implementation.java.introspection;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.apache.tuscany.assembly.AssemblyFactory;
import org.apache.tuscany.assembly.impl.DefaultAssemblyFactory;
import org.apache.tuscany.implementation.java.impl.Parameter;
import org.apache.tuscany.implementation.java.impl.JavaImplementationDefinition;
import org.apache.tuscany.interfacedef.java.introspection.JavaInterfaceProcessorRegistry;
import org.osoa.sca.annotations.Destroy;
import org.osoa.sca.annotations.EagerInit;
import org.osoa.sca.annotations.Init;
import org.osoa.sca.annotations.Reference;

/**
 * A convenience class for annotation processors which alleviates the need to
 * implement unused callbacks
 * 
 * @version $Rev$ $Date$
 */
@EagerInit
public abstract class ImplementationProcessorExtension implements ImplementationProcessor {
    protected AssemblyFactory factory;
    protected IntrospectionRegistry registry;
    protected JavaInterfaceProcessorRegistry interfaceProcessorRegistry;

    public ImplementationProcessorExtension() {
        super();
        this.factory = new DefaultAssemblyFactory();
    }
    
    /**
     * @param registry
     */
    public ImplementationProcessorExtension(IntrospectionRegistry registry) {
        super();
        this.registry = registry;
        this.factory = new DefaultAssemblyFactory();
    }

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

    public <T> void visitClass(Class<T> clazz, JavaImplementationDefinition type) throws ProcessingException {
    }

    public <T> void visitSuperClass(Class<T> clazz, JavaImplementationDefinition type) throws ProcessingException {
    }

    public void visitMethod(Method method, JavaImplementationDefinition type) throws ProcessingException {
    }

    public <T> void visitConstructor(Constructor<T> constructor, JavaImplementationDefinition type) throws ProcessingException {
    }

    public void visitField(Field field, JavaImplementationDefinition type) throws ProcessingException {
    }

    public <T> void visitEnd(Class<T> clazz, JavaImplementationDefinition type) throws ProcessingException {
    }

    public void visitConstructorParameter(Parameter parameter, JavaImplementationDefinition type) throws ProcessingException {
    }
}
