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

import static org.apache.tuscany.sca.implementation.java.introspect.JavaIntrospectionHelper.getAllInterfaces;
import static org.apache.tuscany.sca.implementation.java.introspect.JavaIntrospectionHelper.getAllPublicAndProtectedFields;
import static org.apache.tuscany.sca.implementation.java.introspect.JavaIntrospectionHelper.getAllUniquePublicProtectedMethods;
import static org.apache.tuscany.sca.implementation.java.introspect.JavaIntrospectionHelper.toPropertyName;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.jws.WebService;

import org.apache.tuscany.sca.assembly.AssemblyFactory;
import org.apache.tuscany.sca.assembly.Contract;
import org.apache.tuscany.sca.assembly.Multiplicity;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.implementation.java.IntrospectionException;
import org.apache.tuscany.sca.implementation.java.JavaConstructorImpl;
import org.apache.tuscany.sca.implementation.java.JavaElementImpl;
import org.apache.tuscany.sca.implementation.java.JavaImplementation;
import org.apache.tuscany.sca.implementation.java.JavaParameterImpl;
import org.apache.tuscany.sca.implementation.java.introspect.BaseJavaClassVisitor;
import org.apache.tuscany.sca.implementation.java.introspect.JavaIntrospectionHelper;
import org.apache.tuscany.sca.interfacedef.Interface;
import org.apache.tuscany.sca.interfacedef.InvalidInterfaceException;
import org.apache.tuscany.sca.interfacedef.java.JavaInterface;
import org.apache.tuscany.sca.interfacedef.java.JavaInterfaceContract;
import org.apache.tuscany.sca.interfacedef.java.JavaInterfaceFactory;
import org.oasisopen.sca.annotation.Callback;
import org.oasisopen.sca.annotation.ComponentName;
import org.oasisopen.sca.annotation.Context;
import org.oasisopen.sca.annotation.Property;
import org.oasisopen.sca.annotation.Reference;
import org.oasisopen.sca.annotation.Remotable;

/**
 * Heuristically evaluates an un-annotated Java implementation type to determine
 * services, references, and properties according to the algorithm described in
 * the SCA Java Client and Implementation Model Specification <p/> TODO
 * Implement: <p/> When no service interface is annotated, need to calculate a
 * single service comprising all public methods that are not reference or
 * property injection sites. If that service can be exactly mapped to an
 * interface implemented by the class then the service interface will be defined
 * in terms of that interface.
 * 
 * @version $Rev$ $Date$
 */
public class HeuristicPojoProcessor extends BaseJavaClassVisitor {

    public HeuristicPojoProcessor(AssemblyFactory assemblyFactory, JavaInterfaceFactory javaFactory) {
        super(assemblyFactory);
        this.javaInterfaceFactory = javaFactory;
    }
    
    public HeuristicPojoProcessor(ExtensionPointRegistry registry) {
        super(registry);
    }    

    @Override
    public <T> void visitEnd(Class<T> clazz, JavaImplementation type) throws IntrospectionException {
        List<org.apache.tuscany.sca.assembly.Service> services = type.getServices();
        if (services.isEmpty()) {
            // heuristically determine the service
            /**
             * The following is quoted from Java Specification 1.2.1.3. Introspecting services offered by a Java implementation
             * In the cases described below, the services offered by a Java implementation class may be determined
             * through introspection, eliding the need to specify them using @Service. The following algorithm is used 
             * to determine how services are introspected from an implementation class:
             * 
             * If the interfaces of the SCA services are not specified with the @Service annotation on the 
             * implementation class, it is assumed that all implemented interfaces that have been annotated 
             * as @Remotable are the service interfaces provided by the component. If none of the implemented 
             * interfaces is remotable, then by default the implementation offers a single service whose type 
             * is the implementation class.
             */
            Set<Class<?>> interfaces = getAllInterfaces(clazz);
            for (Class<?> i : interfaces) {
                if (i.isAnnotationPresent(Remotable.class) || i.isAnnotationPresent(WebService.class)) {
                    addService(type, i);
                }
            }
            if (services.isEmpty()) {
                // class is the interface
                addService(type, clazz);
            }
        }
        if (!(type.getReferenceMembers().isEmpty() && type.getPropertyMembers().isEmpty())) {
            // references and properties have been explicitly defined
            //            if (type.getServices().isEmpty()) {
            //                calculateServiceInterface(clazz, type, methods);
            //                if (type.getServices().isEmpty()) {
            //                    throw new ServiceTypeNotFoundException(clazz.getName());
            //                }
            //            }
            evaluateConstructor(type, clazz);
            return;
        }
        Set<Method> methods = getAllUniquePublicProtectedMethods(clazz, false);

        calcPropRefs(methods, services, type, clazz);
        evaluateConstructor(type, clazz);
    }

