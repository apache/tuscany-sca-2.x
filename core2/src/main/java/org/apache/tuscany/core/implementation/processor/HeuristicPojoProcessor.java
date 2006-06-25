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

import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

import org.osoa.sca.annotations.Remotable;
import org.osoa.sca.annotations.Service;

import org.apache.tuscany.spi.deployer.DeploymentContext;

import org.apache.tuscany.core.implementation.ImplementationProcessorSupport;
import org.apache.tuscany.core.implementation.JavaMappedProperty;
import org.apache.tuscany.core.implementation.JavaMappedReference;
import org.apache.tuscany.core.implementation.JavaMappedService;
import org.apache.tuscany.core.implementation.JavaServiceContract;
import org.apache.tuscany.core.implementation.PojoComponentType;
import org.apache.tuscany.core.implementation.ProcessingException;
import static org.apache.tuscany.core.implementation.processor.ProcessorUtils.createService;
import static org.apache.tuscany.core.util.JavaIntrospectionHelper.getAllUniquePublicProtectedMethods;
import static org.apache.tuscany.core.util.JavaIntrospectionHelper.getBaseName;
import static org.apache.tuscany.core.util.JavaIntrospectionHelper.toPropertyName;

/**
 * Heuristically evaluates an un-annotated Java implementation type to determine services, references, and properties
 * according to the algorithm described in the SCA Java Client and Implementation Model Specification
 *
 * @version $Rev$ $Date$
 */
public class HeuristicPojoProcessor extends ImplementationProcessorSupport {
    public void visitEnd(Class<?> clazz,
                         PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>> type,
                         DeploymentContext context) throws ProcessingException {
        Map<String, JavaMappedService> services = type.getServices();
        if (services.isEmpty()) {
            // heuristically determine the service
            // TODO finish algorithm
            Class[] interfaces = clazz.getInterfaces();
            if (interfaces.length == 0) {
                // class is the interface
                throw new UnsupportedOperationException("Classes not yet supported as interfaces");
            } else if (interfaces.length == 1) {
                JavaMappedService service = createService(interfaces[0]);
                type.getServices().put(service.getName(), service);
            }
        }

        if (!type.getReferences().isEmpty() || !type.getProperties().isEmpty()) {
            // references and properties have been explicitly defined
            return;
        }

        // heuristically determine the properties references
        Set<Method> methods = getAllUniquePublicProtectedMethods(clazz);
        // make a first pass through all public methods with one param
        for (Method method : methods) {
            if (method.getParameterTypes().length != 1 || !Modifier.isPublic(method.getModifiers())) {
                continue;
            }
            if (!isInServiceInterface(method, services)) {
                String name = method.getName();
                if (name.startsWith("set")) {
                    name = toPropertyName(name);
                }
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
            String name = method.getName();
            if (name.startsWith("set")) {
                name = toPropertyName(name);
            }
            // avoid duplicate property or ref names
            if (type.getProperties().get(name) == null && type.getReferences().get(name) == null) {
                if (isReferenceType(param)) {
                    type.add(createReference(name, method, param));
                } else {
                    type.add(createProperty(name, method, param));
                }
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

    private JavaMappedProperty createProperty(String name, Member member, Class<?> paramType) {
        JavaMappedProperty property = new JavaMappedProperty();
        property.setName(name);
        property.setMember(member);
        property.setRequired(false);
        property.setJavaType(paramType);
        return property;
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
