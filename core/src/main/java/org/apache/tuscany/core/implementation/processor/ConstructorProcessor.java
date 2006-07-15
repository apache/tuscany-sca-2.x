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

import org.osoa.sca.annotations.Property;
import org.osoa.sca.annotations.Reference;

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
                if (processParam(param, paramAnnotations, names, i, type, explicitNames)) {
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

    /**
     * Processes a constructor parameter by introspecting its annotations
     *
     * @param param            the parameter to process
     * @param paramAnnotations the parameter annotations
     * @param constructorNames the array of constructorNames specified by @Constructor
     * @param pos              the declaration position of the constructor parameter
     * @param type             the component type associated with implementation being reflected
     * @param explicitNames    the list of parameter constructorNames specified on parameter annotations
     * @return true if the parameters have explicit annotation declarations
     * @throws ProcessingException
     */
    private boolean processParam(Class<?> param,
                                 Annotation[] paramAnnotations,
                                 String[] constructorNames,
                                 int pos,
                                 PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>> type,
                                 List<String> explicitNames)
        throws ProcessingException {
        boolean annotationsDeclared = false;
        for (Annotation annot : paramAnnotations) {
            if (Autowire.class.equals(annot.annotationType())) {
                // the param is marked as an autowire
                Autowire autowireAnnot = (Autowire) annot;
                JavaMappedReference reference = new JavaMappedReference();
                reference.setAutowire(true);
                String name = autowireAnnot.name();
                if (name == null || name.length() == 0) {
                    if (constructorNames.length < pos + 1 || constructorNames[pos] == null
                        || constructorNames[pos].length() == 0) {
                        throw new InvalidAutowireException("No name specified for autowire parameter " + (pos + 1));
                    }
                    name = constructorNames[pos];
                } else if (pos < constructorNames.length
                    && constructorNames[pos] != null
                    && constructorNames[pos].length() != 0
                    && !name.equals(constructorNames[pos])) {
                    throw new InvalidConstructorException(
                        "Name specified by @Constructor does not match autowire name at " + (pos + 1));
                }
                reference.setName(name);
                JavaServiceContract contract = new JavaServiceContract();
                contract.setInterfaceClass(param);
                reference.setServiceContract(contract);
                type.getReferences().put(name, reference);
                annotationsDeclared = true;
                explicitNames.add(name);
            } else if (Property.class.equals(annot.annotationType())) {
                // TODO multiplicity
                // the param is marked as a property
                Property propAnnot = (Property) annot;
                JavaMappedProperty property = new JavaMappedProperty();
                String name = propAnnot.name();
                if (name == null || name.length() == 0) {
                    if (constructorNames.length < pos + 1 || constructorNames[pos] == null
                        || constructorNames[pos].length() == 0) {
                        throw new InvalidPropertyException("No name specified for property parameter " + (pos + 1));
                    }
                    name = constructorNames[pos];
                } else if (pos < constructorNames.length
                    && constructorNames[pos] != null
                    && constructorNames[pos].length() != 0
                    && !name.equals(constructorNames[pos])) {
                    throw new InvalidConstructorException(
                        "Name specified by @Constructor does not match property name at " + (pos + 1));
                }
                if (type.getProperties().get(name) != null) {
                    throw new DuplicatePropertyException(name);
                }
                property.setName(name);
                property.setRequired(propAnnot.required());
                property.setJavaType(param);
                type.getProperties().put(name, property);
                annotationsDeclared = true;
                explicitNames.add(name);
            } else if (Reference.class.equals(annot.annotationType())) {
                // TODO multiplicity
                // the param is marked as a reference
                Reference refAnnotation = (Reference) annot;
                JavaMappedReference reference = new JavaMappedReference();
                String name = refAnnotation.name();
                if (name == null || name.length() == 0) {
                    if (constructorNames.length < pos + 1 || constructorNames[pos] == null
                        || constructorNames[pos].length() == 0) {
                        throw new InvalidReferenceException("No name specified for reference parameter " + (pos + 1));
                    }
                    name = constructorNames[pos];
                } else if (pos < constructorNames.length
                    && constructorNames[pos] != null
                    && constructorNames[pos].length() != 0
                    && !name.equals(constructorNames[pos])) {
                    throw new InvalidConstructorException(
                        "Name specified by @Constructor does not match reference name at " + (pos + 1));
                }
                if (type.getReferences().get(name) != null) {
                    throw new DuplicateReferenceException(name);
                }
                reference.setName(name);
                reference.setRequired(refAnnotation.required());
                JavaServiceContract contract = new JavaServiceContract();
                contract.setInterfaceClass(param);
                reference.setServiceContract(contract);
                type.getReferences().put(name, reference);
                annotationsDeclared = true;
                explicitNames.add(name);
            }
        }
        return annotationsDeclared;
    }
}
