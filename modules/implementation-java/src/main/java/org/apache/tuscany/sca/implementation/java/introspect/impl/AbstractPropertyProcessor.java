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

import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.reflect.Field;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.tuscany.sca.assembly.AssemblyFactory;
import org.apache.tuscany.sca.assembly.Property;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.implementation.java.IntrospectionException;
import org.apache.tuscany.sca.implementation.java.JavaElementImpl;
import org.apache.tuscany.sca.implementation.java.JavaImplementation;
import org.apache.tuscany.sca.implementation.java.JavaParameterImpl;
import org.apache.tuscany.sca.implementation.java.introspect.BaseJavaClassVisitor;
import org.apache.tuscany.sca.implementation.java.introspect.JavaIntrospectionHelper;
import org.apache.tuscany.sca.interfacedef.DataType;
import org.apache.tuscany.sca.interfacedef.impl.DataTypeImpl;
import org.apache.tuscany.sca.interfacedef.util.XMLType;

/**
 * Base class for ImplementationProcessors that handle annotations that add
 * Properties.
 * 
 * @version $Rev$ $Date$
 */
public abstract class AbstractPropertyProcessor<A extends Annotation> extends BaseJavaClassVisitor {
    private final Class<A> annotationClass;

    protected AbstractPropertyProcessor(ExtensionPointRegistry registry, Class<A> annotationClass) {
        super(registry);
        this.annotationClass = annotationClass;
    }
    
    private static boolean removeProperty(JavaElementImpl prop, JavaImplementation type) {
        if(prop==null) {
            return false;
        }
        List<Property> props = type.getProperties();
        for(int i=0;i<props.size();i++) {
            if(props.get(i).getName().equals(prop.getName())) {
                props.remove(i);
                return true;
            }
        }
        return false;
    }

    @Override
    public void visitMethod(Method method, JavaImplementation type) throws IntrospectionException {
        A annotation = method.getAnnotation(annotationClass);
        if (annotation != null) {
        	
	        if (!JavaIntrospectionHelper.isSetter(method)) {
	            throw new IllegalPropertyException("Annotated method is not a setter: " + method, method);
	        }
	        
	        if(Modifier.isStatic(method.getModifiers())) {
	        	throw new IllegalPropertyException("Static method " + method.getName() +" in class " + method.getDeclaringClass().getName() + " can not be annotated as a Property");
	        }
	
	        String name = getName(annotation);
	        if (name == null || "".equals(name)) {
	            name = method.getName();
	            if (name.startsWith("set")) {
	                name = JavaIntrospectionHelper.toPropertyName(method.getName());
	            }
	        }
	
	        Map<String, JavaElementImpl> properties = type.getPropertyMembers();
	        JavaElementImpl prop = properties.get(name);
	        // Setter override field
	        if (prop != null && prop.getElementType() != ElementType.FIELD) {
	            throw new DuplicatePropertyException(name);
	        }
	
	        removeProperty(prop, type);
	        
	        JavaElementImpl element = new JavaElementImpl(method, 0);
	        Property property = createProperty(name, element);
	
	        // add databinding available as annotations, as extensions
	
	        initProperty(property, annotation);
	        type.getProperties().add(property);
	        properties.put(name, element);
        }
        
        // enforce the constraint that an ordinary method's argument can not be a Property
        Annotation paramsAnnotations[][] = method.getParameterAnnotations();
        for (int i = 0; i < paramsAnnotations.length; i++) {
        	Annotation argAnnotations[] = paramsAnnotations[i];
        	for (int j = 0; j < argAnnotations.length; j++) {
        		if(argAnnotations[j].annotationType() == org.oasisopen.sca.annotation.Property.class) {
        			throw new IllegalPropertyException("[JCA90001] Argument " + (i+1) + " of method " + method.getName() + " in class " + method.getDeclaringClass() + " can not be a Property");
        		}
        	}
		}
    }


