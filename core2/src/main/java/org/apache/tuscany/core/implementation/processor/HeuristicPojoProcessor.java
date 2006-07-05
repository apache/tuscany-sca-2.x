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

import org.osoa.sca.annotations.Remotable;
import org.osoa.sca.annotations.Service;

import org.apache.tuscany.spi.component.CompositeComponent;
import org.apache.tuscany.spi.deployer.DeploymentContext;

import org.apache.tuscany.core.implementation.ImplementationProcessorSupport;
import org.apache.tuscany.core.implementation.JavaMappedProperty;
import org.apache.tuscany.core.implementation.JavaMappedReference;
import org.apache.tuscany.core.implementation.JavaMappedService;
import org.apache.tuscany.core.implementation.JavaServiceContract;
import org.apache.tuscany.core.implementation.PojoComponentType;
import org.apache.tuscany.core.implementation.ProcessingException;
import static org.apache.tuscany.core.implementation.processor.ProcessorUtils.createService;
import static org.apache.tuscany.core.util.JavaIntrospectionHelper.getAllInterfaces;
import static org.apache.tuscany.core.util.JavaIntrospectionHelper.getAllPublicAndProtectedFields;
import static org.apache.tuscany.core.util.JavaIntrospectionHelper.getAllUniquePublicProtectedMethods;
import static org.apache.tuscany.core.util.JavaIntrospectionHelper.getBaseName;
import static org.apache.tuscany.core.util.JavaIntrospectionHelper.toPropertyName;

/**
 * Heuristically evaluates an un-annotated Java implementation type to determine services, references, and properties
 * according to the algorithm described in the SCA Java Client and Implementation Model Specification
 * <p/>
 * TODO Implement:<p> When no service inteface is annotated, need to calculate a single service comprising all public
 * methods that are not reference or property injection sites. If that service can be exactly mapped to an interface
 * implemented by the class then the service interface will be defined in terms of that interface.
 *
 * @version $Rev$ $Date$
 */
public class HeuristicPojoProcessor extends ImplementationProcessorSupport {
    public void visitEnd(CompositeComponent<?> parent, Class<?> clazz,
                         PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>> type,
                         DeploymentContext context) throws ProcessingException {
        Map<String, JavaMappedService> services = type.getServices();
        if (services.isEmpty()) {
            // heuristically determine the service
            // TODO finish algorithm
            //clazz.get()
            Set<Class> interfaces = getAllInterfaces(clazz);
            if (interfaces.size() == 0) {
                // class is the interface
                throw new UnsupportedOperationException(
                    "Classes not yet supported as interfaces [" + clazz.getName() + "]");
            } else if (interfaces.size() == 1) {
                JavaMappedService service = createService(interfaces.iterator().next());
                type.getServices().put(service.getName(), service);
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
            return;
        }

        // heuristically determine the properties references
        // make a first pass through all public methods with one param
        for (Method method : methods) {
            if (method.getParameterTypes().length != 1 || !Modifier.isPublic(method.getModifiers())) {
                continue;
            }
            if (!isInServiceInterface(method, services)) {
                String name = toPropertyName(method.getName());
                // avoid duplicate property or ref names
                if (type.getProperties().get(name) == null && type.getReferences().get(name) == null) {
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
            if (method.getParameterTypes().length != 1 || !Modifier.isProtected(method.getModifiers())) {
                continue;
            }
            Class<?> param = method.getParameterTypes()[0];
            String name = toPropertyName(method.getName());
            // avoid duplicate property or ref names
            if (type.getProperties().get(name) == null && type.getReferences().get(name) == null) {
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

    private JavaMappedReference createReference(String name, Member member, Class<?> paramType) {
        JavaMappedReference reference = new JavaMappedReference();
        reference.setName(name);
        reference.setMember(member);
        reference.setRequired(false);
        JavaServiceContract contract = new JavaServiceContract();
        String interfaceName = getBaseName(paramType);
        contract.setInterfaceName(interfaceName);
        contract.setInterfaceClass(paramType);
        reference.setServiceContract(contract);
        return reference;
    }

    private <T> JavaMappedProperty<T> createProperty(String name, Member member, Class<T> paramType) {
        JavaMappedProperty<T> property = new JavaMappedProperty<T>();
        property.setName(name);
        property.setMember(member);
        property.setRequired(false);
        property.setJavaType(paramType);
        return property;
    }

    private void calculateServiceInterface(
        Class<?> clazz,
        PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>> type,
        Set<Method> methods) {
        List<Method> nonPropRefMethods = new ArrayList<Method>();
        //Map<String, JavaMappedService> services = type.getServices();
        Map<String, JavaMappedReference> references = type.getReferences();
        Map<String, JavaMappedProperty<?>> properties = type.getProperties();
        // calculate methods that are not properties or references
        for (Method method : methods) {
            String name = toPropertyName(method.getName());
            if (references.get(name) == null && properties.get(name) == null) {
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
                JavaMappedService service = createService(interfaze);
                type.getServices().put(service.getName(), service);
            }
        }
    }

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
1) public setter methods that are not included in any service interface
2) protected setter methods
3) public or protected fields unless there is a setter method for the same name
If the type associated with the member is an array or a
java.util.Collection, then the basetype will be the element type of
the array or the parameterized type of the Collection, otherwise the
basetype will be the member type. If the basetype is an interface with
an @Remotable or @Service annotation then the member will be defined
as a reference, otherwise it will be defined as a property.


*/
