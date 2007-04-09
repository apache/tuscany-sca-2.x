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

import org.apache.tuscany.spi.deployer.DeploymentContext;

/**
 * Implementations process class-level metadata, typically parsing annotations and updating the corresponding
 * <code>ComponentType</code>. A processor may, for example, create a {@link JavaMappedProperty} which is responsible
 * for injecting a complex type on a component implementation instance when it is instantiated.
 * <p/>
 * Processors will receive callbacks as the implementation class is walked while evalauting an assembly. It is the
 * responsibility of the parser to determine whether to perform an action during the callback.
 *
 * @version $Rev$ $Date$
 */
public interface ImplementationProcessor {

    /**
     * A callback received when the component implementation class is first loaded
     *
     * @param clazz   the component implementation class
     * @param type    the incomplete component type associated with the implementation class
     * @param context the current deployment context
     * @throws ProcessingException if an error is encountered while processing metadata
     */
    <T> void visitClass(Class<T> clazz,
                        PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>> type,
                        DeploymentContext context) throws ProcessingException;

    /**
     * A callback received as the component implementation class hierarchy is evaluated
     *
     * @param clazz   the superclass in the component implmentation's class hierarchy
     * @param type    the incomplete component type associated with the implementation class
     * @param context the current deployment context
     * @throws ProcessingException if an error is encountered while processing metadata
     */
    <T> void visitSuperClass(
        Class<T> clazz,
        PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>> type,
        DeploymentContext context) throws ProcessingException;

    /**
     * A callback received as the component implementation's public and protected methods are evaluated
     *
     * @param method  the current public or protected method being evaluated
     * @param type    the incomplete component type associated with the implementation class
     * @param context the current deployment context
     * @throws ProcessingException if an error is encountered while processing metadata
     */
    void visitMethod(
        Method method,
        PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>> type,
        DeploymentContext context) throws ProcessingException;

    /**
     * A callback received as the component implementation's constructor used for instantiation by the runtime is
     * evaluated. If an implementation contains more than one constructor, the constructor passed to the callback will
     * be chosen according to the algorithm described in the SCA Java Client and Implementation Model Specification.
     *
     * @param constructor the constructor used for instantiating component implementation instances
     * @param type        the incomplete component type associated with the implementation class
     * @param context     the current deployment context
     * @throws ProcessingException if an error is encountered while processing metadata
     */
    <T> void visitConstructor(
        Constructor<T> constructor,
        PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>> type,
        DeploymentContext context)
        throws ProcessingException;
    
    /**
     * @param parameter
     * @param type
     * @param context
     * @throws ProcessingException
     */
    void visitConstructorParameter(Parameter parameter,
                                   PojoComponentType<JavaMappedService, 
                                   JavaMappedReference, JavaMappedProperty<?>> type,
                                   DeploymentContext context) throws ProcessingException;

    /**
     * A callback received as the component implementation's public and
     * protected fields are evaluated
     * 
     * @param field the current public or protected field being evaluated
     * @param type the incomplete component type associated with the
     *            implementation class
     * @param context the current deployment context
     * @throws ProcessingException if an error is encountered while processing
     *             metadata
     */
    void visitField(
        Field field,
        PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>> type,
        DeploymentContext context) throws ProcessingException;

    /**
     * The final callback received when all other callbacks during evaluation of the component implementation have been
     * issued
     *
     * @param clazz   the component implementation class
     * @param type    the incomplete component type associated with the implementation class
     * @param context the current deployment context
     * @throws ProcessingException if an error is encountered while processing metadata
     */
    <T> void visitEnd(
        Class<T> clazz,
        PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>> type,
        DeploymentContext context) throws ProcessingException;

}
