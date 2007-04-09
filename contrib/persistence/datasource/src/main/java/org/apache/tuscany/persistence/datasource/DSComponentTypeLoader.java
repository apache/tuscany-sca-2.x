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
package org.apache.tuscany.persistence.datasource;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.net.URI;
import javax.sql.DataSource;

import org.osoa.sca.annotations.Reference;

import org.apache.tuscany.spi.databinding.extension.SimpleTypeMapperExtension;
import org.apache.tuscany.spi.deployer.DeploymentContext;
import org.apache.tuscany.spi.extension.ComponentTypeLoaderExtension;
import org.apache.tuscany.spi.model.TypeInfo;
import org.apache.tuscany.spi.idl.java.JavaServiceContract;
import org.apache.tuscany.spi.implementation.java.JavaMappedProperty;
import org.apache.tuscany.spi.loader.LoaderException;
import org.apache.tuscany.spi.loader.LoaderRegistry;
import org.apache.tuscany.spi.model.ComponentType;
import org.apache.tuscany.spi.model.Property;
import org.apache.tuscany.spi.model.ReferenceDefinition;
import org.apache.tuscany.spi.model.Scope;
import org.apache.tuscany.spi.model.ServiceDefinition;

/**
 * Loads the component type for a DataSource component. Component type information is currently static, although this
 * could be changed in the future to allow for configuration parameter checking for specific providers.
 *
 * @version $Rev$ $Date$
 */
public class DSComponentTypeLoader extends ComponentTypeLoaderExtension<DataSourceImplementation> {
    private SimpleTypeMapperExtension extension = new SimpleTypeMapperExtension();

    public DSComponentTypeLoader(@Reference LoaderRegistry loaderRegistry) {
        super(loaderRegistry);
    }

    protected Class<DataSourceImplementation> getImplementationClass() {
        return DataSourceImplementation.class;
    }

    public void load(DataSourceImplementation implementation, DeploymentContext ctx)
        throws LoaderException {
        ComponentType<ServiceDefinition, ReferenceDefinition, Property<?>> componentType =
            new ComponentType<ServiceDefinition, ReferenceDefinition, Property<?>>();
        componentType.setImplementationScope(Scope.COMPOSITE);
        JavaServiceContract serviceContract = new JavaServiceContract(DataSource.class);
        ServiceDefinition service = new ServiceDefinition(URI.create("#DataSource"), serviceContract, false);
        componentType.add(service);
        componentType.setInitLevel(1);
        Class<?> provider;
        try {
            provider = implementation.getClassLoader().loadClass(implementation.getProviderName());
        } catch (ClassNotFoundException e) {
            throw new LoaderException(e);
        }
        introspectProperties(componentType, provider);
        implementation.setComponentType(componentType);
    }

    /**
     * Creates properties by introspecting the provider class an d including all JavaBean setters that take a simple
     * type parameter
     *
     * @param componentType
     * @param provider
     * @throws AmbiguousPropertyException
     */
    @SuppressWarnings("unchecked")
    private void introspectProperties(ComponentType<ServiceDefinition, ReferenceDefinition, Property<?>> componentType,
                                      Class<?> provider) throws AmbiguousPropertyException {

        Method[] methods = provider.getMethods();
        for (Method method : methods) {
            String name = method.getName();
            if (method.getParameterTypes().length == 1 && name.startsWith("set")) {
                String propName = Character.toLowerCase(name.charAt(3)) + name.substring(4);
                Class<Type> type = (Class<Type>) method.getParameterTypes()[0];
                TypeInfo info = extension.getXMLType(type);
                if (info != null) {
                    // only include methods as properties that take simple type parameters
                    if (componentType.getProperties().containsKey(propName)) {
                        throw new AmbiguousPropertyException(propName);
                    }
                    JavaMappedProperty<Type> property =
                        new JavaMappedProperty<Type>(propName, info.getQName(), type);
                    property.setMember(method);
                    componentType.add(property);
                }
            }
        }
    }

}
