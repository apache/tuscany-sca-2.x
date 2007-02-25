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
import java.util.ArrayList;
import java.util.List;

import org.apache.tuscany.spi.ObjectFactory;
import org.apache.tuscany.spi.builder.BuilderConfigException;
import org.apache.tuscany.spi.component.Component;
import org.apache.tuscany.spi.deployer.DeploymentContext;
import org.apache.tuscany.spi.extension.ComponentBuilderExtension;
import org.apache.tuscany.spi.implementation.java.JavaMappedProperty;
import org.apache.tuscany.spi.model.ComponentDefinition;
import org.apache.tuscany.spi.model.ComponentType;
import org.apache.tuscany.spi.model.PropertyValue;

/**
 * Builds a {@link DataSourceComponent} from its model representation
 *
 * @version $Rev$ $Date$
 */
public class DataSourceBuilder extends ComponentBuilderExtension<DataSourceImplementation> {
    protected Class<DataSourceImplementation> getImplementationType() {
        return DataSourceImplementation.class;
    }

    public Component build(ComponentDefinition<DataSourceImplementation> definition,
                           DeploymentContext deploymentContext) throws BuilderConfigException {
        try {
            DataSourceImplementation implementation = definition.getImplementation();
            ClassLoader classLoader = implementation.getClassLoader();
            Class<?> beanClass = classLoader.loadClass(implementation.getProviderName());
            List<Injector> injectors = new ArrayList<Injector>();
            // handle properties
            ComponentType type = definition.getImplementation().getComponentType();
            for (PropertyValue<?> property : definition.getPropertyValues().values()) {
                ObjectFactory<?> factory = property.getValueFactory();
                if (factory != null) {
                    String name = property.getName();
                    JavaMappedProperty mappedProperty = (JavaMappedProperty) type.getProperties().get(name);
                    if (mappedProperty == null) {
                        throw new MissingPropertyException(name);
                    }
                    Injector injector = new Injector((Method) mappedProperty.getMember(), factory);
                    injectors.add(injector);
                }
            }
            ProviderObjectFactory providerFactory = new ProviderObjectFactory(beanClass, injectors);
            int initLevel = implementation.getComponentType().getInitLevel();
            return new DataSourceComponent(definition.getUri(), providerFactory, initLevel);
        } catch (ClassNotFoundException e) {
            throw new BuilderConfigException(e);
        }
    }

}
