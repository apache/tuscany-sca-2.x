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
package org.apache.tuscany.sca.implementation.spring.introspect;

import static org.apache.tuscany.sca.implementation.java.introspect.JavaIntrospectionHelper.getAllInterfaces;
import static org.apache.tuscany.sca.implementation.java.introspect.JavaIntrospectionHelper.getAllPublicAndProtectedFields;
import static org.apache.tuscany.sca.implementation.java.introspect.JavaIntrospectionHelper.getAllUniquePublicProtectedMethods;
import static org.apache.tuscany.sca.implementation.java.introspect.JavaIntrospectionHelper.getPrivateFields;
import static org.apache.tuscany.sca.implementation.java.introspect.JavaIntrospectionHelper.toPropertyName;

import java.lang.annotation.Annotation;
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
import org.apache.tuscany.sca.implementation.java.IntrospectionException;
import org.apache.tuscany.sca.implementation.java.JavaConstructorImpl;
import org.apache.tuscany.sca.implementation.java.JavaElementImpl;
import org.apache.tuscany.sca.implementation.java.JavaImplementation;
import org.apache.tuscany.sca.implementation.java.JavaParameterImpl;
import org.apache.tuscany.sca.implementation.java.introspect.BaseJavaClassVisitor;
import org.apache.tuscany.sca.implementation.java.introspect.JavaIntrospectionHelper;
import org.apache.tuscany.sca.implementation.java.introspect.impl.AmbiguousConstructorException;
import org.apache.tuscany.sca.implementation.java.introspect.impl.InvalidServiceTypeException;
import org.apache.tuscany.sca.implementation.java.introspect.impl.NoConstructorException;
import org.apache.tuscany.sca.implementation.java.introspect.impl.Resource;
import org.apache.tuscany.sca.implementation.spring.SpringConstructorArgElement;
import org.apache.tuscany.sca.interfacedef.Interface;
import org.apache.tuscany.sca.interfacedef.InvalidInterfaceException;
import org.apache.tuscany.sca.interfacedef.java.JavaInterface;
import org.apache.tuscany.sca.interfacedef.java.JavaInterfaceContract;
import org.apache.tuscany.sca.interfacedef.java.JavaInterfaceFactory;
import org.apache.tuscany.sca.interfacedef.util.JavaXMLMapper;
import org.oasisopen.sca.annotation.Callback;
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
 * @version $Rev: 689426 $ $Date: 2008-08-27 14:56:06 +0530 (Wed, 27 Aug 2008) $
 */
public class SpringBeanPojoProcessor extends BaseJavaClassVisitor {
    private JavaInterfaceFactory javaFactory;
    private List<SpringConstructorArgElement> conArgs;

