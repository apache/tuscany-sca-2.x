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
import java.lang.reflect.Method;

import org.apache.tuscany.spi.annotation.Monitor;
import org.apache.tuscany.spi.component.CompositeComponent;
import org.apache.tuscany.spi.deployer.DeploymentContext;

import org.apache.tuscany.core.component.AutowireComponent;
import org.apache.tuscany.core.implementation.ImplementationProcessorSupport;
import org.apache.tuscany.core.implementation.JavaMappedProperty;
import org.apache.tuscany.core.implementation.JavaMappedReference;
import org.apache.tuscany.core.implementation.JavaMappedService;
import org.apache.tuscany.core.implementation.PojoComponentType;
import org.apache.tuscany.core.implementation.ProcessingException;
import org.apache.tuscany.core.injection.MonitorObjectFactory;
import static org.apache.tuscany.core.util.JavaIntrospectionHelper.toPropertyName;

/**
 * Processes the {@link Monitor} annotation, creating a property on the component type
 *
 * @version $Rev$ $Date$
 */
public class MonitorProcessor extends ImplementationProcessorSupport {

    public void visitMethod(CompositeComponent<?> parent, Method method,
                            PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>> type,
                            DeploymentContext context) throws IllegalPropertyException {
        Monitor annotation = method.getAnnotation(Monitor.class);
        if (annotation == null) {
            return;
        }
        assert parent instanceof AutowireComponent;
        if (method.getParameterTypes().length != 1) {
            IllegalPropertyException e =
                new IllegalPropertyException("Monitor setters must take exactly one parameter");
            e.setIdentifier(method.getName());
            throw e;
        }
        AutowireComponent autowireComponent = (AutowireComponent) parent;
        String name = toPropertyName(method.getName());
        Class<?> monitorType = method.getParameterTypes()[0];
        JavaMappedProperty property = new JavaMappedProperty();
        property.setName(name);
        property.setMember(method);
        property.setJavaType(monitorType);
        property.setDefaultValueFactory(new MonitorObjectFactory(autowireComponent, monitorType));
        type.getProperties().put(name, property);
    }

    public void visitField(CompositeComponent<?> parent, Field field,
                           PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>> type,
                           DeploymentContext context) throws ProcessingException {

        Monitor annotation = field.getAnnotation(Monitor.class);
        if (annotation == null) {
            return;
        }
        assert parent instanceof AutowireComponent;
        AutowireComponent autowireComponent = (AutowireComponent) parent;
        String name = field.getName();
        Class<?> monitorType = field.getType();
        JavaMappedProperty property = new JavaMappedProperty();
        property.setName(name);
        property.setMember(field);
        property.setJavaType(monitorType);
        property.setDefaultValueFactory(new MonitorObjectFactory(autowireComponent, monitorType));
        type.getProperties().put(name, property);
    }
}