    @Override
    public void visitField(Field field, JavaImplementation type) throws IntrospectionException {

        A annotation = field.getAnnotation(annotationClass);
        if (annotation == null) {
            return;
        }
        
        if(Modifier.isStatic(field.getModifiers())) {
        	throw new IllegalPropertyException("Static field " + field.getName() +" in class " + field.getDeclaringClass().getName() + " can not be annotated as a Property");
        }

        if(Modifier.isFinal(field.getModifiers())) {
            throw new IllegalPropertyException("[JCA90011] Final field " + field.getName() +" in class " + field.getDeclaringClass().getName() + " can not be annotated as a Property");
        }

        String name = getName(annotation);
        if (name == null) {
            name = "";
        }
        if ("".equals(name) || name.equals(field.getType().getName())) {
            name = field.getName();
        }

        Map<String, JavaElementImpl> properties = type.getPropertyMembers();
        JavaElementImpl prop = properties.get(name);
        // Setter override field
        if (prop != null && prop.getElementType() == ElementType.FIELD) {
            throw new DuplicatePropertyException(name);
        }

        if (prop == null) {
            JavaElementImpl element = new JavaElementImpl(field);
            Property property = createProperty(name, element);
            initProperty(property, annotation);
            type.getProperties().add(property);
            properties.put(name, element);
        }
    }

    @Override
    public void visitConstructorParameter(JavaParameterImpl parameter, JavaImplementation type)
        throws IntrospectionException {

        Map<String, JavaElementImpl> properties = type.getPropertyMembers();
        A annotation = parameter.getAnnotation(annotationClass);
        if (annotation != null) {
            String name = getName(annotation);
            if (name == null) {
                name = parameter.getType().getName();
            }
            if (!"".equals(name) && !"".equals(parameter.getName()) && !name.equals(parameter.getName())) {
                throw new InvalidConstructorException("Mismatched property name: " + parameter);
            }
            if ("".equals(name) && "".equals(parameter.getName())) {
                throw new InvalidPropertyException("[JCA90013] Missing property name: " + parameter);
            }
            if ("".equals(name)) {
                name = parameter.getName();
            }
            
            if (!getRequired(annotation)) {
                throw new InvalidPropertyException("[JCA90014] Constructor property must not have required=false: " + type.getName());
            }

            JavaElementImpl prop = properties.get(name);
            // Setter override field
            if (prop != null && prop.getElementType() != ElementType.FIELD) {
                throw new DuplicatePropertyException(name);
            }
            removeProperty(prop, type);
            
            parameter.setName(name);
            parameter.setClassifer(annotationClass);
            Property property = createProperty(name, parameter);
            initProperty(property, annotation);
            type.getProperties().add(property);
            properties.put(name, parameter);
        }
    }
    
    protected Property createProperty(String name, JavaElementImpl element) throws IntrospectionException {

        Type type = element.getGenericType();
        Class<?> javaType = element.getType();
        
        return createProperty(assemblyFactory, name, javaType, type);

    }

    public static Property createProperty(AssemblyFactory assemblyFactory, String name, Class<?> javaClass, Type genericType) {
        Property property = assemblyFactory.createProperty();
        property.setName(name);

        if (javaClass.isArray() || Collection.class.isAssignableFrom(javaClass)) {
            property.setMany(true);
            if (javaClass.isArray()) {
                Class<?> propType = javaClass.getComponentType();
                Type genericPropType = propType;
                if (genericType instanceof GenericArrayType) {
                    genericPropType = ((GenericArrayType)genericType).getGenericComponentType();
                }
                DataType dt = new DataTypeImpl(null, propType, genericPropType, XMLType.UNKNOWN);
                property.setDataType(dt);
            } else {
                if (genericType instanceof ParameterizedType) {
                    // Collection<BaseType> property;
                    Type genericPropType = ((ParameterizedType)genericType).getActualTypeArguments()[0];
                    Class<?> propType = JavaIntrospectionHelper.getErasure(genericPropType);
                    DataType dt = new DataTypeImpl(null, propType, genericPropType, XMLType.UNKNOWN);
                    property.setDataType(dt);
                } else {
                    // Collection property;
                    DataType dt = new DataTypeImpl(null, Object.class, Object.class, XMLType.UNKNOWN);
                    property.setDataType(dt);
                }
            }
        } else {
            DataType dt = new DataTypeImpl(null, javaClass, genericType, XMLType.UNKNOWN);
            property.setDataType(dt);
        }
        return property;
    }

    protected abstract String getName(A annotation);
    protected abstract boolean getRequired(A annotation);

    protected abstract void initProperty(Property property, A annotation) throws IntrospectionException;


}