    private void addService(JavaImplementation type, Class<?> clazz) throws IntrospectionException {
        try {
            org.apache.tuscany.sca.assembly.Service service = createService(clazz);
            type.getServices().add(service);
        } catch (InvalidInterfaceException e) {
            throw new IntrospectionException(e);
        }
    }
    
    private static boolean isAnnotatedWithSCA(AnnotatedElement element) {
        for (Annotation a : element.getAnnotations()) {
            if (isSCAAnnotation(a)) {
                return true;
            }
        }
        return false;
    }

    private static boolean isSCAAnnotation(Annotation a) {
        return a.annotationType().getName().startsWith("org.oasisopen.sca.annotation.");
    }

    private <T> void calcPropRefs(Set<Method> methods,
                                  List<org.apache.tuscany.sca.assembly.Service> services,
                                  JavaImplementation type,
                                  Class<T> clazz) throws IntrospectionException {
        // heuristically determine the properties references
        // make a first pass through all public methods with one param
        Set<String> setters = new HashSet<String>();
        Set<String> others = new HashSet<String>();
        for (Method method : methods) {
            if (!isPublicSetter(method)) {
                continue;
            }
            if (isAnnotatedWithSCA(method)) {
                // Add the property name as others
                others.add(toPropertyName(method.getName()));
                continue;
            }
            if (!isInServiceInterface(method, services)) {
                // Not part of the service interface
                String name = toPropertyName(method.getName());
                setters.add(name);
                // avoid duplicate property or ref names
                if (!type.getPropertyMembers().containsKey(name) && !type.getReferenceMembers().containsKey(name)) {
                    Class<?> param = method.getParameterTypes()[0];
                    Type genericType = method.getGenericParameterTypes()[0];
                    if (isReferenceType(param, genericType)) {
                        type.getReferences().add(createReference(name, param));
                        type.getReferenceMembers().put(name, new JavaElementImpl(method, 0));
                    } else {
                        type.getProperties().add(createProperty(name, param, genericType));
                        type.getPropertyMembers().put(name, new JavaElementImpl(method, 0));
                    }
                }
            }
        }
        // second pass for protected methods with one param
        for (Method method : methods) {
            if (!isProtectedSetter(method)) {
                continue;
            }
            if (isAnnotatedWithSCA(method)) {
                // Add the property name as others
                others.add(toPropertyName(method.getName()));
                continue;
            }
            Class<?> param = method.getParameterTypes()[0];
            Type paramType = method.getGenericParameterTypes()[0];
            
            String name = toPropertyName(method.getName());
            setters.add(name);
            // avoid duplicate property or ref names
            if (isReferenceType(param, method.getGenericParameterTypes()[0])) {
                if (!type.getReferenceMembers().containsKey(name)) {
                    type.getReferences().add(createReference(name, param));
                    type.getReferenceMembers().put(name, new JavaElementImpl(method, 0));
                }
            } else {
                if (!type.getPropertyMembers().containsKey(name)) {
                    type.getProperties().add(createProperty(name, param, paramType));
                    type.getPropertyMembers().put(name, new JavaElementImpl(method, 0));
                }
            }
        }

        // Public or protected fields unless there is a public or protected
        // setter method
        // for the same name
        Set<Field> fields = getAllPublicAndProtectedFields(clazz, false);
        for (Field field : fields) {
            if (isAnnotatedWithSCA(field)) {
                continue;
            }
            if (setters.contains(field.getName()) || others.contains(field.getName())) {
                continue;
            }
            String name = field.getName();
            Class<?> paramType = field.getType();
            Type genericType = field.getGenericType();
            if (isReferenceType(paramType, field.getGenericType())) {
                if (!type.getReferenceMembers().containsKey(name)) {
                    type.getReferences().add(createReference(name, paramType));
                    type.getReferenceMembers().put(name, new JavaElementImpl(field));
                }
            } else {
                if (!type.getPropertyMembers().containsKey(name)) {
                    type.getProperties().add(createProperty(name, paramType, genericType));
                    type.getPropertyMembers().put(name, new JavaElementImpl(field));
                }
            }
        }
    }

