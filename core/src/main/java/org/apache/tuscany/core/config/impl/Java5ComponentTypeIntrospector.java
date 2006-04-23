/**
 *
 *  Copyright 2005 The Apache Software Foundation or its licensors, as applicable.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.apache.tuscany.core.config.impl;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.List;
import java.util.ArrayList;

import org.osoa.sca.annotations.Callback;
import org.osoa.sca.annotations.Remotable;

import org.apache.tuscany.core.config.ComponentTypeIntrospector;
import org.apache.tuscany.core.config.ConfigurationLoadException;
import org.apache.tuscany.core.config.InvalidSetterException;
import org.apache.tuscany.core.config.JavaIntrospectionHelper;
import org.apache.tuscany.core.config.AnnotationProcessor;
import org.apache.tuscany.core.system.annotation.Autowire;
import org.apache.tuscany.core.system.assembly.SystemAssemblyFactory;
import org.apache.tuscany.model.assembly.AssemblyFactory;
import org.apache.tuscany.model.assembly.ComponentInfo;
import org.apache.tuscany.model.assembly.Multiplicity;
import org.apache.tuscany.model.assembly.Property;
import org.apache.tuscany.model.assembly.Reference;
import org.apache.tuscany.model.assembly.Scope;
import org.apache.tuscany.model.assembly.Service;
import org.apache.tuscany.model.assembly.ServiceContract;
import org.apache.tuscany.model.types.java.JavaServiceContract;

/**
 * Introspects Java annotation-based metata data
 *
 * @version $Rev$ $Date$
 */
@org.osoa.sca.annotations.Service(interfaces = {ComponentTypeIntrospector.class})
public class Java5ComponentTypeIntrospector implements ComponentTypeIntrospector {
    private AssemblyFactory factory;
    private List<AnnotationProcessor> processors = new ArrayList<AnnotationProcessor>();

    public Java5ComponentTypeIntrospector() {
    }

    public Java5ComponentTypeIntrospector(AssemblyFactory factory) {
        this.factory = factory;
    }

    @Autowire
    public void setFactory(SystemAssemblyFactory factory) {
        this.factory = factory;
    }

    public void registerProcessor(AnnotationProcessor processor) {
        processors.add(processor);
    }

    public void unregisterProcessor(AnnotationProcessor processor) {
       processors.remove(processor);
    }


    public ComponentInfo introspect(Class<?> implClass) throws ConfigurationLoadException {
        ComponentInfo compType = factory.createComponentInfo();
        introspectServices(compType, implClass);
        introspectAnnotatedMembers(compType, implClass);

        // if implementation is not annotated and no annotated members were found, add public fields and setters
        if (!implClass.isAnnotationPresent(org.osoa.sca.annotations.Service.class) && compType.getProperties().isEmpty()
                && compType.getReferences().isEmpty()) {
            introspectMembers(compType, implClass);
        }

        // FIXME scopes should be handled at the interface level
        if (compType != null) {
            Scope scope = getScope(implClass);
            for (Service service : compType.getServices()) {
                ServiceContract intf = service.getServiceContract();
                if (intf != null)
                    intf.setScope(scope);
            }
        }

        return compType;
    }

    /**
     * Returns the scope for a given class
     *
     */
    private static Scope getScope(Class<?> implClass) {
        org.osoa.sca.annotations.Scope scope = implClass.getAnnotation(org.osoa.sca.annotations.Scope.class);
        if (scope == null) {
            // scope was not defined on the implementation class, look for annotated interfaces
            Class<?>[] interfaces = implClass.getInterfaces();
            for (Class<?> anInterface : interfaces) {
                scope = anInterface.getAnnotation(org.osoa.sca.annotations.Scope.class);
            }
        }
        if (scope == null) {
            return Scope.INSTANCE;
        }

        if ("MODULE".equalsIgnoreCase(scope.value())) {
            return (Scope.MODULE);
        } else if ("SESSION".equalsIgnoreCase(scope.value())) {
            return (Scope.SESSION);
        } else if ("REQUEST".equalsIgnoreCase(scope.value())) {
            return (Scope.REQUEST);
        } else {
            return (Scope.INSTANCE);
        }
    }

