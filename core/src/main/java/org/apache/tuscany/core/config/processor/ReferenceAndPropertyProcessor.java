/**
 *
 * Copyright 2005 The Apache Software Foundation or its licensors, as applicable.
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
package org.apache.tuscany.core.config.processor;

import org.apache.tuscany.core.config.ConfigurationLoadException;
import org.apache.tuscany.core.config.InvalidSetterException;
import org.apache.tuscany.model.assembly.ComponentInfo;
import org.apache.tuscany.model.assembly.Multiplicity;
import org.apache.tuscany.model.assembly.Property;
import org.apache.tuscany.model.assembly.Reference;
import org.apache.tuscany.model.assembly.ServiceContract;
import org.osoa.sca.annotations.Remotable;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.List;

/**
 * @version $$Rev$$ $$Date$$
 */
public class ReferenceAndPropertyProcessor extends AnnotationProcessorSupport {


    public void visitImplementationMethod(Method method, Annotation annotation, ComponentInfo type) {

    }

    public void visitField(Field field, Annotation annotation, ComponentInfo type) {

    }

    /**
     * Root method for determining public field and method metadata
     *
     * @throws org.apache.tuscany.core.config.ConfigurationLoadException
     *
     */
    protected void introspectAnnotatedMembers(ComponentInfo compType, Class<?> implClass) throws ConfigurationLoadException {

        introspectPublicFields(compType, implClass);
        introspectPrivateFields(compType, implClass);

        introspectPublicMethods(compType, implClass);
        introspectPrivateMethods(compType, implClass);
    }

    /**
     * Introspects metdata for all public fields and methods for a class hierarchy
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
