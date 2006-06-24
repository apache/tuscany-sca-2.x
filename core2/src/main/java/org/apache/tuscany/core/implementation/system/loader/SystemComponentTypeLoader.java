/**
 *
 * Copyright 2006 The Apache Software Foundation
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
package org.apache.tuscany.core.implementation.system.loader;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URL;

import org.apache.tuscany.spi.deployer.DeploymentContext;
import org.apache.tuscany.spi.extension.ComponentTypeLoaderExtension;
import org.apache.tuscany.spi.loader.LoaderException;
import org.apache.tuscany.core.implementation.JavaMappedProperty;
import org.apache.tuscany.core.implementation.JavaMappedReference;
import org.apache.tuscany.core.implementation.JavaServiceContract;
import org.apache.tuscany.spi.model.ServiceDefinition;

import org.apache.tuscany.core.implementation.PojoComponentType;
import org.apache.tuscany.core.implementation.system.model.SystemImplementation;
import org.apache.tuscany.core.util.JavaIntrospectionHelper;

/**
 * Loads a system component type
 *
 * @version $Rev: 416228 $ $Date: 2006-06-21 19:53:17 -0700 (Wed, 21 Jun 2006) $
 */
public class SystemComponentTypeLoader extends ComponentTypeLoaderExtension<SystemImplementation> {
    protected Class<SystemImplementation> getImplementationClass() {
        return SystemImplementation.class;
    }

    public void load(SystemImplementation implementation, DeploymentContext deploymentContext) throws LoaderException {
        Class<?> implClass = implementation.getImplementationClass();
        URL sidefile = implClass.getResource(JavaIntrospectionHelper.getBaseName(implClass) + ".componentType");
        PojoComponentType componentType;
        if (sidefile == null) {
            componentType = loadByIntrospection(implementation);
        } else {
            componentType = loadFromSidefile(sidefile, deploymentContext);
        }
        implementation.setComponentType(componentType);
    }

    protected PojoComponentType loadByIntrospection(SystemImplementation implementation) {
        PojoComponentType componentType = new PojoComponentType();

        // FIXME: replace this rudimentary introspection mechanism
        Class<?> implClass = implementation.getImplementationClass();
        for (Class<?> serviceIntf : implClass.getInterfaces()) {
            JavaServiceContract serviceContract = new JavaServiceContract(serviceIntf);
            ServiceDefinition service = new ServiceDefinition(serviceIntf.getName(), serviceContract, false);
            componentType.add(service);
        }
        for (Field field : implClass.getFields()) {
            if (Modifier.isStatic(field.getModifiers())) {
                continue;
            }
            String name = field.getName();
            Class<?> javaType = field.getType();
            if (javaType.isInterface()) {
                JavaMappedReference reference = new JavaMappedReference(name, new JavaServiceContract(javaType), field);
                componentType.add(reference);
            } else {
                JavaMappedProperty<?> property = new JavaMappedProperty(name, null, javaType, field);
                componentType.add(property);
            }
        }
        for (Method method : implClass.getMethods()) {
            if (Modifier.isStatic(method.getModifiers())
                || !(Void.TYPE == method.getReturnType())
                || method.getParameterTypes().length != 1
                || !method.getName().startsWith("set")
                || !(method.getName().length() > 3)
                ) {
                continue;
            }
            String name = method.getName();
            name = Character.toLowerCase(name.charAt(3)) + name.substring(4);
            Class<?> javaType = method.getParameterTypes()[0];
            if (javaType.isInterface()) {
                JavaMappedReference reference =
                    new JavaMappedReference(name, new JavaServiceContract(javaType), method);
                componentType.add(reference);
            } else {
                JavaMappedProperty<?> property = new JavaMappedProperty(name, null, javaType, method);
                componentType.add(property);
            }
        }

        return componentType;
    }


    protected PojoComponentType loadFromSidefile(URL url, DeploymentContext deploymentContext) throws LoaderException {
        return loaderRegistry.load(url, PojoComponentType.class, deploymentContext);
    }
}
