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
package org.apache.tuscany.core.implementation.processor;

import java.lang.annotation.Annotation;
import java.lang.reflect.Member;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;
import javax.xml.namespace.QName;

import org.osoa.sca.annotations.Callback;
import org.osoa.sca.annotations.Property;
import org.osoa.sca.annotations.Reference;
import org.osoa.sca.annotations.Remotable;
import org.osoa.sca.annotations.Resource;

import org.apache.tuscany.spi.annotation.Autowire;
import org.apache.tuscany.spi.databinding.extension.SimpleTypeMapperExtension;
import org.apache.tuscany.spi.idl.InvalidServiceContractException;
import org.apache.tuscany.spi.idl.TypeInfo;
import org.apache.tuscany.spi.idl.java.JavaInterfaceProcessorRegistry;
import org.apache.tuscany.spi.implementation.java.DuplicatePropertyException;
import org.apache.tuscany.spi.implementation.java.ImplementationProcessorService;
import org.apache.tuscany.spi.implementation.java.JavaMappedProperty;
import org.apache.tuscany.spi.implementation.java.JavaMappedReference;
import org.apache.tuscany.spi.implementation.java.JavaMappedService;
import org.apache.tuscany.spi.implementation.java.PojoComponentType;
import org.apache.tuscany.spi.implementation.java.ProcessingException;
import org.apache.tuscany.spi.model.Multiplicity;
import org.apache.tuscany.spi.model.OverrideOptions;
import org.apache.tuscany.spi.model.ServiceContract;

import org.apache.tuscany.core.idl.java.IllegalCallbackException;
import static org.apache.tuscany.core.util.JavaIntrospectionHelper.getBaseName;

/**
 * The default implementation of an <code>ImplementationProcessorService</code>
 *
 * @version $Rev$ $Date$
 */
public class ImplementationProcessorServiceImpl implements ImplementationProcessorService {
    private JavaInterfaceProcessorRegistry registry;
    private SimpleTypeMapperExtension typeMapper = new SimpleTypeMapperExtension();

    public ImplementationProcessorServiceImpl(@Autowire JavaInterfaceProcessorRegistry registry) {
        this.registry = registry;
    }

    public JavaMappedService createService(Class<?> interfaze) throws InvalidServiceContractException {
        JavaMappedService service = new JavaMappedService();
        service.setName(interfaze.getName());
        service.setRemotable(interfaze.getAnnotation(Remotable.class) != null);
        ServiceContract<?> contract = registry.introspect(interfaze);
        service.setServiceContract(contract);
        return service;
    }

    public void processCallback(Class<?> interfaze, ServiceContract<?> contract) throws IllegalCallbackException {
        Callback callback = interfaze.getAnnotation(Callback.class);
        if (callback != null && !Void.class.equals(callback.value())) {
            Class<?> callbackClass = callback.value();
            contract.setCallbackClass(callbackClass);
            contract.setCallbackName(getBaseName(callbackClass));
        } else if (callback != null && Void.class.equals(callback.value())) {
            throw new IllegalCallbackException("No callback interface specified on annotation", interfaze.getName());
        }
    }

    public boolean areUnique(Class[] collection) {
        if (collection.length == 0) {
            return true;
        }
        return areUnique(collection, 0);
    }

    public void addName(List<String> names, int pos, String name) {
        if (names.size() < pos) {
            for (int i = 0; i < pos; i++) {
                names.add(i, "");
            }
            names.add(name);
        } else if (names.size() > pos) {
            names.remove(pos);
            names.add(pos, name);
        } else {
            names.add(pos, name);
        }
    }

    public boolean processParam(
        Class<?> param,
        Type genericParam,
        Annotation[] paramAnnotations,
        String[] constructorNames,
        int pos,
        PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>> type, List<String> injectionNames) throws ProcessingException {
        boolean processed = false;
        for (Annotation annot : paramAnnotations) {
            if (Autowire.class.equals(annot.annotationType())) {
                processed = true;
                processAutowire(annot, constructorNames, pos, param, genericParam, type, injectionNames);
            } else if (Property.class.equals(annot.annotationType())) {
                processed = true;
                processProperty(annot, constructorNames, pos, type, param, genericParam, injectionNames);
            } else if (Reference.class.equals(annot.annotationType())) {
                processed = true;
                processReference(annot, constructorNames, pos, type, param, genericParam, injectionNames);
            } else if (Resource.class.equals(annot.annotationType())) {
                processed = true;
                processResource((Resource) annot, constructorNames, pos, type, param, injectionNames);
            }
        }
        return processed;
    }

    public boolean injectionAnnotationsPresent(Annotation[][] annots) {
        for (Annotation[] annotations : annots) {
            for (Annotation annotation : annotations) {
                Class<? extends Annotation> annotType = annotation.annotationType();
                if (annotType.equals(Autowire.class) || annotType.equals(Property.class)
                    || annotType.equals(Reference.class)
                    || annotType.equals(Resource.class)) {
                    return true;
                }
            }
        }
        return false;
    }

