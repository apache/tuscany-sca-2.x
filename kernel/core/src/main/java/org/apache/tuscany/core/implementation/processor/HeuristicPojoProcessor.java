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
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.osoa.sca.annotations.Reference;
import org.osoa.sca.annotations.Remotable;
import org.osoa.sca.annotations.Service;

import org.apache.tuscany.spi.databinding.extension.SimpleTypeMapperExtension;
import org.apache.tuscany.spi.deployer.DeploymentContext;
import org.apache.tuscany.spi.idl.InvalidServiceContractException;
import org.apache.tuscany.spi.idl.TypeInfo;
import org.apache.tuscany.spi.implementation.java.ConstructorDefinition;
import org.apache.tuscany.spi.implementation.java.ImplementationProcessorExtension;
import org.apache.tuscany.spi.implementation.java.ImplementationProcessorService;
import org.apache.tuscany.spi.implementation.java.JavaMappedProperty;
import org.apache.tuscany.spi.implementation.java.JavaMappedReference;
import org.apache.tuscany.spi.implementation.java.JavaMappedService;
import org.apache.tuscany.spi.implementation.java.PojoComponentType;
import org.apache.tuscany.spi.implementation.java.ProcessingException;

import static org.apache.tuscany.core.util.JavaIntrospectionHelper.getAllInterfaces;
import static org.apache.tuscany.core.util.JavaIntrospectionHelper.getAllPublicAndProtectedFields;
import static org.apache.tuscany.core.util.JavaIntrospectionHelper.getAllUniquePublicProtectedMethods;
import static org.apache.tuscany.core.util.JavaIntrospectionHelper.getBaseName;
import static org.apache.tuscany.core.util.JavaIntrospectionHelper.toPropertyName;

/**
 * Heuristically evaluates an un-annotated Java implementation type to determine services, references, and properties
 * according to the algorithm described in the SCA Java Client and Implementation Model Specification <p/> TODO
 * Implement:
 * <p/>
 * When no service inteface is annotated, need to calculate a single service comprising all public methods that are not
 * reference or property injection sites. If that service can be exactly mapped to an interface implemented by the class
 * then the service interface will be defined in terms of that interface.
 *
 * @version $Rev$ $Date$
 */
public class HeuristicPojoProcessor extends ImplementationProcessorExtension {
    private SimpleTypeMapperExtension typeMapper = new SimpleTypeMapperExtension();
    private ImplementationProcessorService implService;

    public HeuristicPojoProcessor(@Reference ImplementationProcessorService service) {
        this.implService = service;
    }

    public <T> void visitEnd(
        Class<T> clazz,
        PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>> type,
        DeploymentContext context) throws ProcessingException {
        Map<String, JavaMappedService> services = type.getServices();
        if (services.isEmpty()) {
            // heuristically determine the service
            // TODO finish algorithm
            Set<Class> interfaces = getAllInterfaces(clazz);
            if (interfaces.size() == 0) {
                // class is the interface
                JavaMappedService service;
                try {
                    service = implService.createService(clazz);
                } catch (InvalidServiceContractException e) {
                    throw new ProcessingException(e);
                }
                type.getServices().put(service.getUri().getFragment(), service);
            } else if (interfaces.size() == 1) {
                JavaMappedService service;
                try {
                    service = implService.createService(interfaces.iterator().next());
                } catch (InvalidServiceContractException e) {
                    throw new ProcessingException(e);
                }
                type.getServices().put(service.getUri().getFragment(), service);
            }
        }
        Set<Method> methods = getAllUniquePublicProtectedMethods(clazz);
        if (!type.getReferences().isEmpty() || !type.getProperties().isEmpty()) {
            // references and properties have been explicitly defined
            if (type.getServices().isEmpty()) {
                calculateServiceInterface(clazz, type, methods);
                if (type.getServices().isEmpty()) {
                    throw new ServiceTypeNotFoundException(clazz.getName());
                }
            }
            evaluateConstructor(type, clazz);
            return;
        }
        calcPropRefs(methods, services, type, clazz);
        evaluateConstructor(type, clazz);
    }

