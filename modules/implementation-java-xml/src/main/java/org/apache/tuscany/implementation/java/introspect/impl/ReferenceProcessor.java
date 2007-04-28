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
package org.apache.tuscany.implementation.java.introspect.impl;

import static org.apache.tuscany.implementation.java.introspect.impl.JavaIntrospectionHelper.getBaseType;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Collection;

import org.apache.tuscany.assembly.AssemblyFactory;
import org.apache.tuscany.assembly.Multiplicity;
import org.apache.tuscany.implementation.java.impl.JavaElement;
import org.apache.tuscany.implementation.java.impl.JavaImplementationDefinition;
import org.apache.tuscany.implementation.java.impl.Parameter;
import org.apache.tuscany.implementation.java.introspect.BaseJavaClassIntrospectorExtension;
import org.apache.tuscany.implementation.java.introspect.IntrospectionException;
import org.apache.tuscany.interfacedef.InvalidInterfaceException;
import org.apache.tuscany.interfacedef.java.JavaFactory;
import org.apache.tuscany.interfacedef.java.JavaInterface;
import org.apache.tuscany.interfacedef.java.JavaInterfaceContract;
import org.apache.tuscany.interfacedef.java.introspect.JavaInterfaceIntrospectorExtensionPoint;
import org.osoa.sca.annotations.Reference;

/**
 * Processes an {@link @Reference} annotation, updating the component type with
 * corresponding {@link
 * org.apache.tuscany.spi.implementation.java.JavaMappedReference}
 * 
 * @version $Rev$ $Date$
 */
public class ReferenceProcessor extends BaseJavaClassIntrospectorExtension {
    private JavaInterfaceIntrospectorExtensionPoint interfaceIntrospector;
    private JavaFactory javaFactory;
    
    public ReferenceProcessor(AssemblyFactory assemblyFactory, JavaFactory javaFactory, JavaInterfaceIntrospectorExtensionPoint interfaceIntrospector) {
        super(assemblyFactory);
        this.javaFactory = javaFactory;
        this.interfaceIntrospector = interfaceIntrospector;
    }

    public void visitMethod(Method method, JavaImplementationDefinition type) throws IntrospectionException {
        Reference annotation = method.getAnnotation(Reference.class);
        if (annotation == null) {
            return; // Not a reference annotation.
        }
        if (method.getParameterTypes().length != 1) {
            throw new IllegalReferenceException("Setter must have one parameter", method);
        }
        String name = annotation.name();
        if ("".equals(name)) {
            name = JavaIntrospectionHelper.toPropertyName(method.getName());
        }
        if (type.getReferenceMembers().get(name) != null) {
            throw new DuplicateReferenceException(name);
        }

        JavaElement element = new JavaElement(method, 0);
        org.apache.tuscany.assembly.Reference reference = createReference(element, name);
        type.getReferences().add(reference);
        type.getReferenceMembers().put(name, element);
    }

    public void visitField(Field field, JavaImplementationDefinition type) throws IntrospectionException {
        Reference annotation = field.getAnnotation(Reference.class);
        if (annotation == null) {
            return;
        }
        String name = annotation.name();
        if ("".equals(name)) {
            name = field.getName();
        }
        if (type.getReferenceMembers().get(name) != null) {
            throw new DuplicateReferenceException(name);
        }
        JavaElement element = new JavaElement(field);
        org.apache.tuscany.assembly.Reference reference = createReference(element, name);
        type.getReferences().add(reference);
        type.getReferenceMembers().put(name, element);
    }

    public void visitConstructorParameter(Parameter parameter, JavaImplementationDefinition type)
        throws IntrospectionException {
        Reference refAnnotation = parameter.getAnnotation(Reference.class);
        if (refAnnotation == null) {
            return;
        }
        String paramName = parameter.getName();
        String name = getReferenceName(paramName, parameter.getIndex(), refAnnotation.name());
        if (type.getReferenceMembers().get(name) != null) {
            throw new DuplicateReferenceException(name);
        }
        org.apache.tuscany.assembly.Reference reference = createReference(parameter, name);
        type.getReferences().add(reference);
        type.getReferenceMembers().put(name, parameter);
        parameter.setClassifer(Reference.class);
        parameter.setName(name);
    }

    private String getReferenceName(String paramName, int pos, String name) throws InvalidConstructorException {
        if ("".equals(name)) {
            name = paramName;
        }
        if ("".equals(name)) {
            return "_ref" + pos;
        }
        if (!"".equals(paramName) && !name.equals(paramName)) {
            throw new InvalidConstructorException("Mismatching names specified for reference parameter " + pos);
        } else {
            return name;
        }
    }

    private org.apache.tuscany.assembly.Reference createReference(JavaElement element, String name) throws IntrospectionException {
        org.apache.tuscany.assembly.Reference reference = assemblyFactory.createReference();
        JavaInterfaceContract interfaceContract = javaFactory.createJavaInterfaceContract();
        reference.setInterfaceContract(interfaceContract);
        
        // reference.setMember((Member)element.getAnchor());
        boolean required = true;
        Reference ref = element.getAnnotation(Reference.class);
        if (ref != null) {
            required = ref.required();
        }
        // reference.setRequired(required);
        reference.setName(name);
        Class<?> rawType = element.getType();
        if (rawType.isArray() || Collection.class.isAssignableFrom(rawType)) {
            if (required) {
                reference.setMultiplicity(Multiplicity.ONE_N);
            } else {
                reference.setMultiplicity(Multiplicity.ZERO_N);
            }
        } else {
            if (required) {
                reference.setMultiplicity(Multiplicity.ONE_ONE);
            } else {
                reference.setMultiplicity(Multiplicity.ZERO_ONE);
            }
        }
        Type genericType = element.getGenericType();
        Class<?> baseType = getBaseType(rawType, genericType);
        try {
            JavaInterface callInterface = interfaceIntrospector.introspect(baseType);
            reference.getInterfaceContract().setInterface(callInterface);
            if (callInterface.getCallbackClass() != null) {
                JavaInterface callbackInterface = interfaceIntrospector.introspect(callInterface.getCallbackClass());
                reference.getInterfaceContract().setCallbackInterface(callbackInterface);
            }
        } catch (InvalidInterfaceException e) {
            throw new IntrospectionException(e);
        }
        return reference;
    }
}