    public JavaMappedReference createReference(String name, Member member, Class<?> paramType)
        throws ProcessingException {
        JavaMappedReference reference = new JavaMappedReference();
        reference.setName(name);
        reference.setMember(member);
        reference.setRequired(false);
        ServiceContract contract;
        try {
            contract = registry.introspect(paramType);
        } catch (InvalidServiceContractException e1) {
            throw new ProcessingException(e1);
        }
        try {
            processCallback(paramType, contract);
        } catch (IllegalCallbackException e) {
            throw new ProcessingException(e);
        }
        reference.setServiceContract(contract);
        return reference;
    }

    /**
     * Determines if all the members of a collection have unique types
     *
     * @param collection the collection to analyze
     * @param start      the position in the collection to start
     * @return true if the types are unique
     */
    private boolean areUnique(Class[] collection, int start) {
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
     * Processes autowire metadata for a constructor parameter
     *
     * @param annot            the autowire annotation
     * @param constructorNames the parameter names as specified in an {@link org.osoa.sca.annotations.Constructor}
     *                         annotation
     * @param pos              the position of the parameter in the constructor's parameter list
     * @param param            the parameter type
     * @param type             the component type associated with the implementation being processed
     * @param injectionNames   the collection of injection names to update
     * @throws InvalidAutowireException
     * @throws InvalidConstructorException
     */
    private void processAutowire(
        Annotation annot,
        String[] constructorNames,
        int pos,
        Class<?> param,
        Type genericParam,
        PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>> type,
        List<String> injectionNames) throws ProcessingException {
        // the param is marked as an autowire
        Autowire autowireAnnot = (Autowire) annot;
        JavaMappedReference reference = new JavaMappedReference();
        reference.setAutowire(true);
        String name = autowireAnnot.name();
        if (name == null || name.length() == 0) {
            if (constructorNames.length > 1 && (constructorNames.length < pos + 1 || constructorNames[pos] == null)) {
                throw new InvalidAutowireException("Names in @Constructor and autowire parameter do not match at "
                    + (pos + 1));
            } else if (constructorNames.length == 1 && constructorNames[0].length() == 0) {
                // special case when @Constructor present with all autowire params not specifying any name 
                name = param.getName() + String.valueOf(pos);
            } else if (constructorNames.length == 1
                && (constructorNames.length < pos + 1 || constructorNames[pos] == null)) {
                throw new InvalidAutowireException("Names in @Constructor and autowire parameter do not match at "
                    + (pos + 1));
            } else if (constructorNames.length == 1 && constructorNames[0].length() > 0) {
                name = constructorNames[pos];
            } else if (constructorNames.length == 0 || constructorNames[pos].length() == 0) {
                name = param.getName() + String.valueOf(pos);
            } else {
                name = constructorNames[pos];
            }
        } else if (pos < constructorNames.length && constructorNames[pos] != null
            && constructorNames[pos].length() != 0 && !name.equals(constructorNames[pos])) {
            String paramNum = String.valueOf(pos + 1);
            throw new InvalidConstructorException("Name specified by @Constructor does not match autowire name",
                paramNum);
        }
        reference.setName(name);
        boolean required = autowireAnnot.required();
        reference.setRequired(required);
        try {
            Class<?> rawType = param;
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
            Class<?> baseType = getBaseType(rawType, genericParam);            
            ServiceContract<?> contract = registry.introspect(baseType);
            reference.setServiceContract(contract);
        } catch (InvalidServiceContractException e) {
            throw new ProcessingException(e);
        }
        type.getReferences().put(name, reference);
        addName(injectionNames, pos, name);
    }

    /**
     * Processes parameter metadata for a constructor parameter
     *
     * @param annot            the parameter annotation
     * @param constructorNames the parameter names as specified in an {@link org.osoa.sca.annotations.Constructor}
     *                         annotation
     * @param pos              the position of the parameter in the constructor's parameter list
     * @param type             the component type associated with the implementation being processed
     * @param param            the parameter type
     * @param explicitNames    the collection of injection names to update
     * @throws ProcessingException
     */
    @SuppressWarnings("unchecked")
    private void processProperty(
        Annotation annot,
        String[] constructorNames,
        int pos,
        PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>> type,
        Class<?> param,
        Type genericParam,
        List<String> explicitNames) throws ProcessingException {
        // the param is marked as a property
        Property propAnnot = (Property) annot;
        JavaMappedProperty property = new JavaMappedProperty();
        Class<?> baseType = getBaseType(param, genericParam);
        if (param.isArray() || Collection.class.isAssignableFrom(param)) {
            property.setMany(true);
        }        
        property.setJavaType(baseType);
        String name = propAnnot.name();
        if (name == null || name.length() == 0) {
            if (constructorNames.length < pos + 1 || constructorNames[pos] == null
                || constructorNames[pos].length() == 0) {
                throw new InvalidPropertyException("No name specified for property parameter " + (pos + 1));
            }
            name = constructorNames[pos];
        } else if (pos < constructorNames.length && constructorNames[pos] != null
            && constructorNames[pos].length() != 0 && !name.equals(constructorNames[pos])) {
            String paramNum = String.valueOf(pos + 1);
            throw new InvalidConstructorException("Name specified by @Constructor does not match property name",
                paramNum);
        }
        if (type.getProperties().get(name) != null) {
            throw new DuplicatePropertyException(name);
        }
        property.setName(name);
        property.setOverride(OverrideOptions.valueOf(propAnnot.override().toUpperCase()));

        String xmlType = propAnnot.xmlType();
        if (xmlType != null && xmlType.length() != 0) {
            property.setXmlType(QName.valueOf(xmlType));
        } else {
            TypeInfo typeInfo = typeMapper.getXMLType(property.getJavaType());
            if (typeInfo != null) {
                property.setXmlType(typeInfo.getQName());
            }
        }
        type.getProperties().put(name, property);
        addName(explicitNames, pos, name);
    }

    /**
     * Processes reference metadata for a constructor parameter
     *
     * @param annot            the parameter annotation
     * @param constructorNames the parameter names as specified in an {@link org.osoa.sca.annotations.Constructor}
     *                         annotation
     * @param pos              the position of the parameter in the constructor's parameter list
     * @param type             the component type associated with the implementation being processed
     * @param param            the parameter type
     * @param explicitNames    the collection of injection names to update
     * @throws ProcessingException
     */
    private void processReference(
        Annotation annot,
        String[] constructorNames,
        int pos,
        PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>> type,
        Class<?> param,
        Type genericParam,
        List<String> explicitNames) throws ProcessingException {

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
        } else if (pos < constructorNames.length && constructorNames[pos] != null
            && constructorNames[pos].length() != 0 && !name.equals(constructorNames[pos])) {
            String paramNum = String.valueOf(pos + 1);
            throw new InvalidConstructorException("Name specified by @Constructor does not match reference name",
                paramNum);
        }
        if (type.getReferences().get(name) != null) {
            throw new DuplicateReferenceException(name);
        }
        reference.setName(name);
        boolean required = refAnnotation.required();
        reference.setRequired(required);
        try {
            Class<?> rawType = param;
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
            Class<?> baseType = getBaseType(rawType, genericParam);            
            ServiceContract<?> contract = registry.introspect(baseType);
            reference.setServiceContract(contract);
        } catch (InvalidServiceContractException e) {
            throw new ProcessingException(e);
        }
        type.getReferences().put(name, reference);
        addName(explicitNames, pos, name);
    }

