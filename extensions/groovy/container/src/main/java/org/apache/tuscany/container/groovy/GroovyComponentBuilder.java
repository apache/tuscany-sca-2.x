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
package org.apache.tuscany.container.groovy;

import groovy.lang.GroovyClassLoader;
import groovy.lang.GroovyObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.tuscany.spi.ObjectFactory;
import org.apache.tuscany.spi.builder.BuilderConfigException;
import org.apache.tuscany.spi.component.Component;
import org.apache.tuscany.spi.component.CompositeComponent;
import org.apache.tuscany.spi.deployer.DeploymentContext;
import org.apache.tuscany.spi.extension.ComponentBuilderExtension;
import org.apache.tuscany.spi.model.ComponentDefinition;
import org.apache.tuscany.spi.model.Property;
import org.apache.tuscany.spi.model.ServiceDefinition;
import org.codehaus.groovy.control.CompilationFailedException;

/**
 * Extension point for creating {@link GroovyAtomicComponent}s from an assembly configuration
 *
 * @version $$Rev$$ $$Date$$
 */
public class GroovyComponentBuilder extends ComponentBuilderExtension<GroovyImplementation> {

    protected Class<GroovyImplementation> getImplementationType() {
        return GroovyImplementation.class;
    }

    @SuppressWarnings("unchecked")
    public Component build(CompositeComponent parent,
                           ComponentDefinition<GroovyImplementation> componentDefinition,
                           DeploymentContext deploymentContext)
        throws BuilderConfigException {

        String name = componentDefinition.getName();
        GroovyImplementation implementation = componentDefinition.getImplementation();
        GroovyComponentType componentType = implementation.getComponentType();

        int initLevel = componentType.getInitLevel();

        // get list of serviceBindings provided by this component
        Collection<ServiceDefinition> collection = componentType.getServices().values();
        List<Class<?>> services = new ArrayList<Class<?>>(collection.size());
        for (ServiceDefinition serviceDefinition : collection) {
            services.add(serviceDefinition.getServiceContract().getInterfaceClass());
        }

        // get the Groovy classloader for this deployment component
        GroovyClassLoader groovyClassLoader = (GroovyClassLoader) deploymentContext.getExtension("groovy.classloader");
        if (groovyClassLoader == null) {
            groovyClassLoader = new GroovyClassLoader(implementation.getApplicationLoader());
            deploymentContext.putExtension("groovy.classloader", groovyClassLoader);
        }

        // create the implementation class for the script
        Class<? extends GroovyObject> groovyClass;
        try {
            String script = implementation.getScript();
            // REVIEW JFM can we cache the class?
            groovyClass = groovyClassLoader.parseClass(script);
        } catch (CompilationFailedException e) {
            throw new BuilderConfigException(name, e);
        }
        // TODO deal with init and destroy

        GroovyConfiguration configuration = new GroovyConfiguration();
        configuration.setName(name);
        configuration.setGroovyClass(groovyClass);
        configuration.setParent(parent);

        configuration.setWireService(wireService);
        configuration.setWorkContext(workContext);
        configuration.setInitLevel(initLevel);
        configuration.setServices(services);
        configuration.setMonitor(monitor);
        GroovyAtomicComponent component = new GroovyAtomicComponent(configuration);

        // handle properties
        for (Property<?> property : componentType.getProperties().values()) {
            ObjectFactory<?> factory = property.getDefaultValueFactory();
            if (factory != null) {
                component.addPropertyFactory(property.getName(), factory);
            }
        }
        return component;
    }


}