    /**
     * Adds the supported services for a component implementation type to its component type
     *
     * @param compType  the component type being generated
     * @param implClass the component implementation type class
     */
    protected void introspectServices(ComponentInfo compType, Class<?> implClass) {
        List<Service> services = compType.getServices();
        assert services.isEmpty() : "componentType already has services defined";

        // add services defined in an @Service annotation
        org.osoa.sca.annotations.Service serviceAnnotation = implClass.getAnnotation(org.osoa.sca.annotations.Service.class);
        if (serviceAnnotation != null) {
            Class<?>[] interfaces = serviceAnnotation.interfaces();
            Class<?> value = serviceAnnotation.value();
            if (interfaces.length > 0) {
                if (!Void.class.equals(value)) {
                    throw new IllegalArgumentException("Both interfaces and value specified in @Service on "
                            + implClass.getName());
                }
                for (Class<?> intf : interfaces) {
                    addService(services, intf);
                }
                return;
            } else if (!Void.class.equals(value)) {
                addService(services, value);
                return;
            }
        }

        // no @Service annotation, add all implemented interfaces with an @Remotable annotation
        for (Class<?> intf : implClass.getInterfaces()) {
            if (intf.isAnnotationPresent(Remotable.class)) {
                addService(services, intf);
            }
        }

        // if no Remotable interfaces were specified, the class itself is the service
        if (services.isEmpty()) {
            addService(services, implClass);
        }
    }

    /**
     * Recursively adds supported services to a component type by walking the class hierarchy
     *
     */
    protected void addService(List<Service> services, Class<?> serviceClass) {
        JavaServiceContract javaInterface = factory.createJavaServiceContract();
        javaInterface.setInterface(serviceClass);
        Callback callback = serviceClass.getAnnotation(Callback.class);
        if (callback != null && !Void.class.equals(callback.value())) {
            javaInterface.setCallbackInterface(callback.value());
        }

        String name = JavaIntrospectionHelper.getBaseName(serviceClass);
        Service service = factory.createService();
        service.setName(name);
        service.setServiceContract(javaInterface);
        services.add(service);
    }

    /**
     * Root method for determining public field and method metadata
     *
     * @throws ConfigurationLoadException
     */
    protected void introspectAnnotatedMembers(ComponentInfo compType, Class<?> implClass) throws ConfigurationLoadException {

        introspectPublicFields(compType, implClass);
        introspectPrivateFields(compType, implClass);

        introspectPublicMethods(compType, implClass);
        introspectPrivateMethods(compType, implClass);
    }

    /**
     * Introspects metdata for all public fields and methods for a class hierarchy
     *
     */
    protected void introspectMembers(ComponentInfo compType, Class<?> implClass) {
        List<Property> properties = compType.getProperties();
        List<Reference> references = compType.getReferences();

        // inspect public fields from class and all superclasses
        Field[] fields = implClass.getFields();
        for (Field field : fields) {
            if (Modifier.isStatic(field.getModifiers())) {
                continue;
            }
            if (field.getType().isAnnotationPresent(Remotable.class)) {
                addReference(references, field);
            } else {
                addProperty(properties, field);
            }
        }

        // add public methods from class and all superclasses
        Method[] methods = implClass.getMethods();
        for (Method method : methods) {
            if (Void.class.equals(method.getReturnType()) && method.getName().startsWith("set")
                    && method.getParameterTypes().length == 1
                    && !method.getParameterTypes()[0].isAnnotationPresent(Remotable.class)) {
                String name = method.getName();
                name = Character.toLowerCase(name.charAt(3)) + name.substring(4);
                Class<?> type = method.getParameterTypes()[0];
                if (type.isAnnotationPresent(Remotable.class)) {
                    addReference(references, name, type, false);
                } else {
                    addProperty(properties, name, type, false);
                }
            }
        }
    }

    private void introspectPublicFields(ComponentInfo compType, Class<?> implClass) {
        List<Property> properties = compType.getProperties();
        List<Reference> references = compType.getReferences();

        // inspect public fields from class and all superclasses
        Field[] fields = implClass.getFields();
        for (Field field : fields) {
            if (Modifier.isStatic(field.getModifiers())) {
                continue;
            }
            if (field.isAnnotationPresent(org.osoa.sca.annotations.Property.class)) {
                addProperty(properties, field);
            } else if (field.isAnnotationPresent(org.osoa.sca.annotations.Reference.class)) {
                addReference(references, field);
            }
        }
    }

    private void introspectPrivateFields(ComponentInfo compType, Class<?> implClass) {
        List<Property> properties = compType.getProperties();
        List<Reference> references = compType.getReferences();

        // inspect private fields declared in class
        Field[] fields = implClass.getDeclaredFields();
        for (Field field : fields) {
            if (!Modifier.isPrivate(field.getModifiers())) {
                continue;
            }
            if (field.isAnnotationPresent(org.osoa.sca.annotations.Property.class)) {
                addProperty(properties, field);
            } else if (field.isAnnotationPresent(org.osoa.sca.annotations.Reference.class)) {
                addReference(references, field);
            }
        }
    }

    private void introspectPublicMethods(ComponentInfo compType, Class<?> implClass) throws ConfigurationLoadException {
        List<Property> properties = compType.getProperties();
        List<Reference> references = compType.getReferences();

        // add public methods from class and all superclasses
        Method[] methods = implClass.getMethods();
        for (Method method : methods) {
            if (method.isAnnotationPresent(org.osoa.sca.annotations.Property.class)) {
                addProperty(properties, method);
            } else if (method.isAnnotationPresent(org.osoa.sca.annotations.Reference.class)) {
                addReference(references, method);
            }
        }
    }