    /**
     * Processes resource metadata for a constructor parameter
     *
     * @param resourceAnnot    the resource annotation
     * @param constructorNames the parameter names as specified in an {@link org.osoa.sca.annotations.Constructor}
     *                         annotation
     * @param pos              the position of the parameter in the constructor's parameter list
     * @param type             the component type associated with the implementation being processed
     * @param param            the parameter type
     * @param explicitNames    the collection of injection names to update
     * @throws ProcessingException
     */
    private <T> void processResource(
        Resource resourceAnnot,
        String[] constructorNames,
        int pos,
        PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>> type,
        Class<T> param,
        List<String> explicitNames) throws ProcessingException {

        org.apache.tuscany.spi.implementation.java.Resource resource =
            new org.apache.tuscany.spi.implementation.java.Resource();
        String name = resourceAnnot.name();
        if (name == null || name.length() == 0) {
            if (constructorNames.length < pos + 1 || constructorNames[pos] == null
                || constructorNames[pos].length() == 0) {
                String paramNum = String.valueOf(pos + 1);
                throw new InvalidResourceException("No name specified for resource parameter", paramNum);
            }
            name = constructorNames[pos];
        } else if (pos < constructorNames.length && constructorNames[pos] != null
            && constructorNames[pos].length() != 0 && !name.equals(constructorNames[pos])) {
            String paramNum = String.valueOf(pos + 1);
            throw new InvalidConstructorException("Name specified by @Constructor does not match resource name",
                paramNum);
        }
        if (type.getResources().get(name) != null) {
            throw new DuplicateResourceException(name);
        }
        resource.setName(name);
        resource.setOptional(resourceAnnot.optional());
        resource.setType(param);
        String mappedName = resourceAnnot.mappedName();
        if (mappedName.length() > 0) {
            resource.setMappedName(mappedName);
        }
        type.add(resource);
        addName(explicitNames, pos, name);
    }

    protected static Class<?> getBaseType(Class<?> cls, Type genericType) {
        if (cls.isArray()) {
            return cls.getComponentType();
        } else if (Collection.class.isAssignableFrom(cls)) {
            if (genericType == cls) {
                return Object.class;
            } else {
                ParameterizedType parameterizedType = (ParameterizedType)genericType;
                Type baseType = parameterizedType.getActualTypeArguments()[0];
                if (baseType instanceof Class) {
                    return (Class<?>)baseType;
                } else if (baseType instanceof ParameterizedType) {
                    return (Class<?>)((ParameterizedType)baseType).getRawType();
                } else {
                    return null;
                }
            }
        } else {
            return cls;
        }
    }        
}