    /**
     * Determines the constructor to use based on the component type's
     * references and properties
     * 
     * @param type the component type
     * @param clazz the implementation class corresponding to the component type
     * @throws NoConstructorException if no suitable constructor is found
     * @throws AmbiguousConstructorException if the parameters of a constructor
     *             cannot be unambiguously mapped to references and properties
     */
    @SuppressWarnings("unchecked")
    private <T> void evaluateConstructor(JavaImplementation type, Class<T> clazz) throws IntrospectionException {
        // determine constructor if one is not annotated
        JavaConstructorImpl<?> definition = type.getConstructor();
        Constructor constructor;
        boolean explict = false;
        if (definition != null && definition.getConstructor()
            .isAnnotationPresent(org.oasisopen.sca.annotation.Constructor.class)) {
            // the constructor was already defined explicitly
            return;
        } else if (definition != null) {
            explict = true;
            constructor = definition.getConstructor();
        } else {
            // no definition, heuristically determine constructor
            Constructor[] constructors = clazz.getConstructors();
            if (constructors.length == 0) {
                throw new NoConstructorException("[JCI50001] No public constructor for class");
            } else if (constructors.length == 1) {
                // Only one constructor, take it
                constructor = constructors[0];
            } else {
                Constructor<T> selected = null;
                for (Constructor<T> ctor : constructors) {
                    if (allArgsAnnotated(ctor)) {
                        selected = ctor;
                        for (Constructor<T> ctor2 : constructors) {
                            if (selected != ctor2 && allArgsAnnotated(ctor2)) {
                                throw new InvalidConstructorException("[JCI50005] Multiple annotated constructors");
                            }
                        }
                    }
                }
                if (selected == null) {
                    for (Constructor<T> ctor : constructors) {
                        if (ctor.getParameterTypes().length == 0) {
                            selected = ctor;
                            break;
                        }
                    }
                }
                if (selected == null) {
                    throw new NoConstructorException();
                }
                constructor = selected;
                definition = type.getConstructors().get(selected);
                type.setConstructor(definition);
            }
            definition = type.getConstructors().get(constructor);
            type.setConstructor(definition);
        }
        JavaParameterImpl[] parameters = definition.getParameters();
        if (parameters.length == 0) {
            return;
        }
        Map<String, JavaElementImpl> props = type.getPropertyMembers();
        Map<String, JavaElementImpl> refs = type.getReferenceMembers();
        Annotation[][] annotations = constructor.getParameterAnnotations();
        if (!explict) {
            // the constructor wasn't defined by an annotation, so check to see
            // if any of the params have an annotation
            // which we can impute as explicitly defining the constructor, e.g.
            // @Property, @Reference, or @Autowire
            explict = injectionAnnotationsPresent(annotations);
        }
        if (explict) {
            for (int i = 0; i < parameters.length; i++) {
                if (isAnnotated(parameters[i])) {
                    continue;
                } else if (!findReferenceOrProperty(parameters[i], props, refs)) {
                    throw new AmbiguousConstructorException(parameters[i].toString());
                }
            }
        } else {
            if (!areUnique(parameters)) {
                throw new AmbiguousConstructorException("Cannot resolve non-unique parameter types, use @Constructor");
            }
            if (!calcPropRefUniqueness(props.values(), refs.values())) {
                throw new AmbiguousConstructorException("Cannot resolve non-unique parameter types, use @Constructor");
            }
            if (!(props.isEmpty() && refs.isEmpty())) {
                calcParamNames(parameters, props, refs);
            } else {
                heuristicParamNames(type, parameters);

            }
        }
    }
    