    public SpringBeanPojoProcessor(AssemblyFactory assemblyFactory, JavaInterfaceFactory javaFactory, List<SpringConstructorArgElement> conArgs) {
        super(assemblyFactory);
        this.javaFactory = javaFactory;
        this.conArgs = conArgs;
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
        Set<Method> methods = getAllUniquePublicProtectedMethods(clazz, false);
        if (!type.getReferenceMembers().isEmpty() || !type.getPropertyMembers().isEmpty()) {
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

    private boolean isPublicSetter(Method method) {
        return method.getParameterTypes().length == 1 && Modifier.isPublic(method.getModifiers())
            && method.getName().startsWith("set")
            && method.getReturnType() == void.class;
    }

    private boolean isProtectedSetter(Method method) {
        return method.getParameterTypes().length == 1 && Modifier.isProtected(method.getModifiers())
            && method.getName().startsWith("set")
            && method.getReturnType() == void.class;
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
            if (method.isAnnotationPresent(Callback.class) || method.isAnnotationPresent(Context.class)) {
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
                        type.getProperties().add(createProperty(name, param));
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
            if (method.isAnnotationPresent(Callback.class) || method.isAnnotationPresent(Context.class)) {
                // Add the property name as others
                others.add(toPropertyName(method.getName()));
                continue;
            }
            Class<?> param = method.getParameterTypes()[0];
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
                    type.getProperties().add(createProperty(name, param));
                    type.getPropertyMembers().put(name, new JavaElementImpl(method, 0));
                }
            }
        }

        // Public or protected fields unless there is a public or protected
        // setter method
        // for the same name
        Set<Field> fields = getAllPublicAndProtectedFields(clazz, false);
        for (Field field : fields) {
            if (field.isAnnotationPresent(Callback.class) || field.isAnnotationPresent(Context.class)) {
                continue;
            }
            if (setters.contains(field.getName()) || others.contains(field.getName())) {
                continue;
            }
            String name = field.getName();
            Class<?> paramType = field.getType();
            if (isReferenceType(paramType, field.getGenericType())) {
                if (!type.getReferenceMembers().containsKey(name)) {
                    type.getReferences().add(createReference(name, paramType));
                    type.getReferenceMembers().put(name, new JavaElementImpl(field));
                }
            } else {
                if (!type.getPropertyMembers().containsKey(name)) {
                    type.getProperties().add(createProperty(name, paramType));
                    type.getPropertyMembers().put(name, new JavaElementImpl(field));
                }
            }
        }
        
        // Private fields unless there is a public or protected
        // setter method for the same name
        Set<Field> privateFields = getPrivateFields(clazz);
        for (Field field : privateFields) {
            if (field.isAnnotationPresent(Callback.class) || field.isAnnotationPresent(Context.class)) {
                continue;
            }
            if (setters.contains(field.getName()) || others.contains(field.getName())) {
                continue;
            }
            String name = field.getName();
            Class<?> paramType = field.getType();
            if (isReferenceType(paramType, field.getGenericType())) {
                if (!type.getReferenceMembers().containsKey(name)) {
                    type.getReferences().add(createReference(name, paramType));
                    type.getReferenceMembers().put(name, new JavaElementImpl(field));
                }
            } else {
                if (!type.getPropertyMembers().containsKey(name)) {
                    type.getProperties().add(createProperty(name, paramType));
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
        Map<String, JavaElementImpl> props = type.getPropertyMembers();
        Map<String, JavaElementImpl> refs = type.getReferenceMembers();        
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
                throw new NoConstructorException("No public constructor for class");
            } else if (constructors.length == 1) {
                // Only one constructor, take it
                constructor = constructors[0];
            } else {
                // multiple constructors scenario
                Constructor<T> selected = null;                
                for (Constructor<T> ctor : constructors) {
                    if (ctor.getParameterTypes().length == 0) {
                        selected = ctor;
                    } else if (ctor.getParameterTypes().length == conArgs.size()) {
                        // we will find a constructor which has atleast one
                    	// reference or property as its parameter types.
                    	Class<?>[] parametersTypes = ctor.getParameterTypes();
                		for (Class<?> pType: parametersTypes) {
                			for (JavaElementImpl property : props.values()) {            				
                				if (pType.equals(property.getType())) 
                					selected = ctor;
                			}
                			for (JavaElementImpl reference : refs.values()) {
                				if (pType.equals(reference.getType())) 
                					selected = ctor;
                			}               			           			
                		}
                    }
                }
                if (selected == null) {
                    throw new NoConstructorException();
                }
                constructor = selected;
            }
            definition = type.getConstructors().get(constructor);
            type.setConstructor(definition);
        }
        
        JavaParameterImpl[] parameters = definition.getParameters();
        if (parameters.length == 0) {
            return;
        }
        
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
                type.getProperties().add(createProperty(name, p.getType()));
                p.setClassifer(Property.class);
                type.getPropertyMembers().put(name, p);
            }
            p.setName(name);
        }
    }

    private static boolean areUnique(Class[] collection) {
        Set<Class> set = new HashSet<Class>(Arrays.asList(collection));
        return set.size() == collection.length;
    }

    /**
     * Returns true if the union of the given collections of properties and
     * references have unique Java types
     */
    private boolean calcPropRefUniqueness(Collection<JavaElementImpl> props, Collection<JavaElementImpl> refs) {

        Class[] classes = new Class[props.size() + refs.size()];
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
    private boolean isReferenceType(Class<?> cls, Type genericType) {
        Class<?> baseType = JavaIntrospectionHelper.getBaseType(cls, genericType);
        return baseType.isInterface() && baseType.isAnnotationPresent(Remotable.class);
    }

    /**
     * Returns true if the given operation is defined in the collection of
     * service interfaces
     */
    private boolean isInServiceInterface(Method operation, List<org.apache.tuscany.sca.assembly.Service> services) {
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
    private boolean isMethodMatched(Class<?> clazz, Method method) {
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
     * Creates a mapped property.
     * 
     * @param name the property name
     * @param paramType the property type
     */
    private org.apache.tuscany.sca.assembly.Property createProperty(String name, Class<?> paramType) {
        org.apache.tuscany.sca.assembly.Property property = assemblyFactory.createProperty();
        property.setName(name);
        property.setXSDType(JavaXMLMapper.getXMLType(paramType));
        return property;
    }

    private boolean isAnnotated(JavaParameterImpl parameter) {
        for (Annotation annotation : parameter.getAnnotations()) {
            Class<? extends Annotation> annotType = annotation.annotationType();
            if (annotType.equals(Property.class) || annotType.equals(Reference.class)
                || annotType.equals(Resource.class)) {
                return true;
            }
        }
        return false;
    }

    public boolean areUnique(JavaParameterImpl[] parameters) {
        Set<Class> set = new HashSet<Class>(parameters.length);
        for (JavaParameterImpl p : parameters) {
            if (!set.add(p.getType())) {
                return false;
            }
        }
        return true;
    }

    public org.apache.tuscany.sca.assembly.Reference createReference(String name, Class<?> paramType)
        throws IntrospectionException {
        org.apache.tuscany.sca.assembly.Reference reference = assemblyFactory.createReference();
        reference.setName(name);
        JavaInterfaceContract interfaceContract = javaFactory.createJavaInterfaceContract();
        reference.setInterfaceContract(interfaceContract);
        try {
            JavaInterface callInterface = javaFactory.createJavaInterface(paramType);
            reference.getInterfaceContract().setInterface(callInterface);
            if (callInterface.getCallbackClass() != null) {
                JavaInterface callbackInterface = javaFactory.createJavaInterface(callInterface.getCallbackClass());
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

    public org.apache.tuscany.sca.assembly.Service createService(Class<?> interfaze) throws InvalidInterfaceException {
        org.apache.tuscany.sca.assembly.Service service = assemblyFactory.createService();
        service.setName(interfaze.getSimpleName());

        JavaInterfaceContract interfaceContract = javaFactory.createJavaInterfaceContract();
        service.setInterfaceContract(interfaceContract);

        JavaInterface callInterface = javaFactory.createJavaInterface(interfaze);
        service.getInterfaceContract().setInterface(callInterface);
        if (callInterface.getCallbackClass() != null) {
            JavaInterface callbackInterface = javaFactory.createJavaInterface(callInterface.getCallbackClass());
            service.getInterfaceContract().setCallbackInterface(callbackInterface);
        }

        Interface javaInterface = service.getInterfaceContract().getInterface();
        javaInterface.setRemotable(interfaze.getAnnotation(Remotable.class) != null);
        service.getInterfaceContract().setInterface(javaInterface);
        return service;
    }

    public void processCallback(Class<?> interfaze, Contract contract) throws InvalidServiceTypeException {
        Callback callback = interfaze.getAnnotation(Callback.class);
        if (callback != null && !Void.class.equals(callback.value())) {
            Class<?> callbackClass = callback.value();
            JavaInterface javaInterface;
            try {
                javaInterface = javaFactory.createJavaInterface(callbackClass);
                contract.getInterfaceContract().setCallbackInterface(javaInterface);
            } catch (InvalidInterfaceException e) {
                throw new InvalidServiceTypeException("Invalid callback interface "+callbackClass, interfaze);
            }
        } else if (callback != null && Void.class.equals(callback.value())) {
            throw new InvalidServiceTypeException("No callback interface specified on annotation", interfaze);
        }
    }

    public boolean injectionAnnotationsPresent(Annotation[][] annots) {
        for (Annotation[] annotations : annots) {
            for (Annotation annotation : annotations) {
                Class<? extends Annotation> annotType = annotation.annotationType();
                if (annotType.equals(Property.class) || annotType.equals(Reference.class)
                    || annotType.equals(Resource.class)) {
                    return true;
                }
            }
        }
        return false;
    }
}
