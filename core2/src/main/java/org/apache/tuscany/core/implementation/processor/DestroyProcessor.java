/**
 *
 * Copyright 2006 The Apache Software Foundation or its licensors, as applicable.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package org.apache.tuscany.core.implementation.processor;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import org.osoa.sca.annotations.Destroy;

import org.apache.tuscany.core.implementation.ImplementationProcessorSupport;
import org.apache.tuscany.core.implementation.JavaMappedProperty;
import org.apache.tuscany.core.implementation.JavaMappedReference;
import org.apache.tuscany.core.implementation.JavaMappedService;
import org.apache.tuscany.core.implementation.PojoComponentType;
import org.apache.tuscany.core.implementation.ProcessingException;
import org.apache.tuscany.spi.component.CompositeComponent;
import org.apache.tuscany.spi.deployer.DeploymentContext;

/**
 * Processes the {@link @Destroy} annotation on a component implementation and updates the component type with the
 * decorated destructor method
 *
 * @version $Rev$ $Date$
 */
public class DestroyProcessor extends ImplementationProcessorSupport {

    public void visitMethod(CompositeComponent<?> parent, Method method,
                            PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>> type,
                            DeploymentContext context)
        throws ProcessingException {
        Destroy annotation = method.getAnnotation(Destroy.class);
        if (annotation == null) {
            return;
        }
        if (method.getParameterTypes().length != 0) {
            IllegalDestructorException e = new IllegalDestructorException("Destructor must not have argments");
            e.setIdentifier(method.getName());
            throw e;
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