    private boolean allArgsAnnotated(Constructor<?> ctor) {
        if (ctor.getParameterTypes().length < 1) {
            return false;
        }
        for (Annotation[] as : ctor.getParameterAnnotations()) {
           if (as.length < 1) {
               return false;
           }
        }
        return true;
    }

    private void calcParamNames(JavaParameterImpl[] parameters,
                                Map<String, JavaElementImpl> props,
                                Map<String, JavaElementImpl> refs) throws AmbiguousConstructorException {
        // the constructor param types must unambiguously match defined
        // reference or property types
        for (JavaParameterImpl param : parameters) {
            if (!findReferenceOrProperty(param, props, refs)) {
                throw new AmbiguousConstructorException(param.getName());
            }
        }
    }

    private void heuristicParamNames(JavaImplementation type, JavaParameterImpl[] parameters)
        throws IntrospectionException {
        // heuristically determine refs and props from the parameter types
        for (JavaParameterImpl p : parameters) {
            String name = p.getType().getSimpleName().toLowerCase();
            if (isReferenceType(p.getType(), p.getGenericType())) {
                type.getReferences().add(createReference(name, p.getType()));
                p.setClassifer(Reference.class);
                type.getReferenceMembers().put(name, p);
            } else {
                type.getProperties().add(createProperty(name, p.getType(), p.getGenericType()));
                p.setClassifer(Property.class);
                type.getPropertyMembers().put(name, p);
            }
            p.setName(name);
        }
    }



    /**
     * Returns true if the union of the given collections of properties and
     * references have unique Java types
     */
    private boolean calcPropRefUniqueness(Collection<JavaElementImpl> props, Collection<JavaElementImpl> refs) {

        Class<?>[] classes = new Class[props.size() + refs.size()];
        int i = 0;
        for (JavaElementImpl property : props) {
            classes[i] = property.getType();
            i++;
        }
        for (JavaElementImpl reference : refs) {
            classes[i] = reference.getType();
            i++;
        }
        return areUnique(classes);
    }

    /**
     * Unambiguously finds the reference or property associated with the given
     * type
     * 
     * @return the name of the reference or property if found, null if not
     * @throws AmbiguousConstructorException if the constructor parameter cannot
     *             be resolved to a property or reference
     */
    private boolean findReferenceOrProperty(JavaParameterImpl parameter,
                                            Map<String, JavaElementImpl> props,
                                            Map<String, JavaElementImpl> refs) throws AmbiguousConstructorException {

        boolean found = false;
        if (!"".equals(parameter.getName())) {
            // Match by name
            JavaElementImpl prop = props.get(parameter.getName());
            if (prop != null && prop.getType() == parameter.getType()) {
                parameter.setClassifer(Property.class);
                return true;
            }
            JavaElementImpl ref = refs.get(parameter.getName());
            if (ref != null && ref.getType() == parameter.getType()) {
                parameter.setClassifer(Reference.class);
                return true;
            }
        }
        for (JavaElementImpl property : props.values()) {
            if (property.getType() == parameter.getType()) {
                if (found) {
                    throw new AmbiguousConstructorException("Ambiguous property or reference for constructor type",
                                                            (Member)parameter.getAnchor());
                }
                parameter.setClassifer(Property.class);
                parameter.setName(property.getName());
                found = true;
                // do not break since ambiguities must be checked, i.e. more
                // than one prop or ref of the same type
            }
        }
        for (JavaElementImpl reference : refs.values()) {
            if (reference.getType() == parameter.getType()) {
                if (found) {
                    throw new AmbiguousConstructorException("Ambiguous property or reference for constructor type",
                                                            (Member)parameter.getAnchor());
                }
                parameter.setClassifer(Reference.class);
                parameter.setName(reference.getName());
                found = true;
                // do not break since ambiguities must be checked, i.e. more
                // than one prop or ref of the same type
            }
        }
        return found;
    }

