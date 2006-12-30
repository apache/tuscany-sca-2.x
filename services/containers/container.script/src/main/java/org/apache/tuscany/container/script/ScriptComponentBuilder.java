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
package org.apache.tuscany.container.script;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

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
 *
 * @version $Rev$ $Date$
 */
public class ScriptComponentBuilder extends ComponentBuilderExtension<ScriptImplementation> {

    public ScriptComponentBuilder() {
    }

    protected Class<ScriptImplementation> getImplementationType() {
        return ScriptImplementation.class;
    }

    @SuppressWarnings("unchecked")
    public Component build(CompositeComponent parent, ComponentDefinition<ScriptImplementation> componentDefinition,
                           DeploymentContext deploymentContext) throws BuilderConfigException {

        String name = componentDefinition.getName();
        ScriptImplementation implementation = componentDefinition.getImplementation();
        ScriptComponentType componentType = implementation.getComponentType();

        // get list of serviceBindings provided by this component
        Collection<ServiceDefinition> collection = componentType.getServices().values();
        List<Class<?>> services = new ArrayList<Class<?>>(collection.size());
        for (ServiceDefinition serviceDefinition : collection) {
            services.add(serviceDefinition.getServiceContract().getInterfaceClass());
        }

        // TODO: have ComponentBuilderExtension pass ScopeContainer in on build method?
        ScopeContainer scopeContainer;
        Scope scope = componentType.getLifecycleScope();
        if (Scope.COMPOSITE == scope) {
            scopeContainer = deploymentContext.getCompositeScope();
        } else {
            scopeContainer = scopeRegistry.getScopeContainer(scope);
        }
        String className = implementation.getClassName();
        String scriptSource = implementation.getScriptSource();
        String scriptName = implementation.getScriptName();
        ClassLoader cl = implementation.getClassLoader();
        ScriptInstanceFactory instanceFactory =
            new ScriptInstanceFactory(scriptName, className, scriptSource, cl);

        // get the properties for the component
        for (PropertyValue propertyValue : componentDefinition.getPropertyValues().values()) {
            //TODO this is not safe for since multiple instances can share mutable properties
            instanceFactory.addContextObjectFactory(propertyValue.getName(), propertyValue.getValueFactory());
        }

        ComponentConfiguration config = new ComponentConfiguration();
        config.setName(name);
        config.setFactory(instanceFactory);
        config.setServices(services);
        config.setParent(parent);
        config.setScopeContainer(scopeContainer);
        config.setWireService(wireService);
        config.setWorkContext(workContext);
        config.setWorkScheduler(workScheduler);
        return new ScriptComponent(config);
    }

}
