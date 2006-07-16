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
import java.util.List;

import org.osoa.sca.annotations.Callback;
import org.osoa.sca.annotations.Property;
import org.osoa.sca.annotations.Reference;
import org.osoa.sca.annotations.Remotable;
import org.osoa.sca.annotations.Scope;

import org.apache.tuscany.spi.annotation.Autowire;
import org.apache.tuscany.spi.model.InteractionScope;

import org.apache.tuscany.core.implementation.JavaMappedProperty;
import org.apache.tuscany.core.implementation.JavaMappedReference;
import org.apache.tuscany.core.implementation.JavaMappedService;
import org.apache.tuscany.core.implementation.JavaServiceContract;
import org.apache.tuscany.core.implementation.PojoComponentType;
import org.apache.tuscany.core.implementation.ProcessingException;
import org.apache.tuscany.core.util.JavaIntrospectionHelper;

/**
 * Contains various utility methods for <code>ImplementationProcessor</code>s
 *
 * @version $Rev$ $Date$
 */
public final class ProcessorUtils {

    private ProcessorUtils() {
    }

    /**
     * Convenience method for creating a mapped service from the given interface
     */
    public static JavaMappedService createService(Class<?> interfaze) {
        JavaMappedService service = new JavaMappedService();
        service.setName(JavaIntrospectionHelper.getBaseName(interfaze));
        service.setRemotable(interfaze.getAnnotation(Remotable.class) != null);
        service.setServiceInterface(interfaze);
        JavaServiceContract contract = new JavaServiceContract();
        contract.setInterfaceClass(interfaze);
        Scope interactionScope = interfaze.getAnnotation(Scope.class);
        if (interactionScope == null) {
            contract.setInteractionScope(InteractionScope.NONCONVERSATIONAL);
        } else {
            if ("CONVERSATIONAL".equalsIgnoreCase(interactionScope.value())) {
                contract.setInteractionScope(InteractionScope.CONVERSATIONAL);
            } else {
                contract.setInteractionScope(InteractionScope.NONCONVERSATIONAL);
            }
        }
        Callback callback = interfaze.getAnnotation(Callback.class);
        if (callback != null && !Void.class.equals(callback.value())) {
            Class<?> callbackClass = callback.value();
            contract.setCallbackClass(callbackClass);
            contract.setCallbackName(JavaIntrospectionHelper.getBaseName(callbackClass));
        }
        service.setServiceContract(contract);
        return service;
    }

    /**
     * Determines if all the members of a collection have unique types
     *
     * @param collection the collection to analyze
     * @return true if the types are unique
     */
    public static boolean areUnique(Class[] collection) {
        if (collection.length == 0) {
            return true;
        }
        return areUnique(collection, 0);
    }

    /**
     * Determines if all the members of a collection have unique types
     *
     * @param collection the collection to analyze
     * @param start      the position in the collection to start
     * @return true if the types are unique
     */
    private static boolean areUnique(Class[] collection, int start) {
        Object compare = collection[start];
        for (int i = start + 1; i < collection.length; i++) {
            if (compare.equals(collection[i])) {
                return false;
            }
        }
        if (start + 1 < collection.length) {
            return areUnique(collection, start + 1);
        } else {
            return true;
        }
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
     * @throws org.apache.tuscany.core.implementation.ProcessingException
     *
     */
    public static boolean processParam(Class<?> param,
                                       Annotation[] paramAnnotations,
                                       String[] constructorNames,
                                       int pos,
                                       PojoComponentType<JavaMappedService, JavaMappedReference,
                                           JavaMappedProperty<?>> type,
                                       List<String> explicitNames)
        throws ProcessingException {
        boolean annotationsDeclared = false;
        for (Annotation annot : paramAnnotations) {
            if (Autowire.class.equals(annot.annotationType())) {
                processAutowire(annot, constructorNames, pos, param, type, explicitNames);
                annotationsDeclared = true;
            } else if (Property.class.equals(annot.annotationType())) {
                processProperty(annot, constructorNames, pos, type, param, explicitNames);
                annotationsDeclared = true;
            } else if (Reference.class.equals(annot.annotationType())) {
                processReference(annot, constructorNames, pos, type, param, explicitNames);
                annotationsDeclared = true;
            }
        }
        return annotationsDeclared;
    }

    private static void processAutowire(Annotation annot, String[] constructorNames,
                                        int pos,
                                        Class<?> param,
                                        PojoComponentType<JavaMappedService, JavaMappedReference,
                                            JavaMappedProperty<?>> type,
                                        List<String> explicitNames) throws InvalidAutowireException,
                                                                           InvalidConstructorException {
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
        explicitNames.add(name);
    }

    private static void processProperty(Annotation annot,
                                        String[] constructorNames,
                                        int pos,
                                        PojoComponentType<JavaMappedService, JavaMappedReference,
                                            JavaMappedProperty<?>> type,
                                        Class<?> param,
                                        List<String> explicitNames)
        throws ProcessingException {
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
        explicitNames.add(name);
    }

    private static void processReference(Annotation annot, String[] constructorNames,
                                         int pos,
                                         PojoComponentType<JavaMappedService, JavaMappedReference,
                                             JavaMappedProperty<?>> type,
                                         Class<?> param,
                                         List<String> explicitNames)
        throws ProcessingException {

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
        explicitNames.add(name);
    }
}