    /**
     * Creates a mapped property.
     * 
     * @param name the property name
     * @param paramType the property type
     */
    private org.apache.tuscany.sca.assembly.Property createProperty(String name, Class<?> javaClass, Type genericType) {
        return AbstractPropertyProcessor.createProperty(assemblyFactory, name, javaClass, genericType);
    }

     private org.apache.tuscany.sca.assembly.Reference createReference(String name, Class<?> paramType)
        throws IntrospectionException {
        org.apache.tuscany.sca.assembly.Reference reference = assemblyFactory.createReference();
        reference.setName(name);
        JavaInterfaceContract interfaceContract = javaInterfaceFactory.createJavaInterfaceContract();
        reference.setInterfaceContract(interfaceContract);
        try {
            JavaInterface callInterface = javaInterfaceFactory.createJavaInterface(paramType);
            reference.getInterfaceContract().setInterface(callInterface);
            if (callInterface.getCallbackClass() != null) {
                JavaInterface callbackInterface = javaInterfaceFactory.createJavaInterface(callInterface.getCallbackClass());
                reference.getInterfaceContract().setCallbackInterface(callbackInterface);
            }
            reference.setMultiplicity(Multiplicity.ZERO_ONE);
        } catch (InvalidInterfaceException e1) {
            throw new IntrospectionException(e1);
        }

        // FIXME:  This part seems to have already been taken care above!!
        try {
            processCallback(paramType, reference);
        } catch (InvalidServiceTypeException e) {
            throw new IntrospectionException(e);
        }
        return reference;
    }

    private org.apache.tuscany.sca.assembly.Service createService(Class<?> interfaze) throws InvalidInterfaceException {
        org.apache.tuscany.sca.assembly.Service service = assemblyFactory.createService();
        service.setName(interfaze.getSimpleName());

        JavaInterfaceContract interfaceContract = javaInterfaceFactory.createJavaInterfaceContract();
        service.setInterfaceContract(interfaceContract);

        JavaInterface callInterface = javaInterfaceFactory.createJavaInterface(interfaze);
        service.getInterfaceContract().setInterface(callInterface);
        if (callInterface.getCallbackClass() != null) {
            JavaInterface callbackInterface = javaInterfaceFactory.createJavaInterface(callInterface.getCallbackClass());
            service.getInterfaceContract().setCallbackInterface(callbackInterface);
        }

        Interface javaInterface = service.getInterfaceContract().getInterface();
        javaInterface.setRemotable(interfaze.getAnnotation(Remotable.class) != null);
        service.getInterfaceContract().setInterface(javaInterface);
        return service;
    }

    private void processCallback(Class<?> interfaze, Contract contract) throws InvalidServiceTypeException {
        Callback callback = interfaze.getAnnotation(Callback.class);
        if (callback != null && !Void.class.equals(callback.value())) {
            Class<?> callbackClass = callback.value();
            JavaInterface javaInterface;
            try {
                javaInterface = javaInterfaceFactory.createJavaInterface(callbackClass);
                contract.getInterfaceContract().setCallbackInterface(javaInterface);
            } catch (InvalidInterfaceException e) {
                throw new InvalidServiceTypeException("Invalid callback interface "+callbackClass, interfaze);
            }
        } else if (callback != null && Void.class.equals(callback.value())) {
            throw new InvalidServiceTypeException("No callback interface specified on annotation", interfaze);
        }
    }


    /**
     * Utility methods
     */
    
    
    /**
     * Verify if the method is a public setter
     * @param method
     * @return
     */
    private static boolean isPublicSetter(Method method) {
        return method.getParameterTypes().length == 1 && Modifier.isPublic(method.getModifiers())
            && method.getName().startsWith("set")
            && method.getReturnType() == void.class;
    }

