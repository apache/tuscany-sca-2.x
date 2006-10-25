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
package org.apache.tuscany.container.script.helper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.tuscany.spi.builder.BuilderConfigException;
import org.apache.tuscany.spi.component.Component;
import org.apache.tuscany.spi.component.CompositeComponent;
import org.apache.tuscany.spi.component.ScopeContainer;
import org.apache.tuscany.spi.deployer.DeploymentContext;
import org.apache.tuscany.spi.extension.ComponentBuilderExtension;
import org.apache.tuscany.spi.model.ComponentDefinition;
import org.apache.tuscany.spi.model.PropertyValue;
import org.apache.tuscany.spi.model.Scope;
import org.apache.tuscany.spi.model.ServiceDefinition;

/**
 * Extension point for creating {@link ScriptComponent}s from an assembly configuration
 */
public class ScriptHelperComponentBuilder extends ComponentBuilderExtension<ScriptHelperImplementation> {

    public ScriptHelperComponentBuilder() {
    }

    protected Class<ScriptHelperImplementation> getImplementationType() {
        return ScriptHelperImplementation.class;
    }

    @SuppressWarnings("unchecked")
    public Component build(CompositeComponent parent, ComponentDefinition<ScriptHelperImplementation> componentDefinition,
            DeploymentContext deploymentContext) throws BuilderConfigException {

        String name = componentDefinition.getName();
        ScriptHelperImplementation implementation = componentDefinition.getImplementation();
        ScriptHelperComponentType componentType = implementation.getComponentType();

        // get list of services provided by this component
        Collection<ServiceDefinition> collection = componentType.getServices().values();
        List<Class<?>> services = new ArrayList<Class<?>>(collection.size());
        for (ServiceDefinition serviceDefinition : collection) {
            services.add(serviceDefinition.getServiceContract().getInterfaceClass());
        }

        // get the properties for the component
        Map<String, Object> properties = new HashMap<String, Object>();
        for (PropertyValue propertyValue : componentDefinition.getPropertyValues().values()) {
            properties.put(propertyValue.getName(), propertyValue.getValueFactory().getInstance());
        }

        // TODO: have ComponentBuilderExtension pass ScopeContainer in on build method?
        ScopeContainer scopeContainer;
        Scope scope = componentType.getLifecycleScope();
        if (Scope.MODULE == scope) {
            scopeContainer = deploymentContext.getModuleScope();
        } else {
            scopeContainer = scopeRegistry.getScopeContainer(scope);
        }

        return new ScriptHelperComponent(name, implementation.getScriptInstanceFactory(), properties, services, parent, scopeContainer, wireService, workContext, workScheduler);
    }

}
