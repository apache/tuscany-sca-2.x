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

import org.apache.tuscany.spi.annotation.Autowire;
import org.apache.tuscany.spi.component.CompositeComponent;
import org.apache.tuscany.spi.deployer.DeploymentContext;

import org.apache.tuscany.core.implementation.ConstructorDefinition;
import org.apache.tuscany.core.implementation.ImplementationProcessorSupport;
import org.apache.tuscany.core.implementation.JavaMappedProperty;
import org.apache.tuscany.core.implementation.JavaMappedReference;
import org.apache.tuscany.core.implementation.JavaMappedService;
import org.apache.tuscany.core.implementation.JavaServiceContract;
import org.apache.tuscany.core.implementation.PojoComponentType;
import org.apache.tuscany.core.implementation.ProcessingException;
import static org.apache.tuscany.core.util.JavaIntrospectionHelper.getBaseName;

/**
 * Handles processing of a constructor decorated with {@link org.osoa.sca.annotations.Constructor}
 *
 * @version $Rev$ $Date$
 */
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
            e.setIdentifier(definition.getConstructor().getDeclaringClass().getName());
            throw e;
        }
        definition = new ConstructorDefinition(constructor);
        String[] names = annotation.value();
        Class<?>[] params = constructor.getParameterTypes();
        Annotation[][] annotatons = constructor.getParameterAnnotations();
        boolean sitesDefined = false;
        for (int i = 0; i < params.length; i++) {
            Class<?> param = params[i];
            Annotation[] paramAnnotations = annotatons[i];
            for (Annotation annot : paramAnnotations) {
                if (Autowire.class.equals(annot.annotationType())) {
                    JavaMappedReference reference = new JavaMappedReference();
                    reference.setAutowire(true);
                    String name = getBaseName(param).toLowerCase();
                    reference.setName(name);
                    JavaServiceContract contract = new JavaServiceContract();
                    contract.setInterfaceClass(param);
                    reference.setServiceContract(contract);
                    type.getReferences().put(name, reference);
                    sitesDefined = true;
                }
            }
        }
        if (!sitesDefined && names.length != params.length) {
            throw new InvalidConstructorException(
                "Number of parameters does not match values specified in @Constructor");
        }
        for (String name : names) {
            definition.getInjectionNames().add(name);
        }
        type.setConstructorDefinition(definition);
    }
}