    private <T> void calcPropRefs(Set<Method> methods,
                                  Map<String, JavaMappedService> services,
                                  PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>> type,
                                  Class<T> clazz) throws ProcessingException {
        // heuristically determine the properties references
        // make a first pass through all public methods with one param
        for (Method method : methods) {
            if (method.getParameterTypes().length != 1 || !Modifier.isPublic(method.getModifiers())
                || !method.getName().startsWith("set")
                || method.getReturnType() != void.class) {
                continue;
            }
            if (!isInServiceInterface(method, services)) {
                String name = toPropertyName(method.getName());
                // avoid duplicate property or ref names
                if (!type.getProperties().containsKey(name) && !type.getReferences().containsKey(name)) {
                    Class<?> param = method.getParameterTypes()[0];
                    Type genericType = method.getGenericParameterTypes()[0];
                    if (isReferenceType(genericType)) {
                        type.add(createReference(name, method, param));
                    } else {
                        type.add(createProperty(name, method, param));
                    }
                }
            }
        }
        // second pass for protected methods with one param
        for (Method method : methods) {
            if (method.getParameterTypes().length != 1 || !Modifier.isProtected(method.getModifiers())
                || !method.getName().startsWith("set")
                || method.getReturnType() != void.class) {
                continue;
            }
            Class<?> param = method.getParameterTypes()[0];
            String name = toPropertyName(method.getName());
            // avoid duplicate property or ref names
            if (!type.getProperties().containsKey(name) && !type.getReferences().containsKey(name)) {
                if (isReferenceType(param)) {
                    type.add(createReference(name, method, param));
                } else {
                    type.add(createProperty(name, method, param));
                }
            }
        }
        Set<Field> fields = getAllPublicAndProtectedFields(clazz);
        for (Field field : fields) {
            Class<?> paramType = field.getType();
            if (isReferenceType(paramType)) {
                type.add(createReference(field.getName(), field, paramType));
            } else {
                type.add(createProperty(field.getName(), field, paramType));
            }
        }
    }

    /**
     * Determines the constructor to use based on the component type's references and properties
     *
     * @param type  the component type
     * @param clazz the implementation class corresponding to the component type
     * @throws NoConstructorException        if no suitable constructor is found
     * @throws AmbiguousConstructorException if the parameters of a constructor cannot be unambiguously mapped to
     *                                       references and properties
     */
    @SuppressWarnings("unchecked")
    private <T> void evaluateConstructor(
        PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>> type,
        Class<T> clazz) throws ProcessingException {
        // determine constructor if one is not annotated
        ConstructorDefinition<?> definition = type.getConstructorDefinition();
        Constructor constructor;
        boolean explict = false;
        if (definition != null
            && definition.getConstructor().getAnnotation(org.osoa.sca.annotations.Constructor.class) != null) {
            // the constructor was already defined explicitly
            return;
        } else if (definition != null) {
            explict = true;
            constructor = definition.getConstructor();
        } else {
            // no definition, heuristically determine constructor
            Constructor[] constructors = clazz.getConstructors();
            if (constructors.length == 0) {
                throw new NoConstructorException("No public constructor for class", clazz.getName());
            } else if (constructors.length == 1) {
                constructor = constructors[0];
            } else {
                // FIXME multiple constructors, none yet done
                Constructor<T> selected = null;
                int sites = type.getProperties().size() + type.getReferences().size();
                for (Constructor<T> ctor : constructors) {
                    if (ctor.getParameterTypes().length == 0) {
                        selected = ctor;
                    }
                    if (ctor.getParameterTypes().length == sites) {
                        // TODO finish
                        // selected = constructor;
                        // select constructor
                        // break;
                    }
                }
                if (selected == null) {
                    throw new NoConstructorException();
                }
                constructor = selected;
                definition = new ConstructorDefinition<T>(selected);
                type.setConstructorDefinition(definition);
                // return;
            }
            definition = new ConstructorDefinition<T>(constructor);
            type.setConstructorDefinition(definition);
        }
        Class[] params = constructor.getParameterTypes();
        if (params.length == 0) {
            return;
        }
        List<String> paramNames = definition.getInjectionNames();
        Map<String, JavaMappedProperty<?>> props = type.getProperties();
        Map<String, JavaMappedReference> refs = type.getReferences();
        Annotation[][] annotations = constructor.getParameterAnnotations();
        if (!explict) {
            // the constructor wasn't defined by an annotation, so check to see if any of the params have an annotation
            // which we can impute as explicitly defining the constructor, e.g. @Property, @Reference, or @Autowire
            explict = implService.injectionAnnotationsPresent(annotations);
        }
        if (explict) {
            for (int i = 0; i < params.length; i++) {
                Class param = params[i];
                implService.processParam(param,
                    constructor.getGenericParameterTypes()[i],
                    annotations[i],
                    new String[0],
                    i,
                    type,
                    paramNames);
            }
        } else {
            if (!implService.areUnique(params)) {
                throw new AmbiguousConstructorException("Cannot resolve non-unique parameter types, use @Constructor");
            }
            if (!calcPropRefUniqueness(props.values(), refs.values())) {
                throw new AmbiguousConstructorException("Cannot resolve non-unique parameter types, use @Constructor");
            }
            boolean empty = props.size() + refs.size() == 0;
            if (!empty) {
                calcParamNames(params, props, refs, paramNames);
            } else {
                heuristicParamNames(params, refs, props, paramNames);

            }
        }
    }

