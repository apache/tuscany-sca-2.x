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

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

import org.apache.tuscany.spi.component.CompositeComponent;
import org.apache.tuscany.spi.deployer.DeploymentContext;

import org.apache.tuscany.core.implementation.ConstructorDefinition;
import org.apache.tuscany.core.implementation.ImplementationProcessorSupport;
import org.apache.tuscany.core.implementation.JavaMappedProperty;
import org.apache.tuscany.core.implementation.JavaMappedReference;
import org.apache.tuscany.core.implementation.JavaMappedService;
import org.apache.tuscany.core.implementation.PojoComponentType;
import org.apache.tuscany.core.implementation.ProcessingException;

/**
 * Handles processing of a constructor decorated with {@link org.osoa.sca.annotations.Constructor}
 *
 * @version $Rev$ $Date$
 */
@SuppressWarnings("unchecked")
public class ConstructorProcessor extends ImplementationProcessorSupport {

    public void visitConstructor(CompositeComponent<?> parent, Constructor<?> constructor,
                                 PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>> type,
                                 DeploymentContext context) throws ProcessingException {
        org.osoa.sca.annotations.Constructor annotation =
            constructor.getAnnotation(org.osoa.sca.annotations.Constructor.class);
        if (annotation == null) {
            return;
        }
        ConstructorDefinition<?> definition = type.getConstructorDefinition();
        if (definition != null) {
            DuplicateConstructorException e =
                new DuplicateConstructorException("More than one constructor marked with @Constructor");
            e.setIdentifier(constructor.getDeclaringClass().getName());
            throw e;
        }
        definition = new ConstructorDefinition(constructor);
        Class<?>[] params = constructor.getParameterTypes();
        String[] names = annotation.value();
        Annotation[][] annotations = constructor.getParameterAnnotations();
        List<String> explicitNames = new ArrayList<String>();
        boolean annotationsDeclared = false;
        for (int i = 0; i < params.length; i++) {
            Class<?> param = params[i];
            Annotation[] paramAnnotations = annotations[i];
            try {
                if (ProcessorUtils.processParam(param, paramAnnotations, names, i, type, explicitNames)) {
                    annotationsDeclared = true;
                }
            } catch (ProcessingException e) {
                e.setIdentifier(constructor.toString());
                throw e;
            }
        }
        if (!annotationsDeclared) {
            if (names.length != params.length) {
                throw new InvalidConstructorException("Names in @Constructor do not match number of parameters");
            }
            for (String name : names) {
                definition.getInjectionNames().add(name);
            }
        } else {
            for (String name : explicitNames) {
                definition.getInjectionNames().add(name);
            }
        }
        type.setConstructorDefinition(definition);
    }

}