    /**
     * Verify if the method is a protected setter
     * @param method
     * @return
     */
    private static boolean isProtectedSetter(Method method) {
        return method.getParameterTypes().length == 1 && Modifier.isProtected(method.getModifiers())
            && method.getName().startsWith("set")
            && method.getReturnType() == void.class;
    }

    /**
     * @param collection
     * @return
     */
    private static boolean areUnique(Class<?>[] collection) {
        Set<Class<?>> set = new HashSet<Class<?>>(Arrays.asList(collection));
        return set.size() == collection.length;
    }

    /**
     * Returns true if a given type is reference according to the SCA
     * specification rules for determining reference types The following rules
     * are used to determine whether an unannotated field or setter method is a
     * property or reference:
     * <ol>
     * <li>If its type is simple, then it is a property.
     * <li>If its type is complex, then if the type is an interface marked by
     * 
     * @Remotable, then it is a reference; otherwise, it is a property.
     *             <li>Otherwise, if the type associated with the member is an
     *             array or a java.util.Collection, the basetype is the element
     *             type of the array or the parameterized type of the
     *             Collection; otherwise the basetype is the member type. If the
     *             basetype is an interface with an
     * @Remotable or
     * @Service annotation then the member is defined as a reference. Otherwise,
     *          it is defined as a property.
     *          </ol>
     *          <p>
     *          The name of the reference or of the property is derived from the
     *          name found on the setter method or on the field.
     */
    // FIXME: [rfeng] What if it's a collection of references?
    private static boolean isReferenceType(Class<?> cls, Type genericType) {
        Class<?> baseType = JavaIntrospectionHelper.getBaseType(cls, genericType);
        return baseType.isInterface() && baseType.isAnnotationPresent(Remotable.class);
    }

    /**
     * Returns true if the given operation is defined in the collection of
     * service interfaces
     * @param operation
     * @param services
     * @return
     */
    private static boolean isInServiceInterface(Method operation, List<org.apache.tuscany.sca.assembly.Service> services) {
        for (org.apache.tuscany.sca.assembly.Service service : services) {
            Interface interface1 = service.getInterfaceContract().getInterface();
            if (interface1 instanceof JavaInterface) {
                Class<?> clazz = ((JavaInterface)interface1).getJavaClass();
                if (isMethodMatched(clazz, operation)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Test if the class declares a method which matches the signature of the
     * given method
     * 
     * @param clazz
     * @param method
     * @return
     */
    private static boolean isMethodMatched(Class<?> clazz, Method method) {
        if (method.getDeclaringClass() == clazz) {
            return true;
        }
        Method[] methods = clazz.getMethods();
        for (Method m : methods) {
            if (JavaIntrospectionHelper.exactMethodMatch(method, m)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Verify if there is any SCA annotation on the parameter
     * @param parameter
     * @return
     */
    private static boolean isAnnotated(JavaParameterImpl parameter) {
        for (Annotation annotation : parameter.getAnnotations()) {
            if (isSCAAnnotation(annotation)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Verify if the parameters are unique
     * @param parameters
     * @return
     */
    private static boolean areUnique(JavaParameterImpl[] parameters) {
        Set<Class<?>> set = new HashSet<Class<?>>(parameters.length);
        for (JavaParameterImpl p : parameters) {
            if (!set.add(p.getType())) {
                return false;
            }
        }
        return true;
    }
    
    /**
     * Verify if the annotations are SCA annotation
     * @param annots
     * @return
     */
    private static boolean injectionAnnotationsPresent(Annotation[][] annots) {
        for (Annotation[] annotations : annots) {
            for (Annotation annotation : annotations) {
                Class<? extends Annotation> annotType = annotation.annotationType();
                if (annotType == Property.class || annotType == Reference.class
                    || annotType == Resource.class
                    || annotType == ComponentName.class
                    || annotType == Context.class
                    || annotType == Callback.class) {
                    return true;
                }
            }
        }
        return false;
    }
}
