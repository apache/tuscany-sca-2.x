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

import static org.apache.tuscany.sca.implementation.java.introspect.JavaIntrospectionHelper.getBaseType;

import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;

import org.apache.tuscany.sca.assembly.AssemblyFactory;
import org.apache.tuscany.sca.assembly.Multiplicity;
import org.apache.tuscany.sca.implementation.java.IntrospectionException;
import org.apache.tuscany.sca.implementation.java.JavaElementImpl;
import org.apache.tuscany.sca.implementation.java.JavaImplementation;
import org.apache.tuscany.sca.implementation.java.JavaParameterImpl;
import org.apache.tuscany.sca.implementation.java.introspect.BaseJavaClassVisitor;
import org.apache.tuscany.sca.implementation.java.introspect.JavaIntrospectionHelper;
import org.apache.tuscany.sca.interfacedef.InvalidInterfaceException;
import org.apache.tuscany.sca.interfacedef.java.JavaInterface;
import org.apache.tuscany.sca.interfacedef.java.JavaInterfaceContract;
import org.apache.tuscany.sca.interfacedef.java.JavaInterfaceFactory;
import org.oasisopen.sca.ServiceReference;
import org.oasisopen.sca.annotation.Reference;

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
        if (annotation != null) {
	
	        if (!JavaIntrospectionHelper.isSetter(method)) {
	            throw new IllegalReferenceException("Annotated method is not a setter: " + method, method);
	        }
	       
	        if(Modifier.isStatic(method.getModifiers())) {
	        	throw new IllegalPropertyException("Static method " + method.getName() +" in class " + method.getDeclaringClass().getName() + " can not be annotated as a Reference");
	        }
	        
	        String name = annotation.name();
	        if ("".equals(name)) {
	            name = JavaIntrospectionHelper.toPropertyName(method.getName());
	        }
	        JavaElementImpl ref = type.getReferenceMembers().get(name);
	        // Setter override field
	        if (ref != null && ref.getElementType() != ElementType.FIELD) {
	            throw new DuplicateReferenceException(name);
	        }
	        removeReference(ref, type);
	
	        JavaElementImpl element = new JavaElementImpl(method, 0);
	        org.apache.tuscany.sca.assembly.Reference reference = createReference(element, name);
	        type.getReferences().add(reference);
	        type.getReferenceMembers().put(name, element);
        }
        
        // enforce the constraint that an ordinary method's argument can not be a reference
        Annotation paramsAnnotations[][] = method.getParameterAnnotations();
        for (int i = 0; i < paramsAnnotations.length; i++) {
        	Annotation argAnnotations[] = paramsAnnotations[i];
        	for (int j = 0; j < argAnnotations.length; j++) {
        		if(argAnnotations[j].annotationType() == Reference.class) {
        			throw new IllegalReferenceException("Argument " + (i+1) + " of method " + method.getName() + " in class " + method.getDeclaringClass() + " can not be a Reference");
        		}
        	}
		}
    }


    @Override
    public void visitField(Field field, JavaImplementation type) throws IntrospectionException {
        Reference annotation = field.getAnnotation(Reference.class);
        if (annotation == null) {
            return;
        }
        
        if(Modifier.isStatic(field.getModifiers())) {
        	throw new IllegalReferenceException("Static field " + field.getName() +" in class " + field.getDeclaringClass().getName() + " can not be annotated as Reference");
        }
        String name = annotation.name();
        if ("".equals(name)) {
            name = field.getName();
        }
        JavaElementImpl ref = type.getReferenceMembers().get(name);
        if (ref != null && ref.getElementType() == ElementType.FIELD) {
            throw new DuplicateReferenceException(name);
        }

        // Setter method override field
        if (ref == null) {
            JavaElementImpl element = new JavaElementImpl(field);
            org.apache.tuscany.sca.assembly.Reference reference = createReference(element, name);
            type.getReferences().add(reference);
            type.getReferenceMembers().put(name, element);
        }
    }

    @Override
    public void visitConstructorParameter(JavaParameterImpl parameter, JavaImplementation type)
        throws IntrospectionException {
        Reference refAnnotation = parameter.getAnnotation(Reference.class);
        if (refAnnotation == null) {
            return;
        }
        
        if (!refAnnotation.required()) {
            throw new InvalidReferenceException("JCA90016 Constructor has @Reference with required=false: " + type.getName());
        }
        
        String paramName = parameter.getName();
        String name = getReferenceName(paramName, parameter.getIndex(), refAnnotation.name());
        JavaElementImpl ref = type.getReferenceMembers().get(name);

        // Setter override field
        if (ref != null && ref.getElementType() != ElementType.FIELD) {
            throw new DuplicateReferenceException(name);
        }

        removeReference(ref, type);
        org.apache.tuscany.sca.assembly.Reference reference = createReference(parameter, name);
        type.getReferences().add(reference);
        type.getReferenceMembers().put(name, parameter);
        parameter.setClassifer(Reference.class);
        parameter.setName(name);
    }

    /**
     * Create a SCA reference for a java Element
     * @param element
     * @param name
     * @return
     * @throws IntrospectionException
     */
    private org.apache.tuscany.sca.assembly.Reference createReference(JavaElementImpl element, String name)
        throws IntrospectionException {
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
        if (ServiceReference.class.isAssignableFrom(baseType)) {
            if (Collection.class.isAssignableFrom(rawType)) {
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


    /**
     * Utility methods
     */

    /**
     * 
     * @param paramName
     * @param pos
     * @param name
     * @return
     * @throws InvalidConstructorException
     */
    private static String getReferenceName(String paramName, int pos, String name) throws InvalidConstructorException {
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
    
    /**
     * 
     * @param ref
     * @param type
     * @return
     */
    private static boolean removeReference(JavaElementImpl ref, JavaImplementation type) {
        if (ref == null) {
            return false;
        }
        List<org.apache.tuscany.sca.assembly.Reference> refs = type.getReferences();
        for (int i = 0; i < refs.size(); i++) {
            if (refs.get(i).getName().equals(ref.getName())) {
                refs.remove(i);
                return true;
            }
        }
        return false;
    }


}
