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

import org.osoa.sca.annotations.Init;

import org.apache.tuscany.spi.deployer.DeploymentContext;

import org.apache.tuscany.core.implementation.ImplementationProcessorSupport;
import org.apache.tuscany.core.implementation.PojoComponentType;
import org.apache.tuscany.core.implementation.ProcessingException;

/**
 * Processes the {@link @Init} annotation on a component implementation and updates the component type with the
 * decorated initializer method
 *
 * @version $Rev$ $Date$
 */
public class InitProcessor extends ImplementationProcessorSupport {

    public void visitMethod(Method method, PojoComponentType type, DeploymentContext context)
        throws ProcessingException {
        Init annotation = method.getAnnotation(Init.class);
        if (annotation == null) {
            return;
        }
        if (method.getParameterTypes().length != 0) {
            IllegalInitException e = new IllegalInitException("Initializer must not have argments");
            e.setIdentifier(method.getName());
            throw e;
        }
        if (type.getInitMethod() != null) {
            throw new DuplicateInitException("More than one initializer found on implementaton");
        }
        method.setAccessible(true);
        type.setInitMethod(method);
    }
}
