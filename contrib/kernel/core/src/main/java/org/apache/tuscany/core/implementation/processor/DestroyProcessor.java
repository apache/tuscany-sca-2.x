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

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import org.osoa.sca.annotations.Destroy;

import org.apache.tuscany.spi.deployer.DeploymentContext;
import org.apache.tuscany.spi.implementation.java.ImplementationProcessorExtension;
import org.apache.tuscany.spi.implementation.java.JavaMappedProperty;
import org.apache.tuscany.spi.implementation.java.JavaMappedReference;
import org.apache.tuscany.spi.implementation.java.JavaMappedService;
import org.apache.tuscany.spi.implementation.java.PojoComponentType;
import org.apache.tuscany.spi.implementation.java.ProcessingException;

/**
 * Processes the {@link @Destroy} annotation on a component implementation and updates the component type with the
 * decorated destructor method
 *
 * @version $Rev$ $Date$
 */
public class DestroyProcessor extends ImplementationProcessorExtension {

    public void visitMethod(Method method,
                            PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>> type,
                            DeploymentContext context)
        throws ProcessingException {
        Destroy annotation = method.getAnnotation(Destroy.class);
        if (annotation == null) {
            return;
        }
        if (method.getParameterTypes().length != 0) {
            throw new IllegalDestructorException("Destructor must not have argments", method.toString());
        }
        if (type.getDestroyMethod() != null) {
            throw new DuplicateDestructorException("More than one destructor found on implementation");
        }
        if (Modifier.isProtected(method.getModifiers())) {
            method.setAccessible(true);
        }
        type.setDestroyMethod(method);
    }
}
