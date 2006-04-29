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

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Set;

import org.apache.tuscany.core.config.ConfigurationLoadException;
import org.apache.tuscany.core.config.JavaIntrospectionHelper;
import org.apache.tuscany.model.assembly.AssemblyFactory;
import org.apache.tuscany.model.assembly.ComponentInfo;
import org.apache.tuscany.model.assembly.Property;
import org.apache.tuscany.model.assembly.Reference;

/**
 * Adds public methods and public/protected fields as properties that are not declared explicitly with an
 * {@link org.osoa.sca.annotations.Property} or  {@link org.osoa.sca.annotations.Reference} annotation
 *
 * @version $$Rev$$ $$Date$$
 */
public class DefaultProcessor extends ImplementationProcessorSupport {

    public DefaultProcessor(AssemblyFactory factory) {
        super(factory);
    }

    public DefaultProcessor() {
    }

    public void visitEnd(Class<?> clazz, ComponentInfo type) throws ConfigurationLoadException {
        // add any public/protected fields and public setter methods as properties
        Set<Field> fields = JavaIntrospectionHelper.getAllPublicAndProtectedFields(clazz);
        List<Property> properties = type.getProperties();
        List<Reference> references = type.getReferences();
        boolean contains;
        Method[] methods = clazz.getMethods();
        String name;
        for (Method method : methods) {
            if (Object.class.equals(method.getDeclaringClass()) || method.getParameterTypes().length != 1
                    || method.isAnnotationPresent(org.osoa.sca.annotations.Property.class)
                    || method.isAnnotationPresent(org.osoa.sca.annotations.Reference.class)) {
                continue;
            }
            contains = containsProperty(properties, method.getName());
            if (contains) {
                continue;
            }
            name = method.getName();
            if (name.length() > 3 && name.startsWith("set")) {
                // follow JavaBeans conventions
                name = JavaIntrospectionHelper.toPropertyName(name);
            }
            contains = containsReference(references, name);
            if (!contains) {
                addProperty(name, method.getParameterTypes()[0], type);
            }
        }
        for (Field field : fields) {
            if (field.isAnnotationPresent(org.osoa.sca.annotations.Property.class)
                    || field.isAnnotationPresent(org.osoa.sca.annotations.Reference.class)) {
                continue;
            }
            contains = containsProperty(properties, field.getName());
            if (contains) {
                continue;
            }
            contains = containsReference(references, field.getName());
            if (!contains) {
                addProperty(field.getName(), field.getType(), type);
            }
        }
    }

    private boolean containsProperty(List<Property> properties, String name) {
        for (Property prop : properties) {
            if (prop.getName().equals(name)) {
                return true;
            }
        }
        return false;
    }

    private boolean containsReference(List<Reference> references, String name) {
        for (Reference ref : references) {
            if (ref.getName().equals(name)) {
                return true;
            }
        }
        return false;
    }

    private void addProperty(String name, Class<?> propType, ComponentInfo type) {
        Property property = factory.createProperty();
        property.setName(name);
        property.setRequired(false);
        property.setType(propType);
        type.getProperties().add(property);
    }
}
