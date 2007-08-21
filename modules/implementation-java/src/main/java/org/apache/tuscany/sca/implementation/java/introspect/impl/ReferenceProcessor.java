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
package org.apache.tuscany.sca.implementation.java.introspect.impl;

import static org.apache.tuscany.sca.implementation.java.introspect.impl.JavaIntrospectionHelper.getBaseType;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Collection;

import org.apache.tuscany.sca.assembly.AssemblyFactory;
import org.apache.tuscany.sca.assembly.Multiplicity;
import org.apache.tuscany.sca.implementation.java.IntrospectionException;
import org.apache.tuscany.sca.implementation.java.JavaImplementation;
import org.apache.tuscany.sca.implementation.java.impl.JavaElementImpl;
import org.apache.tuscany.sca.implementation.java.impl.JavaParameterImpl;
import org.apache.tuscany.sca.interfacedef.InvalidInterfaceException;
import org.apache.tuscany.sca.interfacedef.java.JavaInterface;
import org.apache.tuscany.sca.interfacedef.java.JavaInterfaceContract;
import org.apache.tuscany.sca.interfacedef.java.JavaInterfaceFactory;
import org.osoa.sca.CallableReference;
import org.osoa.sca.annotations.Reference;

/**
 * Processes an {@link @Reference} annotation, updating the component type with
 * corresponding {@link
 * org.apache.tuscany.spi.implementation.java.JavaMappedReference}
 * 
 * @version $Rev$ $Date$
 */
public class ReferenceProcessor extends BaseJavaClassVisitor {
    private JavaInterfaceFactory javaFactory;
    
    public ReferenceProcessor(AssemblyFactory assemblyFactory, JavaInterfaceFactory javaFactory) {
        super(assemblyFactory);
        this.javaFactory = javaFactory;
    }

    @Override
    public void visitMethod(Method method, JavaImplementation type) throws IntrospectionException {
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

        JavaElementImpl element = new JavaElementImpl(method, 0);
        org.apache.tuscany.sca.assembly.Reference reference = createReference(element, name);
        type.getReferences().add(reference);
        type.getReferenceMembers().put(name, element);
    }

    @Override
    public void visitField(Field field, JavaImplementation type) throws IntrospectionException {
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
        JavaElementImpl element = new JavaElementImpl(field);
        org.apache.tuscany.sca.assembly.Reference reference = createReference(element, name);
        type.getReferences().add(reference);
        type.getReferenceMembers().put(name, element);
    }

    @Override
    public void visitConstructorParameter(JavaParameterImpl parameter, JavaImplementation type)
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
        org.apache.tuscany.sca.assembly.Reference reference = createReference(parameter, name);
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

    private org.apache.tuscany.sca.assembly.Reference createReference(JavaElementImpl element, String name) throws IntrospectionException {
        org.apache.tuscany.sca.assembly.Reference reference = assemblyFactory.createReference();
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
        if(CallableReference.class.isAssignableFrom(baseType)) {
            if(Collection.class.isAssignableFrom(rawType)) {
                genericType = JavaIntrospectionHelper.getParameterType(genericType);
            }
            baseType = JavaIntrospectionHelper.getBusinessInterface(baseType, genericType);
        }
        try {
            JavaInterface callInterface = javaFactory.createJavaInterface(baseType);
            reference.getInterfaceContract().setInterface(callInterface);
            if (callInterface.getCallbackClass() != null) {
                JavaInterface callbackInterface = javaFactory.createJavaInterface(callInterface.getCallbackClass());
                reference.getInterfaceContract().setCallbackInterface(callbackInterface);
            }
        } catch (InvalidInterfaceException e) {
            throw new IntrospectionException(e);
        }
        return reference;
    }
}