    private void calcParamNames(Class[] params,
                                Map<String, JavaMappedProperty<?>> props,
                                Map<String, JavaMappedReference> refs,
                                List<String> paramNames)
        throws AmbiguousConstructorException {
        // the constructor param types must unambiguously match defined reference or property types
        for (Class param : params) {
            String name = findReferenceOrProperty(param, props, refs);
            if (name == null) {
                throw new AmbiguousConstructorException(param.getName());
            }
            paramNames.add(name);
        }
    }

    private void heuristicParamNames(Class[] params,
                                     Map<String, JavaMappedReference> refs,
                                     Map<String, JavaMappedProperty<?>> props,
                                     List<String> paramNames)
        throws ProcessingException {
        // heuristically determine refs and props from the parameter types
        for (Class<?> param : params) {
            String name = getBaseName(param).toLowerCase();
            if (isReferenceType(param)) {
                refs.put(name, createReference(name, null, param));
            } else {
                props.put(name, createProperty(name, null, param));
            }
            paramNames.add(name);
        }
    }

    /**
     * Returns true if the union of the given collections of properties and references have unique Java types
     */
    private boolean calcPropRefUniqueness(
        Collection<JavaMappedProperty<?>> props,
        Collection<JavaMappedReference> refs) {

        Class[] classes = new Class[props.size() + refs.size()];
        int i = 0;
        for (JavaMappedProperty<?> property : props) {
            classes[i] = property.getJavaType();
            i++;
        }
        for (JavaMappedReference reference : refs) {
            classes[i] = reference.getServiceContract().getInterfaceClass();
            i++;
        }
        return implService.areUnique(classes);
    }

    /**
     * Unambiguously finds the reference or property associated with the given type
     *
     * @return the name of the reference or property if found, null if not
     * @throws AmbiguousConstructorException if the constructor parameter cannot be resolved to a property or reference
     */
    private String findReferenceOrProperty(
        Class<?> type,
        Map<String, JavaMappedProperty<?>> props,
        Map<String, JavaMappedReference> refs) throws AmbiguousConstructorException {

        String name = null;
        for (JavaMappedProperty<?> property : props.values()) {
            if (property.getJavaType().equals(type)) {
                if (name != null) {
                    throw new AmbiguousConstructorException("Ambiguous property or reference for constructor type",
                        type.getName());
                }
                name = property.getName();
                // do not break since ambiguities must be checked, i.e. more than one prop or ref of the same type
            }
        }
        for (JavaMappedReference reference : refs.values()) {
            if (reference.getServiceContract().getInterfaceClass().equals(type)) {
                if (name != null) {
                    throw new AmbiguousConstructorException("Ambiguous property or reference for constructor type",
                        type.getName());
                }
                name = reference.getUri().getFragment();
                // do not break since ambiguities must be checked, i.e. more than one prop or ref of the same type
            }
        }
        return name;
    }

