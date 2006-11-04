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

import java.beans.PropertyEditor;
import java.beans.PropertyEditorManager;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.tuscany.spi.builder.BuilderConfigException;
import org.apache.tuscany.spi.component.Component;
import org.apache.tuscany.spi.component.CompositeComponent;
import org.apache.tuscany.spi.component.ScopeContainer;
import org.apache.tuscany.spi.deployer.DeploymentContext;
import org.apache.tuscany.spi.extension.ComponentBuilderExtension;
import org.apache.tuscany.spi.model.ComponentDefinition;

/**
 * Builds a {@link DataSourceComponent} from its model representation
 *
 * @version $Rev$ $Date$
 */
public class DataSourceBuilder extends ComponentBuilderExtension<DataSourceImplementation> {
    protected Class<DataSourceImplementation> getImplementationType() {
        return DataSourceImplementation.class;
    }

    public Component build(CompositeComponent parent,
                           ComponentDefinition<DataSourceImplementation> definition,
                           DeploymentContext deploymentContext) throws BuilderConfigException {
        try {
            DataSourceImplementation implementation = definition.getImplementation();
            ClassLoader classLoader = implementation.getClassLoader();
            Class<?> beanClass = classLoader.loadClass(implementation.getProviderName());

            // handle configuration parameters
            List<Injector> injectors = new ArrayList<Injector>();
            Method[] methods = beanClass.getMethods();
            for (Map.Entry<String, String> entry : implementation.getConfigurationParams().entrySet()) {
                Method found = null;
                for (Method method : methods) {
                    String setterName = toSetter(entry.getKey());
                    if (method.getParameterTypes().length == 1 && method.getName().equals(setterName)) {
                        found = method;
                        break;
                    }
                }
                if (found == null) {
                    SetterNotFoundException e = new SetterNotFoundException("Setter method not found for parameter");
                    e.setIdentifier(entry.getKey());
                    throw e;
                }
                Class<?> type = found.getParameterTypes()[0];
                PropertyEditor editor = PropertyEditorManager.findEditor(type);
                if (editor == null) {
                    PropertyEditorNotFoundException e =
                        new PropertyEditorNotFoundException("Parameter type not supported");
                    e.setIdentifier(type.getName());
                    throw e;
                }
                Injector injector = new Injector(found, new ParameterObjectFactory(editor, entry.getValue()));
                injectors.add(injector);
            }

            ProviderObjectFactory providerFactory = new ProviderObjectFactory(beanClass, injectors);
            ScopeContainer scope = deploymentContext.getModuleScope();
            int initLevel = implementation.getComponentType().getInitLevel();
            return new DataSourceComponent(definition.getName(), providerFactory, parent, scope, initLevel);
        } catch (ClassNotFoundException e) {
            throw new BuilderConfigException(e);
        }
    }

    /**
     * Converts a property name to a corresponding setter method
     */
    private String toSetter(String name) {
        return "set" + name.toUpperCase().substring(0, 1) + name.substring(1);
    }

}