    private void introspectPrivateMethods(ComponentInfo compType, Class<?> implClass) throws ConfigurationLoadException {
        List<Property> properties = compType.getProperties();
        List<Reference> references = compType.getReferences();

        // add private methods declared on class
        Method[] methods = implClass.getDeclaredMethods();
        for (Method method : methods) {
            if (!Modifier.isPrivate(method.getModifiers())) {
                continue;
            }
            if (method.isAnnotationPresent(org.osoa.sca.annotations.Property.class)) {
                addProperty(properties, method);
            } else if (method.isAnnotationPresent(org.osoa.sca.annotations.Reference.class)) {
                addReference(references, method);
            }
        }
    }

    protected void addProperty(List<Property> properties, Field field) {
        String name;
        boolean required;
        org.osoa.sca.annotations.Property annotation = field.getAnnotation(org.osoa.sca.annotations.Property.class);
        if (annotation != null) {
            name = annotation.name();
            if (name.length() == 0) {
                name = field.getName();
            }
            required = annotation.required();
        } else {
            name = field.getName();
            required = false;
        }
        addProperty(properties, name, field.getType(), required);
    }

    protected void addProperty(List<Property> properties, Method method) throws ConfigurationLoadException {
        if (!Void.class.equals(method.getReturnType())) {
            throw new InvalidSetterException(method.toString());
        }
        Class<?>[] params = method.getParameterTypes();
        if (params.length != 1) {
            throw new InvalidSetterException(method.toString());
        }

        String name;
        boolean required;
        org.osoa.sca.annotations.Property annotation = method.getAnnotation(org.osoa.sca.annotations.Property.class);
        if (annotation != null) {
            name = annotation.name();
            required = annotation.required();
        } else {
            name = "";
            required = false;
        }
        if (name.length() == 0) {
            name = method.getName();
            if (name.length() > 3 && name.startsWith("set")) {
                name = Character.toLowerCase(name.charAt(3)) + name.substring(4);
            }
        }
        addProperty(properties, name, params[0], required);
    }

    protected void addProperty(List<Property> properties, String name, Class<?> type, boolean required) {
        Property prop = factory.createProperty();
        prop.setName(name);
        prop.setType(type);
        prop.setRequired(required);

        // a java.util.Map is not a "many"
        prop.setMany(type.isArray() || Collection.class.isAssignableFrom(type));

        // todo how is the default specified using annotations?
        prop.setDefaultValue(null);

        properties.add(prop);
    }

    protected void addReference(List<Reference> references, Field field) {
        String name;
        boolean required;
        org.osoa.sca.annotations.Reference annotation = field.getAnnotation(org.osoa.sca.annotations.Reference.class);
        if (annotation != null) {
            name = annotation.name();
            if (name.length() == 0) {
                name = field.getName();
            }
            required = annotation.required();
        } else {
            name = field.getName();
            required = false;
        }
        addReference(references, name, field.getType(), required);
    }

    protected void addReference(List<Reference> references, Method method) throws ConfigurationLoadException {
        if (!Void.TYPE.equals(method.getReturnType())) {
            throw new InvalidSetterException(method.toString());
        }
        Class<?>[] params = method.getParameterTypes();
        if (params.length != 1) {
            throw new InvalidSetterException(method.toString());
        }

        String name;
        boolean required;
        org.osoa.sca.annotations.Reference annotation = method.getAnnotation(org.osoa.sca.annotations.Reference.class);
        if (annotation != null) {
            name = annotation.name();
            required = annotation.required();
        } else {
            name = "";
            required = false;
        }
        if (name.length() == 0) {
            name = method.getName();
            if (name.length() > 3 && name.startsWith("set")) {
                name = Character.toLowerCase(name.charAt(3)) + name.substring(4);
            }
        }
        addReference(references, name, params[0], required);
    }

    protected void addReference(List<Reference> references, String name, Class<?> type, boolean required) {
        Reference ref = factory.createReference();
        ref.setName(name);
        boolean many = type.isArray() || Collection.class.isAssignableFrom(type);
        Multiplicity multiplicity;
        if (required)
            multiplicity = many ? Multiplicity.ONE_N : Multiplicity.ONE_ONE;
        else
            multiplicity = many ? Multiplicity.ZERO_N : Multiplicity.ZERO_ONE;
        ref.setMultiplicity(multiplicity);
        ServiceContract javaInterface = factory.createJavaServiceContract();
        javaInterface.setInterface(type);
        ref.setServiceContract(javaInterface);
        references.add(ref);
    }
}