    /**
     * Returns true if a given type is reference according to the SCA specification rules for determining reference
     * types
     */
    private boolean isReferenceType(Type operationType) {
        Class<?> rawType;
        Class<?> referenceType = null;
        if (operationType instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) operationType;
            rawType = (Class<?>) parameterizedType.getRawType();
            Type[] typeArgs = parameterizedType.getActualTypeArguments();
            if (typeArgs.length == 1) {
                referenceType = (Class<?>) typeArgs[0];
            }
        } else {
            rawType = (Class<?>) operationType;
        }
        if (rawType.isArray()) {
            referenceType = rawType.getComponentType();
        } else if (Collection.class.isAssignableFrom(rawType) && referenceType == null) {
            return true;
        }
        if (referenceType != null) {
            return referenceType.getAnnotation(Remotable.class) != null
                || referenceType.getAnnotation(Service.class) != null;
        } else {
            return rawType.getAnnotation(Remotable.class) != null || rawType.getAnnotation(Service.class) != null;
        }
    }

    /**
     * Returns true if the given operation is defined in the collection of service interfaces
     */
    private boolean isInServiceInterface(Method operation, Map<String, JavaMappedService> services) {
        for (JavaMappedService service : services.values()) {
            Class<?> clazz = service.getServiceContract().getInterfaceClass();
            if (operation.getDeclaringClass().equals(clazz)) {
                return true;
            }
            Method[] methods = service.getServiceContract().getInterfaceClass().getMethods();
            for (Method method : methods) {
                if (operation.getName().equals(method.getName())
                    && operation.getParameterTypes().length == method.getParameterTypes().length) {
                    Class<?>[] methodTypes = method.getParameterTypes();
                    for (int i = 0; i < operation.getParameterTypes().length; i++) {
                        Class<?> paramType = operation.getParameterTypes()[i];
                        if (!paramType.equals(methodTypes[i])) {
                            break;
                        } else if (i == operation.getParameterTypes().length - 1) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    /**
     * Creates a mapped reference
     *
     * @param name      the reference name
     * @param member    the injection site the reference maps to
     * @param paramType the service interface of the reference
     */
    private JavaMappedReference createReference(String name, Member member, Class<?> paramType)
        throws ProcessingException {
        return implService.createReference(name, member, paramType);
    }

    /**
     * Creates a mapped property
     *
     * @param name      the property name
     * @param member    the injection site the reference maps to
     * @param paramType the property type
     */
    private <T> JavaMappedProperty<T> createProperty(String name, Member member, Class<T> paramType) {
        JavaMappedProperty<T> property = new JavaMappedProperty<T>();
        property.setName(name);
        property.setMember(member);
        property.setJavaType(paramType);
        TypeInfo xmlType = typeMapper.getXMLType(paramType);
        if (xmlType != null) {
            property.setXmlType(xmlType.getQName());
        }

        return property;
    }

    /**
     * Populates a component type with a service whose interface type is determined by examining all implemented
     * interfaces of the given class and chosing one whose operations match all of the class's non-property and
     * non-reference methods
     *
     * @param clazz   the class to examine
     * @param type    the component type
     * @param methods all methods in the class to examine
     */
    private void calculateServiceInterface(
        Class<?> clazz,
        PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>> type,
        Set<Method> methods) throws ProcessingException {
        List<Method> nonPropRefMethods = new ArrayList<Method>();
        // Map<String, JavaMappedService> services = type.getServices();
        Map<String, JavaMappedReference> references = type.getReferences();
        Map<String, JavaMappedProperty<?>> properties = type.getProperties();
        // calculate methods that are not properties or references
        for (Method method : methods) {
            String name = toPropertyName(method.getName());
            if (!references.containsKey(name) && !properties.containsKey(name)) {
                nonPropRefMethods.add(method);
            }
        }
        // determine if an implemented interface matches all of the non-property and non-reference methods
        Class[] interfaces = clazz.getInterfaces();
        if (interfaces.length == 0) {
            return;
        }
        for (Class interfaze : interfaces) {
            if (analyzeInterface(interfaze, nonPropRefMethods)) {
                JavaMappedService service;
                try {
                    service = implService.createService(interfaze);
                } catch (InvalidServiceContractException e) {
                    throw new ProcessingException(e);
                }
                type.getServices().put(service.getUri().getFragment(), service);
            }
        }
    }

    /**
     * Determines if the methods of a given interface match the given list of methods
     *
     * @param interfaze         the interface to examine
     * @param nonPropRefMethods the list of methods to match against
     * @return true if the interface matches
     */
    private boolean analyzeInterface(Class<?> interfaze, List<Method> nonPropRefMethods) {
        Method[] interfaceMethods = interfaze.getMethods();
        if (nonPropRefMethods.size() != interfaceMethods.length) {
            return false;
        }
        for (Method method : nonPropRefMethods) {
            boolean found = false;
            for (Method interfaceMethod : interfaceMethods) {
                if (interfaceMethod.getName().equals(method.getName())) {
                    Class<?>[] interfaceParamTypes = interfaceMethod.getParameterTypes();
                    Class<?>[] methodParamTypes = method.getParameterTypes();
                    if (interfaceParamTypes.length == methodParamTypes.length) {
                        if (interfaceParamTypes.length == 0) {
                            found = true;
                        } else {
                            for (int i = 0; i < methodParamTypes.length; i++) {
                                Class<?> param = methodParamTypes[i];
                                if (!param.equals(interfaceParamTypes[i])) {
                                    break;
                                }
                                if (i == methodParamTypes.length - 1) {
                                    found = true;
                                }
                            }
                        }
                    }
                    if (found) {
                        break;
                    }
                }
            }
            if (!found) {
                return false;
            }
        }
        return true;
    }

}

/*
 * 1) public setter methods that are not included in any service interface 2) protected setter methods 3) public or
 * protected fields unless there is a setter method for the same name If the type associated with the member is an array
 * or a java.util.Collection, then the basetype will be the element type of the array or the parameterized type of the
 * Collection, otherwise the basetype will be the member type. If the basetype is an interface with an @Remotable or
 * @Service annotation then the member will be defined as a reference, otherwise it will be defined as a property.
 * 
 * 
 */
