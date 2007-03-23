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

import org.osoa.sca.annotations.AllowsPassByReference;

import org.apache.tuscany.spi.deployer.DeploymentContext;
import org.apache.tuscany.spi.implementation.java.ImplementationProcessorExtension;
import org.apache.tuscany.spi.implementation.java.JavaMappedProperty;
import org.apache.tuscany.spi.implementation.java.JavaMappedReference;
import org.apache.tuscany.spi.implementation.java.JavaMappedService;
import org.apache.tuscany.spi.implementation.java.PojoComponentType;
import org.apache.tuscany.spi.implementation.java.ProcessingException;

/**
 * Processes {@link AllowsPassByReference} on an implementation
 * 
 * @version $Rev: 479093 $ $Date: 2006-11-25 12:34:41 +0530 (Sat, 25 Nov 2006) $
 */
public class AllowsPassByReferenceProcessor extends ImplementationProcessorExtension {

    public <T> void visitClass(Class<T> clazz,
                               PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>> type,
                               DeploymentContext context) throws ProcessingException {
        type.setAllowsPassByReference(clazz.isAnnotationPresent(AllowsPassByReference.class));
    }

    @Override
    public void visitMethod(Method method,
                            PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>> type,
                            DeploymentContext context) throws ProcessingException {
        boolean pbr = method.isAnnotationPresent(AllowsPassByReference.class);
        if (pbr) {
            type.getAllowsPassByReferenceMethods().add(method);
        }
    }
}
